/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/digester/AddDefaultsRule.java,v 1.2 2002/07/08 16:51:38 jvanzyl Exp $
 * $Revision: 1.2 $
 * $Date: 2002/07/08 16:51:38 $
 *
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
 * $Id: AddDefaultsRule.java,v 1.2 2002/07/08 16:51:38 jvanzyl Exp $
 */
package org.apache.commons.betwixt.digester;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Set;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.NodeDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.expression.Updater;

import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Digester;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/** <p><code>AddDefaultsRule</code> appends all the default properties
  * to the current element.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.2 $
  */
public class AddDefaultsRule extends RuleSupport {

    /** Logger */
    private static final Log log = LogFactory.getLog( AddDefaultsRule.class );
    
    
    public AddDefaultsRule() {
    }
    
    // Rule interface
    //-------------------------------------------------------------------------    
    
    /**
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
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
                        NodeDescriptor nodeDescriptor = XMLIntrospectorHelper.createDescriptor( 
                            descriptor, attributesForPrimitives, getXMLIntrospector()
                        );
                        if ( nodeDescriptor != null ) {
                            addDescriptor( nodeDescriptor );
                        }
                    }
                }
            }
            catch (Exception e) {
                log.info( "Caught introspection exception", e );
            }
        }
        
        // default any addProperty() methods
        XMLIntrospectorHelper.defaultAddMethods( getXMLIntrospector(), getRootElementDescriptor(), beanClass );
    }


    // Implementation methods
    //-------------------------------------------------------------------------    
    protected void addDescriptor( NodeDescriptor nodeDescriptor ) throws SAXException {
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
                }
                else {
                    root.addElementDescriptor( elementDescriptor );
                }
            }
            else { 
                throw new SAXException( "the <addDefaults> element should be within an <element> tag" );
            }
        }
        else if ( top instanceof ElementDescriptor ) {
            ElementDescriptor parent = (ElementDescriptor) top;
            if ( nodeDescriptor instanceof ElementDescriptor ) {
                parent.addElementDescriptor( (ElementDescriptor) nodeDescriptor );
            }
            else {
                parent.addAttributeDescriptor( (AttributeDescriptor) nodeDescriptor );
            }
        }
        else {
            throw new SAXException( "Invalid use of <addDefaults>. It should be nested inside <element> element" );
        }            
    }     
    
    protected ElementDescriptor getRootElementDescriptor() {
        Object top = digester.peek();
        if ( top instanceof XMLBeanInfo ) {
            XMLBeanInfo beanInfo = (XMLBeanInfo) top;
            return beanInfo.getElementDescriptor();
        }
        else if ( top instanceof ElementDescriptor ) {
            ElementDescriptor parent = (ElementDescriptor) top;
            // XXX: could maybe walk up the parent hierarchy?
            return parent;
        }
        return null;
    }
}
