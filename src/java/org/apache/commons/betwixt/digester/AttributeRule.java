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
 */
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.expression.ConstantExpression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** 
  * <p><code>AttributeRule</code> the digester Rule for parsing the 
  * &lt;attribute&gt; elements.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Id: AttributeRule.java,v 1.5 2003/01/07 22:32:57 rdonkin Exp $
  */
public class AttributeRule extends RuleSupport {

    /** Logger */
    private static final Log log = LogFactory.getLog( AttributeRule.class );
    /** This loads all classes created by name. Defaults to this class's classloader */
    private ClassLoader classLoader;
    /** The <code>Class</code> whose .betwixt file is being digested */
    private Class beanClass;
    
    /** Base constructor */
    public AttributeRule() {
        this.classLoader = getClass().getClassLoader();
    }
    
    // Rule interface
    //-------------------------------------------------------------------------    
    
    /**
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
     * @throws SAXException if the attribute tag is not inside an element tag
     */
    public void begin(Attributes attributes) throws SAXException {
        
        AttributeDescriptor descriptor = new AttributeDescriptor();
        String name = attributes.getValue( "name" );
        descriptor.setQualifiedName( name );
        descriptor.setLocalName( name );
        String uri = attributes.getValue( "uri" );
        if ( uri != null ) {
            descriptor.setURI( uri );        
        }
        String propertyName = attributes.getValue( "property" );
        descriptor.setPropertyName( propertyName );
        descriptor.setPropertyType( loadClass( attributes.getValue( "type" ) ) );
        
        if ( propertyName != null && propertyName.length() > 0 ) {
            configureDescriptor(descriptor);
        } else {
            String value = attributes.getValue( "value" );
            if ( value != null ) {
                descriptor.setTextExpression( new ConstantExpression( value ) );
            }
        }

        Object top = digester.peek();
        if ( top instanceof ElementDescriptor ) {
            ElementDescriptor parent = (ElementDescriptor) top;
            parent.addAttributeDescriptor( descriptor );
        } else {
            throw new SAXException( "Invalid use of <attribute>. It should " 
                + "be nested inside an <element> element" );
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
     * Loads a class (using the appropriate classloader)
     *
     * @param name the name of the class to load
     * @return the class instance loaded by the appropriate classloader
     */
    protected Class loadClass( String name ) {
        // XXX: should use a ClassLoader to handle complex class loading situations
        if ( name != null ) {
            try {
                return classLoader.loadClass(name);
            } catch (Exception e) { // SWALLOW
            }
        }
        return null;            
    }
    
    /** 
     * Set the Expression and Updater from a bean property name 
     * @param attributeDescriptor configure this <code>AttributeDescriptor</code> 
     * from the property with a matching name in the bean class
     */
    protected void configureDescriptor(AttributeDescriptor attributeDescriptor) {
        Class beanClass = getBeanClass();
        if ( beanClass != null ) {
            String name = attributeDescriptor.getPropertyName();
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo( beanClass );
                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
                if ( descriptors != null ) {
                    for ( int i = 0, size = descriptors.length; i < size; i++ ) {
                        PropertyDescriptor descriptor = descriptors[i];
                        if ( name.equals( descriptor.getName() ) ) {
                            XMLIntrospectorHelper
                                .configureProperty( attributeDescriptor, descriptor );
                            getProcessedPropertyNameSet().add( name );
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn( "Caught introspection exception", e );
            }
        }
    }    
}
