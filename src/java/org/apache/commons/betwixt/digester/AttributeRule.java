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
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLUtils;
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
  * @version $Id: AttributeRule.java,v 1.9 2004/02/28 13:38:32 yoavs Exp $
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
     * @throws SAXException 1. If the attribute tag is not inside an element tag.
     * 2. If the name attribute is not valid XML attribute name.
     */
    public void begin(Attributes attributes) throws SAXException {
        
        AttributeDescriptor descriptor = new AttributeDescriptor();
        String name = attributes.getValue( "name" );

        // check that name is well formed 
        if ( !XMLUtils.isWellFormedXMLName( name ) ) {
            throw new SAXException("'" + name + "' would not be a well formed xml attribute name.");
        }
        
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
