package org.apache.commons.betwixt;

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/XMLIntrospector.java,v 1.27.2.6 2004/01/19 22:38:08 rdonkin Exp $
 * $Revision: 1.27.2.6 $
 * $Date: 2004/01/19 22:38:08 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.betwixt.digester.XMLBeanInfoDigester;
import org.apache.commons.betwixt.digester.XMLIntrospectorHelper;
import org.apache.commons.betwixt.expression.EmptyExpression;
import org.apache.commons.betwixt.expression.IteratorExpression;
import org.apache.commons.betwixt.expression.MapEntryAdder;
import org.apache.commons.betwixt.expression.MethodUpdater;
import org.apache.commons.betwixt.expression.StringExpression;
import org.apache.commons.betwixt.registry.DefaultXMLBeanInfoRegistry;
import org.apache.commons.betwixt.registry.XMLBeanInfoRegistry;
import org.apache.commons.betwixt.strategy.ClassNormalizer;
import org.apache.commons.betwixt.strategy.DefaultNameMapper;
import org.apache.commons.betwixt.strategy.DefaultPluralStemmer;
import org.apache.commons.betwixt.strategy.NameMapper;
import org.apache.commons.betwixt.strategy.PluralStemmer;
import org.apache.commons.logging.Log;

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
  * @version $Id: XMLIntrospector.java,v 1.27.2.6 2004/01/19 22:38:08 rdonkin Exp $
  */
public class XMLIntrospector {
    
    /** Maps classes to <code>XMLBeanInfo</code>'s */
    private XMLBeanInfoRegistry registry = new DefaultXMLBeanInfoRegistry();
    
    /** Digester used to parse the XML descriptor files */
    private XMLBeanInfoDigester digester;

    /** Configuration to be used for introspection*/
    private IntrospectionConfiguration configuration;
    
    /** Base constructor */
    public XMLIntrospector() {
        this(new IntrospectionConfiguration());
    }
    
    /**
     * Construct allows a custom configuration to be set on construction.
     * This allows <code>IntrospectionConfiguration</code> subclasses
     * to be easily used.
     * @param configuration IntrospectionConfiguration, not null
     */
    public XMLIntrospector(IntrospectionConfiguration configuration) {
        setConfiguration(configuration);
    }
    
    
    // Properties
    //-------------------------------------------------------------------------   
    
    /**
     * <p>Gets the current logging implementation. </p>
     * @return the Log implementation which this class logs to
     */ 
    public Log getLog() {
        return getConfiguration().getIntrospectionLog();
    }

