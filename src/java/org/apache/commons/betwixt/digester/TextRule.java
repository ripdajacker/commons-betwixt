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

import java.lang.reflect.Method;

import org.apache.commons.betwixt.expression.MethodExpression;
import org.apache.commons.betwixt.expression.ConstantExpression;
import org.apache.commons.betwixt.TextDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** 
  * <p>Rule for parsing &lt;text&gt; elements.
  * These allow mixed content text to be specified.
  * A mixed content element example:
  * <pre>
  * 	&lt;foo&gt;text&lt;bar/&gt;&lt;/foo&gt;
  * </pre>
  * </p>
  *
  * @author Robert Burrell Donkin
  * @version $Id: TextRule.java,v 1.1 2003/03/19 22:59:01 rdonkin Exp $
  */
public class TextRule extends MappedPropertyRule {

    /** Logger */
    private static final Log log = LogFactory.getLog( TextRule.class );
    /** Class for which the .bewixt file is being digested */
    private Class beanClass;
    /** Base constructor */
    public TextRule() {}
    
    // Rule interface
    //-------------------------------------------------------------------------    
    
    /**
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
     * @throws SAXException 1. If this tag's parent is not an element tag.
     * 2. If this tag has a value attribute together with either a property
     * or type attribute.
     */
    public void begin(Attributes attributes) throws SAXException {
        
        TextDescriptor descriptor = new TextDescriptor();
        
        String value = attributes.getValue( "value" );
        String propertyName = attributes.getValue( "property" );
        String propertyType = attributes.getValue( "type" );
        
        if ( value != null) {
            if ( propertyName != null || propertyType != null ) {
                // not allowed
                throw new SAXException("You cannot specify attribute 'value' together with either " +
                    " the 'property' or 'type' attributes");                
            }
            // fixed value text
            descriptor.setTextExpression( new ConstantExpression( value ) );
            
        } else {
            // property based text
            descriptor.setPropertyName( propertyName );
            
            
            // set the property type using reflection
            descriptor.setPropertyType( 
                getPropertyType( propertyType, beanClass, propertyName ) 
            );
            
            Class beanClass = getBeanClass();
            if ( beanClass != null ) {
                String name = descriptor.getPropertyName();
                PropertyDescriptor propertyDescriptor = 
                    getPropertyDescriptor( beanClass, name );
                if ( propertyDescriptor != null ) { 
                        Method readMethod = propertyDescriptor.getReadMethod();
                        descriptor.setTextExpression( new MethodExpression( readMethod ) );
                        getProcessedPropertyNameSet().add( name );
                }
            }
        }
        
        Object top = digester.peek();
        if ( top instanceof XMLBeanInfo ) {
            XMLBeanInfo beanInfo = (XMLBeanInfo) top;
            ElementDescriptor elementDescriptor = beanInfo.getElementDescriptor();
            if (elementDescriptor == null) {
                elementDescriptor.addContentDescriptor( descriptor );
            }
            beanClass = beanInfo.getBeanClass();
            
        } else if ( top instanceof ElementDescriptor ) {
            ElementDescriptor parent = (ElementDescriptor) top;
            parent.addContentDescriptor( descriptor );
            
        } else {
            throw new SAXException( "Invalid use of <text>. It should " 
                + "be nested <text> nodes" );
        }
    }
}
