package org.apache.commons.betwixt.digester;
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
import java.beans.PropertyDescriptor;

import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLUtils;
import org.apache.commons.betwixt.expression.ConstantExpression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** 
  * <p><code>ElementRule</code> the digester Rule for parsing 
  * the &lt;element&gt; elements.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Id: ElementRule.java,v 1.14.2.1 2004/06/19 16:24:10 rdonkin Exp $
  */
public class ElementRule extends MappedPropertyRule {

    /** Logger */
    private static Log log = LogFactory.getLog( ElementRule.class );
    /** 
     * Sets the log for this class 
     * 
     * @param newLog the new Log implementation for this class to use
     * @since 0.5
     */
    public static final void setLog(Log newLog) {
        log = newLog;
    }

    /** Class for which the .bewixt file is being digested */
    private Class beanClass;
    /** Base constructor */
    public ElementRule() {}
    
    // Rule interface
    //-------------------------------------------------------------------------    
    
    /**
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
     * @throws SAXException 1. If this tag's parent is not either an info or element tag.
     * 2. If the name attribute is not valid XML element name.
     * 3. If the name attribute is not present 
     * 4. If the class attribute is not a loadable (fully qualified) class name
     */
    public void begin(Attributes attributes) throws SAXException {
        String name = attributes.getValue( "name" );
        
        // check that the name attribute is present 
        if ( name == null || name.trim().equals( "" ) ) {
            throw new SAXException("Name attribute is required.");
        }
        
        // check that name is well formed 
        if ( !XMLUtils.isWellFormedXMLName( name ) ) {
            throw new SAXException("'" + name + "' would not be a well formed xml element name.");
        }
        
        ElementDescriptor descriptor = new ElementDescriptor();
        descriptor.setQualifiedName( name );
        descriptor.setLocalName( name );
        String uri = attributes.getValue( "uri" );
        if ( uri != null ) {
            descriptor.setURI( uri );        
        }
        
        String propertyName = attributes.getValue( "property" );
        descriptor.setPropertyName( propertyName );
        
        String propertyType = attributes.getValue( "type" );
        
        if (log.isTraceEnabled()) {
            log.trace(
                    "(BEGIN) name=" + name + " uri=" + uri 
                    + " property=" + propertyName + " type=" + propertyType);
        }
        
        // set the property type using reflection
        descriptor.setPropertyType( 
            getPropertyType( propertyType, beanClass, propertyName ) 
        );
        
        String implementationClass = attributes.getValue( "class" );
        if ( log.isTraceEnabled() ) {
            log.trace("'class' attribute=" + implementationClass);
        }
        if ( implementationClass != null ) {
            try {
                
                Class clazz = Class.forName(implementationClass);
                descriptor.setImplementationClass( clazz );
                
            } catch (Exception e)  {
                if ( log.isDebugEnabled() ) {
                    log.debug("Cannot load class named: " + implementationClass, e);
                }
                throw new SAXException("Cannot load class named: " + implementationClass);
            }
        }
        
        if ( propertyName != null && propertyName.length() > 0 ) {
            configureDescriptor(descriptor, attributes.getValue( "updater" ));
            
        } else {
            String value = attributes.getValue( "value" );
            if ( value != null ) {
                descriptor.setTextExpression( new ConstantExpression( value ) );
            }
        }
        
        Object top = digester.peek();
        if ( top instanceof XMLBeanInfo ) {
            XMLBeanInfo beanInfo = (XMLBeanInfo) top;
            beanInfo.setElementDescriptor( descriptor );
            beanClass = beanInfo.getBeanClass();
            
        } else if ( top instanceof ElementDescriptor ) {
            ElementDescriptor parent = (ElementDescriptor) top;
            parent.addElementDescriptor( descriptor );
            
        } else {
            throw new SAXException( "Invalid use of <element>. It should " 
                + "be nested inside <info> or other <element> nodes" );
        }

        digester.push(descriptor);        
    }


    /**
     * Process the end of this element.
     */
    public void end() {
        Object top = digester.pop();
    }

    
    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /** 
     * Sets the Expression and Updater from a bean property name 
     * Uses the default updater (from the standard java bean property).
     *
     * @param elementDescriptor configure this <code>ElementDescriptor</code>
     * since 0.5
     */
    protected void configureDescriptor(ElementDescriptor elementDescriptor) {
        configureDescriptor( elementDescriptor, null );
    }       
    
    /** 
     * Sets the Expression and Updater from a bean property name 
     * Allows a custom updater to be passed in.
     *
     * @param elementDescriptor configure this <code>ElementDescriptor</code>
     * @param updateMethodName custom update method. If null, then use standard
     * since 0.5
     */
    protected void configureDescriptor(
                                        ElementDescriptor elementDescriptor,
                                        String updateMethodName) {
        Class beanClass = getBeanClass();
        if ( beanClass != null ) {
            String name = elementDescriptor.getPropertyName();
            PropertyDescriptor descriptor = 
                getPropertyDescriptor( beanClass, name );
            
            if ( descriptor != null ) { 
                XMLIntrospectorHelper
                    .configureProperty( 
                                        elementDescriptor, 
                                        descriptor, 
                                        updateMethodName, 
                                        beanClass );
                
                getProcessedPropertyNameSet().add( name );
            }
        }
    }  
}
