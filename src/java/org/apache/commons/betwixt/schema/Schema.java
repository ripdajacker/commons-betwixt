/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/schema/Schema.java,v 1.1.2.4 2004/02/04 22:57:41 rdonkin Exp $
 * $Revision: 1.1.2.4 $
 * $Date: 2004/02/04 22:57:41 $
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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.collections.CollectionUtils;

/**
 * Model for top level element in an XML Schema
 * 
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.4 $
 */
public class Schema {
	
	private List elements = new ArrayList();
	private List complexTypes = new ArrayList();
	private List simpleTypes = new ArrayList(); 
    
    private XMLIntrospector introspector;
	
    public Schema() {
        this(new XMLIntrospector());
    }
    
    public Schema(XMLIntrospector introspector) {
        this.introspector = introspector;
    }
    
    /**
     * Introspects the given type giving an <code>XMLBeanInfo</code>.
     * @param type Class to introspect, not null
     * @return <code>XMLBeanInfo</code>, not null
     * @throws IntrospectionException
     */
    public XMLBeanInfo introspect(Class type) throws IntrospectionException {
         return introspector.introspect(type);
    }
    
    /**
     * Gets the complex types defined
     * @return list of <code>ComplexType</code>'s not null
     */
    public List getComplexTypes() {
        return complexTypes;
    }


	/**
	 * Adds a new complex type to those defined
	 * @param complexType not null
	 */
	public void addComplexType(ComplexType complexType) {
		complexTypes.add(complexType);
	}
	

    /**
     * Gets the elements definied
     * @return list of <code>Element</code>s not null
     */
    public List getElements() {
        return elements;
    }

	/**
	 * Adds a new element to those defined.
	 * @param element not null
	 */
	public void addElement(Element element) {
		elements.add(element);
	}

    /**
     * Gets the simple types defined.
     * @return list of <code>SimpleType</code>s not null
     */
    public List getSimpleTypes() {
        return simpleTypes;
    }

	/**
	 * Adds a new simple type to those defined.
	 * @param simpleType
	 */
	public void addSimpleType(SimpleType simpleType) {
		simpleTypes.add(simpleType);
	}


    /**
     * Adds global (top level) element and type declarations matching the given descriptor.
     * @param elementDescriptor ElementDescriptor not null
     */
    public void addGlobalElementType(ElementDescriptor elementDescriptor) throws IntrospectionException {
        // need to create a global element declaration and a complex type 
        // use the fully qualified class name as the type name
        Element element = new Element(
                            elementDescriptor.getLocalName(), 
                            elementDescriptor.getPropertyType().getName());
        addElement(element);
        
        ComplexType type = new ComplexType(elementDescriptor, this);
        addComplexType(type);
    }	
	
    public boolean equals(Object obj) {
    	boolean result = false;
        if (obj instanceof Schema) {
        	Schema schema = (Schema) obj;
        	result =
        	CollectionUtils.isEqualCollection(elements, schema.elements) &&
			CollectionUtils.isEqualCollection(complexTypes, schema.complexTypes) &&
			CollectionUtils.isEqualCollection(simpleTypes, schema.simpleTypes);
        }
        return result;
    }

    public int hashCode() {
        return 0;
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version='1.0'?>");
        buffer.append("<xsd:schema xmlns:xsd='http://www.w3c.org/2001/XMLSchema'>");
        
        for (Iterator it=simpleTypes.iterator(); it.hasNext();) {
              buffer.append(it.next());    
        }    
        
        for (Iterator it=complexTypes.iterator(); it.hasNext();) {
              buffer.append(it.next());    
        }
        
  
        for (Iterator it=elements.iterator(); it.hasNext();) {
              buffer.append(it.next());    
        } 
        buffer.append("</xsd:schema>");
        return buffer.toString();
    }
}
