/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/schema/ComplexType.java,v 1.1.2.7 2004/02/08 12:11:17 rdonkin Exp $
 * $Revision: 1.1.2.7 $
 * $Date: 2004/02/08 12:11:17 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
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
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior 
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

package org.apache.commons.betwixt.schema;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;

/**
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.7 $
 */
public abstract class ComplexType {

    protected List elements = new ArrayList();

    protected List attributes = new ArrayList();

    public ComplexType() {}

    public ComplexType(ElementDescriptor elementDescriptor, Schema schema) throws IntrospectionException {
        if (elementDescriptor.isHollow()) {
            // need to introspector for filled descriptor
            Class type = elementDescriptor.getSingularPropertyType();
            if (type == null) {
                type = elementDescriptor.getPropertyType();
            }
            XMLBeanInfo filledBeanInfo = schema.introspect(type);
            elementDescriptor = filledBeanInfo.getElementDescriptor();
        }
        init(elementDescriptor, schema);      
    }

    protected void init(ElementDescriptor elementDescriptor, Schema schema) throws IntrospectionException {
        
        AttributeDescriptor[] attributeDescriptors = elementDescriptor.getAttributeDescriptors();
        for (int i=0,length=attributeDescriptors.length; i<length ; i++) {
            //TODO: need to think about computing schema types from descriptors
            // this will probably depend on the class mapped to
            String uri = attributeDescriptors[i].getURI();
            if (! SchemaTranscriber.W3C_SCHEMA_INSTANCE_URI.equals(uri)) {
                attributes.add(new Attribute(attributeDescriptors[i]));
            }
        }
        
        //TODO: add support for spacing elements
        ElementDescriptor[] elementDescriptors = elementDescriptor.getElementDescriptors();
        for (int i=0,length=elementDescriptors.length; i<length ; i++) {
            if (elementDescriptors[i].isHollow()) {
                elements.add(new ElementReference(elementDescriptors[i], schema));
            } else if (elementDescriptors[i].isSimple()){
                elements.add(new SimpleLocalElement(elementDescriptors[i], schema));
            } else {
                elements.add(new ComplexLocalElement(elementDescriptors[i], schema));
            }
        } 
    }

    /**
    	 * Gets the elements contained by this type
    	 * @return 
    	 */
    public List getElements() {
    	return elements;
    }

    /**
    	 * Adds an element to those contained by this type
    	 * @param element
    	 */
    public void addElement(ElementReference element) {
    	elements.add(element);
    }
    
    /**
          * Adds an element to those contained by this type
          * @param element
          */
     public void addElement(LocalElement element) {
         elements.add(element);
     }


    /**
    	 * Gets the attributes contained by this type.
    	 * @return
    	 */
    public List getAttributes() {
    	return attributes;
    }

    /**
    	 * Adds an attribute to those contained by this type
    	 * @param attribute
    	 */
    public void addAttribute(Attribute attribute) {
    	attributes.add(attribute);
    }

}
