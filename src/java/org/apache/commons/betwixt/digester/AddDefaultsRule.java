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
package org.apache.commons.betwixt.digester;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Set;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.Descriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.NodeDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** <p><code>AddDefaultsRule</code> appends all the default properties
  * to the current element.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.11 $
  */
public class AddDefaultsRule extends RuleSupport {

    /** Logger */
    private static final Log log = LogFactory.getLog( AddDefaultsRule.class );
    
    /** Base constructor */
    public AddDefaultsRule() {
    }
    
    // Rule interface
    //-------------------------------------------------------------------------    
    
    /**
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
     * @throws Exception generally this will indicate an unrecoverable error 
     */
    public void begin(Attributes attributes) throws Exception {
        Class beanClass = getBeanClass();
        Set procesedProperties = getProcessedPropertyNameSet();
        if ( beanClass != null ) {
            try {
                boolean attributesForPrimitives = getXMLInfoDigester().isAttributesForPrimitives();
                BeanInfo beanInfo = Introspector.getBeanInfo( beanClass );
                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
                if ( descriptors != null ) {
                    for ( int i = 0, size = descriptors.length; i < size; i++ ) {
                        PropertyDescriptor descriptor = descriptors[i];
                        // have we already created a property for this
                        String name = descriptor.getName();
                        if ( procesedProperties.contains( name ) ) {
                            continue;
                        }
                        Descriptor nodeDescriptor = getXMLIntrospector().createDescriptor(
                                    descriptor, attributesForPrimitives);
                        if ( nodeDescriptor != null ) {
                            addDescriptor( nodeDescriptor );
                        }
                    }
                }
            } catch (Exception e) {
                log.info( "Caught introspection exception", e );
            }
        }
        
        // default any addProperty() methods
        XMLIntrospectorHelper.defaultAddMethods( 
                                            getXMLIntrospector(), 
                                            getRootElementDescriptor(), 
                                            beanClass );
    }


    // Implementation methods
    //-------------------------------------------------------------------------    
   
    /**
    * Add a desciptor to the top object on the Digester stack.
    * 
    * @param nodeDescriptor add this <code>NodeDescriptor</code>. Must not be null.
    * @throws SAXException if the parent for the addDefaults element is not a <element> 
    * or if the top object on the stack is not a <code>XMLBeanInfo</code> or a 
    * <code>ElementDescriptor</code>
    * @deprecated replaced {@link #addDescriptor( Descriptor )} 
    */
    protected void addDescriptor( NodeDescriptor nodeDescriptor ) throws SAXException {
        addDescriptor( (Descriptor) nodeDescriptor );
    }
      
    /**
    * Add a desciptor to the top object on the Digester stack.
    * 
    * @param nodeDescriptor add this <code>NodeDescriptor</code>. Must not be null.
    * @throws SAXException if the parent for the addDefaults element is not a <element> 
    * or if the top object on the stack is not a <code>XMLBeanInfo</code> or a 
    * <code>ElementDescriptor</code>
    */
    protected void addDescriptor( Descriptor nodeDescriptor ) throws SAXException {
        Object top = digester.peek();
        if ( top instanceof XMLBeanInfo ) {
            log.warn( "It is advisable to put an <addDefaults/> element inside an <element> tag" );
            
            XMLBeanInfo beanInfo = (XMLBeanInfo) top;
            // if there is already a root element descriptor then use it
            // otherwise use this descriptor
            if ( nodeDescriptor instanceof ElementDescriptor ) {
                ElementDescriptor elementDescriptor = (ElementDescriptor) nodeDescriptor;
                ElementDescriptor root = beanInfo.getElementDescriptor() ;
                if ( root == null ) {
                    beanInfo.setElementDescriptor( elementDescriptor );
                } else {
                    root.addElementDescriptor( elementDescriptor );
                }
            } else { 
                throw new SAXException( 
                    "the <addDefaults> element should be within an <element> tag" );
            }
        } else if ( top instanceof ElementDescriptor ) {
            ElementDescriptor parent = (ElementDescriptor) top;
            if ( nodeDescriptor instanceof ElementDescriptor ) {
                parent.addElementDescriptor( (ElementDescriptor) nodeDescriptor );
            } else {
                parent.addAttributeDescriptor( (AttributeDescriptor) nodeDescriptor );
            }
        } else {
            throw new SAXException( 
                "Invalid use of <addDefaults>. It should be nested inside <element> element" );
        }            
    }     

    /**
     * Gets an <code>ElementDescriptor</code> for the top on digester's stack.
     *
     * @return the top object or the element description if the top object 
     * is an <code>ElementDescriptor</code> or a <code>XMLBeanInfo</code> class (respectively)
     * Otherwise null.
     */
    protected ElementDescriptor getRootElementDescriptor() {
        Object top = digester.peek();
        if ( top instanceof XMLBeanInfo ) {
            XMLBeanInfo beanInfo = (XMLBeanInfo) top;
            return beanInfo.getElementDescriptor();
            
        } else if ( top instanceof ElementDescriptor ) {
            ElementDescriptor parent = (ElementDescriptor) top;
            // XXX: could maybe walk up the parent hierarchy?
            return parent;
        }
        return null;
    }
}
