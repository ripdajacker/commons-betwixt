package org.apache.commons.betwixt;

/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 */

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import org.apache.commons.betwixt.expression.EmptyExpression;
import org.apache.commons.betwixt.expression.IteratorExpression;
import org.apache.commons.betwixt.expression.MethodExpression;
import org.apache.commons.betwixt.expression.MethodUpdater;
import org.apache.commons.betwixt.expression.StringExpression;
import org.apache.commons.betwixt.digester.XMLBeanInfoDigester;
import org.apache.commons.betwixt.digester.XMLIntrospectorHelper;
import org.apache.commons.betwixt.strategy.DefaultNameMapper;
import org.apache.commons.betwixt.strategy.DefaultPluralStemmer;
import org.apache.commons.betwixt.strategy.NameMapper;
import org.apache.commons.betwixt.strategy.PluralStemmer;
import org.apache.commons.betwixt.registry.XMLBeanInfoRegistry;
import org.apache.commons.betwixt.registry.DefaultXMLBeanInfoRegistry;

/** 
  * <p><code>XMLIntrospector</code> an introspector of beans to create a 
  * XMLBeanInfo instance.</p>
  *
  * <p>By default, <code>XMLBeanInfo</code> caching is switched on.
  * This means that the first time that a request is made for a <code>XMLBeanInfo</code>
  * for a particular class, the <code>XMLBeanInfo</code> is cached.
  * Later requests for the same class will return the cached value.</p>
  * 
  * <p>Note :</p>
  * <p>This class makes use of the <code>java.bean.Introspector</code>
  * class, which contains a BeanInfoSearchPath. To make sure betwixt can
  * do his work correctly, this searchpath is completely ignored during 
  * processing. The original values will be restored after processing finished
  * </p>
  * 
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
  * @version $Id: XMLIntrospector.java,v 1.12 2002/11/30 17:16:37 rdonkin Exp $
  */
public class XMLIntrospector {

    /** Log used for logging (Doh!) */    
    protected Log log = LogFactory.getLog( XMLIntrospector.class );
    
    /** should attributes or elements be used for primitive types */
    private boolean attributesForPrimitives = false;
    
    /** should we wrap collections in an extra element? */
    private boolean wrapCollectionsInElement = true;
    
    /** Maps classes to <code>XMLBeanInfo</code>'s */
    private XMLBeanInfoRegistry registry = new DefaultXMLBeanInfoRegistry();
    
    /** Digester used to parse the XML descriptor files */
    private XMLBeanInfoDigester digester;

    // pluggable strategies
        
    /** The strategy used to detect matching singular and plural properties */
    private PluralStemmer pluralStemmer;
    
    /** The strategy used to convert bean type names into element names */
    private NameMapper elementNameMapper;

    /**
     * The strategy used to convert bean type names into attribute names
     * It will default to the normal nameMapper.
     */
    private NameMapper attributeNameMapper;
    
    private boolean useBeanInfoSearchPath = false;
    
    /** Base constructor */
    public XMLIntrospector() {
    }
    
    /**
     * <p> Get the current logging implementation. </p>
     */ 
    public Log getLog() {
        return log;
    }

    /**
     * <p> Set the current logging implementation. </p>
     */ 
    public void setLog(Log log) {
        this.log = log;
    }
    
    public XMLBeanInfoRegistry getRegistry() {
        return registry;
    }
    
    public void setRegistry(XMLBeanInfoRegistry registry) {
        this.registry = registry;
    }
    
    
    /** 
     * Is <code>XMLBeanInfo</code> caching enabled? 
     *
     * @deprecated replaced by XMlBeanInfoRegistry
     */
    public boolean isCachingEnabled() {
        return true;
    }

    /**
     * Set whether <code>XMLBeanInfo</code> caching should be enabled.
     *
     * @deprecated replaced by XMlBeanInfoRegistry
     */    
    public void setCachingEnabled(boolean cachingEnabled) {
        //
    }
    
