package org.apache.commons.betwixt;

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/BeanProperty.java,v 1.4.2.4 2004/01/22 11:00:03 rdonkin Exp $
 * $Revision: 1.4.2.4 $
 * $Date: 2004/01/22 11:00:03 $
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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.betwixt.digester.XMLIntrospectorHelper;
import org.apache.commons.betwixt.expression.DynaBeanExpression;
import org.apache.commons.betwixt.expression.Expression;
import org.apache.commons.betwixt.expression.IteratorExpression;
import org.apache.commons.betwixt.expression.MethodExpression;
import org.apache.commons.betwixt.expression.MethodUpdater;
import org.apache.commons.betwixt.expression.Updater;
import org.apache.commons.betwixt.strategy.NameMapper;
import org.apache.commons.logging.Log;

/** 
  * Betwixt-centric view of a bean (or pseudo-bean) property.
  * This object decouples the way that the (possibly pseudo) property introspection
  * is performed from the results of that introspection.
  *
  * @author Robert Burrell Donkin
  * @version $Id: BeanProperty.java,v 1.4.2.4 2004/01/22 11:00:03 rdonkin Exp $
  */
public class BeanProperty {

    /** The bean name for the property (not null) */
    private final String propertyName;
    /** The type of this property (not null) */
    private final Class propertyType;
    /** The Expression used to read values of this property (possibly null) */
    private Expression propertyExpression;
    /** The Updater used to write values of this property (possibly null) */
    private Updater propertyUpdater;

