/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.apache.commons.betwixt.io;

import java.beans.IntrospectionException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;

import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.read.ReadConfiguration;
import org.apache.commons.betwixt.io.read.ReadContext;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ExtendedBaseRules;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.XMLReader;

/** <p><code>BeanReader</code> reads a tree of beans from an XML document.</p>
  *
  * <p>Call {@link #registerBeanClass(Class)} or {@link #registerBeanClass(String, Class)}
  * to add rules to map a bean class.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.21 $
  */
public class BeanReader extends Digester {

    /** Introspector used */
    private XMLIntrospector introspector = new XMLIntrospector();    
    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( BeanReader.class );
    /** The registered classes */
    private Set registeredClasses = new HashSet();
    /** Dynamic binding configuration settings */
    private BindingConfiguration bindingConfiguration = new BindingConfiguration();
    /** Reading specific configuration settings */
    private ReadConfiguration readConfiguration = new ReadConfiguration();
    
    /**
     * Construct a new BeanReader with default properties.
     */
    public BeanReader() {
    	// TODO: now we require extended rules may need to document this
    	setRules(new ExtendedBaseRules());
    }

    /**
     * Construct a new BeanReader, allowing a SAXParser to be passed in.  This
     * allows BeanReader to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Thanks for the request to change go to
     * James House (james@interobjective.com).  This may help in places where
     * you are able to load JAXP 1.1 classes yourself.
     *
     * @param parser use this <code>SAXParser</code>
     */
    public BeanReader(SAXParser parser) {
        super(parser);
		setRules(new ExtendedBaseRules());
    }

    /**
     * Construct a new BeanReader, allowing an XMLReader to be passed in.  This
     * allows BeanReader to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Note that if you use this option you
     * have to configure namespace and validation support yourself, as these
     * properties only affect the SAXParser and emtpy constructor.
     *
     * @param reader use this <code>XMLReader</code> as source for SAX events
     */
    public BeanReader(XMLReader reader) {
        super(reader);
		setRules(new ExtendedBaseRules());
    }

    
    /** 
     * <p>Register a bean class and add mapping rules for this bean class.</p>
     * 
     * <p>A bean class is introspected when it is registered.
     * It will <strong>not</strong> be introspected again even if the introspection
     * settings are changed.
     * If re-introspection is required, then {@link #deregisterBeanClass} must be called 
     * and the bean re-registered.</p>
     *
     * <p>A bean class can only be registered once. 
     * If the same class is registered a second time, this registration will be ignored.
     * In order to change a registration, call {@link #deregisterBeanClass} 
     * before calling this method.</p>
     *
     * <p>All the rules required to digest this bean are added when this method is called.
     * Other rules that you want to execute before these should be added before this
     * method is called. 
     * Those that should be executed afterwards, should be added afterwards.</p>
     *
     * @param beanClass the <code>Class</code> to be registered
     * @throws IntrospectionException if the bean introspection fails
     */
    public void registerBeanClass(Class beanClass) throws IntrospectionException {
        if ( ! registeredClasses.contains( beanClass ) ) {
            if ( log.isTraceEnabled() ) {
                log.trace( "Registering class " + beanClass );
            }
            registeredClasses.add( beanClass );
            
            // introspect and find the ElementDescriptor to use as the root
            XMLBeanInfo xmlInfo = introspector.introspect( beanClass );
            ElementDescriptor elementDescriptor = xmlInfo.getElementDescriptor();        

            String path = elementDescriptor.getQualifiedName();
            if (log.isTraceEnabled()) {
                log.trace("Added path: " + path + ", mapped to: " + beanClass.getName());
            }
            addBeanCreateRule( path, elementDescriptor, beanClass );
            
        } else {
            if ( log.isWarnEnabled() ) {
                log.warn("Cannot add class "  + beanClass.getName() + " since it already exists");
            }
        }
    }
    
    /** 
     * <p>Registers a bean class  
     * and add mapping rules for this bean class at the given path expression.</p>
     * 
     * 
     * <p>A bean class is introspected when it is registered.
     * It will <strong>not</strong> be introspected again even if the introspection
     * settings are changed.
     * If re-introspection is required, then {@link #deregisterBeanClass} must be called 
     * and the bean re-registered.</p>
     *
     * <p>A bean class can only be registered once. 
     * If the same class is registered a second time, this registration will be ignored.
     * In order to change a registration, call {@link #deregisterBeanClass} 
     * before calling this method.</p>
     *
     * <p>All the rules required to digest this bean are added when this method is called.
     * Other rules that you want to execute before these should be added before this
     * method is called. 
     * Those that should be executed afterwards, should be added afterwards.</p>
     *
     * @param path the xml path expression where the class is to registered. 
     * This should be in digester path notation
     * @param beanClass the <code>Class</code> to be registered
     * @throws IntrospectionException if the bean introspection fails
     */
    public void registerBeanClass(String path, Class beanClass) throws IntrospectionException {
        if ( ! registeredClasses.contains( beanClass ) ) {
            registeredClasses.add( beanClass );
            
            // introspect and find the ElementDescriptor to use as the root
            XMLBeanInfo xmlInfo = introspector.introspect( beanClass );
            ElementDescriptor elementDescriptor = xmlInfo.getElementDescriptor();        

            addBeanCreateRule( path, elementDescriptor, beanClass );
        } else {
            if ( log.isWarnEnabled() ) {
                log.warn("Cannot add class "  + beanClass.getName() + " since it already exists");
            }
        }
    }
    