    /**
     * Flush existing cached <code>XMLBeanInfo</code>'s.
     *
     * @deprecated use flushable registry instead
     */
    public void flushCache() {}
    
    /** Create a standard <code>XMLBeanInfo</code> by introspection
        The actual introspection depends only on the <code>BeanInfo</code>
        associated with the bean.
        */
    public XMLBeanInfo introspect(Object bean) throws IntrospectionException {
        if (log.isDebugEnabled()) {
            log.debug( "Introspecting..." );
            log.debug(bean);
        }
        return introspect( bean.getClass() );
    }
    
    /** Create a standard <code>XMLBeanInfo</code> by introspection.
        The actual introspection depends only on the <code>BeanInfo</code>
        associated with the bean.        
      */
    public XMLBeanInfo introspect(Class aClass) throws IntrospectionException {
        // we first reset the beaninfo searchpath.
        String[] searchPath = null;
        if (!useBeanInfoSearchPath)
        {
            searchPath = Introspector.getBeanInfoSearchPath();
            Introspector.setBeanInfoSearchPath(new String[] { });
        }
        
        XMLBeanInfo xmlInfo = null;
        // see if info's in registry
        xmlInfo = registry.get( aClass );
        if (xmlInfo == null) {
            // lets see if we can find an XML descriptor first
            if ( log.isDebugEnabled() ) {
                log.debug( "Attempting to lookup an XML descriptor for class: " + aClass );
            }
            
            xmlInfo = findByXMLDescriptor( aClass );
            if ( xmlInfo == null ) {
                BeanInfo info = Introspector.getBeanInfo( aClass );
                xmlInfo = introspect( info );
            }
            
            if (xmlInfo != null) {
                registry.put( aClass, xmlInfo );
            }
        } else {
            log.trace("Used cached XMLBeanInfo.");
        }
        
        if (log.isTraceEnabled()) {
            log.trace(xmlInfo);
        }
        if (!useBeanInfoSearchPath)
        {
            // we restore the beaninfo searchpath.
            Introspector.setBeanInfoSearchPath(searchPath);
        }
        
        return xmlInfo;
    }
    
    /** Create a standard <code>XMLBeanInfo</code> by introspection. 
        The actual introspection depends only on the <code>BeanInfo</code>
        associated with the bean.
        */
    public XMLBeanInfo introspect(BeanInfo beanInfo) throws IntrospectionException {    
        XMLBeanInfo answer = createXMLBeanInfo( beanInfo );

        BeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor();
        Class beanClass = beanDescriptor.getBeanClass();
        
        ElementDescriptor elementDescriptor = new ElementDescriptor();
        elementDescriptor.setLocalName( getElementNameMapper().mapTypeToElementName( beanDescriptor.getName() ) );
        elementDescriptor.setPropertyType( beanInfo.getBeanDescriptor().getBeanClass() );
        
        if (log.isTraceEnabled()) {
            log.trace(elementDescriptor);
        }

        // add default string value for primitive types
        if ( isPrimitiveType( beanClass ) ) {
            elementDescriptor.setTextExpression( StringExpression.getInstance() );
            elementDescriptor.setPrimitiveType(true);
        }
        else if ( isLoopType( beanClass ) ) {
            ElementDescriptor loopDescriptor = new ElementDescriptor();
            loopDescriptor.setContextExpression(
                new IteratorExpression( EmptyExpression.getInstance() )
            );
            if ( Map.class.isAssignableFrom( beanClass ) ) {
                loopDescriptor.setQualifiedName( "entry" );
            }
            elementDescriptor.setElementDescriptors( new ElementDescriptor[] { loopDescriptor } );
            
/*            
            elementDescriptor.setContextExpression(
                new IteratorExpression( EmptyExpression.getInstance() )
            );
*/
        }
        else {
            List elements = new ArrayList();
            List attributes = new ArrayList();

            addProperties( beanInfo, elements, attributes );

            BeanInfo[] additionals = beanInfo.getAdditionalBeanInfo();
            if ( additionals != null ) {
                for ( int i = 0, size = additionals.length; i < size; i++ ) {
                    BeanInfo otherInfo = additionals[i];
                    addProperties( otherInfo, elements, attributes );
                }            
            }        

            int size = elements.size();
            if ( size > 0 ) {
                ElementDescriptor[] descriptors = new ElementDescriptor[size];
                elements.toArray( descriptors );
                elementDescriptor.setElementDescriptors( descriptors );
            }
            size = attributes.size();
            if ( size > 0 ) {
                AttributeDescriptor[] descriptors = new AttributeDescriptor[size];
                attributes.toArray( descriptors );
                elementDescriptor.setAttributeDescriptors( descriptors );
            }
        }
        
        answer.setElementDescriptor( elementDescriptor );        
        
        // default any addProperty() methods
        XMLIntrospectorHelper.defaultAddMethods( this, elementDescriptor, beanClass );
        
        return answer;
    }


