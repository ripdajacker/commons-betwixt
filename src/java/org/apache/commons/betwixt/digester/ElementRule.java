package org.apache.commons.betwixt.digester;
/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/digester/ElementRule.java,v 1.13.2.6 2004/02/08 12:11:17 rdonkin Exp $
 * $Revision: 1.13.2.6 $
 * $Date: 2004/02/08 12:11:17 $
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
  * @version $Id: ElementRule.java,v 1.13.2.6 2004/02/08 12:11:17 rdonkin Exp $
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