    /**
     * <p>Sets the current logging implementation.</p>
     * @param log the Log implementation to use for logging
     */ 
    public void setLog(Log log) {
        getConfiguration().setIntrospectionLog(log);
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
     * Gets the configuration to be used for introspection.
     * The various introspection-time strategies 
     * and configuration variables have been consolidated as properties
     * of this bean.
     * This allows the configuration to be more easily shared.
     * @return IntrospectionConfiguration, not null
     */
    public IntrospectionConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Sets the configuration to be used for introspection.
     * The various introspection-time strategies 
     * and configuration variables have been consolidated as properties
     * of this bean.
     * This allows the configuration to be more easily shared.
     * @param configuration IntrospectionConfiguration, not null
     */
    public void setConfiguration(IntrospectionConfiguration configuration) {
        this.configuration = configuration;
    }
    
    
    /**
      * Gets the <code>ClassNormalizer</code> strategy.
      * This is used to determine the Class to be introspected
      * (the normalized Class). 
      *
      * @return the <code>ClassNormalizer</code> used to determine the Class to be introspected
      * for a given Object.
      * @deprecated use getConfiguration().getClassNormalizer
      */
    public ClassNormalizer getClassNormalizer() {
        return getConfiguration().getClassNormalizer();
    }
    
    /**
      * Sets the <code>ClassNormalizer</code> strategy.
      * This is used to determine the Class to be introspected
      * (the normalized Class). 
      *
      * @param classNormalizer the <code>ClassNormalizer</code> to be used to determine 
      * the Class to be introspected for a given Object.
      * @deprecated use getConfiguration().setClassNormalizer
      */    
    public void setClassNormalizer(ClassNormalizer classNormalizer) {
        getConfiguration().setClassNormalizer(classNormalizer);
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
      * Should attributes (or elements) be used for primitive types.
      * @return true if primitive types will be mapped to attributes in the introspection
      * @deprecated use getConfiguration().isAttributesForPrimitives
      */
    public boolean isAttributesForPrimitives() {
        return getConfiguration().isAttributesForPrimitives();
    }

    /** 
      * Set whether attributes (or elements) should be used for primitive types. 
      * @param attributesForPrimitives pass trus to map primitives to attributes,
      *        pass false to map primitives to elements
      * @deprecated use getConfiguration().setAttributesForPrimitives
      */
    public void setAttributesForPrimitives(boolean attributesForPrimitives) {
        getConfiguration().setAttributesForPrimitives(attributesForPrimitives);
    }

    /**
     * Should collections be wrapped in an extra element?
     * 
     * @return whether we should we wrap collections in an extra element? 
     * @deprecated use getConfiguration().isWrapCollectionsInElement
     */
    public boolean isWrapCollectionsInElement() {
        return getConfiguration().isWrapCollectionsInElement();
    }

    /** 
     * Sets whether we should we wrap collections in an extra element.
     *
     * @param wrapCollectionsInElement pass true if collections should be wrapped in a
     *        parent element
     * @deprecated use getConfiguration().setWrapCollectionsInElement
     */
    public void setWrapCollectionsInElement(boolean wrapCollectionsInElement) {
        getConfiguration().setWrapCollectionsInElement(wrapCollectionsInElement);
    }

    /** 
     * Get singular and plural matching strategy.
     *
     * @return the strategy used to detect matching singular and plural properties 
     * @deprecated use getConfiguration().getPluralStemmer
     */
    public PluralStemmer getPluralStemmer() {
        return getConfiguration().getPluralStemmer();
    }
    
    /** 
     * Sets the strategy used to detect matching singular and plural properties 
     *
     * @param pluralStemmer the PluralStemmer used to match singular and plural
     * @deprecated use getConfiguration().setPluralStemmer 
     */
    public void setPluralStemmer(PluralStemmer pluralStemmer) {
        getConfiguration().setPluralStemmer(pluralStemmer);
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
     * @deprecated use getConfiguration().getElementNameMapper
     */ 
    public NameMapper getElementNameMapper() {
        return getConfiguration().getElementNameMapper();
    }
     
    /**
     * Sets the strategy used to convert bean type names into element names
     * @param nameMapper the NameMapper to use for the conversion
     * @deprecated use getConfiguration().setElementNameMapper
     */
    public void setElementNameMapper(NameMapper nameMapper) {
        getConfiguration().setElementNameMapper( nameMapper );
    }
    

    /**
     * Gets the name mapping strategy used to convert bean names into attributes.
     *
     * @return the strategy used to convert bean type names into attribute
     * names. If no attributeNamemapper is known, it will default to the ElementNameMapper
     * @deprecated getConfiguration().getAttributeNameMapper
     */
    public NameMapper getAttributeNameMapper() {
        return getConfiguration().getAttributeNameMapper();
     }


    /**
     * Sets the strategy used to convert bean type names into attribute names
     * @param nameMapper the NameMapper to use for the convertion
     * @deprecated use getConfiguration().setAttributeNameMapper
     */
    public void setAttributeNameMapper(NameMapper nameMapper) {
        getConfiguration().setAttributeNameMapper( nameMapper );
    }
    
    /**
     * Should the original <code>java.reflect.Introspector</code> bean info search path be used?
     * By default it will be false.
     * 
     * @return boolean if the beanInfoSearchPath should be used.
     * @deprecated use getConfiguration().useBeanInfoSearchPath
     */
    public boolean useBeanInfoSearchPath() {
        return getConfiguration().useBeanInfoSearchPath();
    }

    /**
     * Specifies if you want to use the beanInfoSearchPath 
     * @see java.beans.Introspector for more details
     * @param useBeanInfoSearchPath 
     * @deprecated use getConfiguration().setUseBeanInfoSearchPath
     */
    public void setUseBeanInfoSearchPath(boolean useBeanInfoSearchPath) {
        getConfiguration().setUseBeanInfoSearchPath( useBeanInfoSearchPath );
    }
    
    // Methods
    //------------------------------------------------------------------------- 
    
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
        if (getLog().isDebugEnabled()) {
            getLog().debug( "Introspecting..." );
            getLog().debug(bean);
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
        if ( !getConfiguration().useBeanInfoSearchPath() ) {
            searchPath = Introspector.getBeanInfoSearchPath();
            Introspector.setBeanInfoSearchPath(new String[] { });
        }
        
        XMLBeanInfo xmlInfo = registry.get( aClass );
        
        if ( xmlInfo == null ) {
            // lets see if we can find an XML descriptor first
            if ( getLog().isDebugEnabled() ) {
                getLog().debug( "Attempting to lookup an XML descriptor for class: " + aClass );
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
            getLog().trace( "Used cached XMLBeanInfo." );
        }
        
        if ( getLog().isTraceEnabled() ) {
            getLog().trace( xmlInfo );
        }
        if ( !getConfiguration().useBeanInfoSearchPath() ) {
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
        
        if (getLog().isTraceEnabled()) {
            getLog().trace("Populating:" + bean);
        }

        // add default string value for primitive types
        if ( bean.isPrimitiveType() ) {
            getLog().trace("Bean is primitive");
            elementDescriptor.setTextExpression( StringExpression.getInstance() );
            
        } else if ( bean.isLoopType() ) {
            getLog().trace("Bean is loop");
            ElementDescriptor loopDescriptor = new ElementDescriptor();
            loopDescriptor.setContextExpression(
                new IteratorExpression( EmptyExpression.getInstance() )
            );
            if ( bean.isMapType() ) {
                loopDescriptor.setQualifiedName( "entry" );
            }
            elementDescriptor.setElementDescriptors( new ElementDescriptor[] { loopDescriptor } );
            
        } else {
            getLog().trace("Bean is standard type");
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
        defaultAddMethods( elementDescriptor, bean.getElementType() );
        
        if (getLog().isTraceEnabled()) {
            getLog().trace("Populated descriptor:");
            getLog().trace(elementDescriptor);
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
        return beanProperty.createXMLDescriptor( configuration );
    }


    /** 
     * Add any addPropety(PropertyType) methods as Updaters 
     * which are often used for 1-N relationships in beans.
     * <br>
     * The tricky part here is finding which ElementDescriptor corresponds
     * to the method. e.g. a property 'items' might have an Element descriptor
     * which the method addItem() should match to. 
     * <br>
     * So the algorithm we'll use 
     * by default is to take the decapitalized name of the property being added
     * and find the first ElementDescriptor that matches the property starting with
     * the string. This should work for most use cases. 
     * e.g. addChild() would match the children property.
     * <br>
     * TODO this probably needs refactoring. It probably belongs in the bean wrapper
     * (so that it'll work properly with dyna-beans) and so that the operations can 
     * be optimized by caching. Multiple hash maps are created and getMethods is
     * called multiple times. This is relatively expensive and so it'd be better
     * to push into a proper class and cache.
     * <br>
     * TODO this probably does work properly with DynaBeans: need to push
     * implementation into an class and expose it on BeanType.
     *
     * @param introspector use this <code>XMLIntrospector</code> for introspection
     * @param rootDescriptor add defaults to this descriptor
     * @param beanClass the <code>Class</code> to which descriptor corresponds
     */
    public void defaultAddMethods( 
                                            ElementDescriptor rootDescriptor, 
                                            Class beanClass ) {
                                              
        // lets iterate over all methods looking for one of the form
        // add*(PropertyType)
        if ( beanClass != null ) {
            ArrayList singleParameterAdders = new ArrayList();
            ArrayList twinParameterAdders = new ArrayList();
            
            Method[] methods = beanClass.getMethods();
            for ( int i = 0, size = methods.length; i < size; i++ ) {
                Method method = methods[i];
                String name = method.getName();
                if ( name.startsWith( "add" )) {
                    // XXX: should we filter out non-void returning methods?
                    // some beans will return something as a helper
                    Class[] types = method.getParameterTypes();
                    if ( types != null) {
                        if ( getLog().isTraceEnabled() ) {
                            getLog().trace("Searching for match for " + method);
                        }
                        
                        switch (types.length)
                        {
                            case 1:
                                singleParameterAdders.add(method);
                                break;
                            case 2:
                                twinParameterAdders.add(method);
                                break;
                            default:
                                // ignore
                                break;
                        }
                    }
                }
            }
            
            Map elementsByPropertyName = makeElementDescriptorMap( rootDescriptor );
            
            for (Iterator it=singleParameterAdders.iterator();it.hasNext();) {
                Method singleParameterAdder = (Method) it.next();
                setIteratorAdder(elementsByPropertyName, singleParameterAdder);
            }
            
            for (Iterator it=twinParameterAdders.iterator();it.hasNext();) {
                Method twinParameterAdder = (Method) it.next();
                setMapAdder(elementsByPropertyName, twinParameterAdder);
            }
        }
    }
    
    /**
     * Sets the adder method where the corresponding property is an iterator
     * @param rootDescriptor
     * @param singleParameterAdder
     */
    private void setIteratorAdder(
        Map elementsByPropertyName,
        Method singleParameterAdderMethod) {
        
        String adderName = singleParameterAdderMethod.getName();
        String propertyName = Introspector.decapitalize(adderName.substring(3));
        ElementDescriptor matchingDescriptor = getMatchForAdder(propertyName, elementsByPropertyName);
        if (matchingDescriptor != null) {
            //TODO defensive code: probably should check descriptor type
            
            Class singularType = singleParameterAdderMethod.getParameterTypes()[0];
            if (getLog().isTraceEnabled()) {
                getLog().trace(adderName + "->" + propertyName);
            }
            // this may match a standard collection or iteration
            getLog().trace("Matching collection or iteration");
                                    
            matchingDescriptor.setUpdater( new MethodUpdater( singleParameterAdderMethod ) );
            matchingDescriptor.setSingularPropertyType( singularType );
                                    
            if ( getLog().isDebugEnabled() ) {
                getLog().debug( "!! " + singleParameterAdderMethod);
                getLog().debug( "!! " + singularType);
            }
                                    
            // is there a child element with no localName
            ElementDescriptor[] children 
                = matchingDescriptor.getElementDescriptors();
            if ( children != null && children.length > 0 ) {
                ElementDescriptor child = children[0];
                String localName = child.getLocalName();
                if ( localName == null || localName.length() == 0 ) {
                    child.setLocalName( 
                        getElementNameMapper()
                            .mapTypeToElementName( propertyName ) );
                }
            }
        }
    }
    
    /**
     * Sets the adder where the corresponding property type is an map
     * @param rootDescriptor
     * @param singleParameterAdder
     */
    private void setMapAdder(
        Map elementsByPropertyName,
        Method twinParameterAdderMethod) {
        String adderName = twinParameterAdderMethod.getName();
        String propertyName = Introspector.decapitalize(adderName.substring(3));
        ElementDescriptor matchingDescriptor = getMatchForAdder(propertyName, elementsByPropertyName);
        if ( matchingDescriptor != null && Map.class.isAssignableFrom( matchingDescriptor.getPropertyType() )) {
            // this may match a map
            getLog().trace("Matching map");
            ElementDescriptor[] children 
                = matchingDescriptor.getElementDescriptors();
            // see if the descriptor's been set up properly
            if ( children.length == 0 ) {                                        
                getLog().info(
                    "'entry' descriptor is missing for map. "
                    + "Updaters cannot be set");
                                        
            } else {
                Class[] types = twinParameterAdderMethod.getParameterTypes();
                Class keyType = types[0];
                Class valueType = types[1];
                
                // loop through grandchildren 
                // adding updaters for key and value
                ElementDescriptor[] grandchildren
                    = children[0].getElementDescriptors();
                MapEntryAdder adder = new MapEntryAdder(twinParameterAdderMethod);
                for ( 
                    int n=0, 
                        noOfGrandChildren = grandchildren.length;
                    n < noOfGrandChildren;
                    n++ ) {
                    if ( "key".equals( 
                            grandchildren[n].getLocalName() ) ) {
                                            
                        grandchildren[n].setUpdater( 
                                        adder.getKeyUpdater() );
                        grandchildren[n].setSingularPropertyType( 
                                        keyType );
                        if ( getLog().isTraceEnabled() ) {
                            getLog().trace(
                                "Key descriptor: " + grandchildren[n]);
                        }                                               
                                                
                    } else if ( 
                        "value".equals( 
                            grandchildren[n].getLocalName() ) ) {

                        grandchildren[n].setUpdater( 
                                            adder.getValueUpdater() );
                        grandchildren[n].setSingularPropertyType( 
                                            valueType );
                        if ( getLog().isTraceEnabled() ) {
                            getLog().trace(
                                "Value descriptor: " + grandchildren[n]);
                        }
                    }
                }
            }       
        }
    }
        
    /**
     * Gets an ElementDescriptor for the property matching the adder
     * @param adderName
     * @param rootDescriptor
     * @return
     */
    private ElementDescriptor getMatchForAdder(
                                                String propertyName, 
                                                Map elementsByPropertyName) {
        ElementDescriptor matchingDescriptor = null;
        if (propertyName.length() > 0) {
            if ( getLog().isTraceEnabled() ) {
                getLog().trace( "findPluralDescriptor( " + propertyName 
                    + " ):root property name=" + propertyName );
            }
        
            PluralStemmer stemmer = getPluralStemmer();
            matchingDescriptor = stemmer.findPluralDescriptor( propertyName, elementsByPropertyName );
        
            if ( getLog().isTraceEnabled() ) {
                getLog().trace( 
                    "findPluralDescriptor( " + propertyName 
                        + " ):ElementDescriptor=" + matchingDescriptor );
            }
        }
        return matchingDescriptor;
    }
    
    // Implementation methods
    //------------------------------------------------------------------------- 
         

    /**
     * Creates a map where the keys are the property names and the values are the ElementDescriptors
     */
    private Map makeElementDescriptorMap( ElementDescriptor rootDescriptor ) {
        Map result = new HashMap();
        String rootPropertyName = rootDescriptor.getPropertyName();
        if (rootPropertyName != null) {
            result.put(rootPropertyName, rootDescriptor);
        }
        makeElementDescriptorMap( rootDescriptor, result );
        return result;
    }
    
    /**
     * Creates a map where the keys are the property names and the values are the ElementDescriptors
     * 
     * @param rootDescriptor the values of the maps are the children of this 
     * <code>ElementDescriptor</code> index by their property names
     * @param map the map to which the elements will be added
     */
    private void makeElementDescriptorMap( ElementDescriptor rootDescriptor, Map map ) {
        ElementDescriptor[] children = rootDescriptor.getElementDescriptors();
        if ( children != null ) {
            for ( int i = 0, size = children.length; i < size; i++ ) {
                ElementDescriptor child = children[i];                
                String propertyName = child.getPropertyName();                
                if ( propertyName != null ) {
                    map.put( propertyName, child );
                }
                makeElementDescriptorMap( child, map );
            }
        }
    }
    
    /** 
     * A Factory method to lazily create a new strategy 
     * to detect matching singular and plural properties.
     *
     * @return new defualt PluralStemmer implementation
     * @deprecated this method has been moved into IntrospectionConfiguration.
     * Those who need to vary this should subclass that class instead
     */
    protected PluralStemmer createPluralStemmer() {
        return new DefaultPluralStemmer();
    }
    
    /** 
     * A Factory method to lazily create a strategy 
     * used to convert bean type names into element names.
     *
     * @return new default NameMapper implementation
     * @deprecated this method has been moved into IntrospectionConfiguration.
     * Those who need to vary this should subclass that class instead
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
                if ( getLog().isDebugEnabled( )) {
                    getLog().debug( "Parsing Betwixt XML descriptor: " + urlText );
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
                getLog().warn( "Caught exception trying to parse: " + name, e );
            }
        }
        
        if ( getLog().isTraceEnabled() ) {
            getLog().trace( "Could not find betwixt file " + name );
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
        if (getLog().isTraceEnabled()) {
            getLog().trace(elements);
            getLog().trace(attributes);
            getLog().trace(contents);
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
            if (getLog().isTraceEnabled()) {
                getLog().trace(beanProperties.length + " properties to be added");
            }
            for ( int i = 0, size = beanProperties.length; i < size; i++ ) {
                addProperty(beanProperties[i], elements, attributes, contents);
            }
        }
        if (getLog().isTraceEnabled()) {
            getLog().trace("After properties have been added (elements, attributes, contents):");
            getLog().trace(elements);
            getLog().trace(attributes);
            getLog().trace(contents);
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
        if (getLog().isTraceEnabled()) {
            getLog().trace(elements);
            getLog().trace(attributes);
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
     * TODO: this method will probably be removed when primitive types
     * are subsumed into the simple type concept 
     * @param type the Class to test
     * @return true for primitive types 
     */
    public boolean isPrimitiveType(Class type) {
        return XMLIntrospectorHelper.isPrimitiveType(type);
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