    // Properties
    //-------------------------------------------------------------------------        
    
    /** Should attributes (or elements) be used for primitive types.
     */
    public boolean isAttributesForPrimitives() {
        return attributesForPrimitives;
    }

    /** Set whether attributes (or elements) should be used for primitive types. */
    public void setAttributesForPrimitives(boolean attributesForPrimitives) {
        this.attributesForPrimitives = attributesForPrimitives;
    }

    /** @return whether we should we wrap collections in an extra element? */
    public boolean isWrapCollectionsInElement() {
        return wrapCollectionsInElement;
    }

    /** Sets whether we should we wrap collections in an extra element? */
    public void setWrapCollectionsInElement(boolean wrapCollectionsInElement) {
        this.wrapCollectionsInElement = wrapCollectionsInElement;
    }

    /** 
     * @return the strategy used to detect matching singular and plural properties 
     */
    public PluralStemmer getPluralStemmer() {
        if ( pluralStemmer == null ) {
            pluralStemmer = createPluralStemmer();
        }
        return pluralStemmer;
    }
    
    /** 
     * Sets the strategy used to detect matching singular and plural properties 
     */
    public void setPluralStemmer(PluralStemmer pluralStemmer) {
        this.pluralStemmer = pluralStemmer;
    }

    /** 
     * @return the strategy used to convert bean type names into element names
     * @deprecated getNameMapper is split up in {@link #getElementNameMapper()} and {@link #getAttributeNameMapper()}
     */
    public NameMapper getNameMapper() {
        return getElementNameMapper();
    }
    
    /** 
     * Sets the strategy used to convert bean type names into element names
     * @param nameMapper
     * @deprecated setNameMapper is split up in {@link #setElementNameMapper(NameMapper)} and {@link #setAttributeNameMapper(NameMapper)}
     */
    public void setNameMapper(NameMapper nameMapper) {
        setElementNameMapper(nameMapper);
    }


    /**
     * @return the strategy used to convert bean type names into element 
     * names. If no element mapper is currently defined then a default one is created.
     */
    public NameMapper getElementNameMapper() {
        if ( elementNameMapper == null ) {
            elementNameMapper = createNameMapper();
         }
        return elementNameMapper;
    }
     
    /**
     *  Sets the strategy used to convert bean type names into element names
     * @param nameMapper
     */
    public void setElementNameMapper(NameMapper nameMapper) {
        this.elementNameMapper = nameMapper;
    }
    

    /**
     * @return the strategy used to convert bean type names into attribute
     * names. If no attributeNamemapper is known, it will default to the ElementNameMapper
     */
    public NameMapper getAttributeNameMapper() {
        if (attributeNameMapper == null) {
            attributeNameMapper = createNameMapper();
        }
        return attributeNameMapper;
     }


    /**
     * Sets the strategy used to convert bean type names into attribute names
     * @param nameMapper
     */
    public void setAttributeNameMapper(NameMapper nameMapper) {
        this.attributeNameMapper = nameMapper;
    }

 



    
    // Implementation methods
    //-------------------------------------------------------------------------        
    
