package org.apache.commons.betwixt;

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/XMLIntrospector.java,v 1.27 2003/10/19 14:53:52 mvdb Exp $
 * $Revision: 1.27 $
 * $Date: 2003/10/19 14:53:52 $
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

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.betwixt.digester.XMLBeanInfoDigester;
import org.apache.commons.betwixt.digester.XMLIntrospectorHelper;
import org.apache.commons.betwixt.expression.EmptyExpression;
import org.apache.commons.betwixt.expression.Expression;
import org.apache.commons.betwixt.expression.IteratorExpression;
import org.apache.commons.betwixt.expression.StringExpression;
import org.apache.commons.betwixt.expression.Updater;
import org.apache.commons.betwixt.registry.DefaultXMLBeanInfoRegistry;
import org.apache.commons.betwixt.registry.XMLBeanInfoRegistry;
import org.apache.commons.betwixt.strategy.ClassNormalizer;
import org.apache.commons.betwixt.strategy.DefaultNameMapper;
import org.apache.commons.betwixt.strategy.DefaultPluralStemmer;
import org.apache.commons.betwixt.strategy.NameMapper;
import org.apache.commons.betwixt.strategy.PluralStemmer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
  * @version $Id: XMLIntrospector.java,v 1.27 2003/10/19 14:53:52 mvdb Exp $
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

    /** Strategy normalizes the Class of the Object before introspection */
    private ClassNormalizer classNormalizer = new ClassNormalizer(); 

    /**
     * The strategy used to convert bean type names into attribute names
     * It will default to the normal nameMapper.
     */
    private NameMapper attributeNameMapper;
    /** Should the existing bean info search path for java.reflect.Introspector be used? */
    private boolean useBeanInfoSearchPath = false;
    
    /** Base constructor */
    public XMLIntrospector() {
    }
    
    /**
     * <p>Gets the current logging implementation. </p>
     * @return the Log implementation which this class logs to
     */ 
    public Log getLog() {
        return log;
    }

    /**
     * <p>Sets the current logging implementation.</p>
     * @param log the Log implementation to use for logging
     */ 
    public void setLog(Log log) {
        this.log = log;
    }
    
    /** 
     * <p>Gets the current registry implementation.
     * The registry is checked to see if it has an <code>XMLBeanInfo</code> for a class
     * before introspecting. 
     * After standard introspection is complete, the instance will be passed to the registry.</p>
     *
     * <p>This allows finely grained control over the caching strategy.
     * It also allows the standard introspection mechanism 
     * to be overridden on a per class basis.</p>
     *
     * @return the XMLBeanInfoRegistry currently used 
     */
    public XMLBeanInfoRegistry getRegistry() {
        return registry;
    }
    
    /** 
     * <p>Sets the <code>XMLBeanInfoRegistry</code> implementation.
     * The registry is checked to see if it has an <code>XMLBeanInfo</code> for a class
     * before introspecting. 
     * After standard introspection is complete, the instance will be passed to the registry.</p>
     *
     * <p>This allows finely grained control over the caching strategy.
     * It also allows the standard introspection mechanism 
     * to be overridden on a per class basis.</p>
     *
     * @param registry the XMLBeanInfoRegistry to use
     */
    public void setRegistry(XMLBeanInfoRegistry registry) {
        this.registry = registry;
    }
    
    
    /**
      * Gets the <code>ClassNormalizer</code> strategy.
      * This is used to determine the Class to be introspected
      * (the normalized Class). 
      *
      * @return the <code>ClassNormalizer</code> used to determine the Class to be introspected
      * for a given Object.
      */
    public ClassNormalizer getClassNormalizer() {
        return classNormalizer;
    }
    
    /**
      * Sets the <code>ClassNormalizer</code> strategy.
      * This is used to determine the Class to be introspected
      * (the normalized Class). 
      *
      * @param classNormalizer the <code>ClassNormalizer</code> to be used to determine 
      * the Class to be introspected for a given Object.
      */    
    public void setClassNormalizer(ClassNormalizer classNormalizer) {
        this.classNormalizer = classNormalizer;
    }
    
    /** 
     * Is <code>XMLBeanInfo</code> caching enabled? 
     *
     * @deprecated replaced by XMlBeanInfoRegistry
     * @return true if caching is enabled
     */
    public boolean isCachingEnabled() {
        return true;
    }

    /**
     * Set whether <code>XMLBeanInfo</code> caching should be enabled.
     *
     * @deprecated replaced by XMlBeanInfoRegistry
     * @param cachingEnabled ignored
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
      * The actual introspection depends only on the <code>BeanInfo</code>
      * associated with the bean.
      * 
      * @param bean introspect this bean
      * @return XMLBeanInfo describing bean-xml mapping
      * @throws IntrospectionException when the bean introspection fails
      */
    public XMLBeanInfo introspect(Object bean) throws IntrospectionException {
        if (log.isDebugEnabled()) {
            log.debug( "Introspecting..." );
            log.debug(bean);
        }
        
        if ( bean instanceof DynaBean ) {
            // allow DynaBean implementations to be overridden by .betwixt files
            XMLBeanInfo xmlBeanInfo = findByXMLDescriptor( bean.getClass() );
            if (xmlBeanInfo != null) {
                return xmlBeanInfo;
            }
            // this is DynaBean use the DynaClass for introspection
            return introspect( ((DynaBean) bean).getDynaClass() );
            
        } else {
            // normal bean so normal introspection
            Class normalClass = getClassNormalizer().getNormalizedClass( bean );
            return introspect( normalClass );
        }
    }
    
    /**
     * Creates XMLBeanInfo by reading the DynaProperties of a DynaBean.
     * Customizing DynaBeans using betwixt is not supported.
     * 
     * @param dynaClass the DynaBean to introspect
     * 
     * @return XMLBeanInfo for the DynaClass
     */
    public XMLBeanInfo introspect(DynaClass dynaClass) {

        // for now this method does not do much, since XMLBeanInfoRegistry cannot
        // use a DynaClass as a key
        // TODO: add caching for DynaClass XMLBeanInfo
        // need to work out if this is possible
        
        // this line allows subclasses to change creation strategy
        XMLBeanInfo xmlInfo = createXMLBeanInfo( dynaClass );
        
        // populate the created info with 
        DynaClassBeanType beanClass = new DynaClassBeanType( dynaClass );
        populate( xmlInfo, beanClass );
        
        return xmlInfo;  
    }
    
    /** Create a standard <code>XMLBeanInfo</code> by introspection.
      * The actual introspection depends only on the <code>BeanInfo</code>
      * associated with the bean.    
      *    
      * @param aClass introspect this class
      * @return XMLBeanInfo describing bean-xml mapping
      * @throws IntrospectionException when the bean introspection fails
      */
    public XMLBeanInfo introspect(Class aClass) throws IntrospectionException {
        // we first reset the beaninfo searchpath.
        String[] searchPath = null;
        if ( !useBeanInfoSearchPath ) {
            searchPath = Introspector.getBeanInfoSearchPath();
            Introspector.setBeanInfoSearchPath(new String[] { });
        }
        
        XMLBeanInfo xmlInfo = registry.get( aClass );
        
        if ( xmlInfo == null ) {
            // lets see if we can find an XML descriptor first
            if ( log.isDebugEnabled() ) {
                log.debug( "Attempting to lookup an XML descriptor for class: " + aClass );
            }
            
            xmlInfo = findByXMLDescriptor( aClass );
            if ( xmlInfo == null ) {
                BeanInfo info = Introspector.getBeanInfo( aClass );
                xmlInfo = introspect( info );
            }
            
            if ( xmlInfo != null ) {
                registry.put( aClass, xmlInfo );
            }
        } else {
            log.trace( "Used cached XMLBeanInfo." );
        }
        
        if ( log.isTraceEnabled() ) {
            log.trace( xmlInfo );
        }
        if ( !useBeanInfoSearchPath ) {
            // we restore the beaninfo searchpath.
            Introspector.setBeanInfoSearchPath( searchPath );
        }
        
        return xmlInfo;
    }
    
    /** Create a standard <code>XMLBeanInfo</code> by introspection. 
      * The actual introspection depends only on the <code>BeanInfo</code>
      * associated with the bean.
      *
      * @param beanInfo the BeanInfo the xml-bean mapping is based on
      * @return XMLBeanInfo describing bean-xml mapping
      * @throws IntrospectionException when the bean introspection fails
      */
    public XMLBeanInfo introspect(BeanInfo beanInfo) throws IntrospectionException {    
        XMLBeanInfo xmlBeanInfo = createXMLBeanInfo( beanInfo );
        populate( xmlBeanInfo, new JavaBeanType( beanInfo ) );
        return xmlBeanInfo;
    }
    
    /**
     * Populates the given <code>XMLBeanInfo</code> based on the given type of bean.
     *
     * @param xmlBeanInfo populate this, not null
     * @param bean the type definition for the bean, not null
     */
    private void populate(XMLBeanInfo xmlBeanInfo, BeanType bean) {    
        String name = bean.getBeanName();
        
        ElementDescriptor elementDescriptor = new ElementDescriptor();
        elementDescriptor.setLocalName( 
            getElementNameMapper().mapTypeToElementName( name ) );
        elementDescriptor.setPropertyType( bean.getElementType() );
        
        if (log.isTraceEnabled()) {
            log.trace("Populating:" + bean);
        }

        // add default string value for primitive types
        if ( bean.isPrimitiveType() ) {
            log.trace("Bean is primitive");
            elementDescriptor.setTextExpression( StringExpression.getInstance() );
            elementDescriptor.setPrimitiveType(true);
            
        } else if ( bean.isLoopType() ) {
            log.trace("Bean is loop");
            ElementDescriptor loopDescriptor = new ElementDescriptor();
            loopDescriptor.setContextExpression(
                new IteratorExpression( EmptyExpression.getInstance() )
            );
            if ( bean.isMapType() ) {
                loopDescriptor.setQualifiedName( "entry" );
            }
            elementDescriptor.setElementDescriptors( new ElementDescriptor[] { loopDescriptor } );
            
/*            
            elementDescriptor.setContextExpression(
                new IteratorExpression( EmptyExpression.getInstance() )
            );
*/
        } else {
            log.trace("Bean is standard type");
            List elements = new ArrayList();
            List attributes = new ArrayList();
            List contents = new ArrayList();

            addProperties( bean.getProperties(), elements, attributes, contents );    

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
            size = contents.size();
            if ( size > 0 ) {
                if ( size > 0 ) {
                    Descriptor[] descriptors = new Descriptor[size];
                    contents.toArray( descriptors );
                    elementDescriptor.setContentDescriptors( descriptors );
                }
            }
        }
        
        xmlBeanInfo.setElementDescriptor( elementDescriptor );        
        
        // default any addProperty() methods
        XMLIntrospectorHelper.defaultAddMethods( this, elementDescriptor, bean.getElementType() );
        
        if (log.isTraceEnabled()) {
            log.trace("Populated descriptor:");
            log.trace(elementDescriptor);
        }
    }

    
    /**
     * Creates XMLBeanInfo for the given DynaClass.
     * 
     * @param dynaClass the class describing a DynaBean
     * 
     * @return XMLBeanInfo that describes the properties of the given 
     * DynaClass
     */
    protected XMLBeanInfo createXMLBeanInfo(DynaClass dynaClass) {
        // XXX is the chosen class right?
        XMLBeanInfo beanInfo = new XMLBeanInfo(dynaClass.getClass());
        return beanInfo;
    }


    // Properties
    //-------------------------------------------------------------------------        
    
    /** 
      * Should attributes (or elements) be used for primitive types.
      * @return true if primitive types will be mapped to attributes in the introspection
      */
    public boolean isAttributesForPrimitives() {
        return attributesForPrimitives;
    }

    /** 
      * Set whether attributes (or elements) should be used for primitive types. 
      * @param attributesForPrimitives pass trus to map primitives to attributes,
      *        pass false to map primitives to elements
      */
    public void setAttributesForPrimitives(boolean attributesForPrimitives) {
        this.attributesForPrimitives = attributesForPrimitives;
    }

    /**
     * Should collections be wrapped in an extra element?
     * 
     * @return whether we should we wrap collections in an extra element? 
     */
    public boolean isWrapCollectionsInElement() {
        return wrapCollectionsInElement;
    }

    /** 
     * Sets whether we should we wrap collections in an extra element.
     *
     * @param wrapCollectionsInElement pass true if collections should be wrapped in a
     *        parent element
     */
    public void setWrapCollectionsInElement(boolean wrapCollectionsInElement) {
        this.wrapCollectionsInElement = wrapCollectionsInElement;
    }

    /** 
     * Get singular and plural matching strategy.
     *
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
     *
     * @param pluralStemmer the PluralStemmer used to match singular and plural
     */
    public void setPluralStemmer(PluralStemmer pluralStemmer) {
        this.pluralStemmer = pluralStemmer;
    }

    /** 
     * Gets the name mapper strategy.
     * 
     * @return the strategy used to convert bean type names into element names
     * @deprecated getNameMapper is split up in 
     * {@link #getElementNameMapper()} and {@link #getAttributeNameMapper()}
     */
    public NameMapper getNameMapper() {
        return getElementNameMapper();
    }
    
    /** 
     * Sets the strategy used to convert bean type names into element names
     * @param nameMapper the NameMapper strategy to be used
     * @deprecated setNameMapper is split up in 
     * {@link #setElementNameMapper(NameMapper)} and {@link #setAttributeNameMapper(NameMapper)}
     */
    public void setNameMapper(NameMapper nameMapper) {
        setElementNameMapper(nameMapper);
    }


    /**
     * Gets the name mapping strategy used to convert bean names into elements.
     *
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
     * Sets the strategy used to convert bean type names into element names
     * @param nameMapper the NameMapper to use for the conversion
     */
    public void setElementNameMapper(NameMapper nameMapper) {
        this.elementNameMapper = nameMapper;
    }
    

    /**
     * Gets the name mapping strategy used to convert bean names into attributes.
     *
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
     * @param nameMapper the NameMapper to use for the convertion
     */
    public void setAttributeNameMapper(NameMapper nameMapper) {
        this.attributeNameMapper = nameMapper;
    }

    /** 
     * Create a XML descriptor from a bean one. 
     * Go through and work out whether it's a loop property, a primitive or a standard.
     * The class property is ignored.
     *
     * @param propertyDescriptor create a <code>NodeDescriptor</code> for this property
     * @param useAttributesForPrimitives write primitives as attributes (rather than elements)
     * @return a correctly configured <code>NodeDescriptor</code> for the property
     * @throws IntrospectionException when bean introspection fails
     * @deprecated use {@link #createXMLDescriptor}.
     * @since Alpha1
     */
    public Descriptor createDescriptor(
        PropertyDescriptor propertyDescriptor, 
        boolean useAttributesForPrimitives
    ) throws IntrospectionException {
        return createXMLDescriptor( new BeanProperty( propertyDescriptor ) );
    }
 
    /** 
     * Create a XML descriptor from a bean one. 
     * Go through and work out whether it's a loop property, a primitive or a standard.
     * The class property is ignored.
     *
     * @param beanProperty the BeanProperty specifying the property
     * @return a correctly configured <code>NodeDescriptor</code> for the property
     */
    public Descriptor createXMLDescriptor( BeanProperty beanProperty ) {
        String name = beanProperty.getPropertyName();
        Class type = beanProperty.getPropertyType();
       
        if (log.isTraceEnabled()) {
            log.trace("Creating descriptor for property: name="
                + name + " type=" + type);
        }
        
        Descriptor descriptor = null;
        Expression propertyExpression = beanProperty.getPropertyExpression();
        Updater propertyUpdater = beanProperty.getPropertyUpdater();
        
        if ( propertyExpression == null ) {
            if (log.isTraceEnabled()) {
                log.trace( "No read method for property: name="
                    + name + " type=" + type);
            }
            return null;
        }
        
        if ( log.isTraceEnabled() ) {
            log.trace( "Property expression=" + propertyExpression );
        }
        
        // choose response from property type
        
        // XXX: ignore class property ??
        if ( Class.class.equals( type ) && "class".equals( name ) ) {
            log.trace( "Ignoring class property" );
            return null;
            
        }
        
        if ( isPrimitiveType( type ) ) {
            if (log.isTraceEnabled()) {
                log.trace( "Primitive type: " + name);
            }
            if ( isAttributesForPrimitives() ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Adding property as attribute: " + name );
                }
                descriptor = new AttributeDescriptor();
            } else {
                if (log.isTraceEnabled()) {
                    log.trace( "Adding property as element: " + name );
                }
                descriptor = new ElementDescriptor(true);
            }
            descriptor.setTextExpression( propertyExpression );
            if ( propertyUpdater != null ) {
                descriptor.setUpdater( propertyUpdater );
            }
            
        } else if ( isLoopType( type ) ) {
            if (log.isTraceEnabled()) {
                log.trace("Loop type: " + name);
                log.trace("Wrap in collections? " + isWrapCollectionsInElement());
            }
            ElementDescriptor loopDescriptor = new ElementDescriptor();
            loopDescriptor.setContextExpression(
                new IteratorExpression( propertyExpression )
            );
            loopDescriptor.setWrapCollectionsInElement( isWrapCollectionsInElement() );
            // XXX: need to support some kind of 'add' or handle arrays, Lists or indexed properties
            //loopDescriptor.setUpdater( new MethodUpdater( writeMethod ) );
            if ( Map.class.isAssignableFrom( type ) ) {
                loopDescriptor.setQualifiedName( "entry" );
                // add elements for reading
                loopDescriptor.addElementDescriptor( new ElementDescriptor( "key" ) );
                loopDescriptor.addElementDescriptor( new ElementDescriptor( "value" ) );
            }

            ElementDescriptor elementDescriptor = new ElementDescriptor();
            elementDescriptor.setWrapCollectionsInElement( isWrapCollectionsInElement() );
            elementDescriptor.setElementDescriptors( new ElementDescriptor[] { loopDescriptor } );
            
            descriptor = elementDescriptor;
            
        } else {
            if (log.isTraceEnabled()) {
                log.trace( "Standard property: " + name);
            }
            ElementDescriptor elementDescriptor = new ElementDescriptor();
            elementDescriptor.setContextExpression( propertyExpression );
            if ( propertyUpdater != null ) {
                elementDescriptor.setUpdater( propertyUpdater );
            }
            
            descriptor = elementDescriptor;
        }

        if (descriptor instanceof NodeDescriptor) {
            NodeDescriptor nodeDescriptor = (NodeDescriptor) descriptor;
            if (descriptor instanceof AttributeDescriptor) {
                // we want to use the attributemapper only when it is an attribute.. 
                nodeDescriptor.setLocalName( 
                    getAttributeNameMapper().mapTypeToElementName( name ) );
                
            } else {
                nodeDescriptor.setLocalName( 
                    getElementNameMapper().mapTypeToElementName( name ) );
            }        
        }
  
        descriptor.setPropertyName( name );
        descriptor.setPropertyType( type );
        
        // XXX: associate more bean information with the descriptor?
        //nodeDescriptor.setDisplayName( propertyDescriptor.getDisplayName() );
        //nodeDescriptor.setShortDescription( propertyDescriptor.getShortDescription() );
        
        if (log.isTraceEnabled()) {
            log.trace( "Created descriptor:" );
            log.trace( descriptor );
        }
        return descriptor;
    }



    
    // Implementation methods
    //-------------------------------------------------------------------------        
    
    /** 
     * A Factory method to lazily create a new strategy 
     * to detect matching singular and plural properties.
     *
     * @return new defualt PluralStemmer implementation
     */
    protected PluralStemmer createPluralStemmer() {
        return new DefaultPluralStemmer();
    }
    
    /** 
     * A Factory method to lazily create a strategy 
     * used to convert bean type names into element names.
     *
     * @return new default NameMapper implementation
     */
    protected NameMapper createNameMapper() {
        return new DefaultNameMapper();
    }
    
    /** 
     * Attempt to lookup the XML descriptor for the given class using the
     * classname + ".betwixt" using the same ClassLoader used to load the class
     * or return null if it could not be loaded
     * 
     * @param aClass digester .betwixt file for this class
     * @return XMLBeanInfo digested from the .betwixt file if one can be found.
     *         Otherwise null.
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
            } catch (Exception e) {
                log.warn( "Caught exception trying to parse: " + name, e );
            }
        }
        
        if ( log.isTraceEnabled() ) {
            log.trace( "Could not find betwixt file " + name );
        }
        return null;
    }
            
    /** 
     * Loop through properties and process each one 
     *
     * @param beanInfo the BeanInfo whose properties will be processed
     * @param elements ElementDescriptor list to which elements will be added
     * @param attributes AttributeDescriptor list to which attributes will be added
     * @param contents Descriptor list to which mixed content will be added
     * @throws IntrospectionException if the bean introspection fails
     * @deprecated use {@link #addProperties(BeanProperty[], List, List,List)}
     */
    protected void addProperties(
                                    BeanInfo beanInfo, 
                                    List elements, 
                                    List attributes,
                                    List contents)
                                        throws 
                                            IntrospectionException {
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        if ( descriptors != null ) {
            for ( int i = 0, size = descriptors.length; i < size; i++ ) {
                addProperty(beanInfo, descriptors[i], elements, attributes, contents);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace(elements);
            log.trace(attributes);
            log.trace(contents);
        }
    }
    /** 
     * Loop through properties and process each one 
     *
     * @param beanProperties the properties to be processed
     * @param elements ElementDescriptor list to which elements will be added
     * @param attributes AttributeDescriptor list to which attributes will be added
     * @param contents Descriptor list to which mixed content will be added
     */
    protected void addProperties(
                                    BeanProperty[] beanProperties, 
                                    List elements, 
                                    List attributes,
                                    List contents) {
        if ( beanProperties != null ) {
            if (log.isTraceEnabled()) {
                log.trace(beanProperties.length + " properties to be added");
            }
            for ( int i = 0, size = beanProperties.length; i < size; i++ ) {
                addProperty(beanProperties[i], elements, attributes, contents);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("After properties have been added (elements, attributes, contents):");
            log.trace(elements);
            log.trace(attributes);
            log.trace(contents);
        }
    }    

    
    /** 
     * Process a property. 
     * Go through and work out whether it's a loop property, a primitive or a standard.
     * The class property is ignored.
     *
     * @param beanInfo the BeanInfo whose property is being processed
     * @param propertyDescriptor the PropertyDescriptor to process
     * @param elements ElementDescriptor list to which elements will be added
     * @param attributes AttributeDescriptor list to which attributes will be added
     * @param contents Descriptor list to which mixed content will be added
     * @throws IntrospectionException if the bean introspection fails
     * @deprecated BeanInfo is no longer required. 
     * Use {@link #addProperty(PropertyDescriptor, List, List, List)} instead.
     */
    protected void addProperty(
                                BeanInfo beanInfo, 
                                PropertyDescriptor propertyDescriptor, 
                                List elements, 
                                List attributes,
                                List contents)
                                    throws 
                                        IntrospectionException {
       addProperty( propertyDescriptor, elements, attributes, contents);
    }
    
    /** 
     * Process a property. 
     * Go through and work out whether it's a loop property, a primitive or a standard.
     * The class property is ignored.
     *
     * @param propertyDescriptor the PropertyDescriptor to process
     * @param elements ElementDescriptor list to which elements will be added
     * @param attributes AttributeDescriptor list to which attributes will be added
     * @param contents Descriptor list to which mixed content will be added
     * @throws IntrospectionException if the bean introspection fails
     * @deprecated use {@link #addProperty(BeanProperty, List, List, List)} instead
     */
    protected void addProperty(
                                PropertyDescriptor propertyDescriptor, 
                                List elements, 
                                List attributes,
                                List contents)
                                    throws 
                                        IntrospectionException {
        addProperty(new BeanProperty( propertyDescriptor ), elements, attributes, contents);
    }
    
    /** 
     * Process a property. 
     * Go through and work out whether it's a loop property, a primitive or a standard.
     * The class property is ignored.
     *
     * @param beanProperty the bean property to process
     * @param elements ElementDescriptor list to which elements will be added
     * @param attributes AttributeDescriptor list to which attributes will be added
     * @param contents Descriptor list to which mixed content will be added
     */
    protected void addProperty(
                                BeanProperty beanProperty, 
                                List elements, 
                                List attributes,
                                List contents) {
        Descriptor nodeDescriptor = createXMLDescriptor(beanProperty);
        if (nodeDescriptor == null) {
           return;
        }
        if (nodeDescriptor instanceof ElementDescriptor) {
           elements.add(nodeDescriptor);
        } else if (nodeDescriptor instanceof AttributeDescriptor) {
           attributes.add(nodeDescriptor);
        } else {
           contents.add(nodeDescriptor);
        }                                 
    }
    
    /** 
     * Loop through properties and process each one 
     *
     * @param beanInfo the BeanInfo whose properties will be processed
     * @param elements ElementDescriptor list to which elements will be added
     * @param attributes AttributeDescriptor list to which attributes will be added
     * @throws IntrospectionException if the bean introspection fails
     * @deprecated this method does not support mixed content. 
     * Use {@link #addProperties(BeanInfo, List, List, List)} instead.
     */
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
     *
     * @param beanInfo the BeanInfo whose property is being processed
     * @param propertyDescriptor the PropertyDescriptor to process
     * @param elements ElementDescriptor list to which elements will be added
     * @param attributes AttributeDescriptor list to which attributes will be added
     * @throws IntrospectionException if the bean introspection fails
     * @deprecated this method does not support mixed content. 
     * Use {@link #addProperty(BeanInfo, PropertyDescriptor, List, List, List)} instead.
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

    
    /** 
     * Factory method to create XMLBeanInfo instances 
     *
     * @param beanInfo the BeanInfo from which the XMLBeanInfo will be created
     * @return XMLBeanInfo describing the bean-xml mapping
     */
    protected XMLBeanInfo createXMLBeanInfo( BeanInfo beanInfo ) {
        XMLBeanInfo xmlBeanInfo = new XMLBeanInfo( beanInfo.getBeanDescriptor().getBeanClass() );
        return xmlBeanInfo;
    }

    /** 
     * Is this class a loop?
     *
     * @param type the Class to test
     * @return true if the type is a loop type 
     */
    public boolean isLoopType(Class type) {
        return XMLIntrospectorHelper.isLoopType(type);
    }
    
    
    /** 
     * Is this class a primitive?
     * @param type the Class to test
     * @return true for primitive types 
     */
    public boolean isPrimitiveType(Class type) {
        return XMLIntrospectorHelper.isPrimitiveType(type);
    }
    /**
     * Should the original <code>java.reflect.Introspector</code> bean info search path be used?
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
    
    /** Some type of pseudo-bean */
    private abstract class BeanType {
        /** 
         * Gets the name for this bean type 
         * @return the bean type name, not null
         */
        public abstract String getBeanName();
        
        /** 
         * Gets the type to be used by the associated element
         * @return a Class that is the type not null
         */
        public abstract Class getElementType();

        /**
         * Is this type a primitive?
         * @return true if this type should be treated by betwixt as a primitive
         */
        public abstract boolean isPrimitiveType();
        
        /**
         * is this type a map?
         * @return true this should be treated as a map.
         */
        public abstract boolean isMapType();
        
        /** 
         * Is this type a loop?
         * @return true if this should be treated as a loop
         */
        public abstract boolean isLoopType();
        
        /**
         * Gets the properties associated with this bean.
         * @return the BeanProperty's, not null
         */
        public abstract BeanProperty[] getProperties();
        
        /**
         * Create string representation
         * @return something useful for logging
         */
        public String toString() {
            return "Bean[name=" + getBeanName() + ", type=" + getElementType();
        }
    }
    
    /** Supports standard Java Beans */
    private class JavaBeanType extends BeanType {
        /** Introspected bean */
        private BeanInfo beanInfo;
        /** Bean class */
        private Class beanClass;
        /** Bean name */
        private String name;
        /** Bean properties */
        private BeanProperty[] properties;
        
        /**
         * Constructs a BeanType for a standard Java Bean
         * @param beanInfo the BeanInfo describing the standard Java Bean, not null
         */
        public JavaBeanType(BeanInfo beanInfo) {
            this.beanInfo = beanInfo;
            BeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor();
            beanClass = beanDescriptor.getBeanClass();
            name = beanDescriptor.getName();
            // Array's contain a bad character
            if (beanClass.isArray()) {
                // called all array's Array
                name = "Array";
            }
            
        }
        
        /** @see BeanType #getElementType */
        public Class getElementType() {
            return beanClass;
        }
        
        /** @see BeanType#getBeanName */
        public String getBeanName() {
            return name;
        }
        
        /** @see BeanType#isPrimitiveType */
        public boolean isPrimitiveType() {
            return XMLIntrospectorHelper.isPrimitiveType( beanClass );
        }
        
        /** @see BeanType#isLoopType */
        public boolean isLoopType() {
            return XMLIntrospectorHelper.isLoopType( beanClass );
        }
        
        /** @see BeanType#isMapType */
        public boolean isMapType() {
            return Map.class.isAssignableFrom( beanClass );
        }
        
        /** @see BeanType#getProperties */
        public BeanProperty[] getProperties() {
            // lazy creation
            if ( properties == null ) {
                ArrayList propertyDescriptors = new ArrayList();
                // add base bean info
                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
                if ( descriptors != null ) {
                    for (int i=0, size=descriptors.length; i<size; i++) {
                        propertyDescriptors.add( descriptors[i] );
                    }
                }
                
                // add properties from additional bean infos
                BeanInfo[] additionals = beanInfo.getAdditionalBeanInfo();
                if ( additionals != null ) {
                    for ( int i=0, outerSize=additionals.length; i<outerSize; i++ ) {
                        BeanInfo additionalInfo = additionals[i];
                        descriptors = beanInfo.getPropertyDescriptors();
                        if ( descriptors != null ) {
                            for (int j=0, innerSize=descriptors.length; j<innerSize; j++) {
                                propertyDescriptors.add( descriptors[j] );
                            }
                        }
                    }            
                }
                // what happens when size is zero?
                properties = new BeanProperty[ propertyDescriptors.size() ];
                int count = 0;
                for ( Iterator it = propertyDescriptors.iterator(); it.hasNext(); count++) {
                    PropertyDescriptor propertyDescriptor = (PropertyDescriptor) it.next();
                    properties[count] = new BeanProperty( propertyDescriptor );
                }
            }
            return properties;
        }
    }
    
    /** Implementation for DynaClasses */
    private class DynaClassBeanType extends BeanType {
        /** BeanType for this DynaClass */
        private DynaClass dynaClass;
        /** Properties extracted in constuctor */
        private BeanProperty[] properties;
        
        /** 
         * Constructs a BeanType for a DynaClass
         * @param dynaClass not null
         */
        public DynaClassBeanType(DynaClass dynaClass) {
            this.dynaClass = dynaClass;
            DynaProperty[] dynaProperties = dynaClass.getDynaProperties();
            properties = new BeanProperty[dynaProperties.length];
            for (int i=0, size=dynaProperties.length; i<size; i++) {
                properties[i] = new BeanProperty(dynaProperties[i]);
            }
        }
        
        /** @see BeanType#getBeanName */
        public String getBeanName() {
            return dynaClass.getName();
        }
        /** @see BeanType#getElementType */
        public Class getElementType() {
            return DynaClass.class;
        }
        /** @see BeanType#isPrimitiveType */
        public boolean isPrimitiveType() {
            return false;
        }
        /** @see BeanType#isMapType */
        public boolean isMapType() {
            return false;
        }
        /** @see BeanType#isLoopType */
        public boolean isLoopType() {
            return false;
        }
        /** @see BeanType#getProperties */
        public BeanProperty[] getProperties() {
            return properties;
        }
    }
}
