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
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLUtils;
import org.apache.commons.betwixt.expression.ConstantExpression;
import org.apache.commons.betwixt.expression.IteratorExpression;
import org.apache.commons.betwixt.expression.MethodExpression;
import org.apache.commons.betwixt.expression.MethodUpdater;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** 
  * <p><code>ElementRule</code> the digester Rule for parsing 
  * the &lt;element&gt; elements.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Id: ElementRule.java,v 1.15 2004/06/13 21:32:45 rdonkin Exp $
  */
public class ElementRule extends MappedPropertyRule {

    /** Logger */
    private static Log log = LogFactory.getLog( ElementRule.class );
    /** 
     * Sets the log for this class 
     * 
     * @param newLog the new Log implementation for this class to use
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
    public void begin(String name, String namespace, Attributes attributes) throws SAXException {
        String nameAttributeValue = attributes.getValue( "name" );
        
        // check that the name attribute is present 
        if ( nameAttributeValue == null || nameAttributeValue.trim().equals( "" ) ) {
            throw new SAXException("Name attribute is required.");
        }
        
        // check that name is well formed 
        if ( !XMLUtils.isWellFormedXMLName( nameAttributeValue ) ) {
            throw new SAXException("'" + nameAttributeValue + "' would not be a well formed xml element name.");
        }
        
        ElementDescriptor descriptor = new ElementDescriptor();
        descriptor.setLocalName( nameAttributeValue );
        String uri = attributes.getValue( "uri" );
        String qName = nameAttributeValue;
        if ( uri != null ) {
            descriptor.setURI( uri );  
            String prefix = getXMLIntrospector().getConfiguration().getPrefixMapper().getPrefix(uri);
            qName = prefix + ":" + nameAttributeValue;
        }
        descriptor.setQualifiedName( qName );
        
        String propertyName = attributes.getValue( "property" );
        descriptor.setPropertyName( propertyName );
        
        String propertyType = attributes.getValue( "type" );
        
        if (log.isTraceEnabled()) {
            log.trace(
                    "(BEGIN) name=" + nameAttributeValue + " uri=" + uri 
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
            descriptor.setPropertyType( beanClass );
            
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
    public void end(String name, String namespace) {
        Object top = digester.pop();
    }

    
    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /** 
     * Sets the Expression and Updater from a bean property name 
     * Uses the default updater (from the standard java bean property).
     *
     * @param elementDescriptor configure this <code>ElementDescriptor</code>
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
                configureProperty( 
                                        elementDescriptor, 
                                        descriptor, 
                                        updateMethodName, 
                                        beanClass );
                
                getProcessedPropertyNameSet().add( name );
            }
        }
    }  
    
                                    
    /**
     * Configure an <code>ElementDescriptor</code> from a <code>PropertyDescriptor</code>.
     * A custom update method may be set.
     *
     * @param elementDescriptor configure this <code>ElementDescriptor</code>
     * @param propertyDescriptor configure from this <code>PropertyDescriptor</code>
     * @param updateMethodName the name of the custom updater method to user. 
     * If null, then then 
     * @param beanClass the <code>Class</code> from which the update method should be found.
     * This may be null only when <code>updateMethodName</code> is also null.
     */
    private void configureProperty( 
                                    ElementDescriptor elementDescriptor, 
                                    PropertyDescriptor propertyDescriptor,
                                    String updateMethodName,
                                    Class beanClass ) {
        
        Class type = propertyDescriptor.getPropertyType();
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        
        String existingLocalName = elementDescriptor.getLocalName();
        if (existingLocalName == null || "".equals(existingLocalName)) {
            elementDescriptor.setLocalName( propertyDescriptor.getName() );
        }
        elementDescriptor.setPropertyType( type );        
        
        // TODO: associate more bean information with the descriptor?
        //nodeDescriptor.setDisplayName( propertyDescriptor.getDisplayName() );
        //nodeDescriptor.setShortDescription( propertyDescriptor.getShortDescription() );
        
        if ( readMethod == null ) {
            log.trace( "No read method" );
            return;
        }
        
        if ( log.isTraceEnabled() ) {
            log.trace( "Read method=" + readMethod.getName() );
        }
        
        // choose response from property type
        
        // TODO: ignore class property ??
        if ( Class.class.equals( type ) && "class".equals( propertyDescriptor.getName() ) ) {
            log.trace( "Ignoring class property" );
            return;
        }
        if ( getXMLIntrospector().isPrimitiveType( type ) ) {
            elementDescriptor.setTextExpression( new MethodExpression( readMethod ) );
            
        } else if ( getXMLIntrospector().isLoopType( type ) ) {
            log.trace("Loop type ??");
            
            // don't wrap this in an extra element as its specified in the 
            // XML descriptor so no need.            
            elementDescriptor.setContextExpression(
                new IteratorExpression( new MethodExpression( readMethod ) )
            );
            elementDescriptor.setHollow(true);

            writeMethod = null;
            
            if (Map.class.isAssignableFrom(type)) {
                elementDescriptor.setLocalName( "entry" );
                // add elements for reading
                ElementDescriptor keyDescriptor = new ElementDescriptor( "key" );
                keyDescriptor.setHollow( true );
                elementDescriptor.addElementDescriptor( keyDescriptor );
            
                ElementDescriptor valueDescriptor = new ElementDescriptor( "value" );
                valueDescriptor.setHollow( true );
                elementDescriptor.addElementDescriptor( valueDescriptor );
            }
            
        } else {
            log.trace( "Standard property" );
            elementDescriptor.setHollow(true);
            elementDescriptor.setContextExpression( new MethodExpression( readMethod ) );
        }
    
        // see if we have a custom method update name
        if (updateMethodName == null) {
            // set standard write method
            if ( writeMethod != null ) {
                elementDescriptor.setUpdater( new MethodUpdater( writeMethod ) );
            }
            
        } else {
            // see if we can find and set the custom method
            if ( log.isTraceEnabled() ) {
                log.trace( "Finding custom method: " );
                log.trace( "  on:" + beanClass );
                log.trace( "  name:" + updateMethodName );
            }
            
            Method updateMethod = null;
            Method[] methods = beanClass.getMethods();
            for ( int i = 0, size = methods.length; i < size; i++ ) {
                Method method = methods[i];
                if ( updateMethodName.equals( method.getName() ) ) {
                    // we have a matching name
                    // check paramters are correct
                    if (methods[i].getParameterTypes().length == 1) {
                        // we'll use first match
                        updateMethod = methods[i];
                        if ( log.isTraceEnabled() ) {
                            log.trace("Matched method:" + updateMethod);
                        } 
                        // done since we're using the first match
                        break;
                    }
                }
            }
            
            if (updateMethod == null) {
                if ( log.isInfoEnabled() ) {
                    
                    log.info("No method with name '" + updateMethodName + "' found for update");
                }
            } else {
    
                elementDescriptor.setUpdater( new MethodUpdater( updateMethod ) );
                elementDescriptor.setSingularPropertyType( updateMethod.getParameterTypes()[0] );
                if ( log.isTraceEnabled() ) {
                    log.trace( "Set custom updater on " + elementDescriptor);
                }
            }
        }
    }
}