    /** 
     * A Factory method to lazily create a new strategy to detect matching singular and plural properties 
     */
    protected PluralStemmer createPluralStemmer() {
        return new DefaultPluralStemmer();
    }
    
    /** 
     * A Factory method to lazily create a strategy used to convert bean type names into element names
     */
    protected NameMapper createNameMapper() {
        return new DefaultNameMapper();
    }
    
    /** 
     * Attempt to lookup the XML descriptor for the given class using the
     * classname + ".betwixt" using the same ClassLoader used to load the class
     * or return null if it could not be loaded
     */
    protected synchronized XMLBeanInfo findByXMLDescriptor( Class aClass ) {
        // trim the package name
        String name = aClass.getName();
        int idx = name.lastIndexOf( '.' );
        if ( idx >= 0 ) {
            name = name.substring( idx + 1 );
        }
        name += ".betwixt";
        
        URL url = aClass.getResource( name );
        if ( url != null ) {
            try {
                String urlText = url.toString();
                if ( log.isDebugEnabled( )) {
                    log.debug( "Parsing Betwixt XML descriptor: " + urlText );
                }
                // synchronized method so this digester is only used by
                // one thread at once
                if ( digester == null ) {
                    digester = new XMLBeanInfoDigester();
                    digester.setXMLIntrospector( this );
                }
                digester.setBeanClass( aClass );
                return (XMLBeanInfo) digester.parse( urlText );
            }
            catch (Exception e) {
                log.warn( "Caught exception trying to parse: " + name, e );
            }
        }
        
        if ( log.isTraceEnabled() ) {
            log.trace( "Could not find betwixt file " + name );
        }
        return null;
    }
            
    /** Loop through properties and process each one */
    protected void addProperties(
                                    BeanInfo beanInfo, 
                                    List elements, 
                                    List attributes) 
                                        throws 
                                            IntrospectionException {
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        if ( descriptors != null ) {
            for ( int i = 0, size = descriptors.length; i < size; i++ ) {
                addProperty(beanInfo, descriptors[i], elements, attributes);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace(elements);
            log.trace(attributes);
        }
    }
    
    /** 
     * Process a property. 
     * Go through and work out whether it's a loop property, a primitive or a standard.
     * The class property is ignored.
     */
    protected void addProperty(
                                BeanInfo beanInfo, 
                                PropertyDescriptor propertyDescriptor, 
                                List elements, 
                                List attributes) 
                                    throws 
                                        IntrospectionException {
        NodeDescriptor nodeDescriptor = XMLIntrospectorHelper
            .createDescriptor(propertyDescriptor,
                                 isAttributesForPrimitives(),
                                 this);
        if (nodeDescriptor == null) {
           return;
        }
        if (nodeDescriptor instanceof ElementDescriptor) {
           elements.add(nodeDescriptor);
        } else {
           attributes.add(nodeDescriptor);
        }
    }
    
    /** Factory method to create XMLBeanInfo instances */
    protected XMLBeanInfo createXMLBeanInfo( BeanInfo beanInfo ) {
        XMLBeanInfo answer = new XMLBeanInfo( beanInfo.getBeanDescriptor().getBeanClass() );
        return answer;
    }

    /** Returns true if the type is a loop type */
    public boolean isLoopType(Class type) {
        return XMLIntrospectorHelper.isLoopType(type);
    }
    
    
    /** Returns true for primitive types */
    public boolean isPrimitiveType(Class type) {
        return XMLIntrospectorHelper.isPrimitiveType(type);
    }
    /**
     * By default it will be false.
     * 
     * @return boolean if the beanInfoSearchPath should be used.
     */
    public boolean useBeanInfoSearchPath() {
        return useBeanInfoSearchPath;
    }

    /**
     * Specifies if you want to use the beanInfoSearchPath 
     * @see java.beans.Introspector for more details
     * @param useBeanInfoSearchPath 
     */
    public void setUseBeanInfoSearchPath(boolean useBeanInfoSearchPath) {
        this.useBeanInfoSearchPath = useBeanInfoSearchPath;
    }

}
