/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/BeanReader.java,v 1.17 2003/10/09 20:52:06 rdonkin Exp $
 * $Revision: 1.17 $
 * $Date: 2003/10/09 20:52:06 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 
package org.apache.commons.betwixt.io;

import java.beans.IntrospectionException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;

import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.read.ReadConfiguration;
import org.apache.commons.betwixt.io.read.ReadContext;

import org.apache.commons.digester.Digester;
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
  * @version $Revision: 1.17 $
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
