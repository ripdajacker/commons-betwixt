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

/** 
  * <p><code>XMLIntrospector</code> an introspector of beans to create a 
  * XMLBeanInfo instance.</p>
  *
  * <p>By default, <code>XMLBeanInfo</code> caching is switched on.
  * This means that the first time that a request is made for a <code>XMLBeanInfo</code>
  * for a particular class, the <code>XMLBeanInfo</code> is cached.
  * Later requests for the same class will return the cached value.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Id: XMLIntrospector.java,v 1.1 2002/06/10 17:53:34 jstrachan Exp $
  */
public class XMLIntrospector {

    /** Log used for logging (Doh!) */    
    protected Log log = LogFactory.getLog( XMLIntrospector.class );
    
    /** should attributes or elements be used for primitive types */
    private boolean attributesForPrimitives = false;
    
    /** should we wrap collections in an extra element? */
    private boolean wrapCollectionsInElement = false;
    
    /** Is <code>XMLBeanInfo</code> caching enabled? */
    boolean cachingEnabled = true;
    
    /** Maps classes to <code>XMLBeanInfo</code>'s */
    protected Map cacheXMLBeanInfos = new HashMap();
    
    /** Digester used to parse the XML descriptor files */
    private XMLBeanInfoDigester digester;

    // pluggable strategies
        
    /** The strategy used to detect matching singular and plural properties */
    private PluralStemmer pluralStemmer;
    
    /** The strategy used to convert bean type names into element names */
    private NameMapper nameMapper;
    
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
    
    /** 
     * Is <code>XMLBeanInfo</code> caching enabled? 
     */
    public boolean isCachingEnabled() {
        return cachingEnabled;
    }

    /**
     * Set whether <code>XMLBeanInfo</code> caching should be enabled.
     */    
    public void setCachingEnabled(boolean cachingEnabled) {
        this.cachingEnabled = cachingEnabled;
    }
    
    /**
     * Flush existing cached <code>XMLBeanInfo</code>'s.
     */
    public void flushCache() {
        cacheXMLBeanInfos.clear();
    }
    
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
        XMLBeanInfo xmlInfo = null;
        if ( cachingEnabled ) {
            // if caching is enabled, try in caching first
            xmlInfo = (XMLBeanInfo) cacheXMLBeanInfos.get( aClass );
        }
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
                cacheXMLBeanInfos.put( aClass, xmlInfo );
            }
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
        elementDescriptor.setLocalName( getNameMapper().mapTypeToElementName( beanDescriptor.getName() ) );
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
     */
    public NameMapper getNameMapper() {
        if ( nameMapper == null ) {
            nameMapper = createNameMapper();
        }
        return nameMapper;
    }
    
    /** 
     * Sets the strategy used to convert bean type names into element names
     */
    public void setNameMapper(NameMapper nameMapper) {
        this.nameMapper = nameMapper;
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
        Class type = propertyDescriptor.getPropertyType();
        NodeDescriptor nodeDescriptor = null;
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        
        if ( readMethod == null ) {
            if (log.isTraceEnabled()) {
                log.trace( "No read method" );
            }
            return;
        }
        
        if ( log.isTraceEnabled() ) {
            log.trace( "Read method=" + readMethod.getName() );
        }
        
        // choose response from property type
        
        // XXX: ignore class property ??
        if ( Class.class.equals( type ) && "class".equals( propertyDescriptor.getName() ) ) {
            if (log.isTraceEnabled()) {
                log.trace( "Ignoring class property" );
            }
            return;
        }
        if ( isPrimitiveType( type ) ) {
            if (log.isTraceEnabled()) {
                log.trace( "Primative type" );
            }
            if ( isAttributesForPrimitives() ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Added attribute" );
                }
                nodeDescriptor = new AttributeDescriptor();
                attributes.add( nodeDescriptor );
            }
            else {
                if (log.isTraceEnabled()) {
                    log.trace( "Added element" );
                }
                nodeDescriptor = new ElementDescriptor(true);
                elements.add( nodeDescriptor );
            }
            nodeDescriptor.setTextExpression( new MethodExpression( readMethod ) );
            
            if ( writeMethod != null ) {
                nodeDescriptor.setUpdater( new MethodUpdater( writeMethod ) );
            }
        }
        else if ( isLoopType( type ) ) {
            if (log.isTraceEnabled()) {
                log.trace("Loop type");
            }
            ElementDescriptor loopDescriptor = new ElementDescriptor();
            loopDescriptor.setContextExpression(
                new IteratorExpression( new MethodExpression( readMethod ) )
            );
            // XXX: need to support some kind of 'add' or handle arrays, s or indexed properties
            //loopDescriptor.setUpdater( new MethodUpdater( writeMethod ) );
            if ( Map.class.isAssignableFrom( type ) ) {
                loopDescriptor.setQualifiedName( "entry" );
            }
            
            ElementDescriptor elementDescriptor = new ElementDescriptor();
            elementDescriptor.setElementDescriptors( new ElementDescriptor[] { loopDescriptor } );
            
            nodeDescriptor = elementDescriptor;            
            elements.add( nodeDescriptor );
        }
        else {
            if (log.isTraceEnabled()) {
                log.trace( "Standard property" );
            }
            ElementDescriptor elementDescriptor = new ElementDescriptor();
            elementDescriptor.setContextExpression( new MethodExpression( readMethod ) );
            
            if ( writeMethod != null ) {
                elementDescriptor.setUpdater( new MethodUpdater( writeMethod ) );
            }
            
            nodeDescriptor = elementDescriptor;            
            elements.add( nodeDescriptor );
        }

        nodeDescriptor.setLocalName( getNameMapper().mapTypeToElementName( propertyDescriptor.getName() ) );
        nodeDescriptor.setPropertyName( propertyDescriptor.getName() );
        nodeDescriptor.setPropertyType( type );        
        
        // XXX: associate more bean information with the descriptor?
        //nodeDescriptor.setDisplayName( propertyDescriptor.getDisplayName() );
        //nodeDescriptor.setShortDescription( propertyDescriptor.getShortDescription() );
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
}
