package org.apache.commons.betwixt.digester;

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
 * 
 */

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
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
  * @version $Id: ElementRule.java,v 1.3 2002/12/30 18:18:37 mvdb Exp $
  */
public class ElementRule extends RuleSupport {

    /** Logger */
    private static final Log log = LogFactory.getLog( ElementRule.class );
    
    private ClassLoader classLoader;
    
    private Class beanClass;
    
    public ElementRule() {
        this.classLoader = getClass().getClassLoader();
    }
    
    // Rule interface
    //-------------------------------------------------------------------------    
    
    /**
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
     */
    public void begin(Attributes attributes) throws Exception {
        String name = attributes.getValue( "name" );
        
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
        
        
        if ( propertyName != null && propertyName.length() > 0 ) {
            configureDescriptor(descriptor);
        }
        else {
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
        }
        else if ( top instanceof ElementDescriptor ) {
            ElementDescriptor parent = (ElementDescriptor) top;
            parent.addElementDescriptor( descriptor );
        }
        else {
            throw new SAXException( "Invalid use of <element>. It should " + 
                "be nested inside <info> or other <element> nodes" );
        }

        digester.push(descriptor);        
    }


    /**
     * Process the end of this element.
     */
    public void end() throws Exception {
        Object top = digester.pop();
    }

    
    // Implementation methods
    //-------------------------------------------------------------------------    
    protected Class getPropertyType( String propertyClassName, 
                                     Class beanClass, String propertyName ) {
        // XXX: should use a ClassLoader to handle 
        //      complex class loading situations
        if ( propertyClassName != null ) {
            try {
                Class answer = classLoader.loadClass(propertyClassName);
                if (answer != null) {
                    if (log.isTraceEnabled()) {
                        log.trace("Used specified type " + answer);
                    }
                    return answer;
                }
            }
            catch (Exception e) {
                log.warn("Cannot load specified type", e);
            }
        }
        
        PropertyDescriptor descriptor = 
            getPropertyDescriptor( beanClass, propertyName );        
        if ( descriptor != null ) { 
            return descriptor.getPropertyType();
        }
        
        if (log.isTraceEnabled()) {
            log.trace("Cannot find property type.");
            log.trace("  className=" + propertyClassName + " base=" + beanClass + " name=" + propertyName);
        }
        return null;            
    }
    
    /** Set the Expression and Updater from a bean property name */
    protected void configureDescriptor(ElementDescriptor elementDescriptor) {
        Class beanClass = getBeanClass();
        if ( beanClass != null ) {
            String name = elementDescriptor.getPropertyName();
            PropertyDescriptor descriptor = 
                getPropertyDescriptor( beanClass, name );
            if ( descriptor != null ) { 
                XMLIntrospectorHelper
                    .configureProperty( elementDescriptor, descriptor );
                getProcessedPropertyNameSet().add( name );
            }
        }
    }    

    /** 
     * Returns the property descriptor for the class and property name.
     * Note that some caching could be used to improve performance of 
     * this method. Or this method could be added to PropertyUtils.
     */
    protected PropertyDescriptor getPropertyDescriptor( Class beanClass, 
                                                        String propertyName ) {
        if ( beanClass != null && propertyName != null ) {
            if (log.isTraceEnabled()) {
                log.trace("Searching for property " + propertyName + " on " + beanClass);
            }
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo( beanClass );
                PropertyDescriptor[] descriptors = 
                    beanInfo.getPropertyDescriptors();
                if ( descriptors != null ) {
                    for ( int i = 0, size = descriptors.length; i < size; i++ ) {
                        PropertyDescriptor descriptor = descriptors[i];
                        if ( propertyName.equals( descriptor.getName() ) ) {
                            log.trace("Found matching method.");
                            return descriptor;
                        }
                    }
                }
                log.trace("No match found.");
                return null;
            }
            catch (Exception e) {
                log.warn( "Caught introspection exception", e );
            }
        }
        return null;
    }
    
}