    /**
     * Construct a BeanProperty.
     * @param propertyName not null
     * @param propertyType not null
     * @param propertyExpression the Expression used to read the property, 
     * null if the property is not readable
     * @param propertyUpdater the Updater used to write the property, 
     * null if the property is not writable
     */
    public BeanProperty (
                        String propertyName, 
                        Class propertyType, 
                        Expression propertyExpression, 
                        Updater propertyUpdater) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.propertyExpression = propertyExpression;
        this.propertyUpdater = propertyUpdater;        
    }
    
    /**
     * Constructs a BeanProperty from a <code>PropertyDescriptor</code>.
     * @param descriptor not null
     */
    public BeanProperty(PropertyDescriptor descriptor) {
        this.propertyName = descriptor.getName();
        this.propertyType = descriptor.getPropertyType();
        
        Method readMethod = descriptor.getReadMethod();
        if ( readMethod != null ) {
            this.propertyExpression = new MethodExpression( readMethod );
        }
        
        Method writeMethod = descriptor.getWriteMethod();
        if ( writeMethod != null ) {
            this.propertyUpdater = new MethodUpdater( writeMethod ); 
        }
    }
    
    /**
     * Constructs a BeanProperty from a <code>DynaProperty</code>
     * @param dynaProperty not null
     */
    public BeanProperty(DynaProperty dynaProperty) {
        this.propertyName = dynaProperty.getName();
        this.propertyType = dynaProperty.getType();
        this.propertyExpression = new DynaBeanExpression( propertyName );
        // todo: add updater
    }

    /**
      * Gets the bean name for this property.
      * Betwixt will map this to an xml name.
      * @return the bean name for this property, not null
      */
    public String getPropertyName() {
        return propertyName;
    }

    /** 
      * Gets the type of this property.
      * @return the property type, not null
      */
    public Class getPropertyType() {
        return propertyType;
    }
    
    /**
      * Gets the expression used to read this property.
      * @return the expression to be used to read this property 
      * or null if this property is not readable.
      */
    public Expression getPropertyExpression() {
        return propertyExpression;
    }
    
    /**
      * Gets the updater used to write to this properyty.
      * @return the Updater to the used to write to this property
      * or null if this property is not writable.
      */ 
    public Updater getPropertyUpdater() {
        return propertyUpdater;
    }
    
    /** 
     * Create a XML descriptor from a bean one. 
     * Go through and work out whether it's a loop property, a primitive or a standard.
     * The class property is ignored.
     *
     * @param beanProperty the BeanProperty specifying the property
     * @return a correctly configured <code>NodeDescriptor</code> for the property
     */
    public Descriptor createXMLDescriptor( IntrospectionConfiguration configuration ) {
        Log log = configuration.getIntrospectionLog();
        if (log.isTraceEnabled()) {
            log.trace("Creating descriptor for property: name="
                + getPropertyName() + " type=" + getPropertyType());
        }
        
        NodeDescriptor descriptor = null;
        Expression propertyExpression = getPropertyExpression();
        Updater propertyUpdater = getPropertyUpdater();
        
        if ( propertyExpression == null ) {
            if (log.isTraceEnabled()) {
                log.trace( "No read method for property: name="
                    + getPropertyName() + " type=" + getPropertyType());
            }
            return null;
        }
        
        if ( log.isTraceEnabled() ) {
            log.trace( "Property expression=" + propertyExpression );
        }
        
        // choose response from property type
        
        // XXX: ignore class property ??
        if ( Class.class.equals( getPropertyType() ) && "class".equals( getPropertyName() ) ) {
            log.trace( "Ignoring class property" );
            return null;
            
        }
        
        //TODO this big conditional should be replaced with subclasses based
        // on the type
        
        //TODO replace with simple type support
        if ( XMLIntrospectorHelper.isPrimitiveType( getPropertyType() ) ) {
            descriptor =
                createDescriptorForPrimitive(
                    configuration,
                    propertyExpression,
                    propertyUpdater);
            
        } else if ( XMLIntrospectorHelper.isLoopType( getPropertyType() ) ) {
            
            if (log.isTraceEnabled()) {
                log.trace("Loop type: " + getPropertyName());
                log.trace("Wrap in collections? " + configuration.isWrapCollectionsInElement());
            }
            
            if ( Map.class.isAssignableFrom( getPropertyType() )) {
                descriptor = createDescriptorForMap( configuration, propertyExpression );
            } else {
            
                descriptor 
                    = createDescriptorForCollective( configuration, propertyExpression );
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace( "Standard property: " + getPropertyName());
            }
            descriptor =
                createDescriptorForStandard(
                    propertyExpression,
                    propertyUpdater,
                    configuration);
        }
        
        
       
        if (log.isTraceEnabled()) {
            log.trace( "Created descriptor:" );
            log.trace( descriptor );
        }
        return descriptor;
    }

    /**
     * Configures descriptor (in the standard way).
     * This sets the common properties.
     * 
     * @param propertyName the name of the property mapped to the Descriptor, not null
     * @param propertyType the type of the property mapped to the Descriptor, not null
     * @param descriptor Descriptor to map, not null
     * @param configuration IntrospectionConfiguration, not null
     */
    private void configureDescriptor(
        NodeDescriptor descriptor,
        IntrospectionConfiguration configuration) {
        NameMapper nameMapper = configuration.getElementNameMapper();
        if (descriptor instanceof AttributeDescriptor) {
            // we want to use the attributemapper only when it is an attribute.. 
            nameMapper = configuration.getAttributeNameMapper();
        
        }           
        descriptor.setLocalName( nameMapper.mapTypeToElementName( propertyName ));
        descriptor.setPropertyName( getPropertyName() );
        descriptor.setPropertyType( getPropertyType() );
    }
    
    /**
     * Creates an <code>ElementDescriptor</code> for a standard property
     * @param propertyExpression
     * @param propertyUpdater
     * @return
     */
    private ElementDescriptor createDescriptorForStandard(
        Expression propertyExpression,
        Updater propertyUpdater, 
        IntrospectionConfiguration configuration) {
            
        ElementDescriptor result;

        ElementDescriptor elementDescriptor = new ElementDescriptor();
        elementDescriptor.setContextExpression( propertyExpression );
        if ( propertyUpdater != null ) {
            elementDescriptor.setUpdater( propertyUpdater );
        }
        
        elementDescriptor.setHollow(true);
        
        result = elementDescriptor;
        
        configureDescriptor(result, configuration);
        return result;
    }

    /**
     * Creates an ElementDescriptor for an <code>Map</code> type property
     * @param configuration
     * @param propertyExpression
     * @return
     */
    private ElementDescriptor createDescriptorForMap(
        IntrospectionConfiguration configuration,
        Expression propertyExpression) {
            
        ElementDescriptor result;
        
        ElementDescriptor loopDescriptor = new ElementDescriptor();
        loopDescriptor.setContextExpression(
            new IteratorExpression( propertyExpression )
        );
        loopDescriptor.setWrapCollectionsInElement( configuration.isWrapCollectionsInElement() );

        loopDescriptor.setQualifiedName( "entry" );
        // add elements for reading
        loopDescriptor.addElementDescriptor( new ElementDescriptor( "key" ) );
        loopDescriptor.addElementDescriptor( new ElementDescriptor( "value" ) );
        
        
        ElementDescriptor elementDescriptor = new ElementDescriptor();
        elementDescriptor.setWrapCollectionsInElement( configuration.isWrapCollectionsInElement() );
        elementDescriptor.setElementDescriptors( new ElementDescriptor[] { loopDescriptor } );
        
        result = elementDescriptor;
        
        configureDescriptor(result, configuration);
        return result;
    }

    /**
     * Creates an <code>ElementDescriptor</code> for a collective type property
     * @param configuration
     * @param propertyExpression
     * @return
     */
    private ElementDescriptor createDescriptorForCollective(
        IntrospectionConfiguration configuration,
        Expression propertyExpression) {
            
        ElementDescriptor result;
        
        ElementDescriptor loopDescriptor = new ElementDescriptor();
        loopDescriptor.setContextExpression(
            new IteratorExpression( propertyExpression )
        );
        loopDescriptor.setWrapCollectionsInElement( configuration.isWrapCollectionsInElement() );
        
        ElementDescriptor elementDescriptor = new ElementDescriptor();
        elementDescriptor.setWrapCollectionsInElement( configuration.isWrapCollectionsInElement() );
        elementDescriptor.setElementDescriptors( new ElementDescriptor[] { loopDescriptor } );
        
        result = elementDescriptor;
        configureDescriptor(result, configuration);
        return result;
    }

    /**
     * Creates a NodeDescriptor for a primitive type node
     * @param configuration
     * @param name
     * @param log
     * @param propertyExpression
     * @param propertyUpdater
     * @return
     */
    private NodeDescriptor createDescriptorForPrimitive(
        IntrospectionConfiguration configuration,
        Expression propertyExpression,
        Updater propertyUpdater) {
        Log log = configuration.getIntrospectionLog();
        NodeDescriptor descriptor;
        if (log.isTraceEnabled()) {
            log.trace( "Primitive type: " + getPropertyName());
        }
        if ( configuration.isAttributesForPrimitives() ) {
            if (log.isTraceEnabled()) {
                log.trace( "Adding property as attribute: " + getPropertyName() );
            }
            descriptor = new AttributeDescriptor();
        } else {
            if (log.isTraceEnabled()) {
                log.trace( "Adding property as element: " + getPropertyName() );
            }
            descriptor = new ElementDescriptor();
        }
        descriptor.setTextExpression( propertyExpression );
        if ( propertyUpdater != null ) {
            descriptor.setUpdater( propertyUpdater );
        }
        configureDescriptor(descriptor, configuration);
        return descriptor;
    }
}