    /**
     * <p>Flush all registered bean classes.
     * This allows all bean classes to be re-registered 
     * by a subsequent calls to <code>registerBeanClass</code>.</p>
     *
     * <p><strong>Note</strong> that deregistering a bean does <strong>not</strong>
     * remove the Digester rules associated with that bean.</p>
     */
    public void flushRegisteredBeanClasses() {    
        registeredClasses.clear();
    }
    
    /**
     * <p>Remove the given class from the register.
     * Calling this method will allow the bean class to be re-registered 
     * by a subsequent call to <code>registerBeanClass</code>.
     * This allows (for example) a bean to be reintrospected after a change
     * to the introspection settings.</p>
     *
     * <p><strong>Note</strong> that deregistering a bean does <strong>not</strong>
     * remove the Digester rules associated with that bean.</p>
     *
     * @param beanClass the <code>Class</code> to remove from the set of registered bean classes
     */
    public void deregisterBeanClass( Class beanClass ) {
        registeredClasses.remove( beanClass );
    }
    
    // Properties
    //-------------------------------------------------------------------------        

    /**
     * <p> Get the introspector used. </p>
     *
     * <p> The {@link XMLBeanInfo} used to map each bean is 
     * created by the <code>XMLIntrospector</code>.
     * One way in which the mapping can be customized is by 
     * altering the <code>XMLIntrospector</code>. </p>
     * 
     * @return the <code>XMLIntrospector</code> used for the introspection
     */
    public XMLIntrospector getXMLIntrospector() {
        return introspector;
    }
    

    /**
     * <p> Set the introspector to be used. </p>
     *
     * <p> The {@link XMLBeanInfo} used to map each bean is 
     * created by the <code>XMLIntrospector</code>.
     * One way in which the mapping can be customized is by 
     * altering the <code>XMLIntrospector</code>. </p>
     *
     * @param introspector use this introspector
     */
    public void setXMLIntrospector(XMLIntrospector introspector) {
        this.introspector = introspector;
    }

    /**
     * <p> Get the current level for logging. </p>
     *
     * @return the <code>Log</code> implementation this class logs to
     */ 
    public Log getLog() {
        return log;
    }

    /**
     * <p> Set the current logging level. </p>
     *
     * @param log the <code>Log</code>implementation to use for logging
     */ 
    public void setLog(Log log) {
        this.log = log;
        setLogger(log);
    }
    
    /** 
     * Should the reader use <code>ID</code> attributes to match beans.
     *
     * @return true if <code>ID</code> and <code>IDREF</code> 
     * attributes should be used to match instances
     * @deprecated use {@link BindingConfiguration#getMapIDs}
     */
    public boolean getMatchIDs() {
        return getBindingConfiguration().getMapIDs();
    }
    
    /**
     * Set whether the read should use <code>ID</code> attributes to match beans.
     *
     * @param matchIDs pass true if <code>ID</code>'s should be matched
     * @deprecated use {@link BindingConfiguration#setMapIDs}
     */
    public void setMatchIDs(boolean matchIDs) {
        getBindingConfiguration().setMapIDs( matchIDs );
    }
    
    /**
     * Gets the dynamic configuration setting to be used for bean reading.
     * @return the BindingConfiguration settings, not null
     */
    public BindingConfiguration getBindingConfiguration() {
        return bindingConfiguration;
    }
    
    /**
     * Sets the dynamic configuration setting to be used for bean reading.
     * @param bindingConfiguration the BindingConfiguration settings, not null
     */
    public void setBindingConfiguration( BindingConfiguration bindingConfiguration ) {
        this.bindingConfiguration = bindingConfiguration;
    }
    
    /**
     * Gets read specific configuration details.
     * @return the ReadConfiguration, not null
     */
    public ReadConfiguration getReadConfiguration() {
        return readConfiguration;
    }
    
    /**
     * Sets the read specific configuration details.
     * @param readConfiguration not null
     */
    public void setReadConfiguration( ReadConfiguration readConfiguration ) {
        this.readConfiguration = readConfiguration;
    }
        
    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /** 
     * Adds a new bean create rule for the specified path
     *
     * @param path the digester path at which this rule should be added
     * @param elementDescriptor the <code>ElementDescriptor</code> describes the expected element 
     * @param beanClass the <code>Class</code> of the bean created by this rule
     */
    protected void addBeanCreateRule( 
                                    String path, 
                                    ElementDescriptor elementDescriptor, 
                                    Class beanClass ) {
        if (log.isTraceEnabled()) {
            log.trace("Adding BeanRuleSet for " + beanClass);
        }
        RuleSet ruleSet = new BeanRuleSet( 
                                            introspector, 
                                            path ,  
                                            elementDescriptor, 
                                            beanClass, 
                                            makeContext());
        addRuleSet( ruleSet );
    }
        
    /**
      * Factory method for new contexts.
      * Ensure that they are correctly configured.
      * @return the ReadContext created, not null
      */
    private ReadContext makeContext() {
        return new ReadContext( log, bindingConfiguration, readConfiguration );
    }
}
