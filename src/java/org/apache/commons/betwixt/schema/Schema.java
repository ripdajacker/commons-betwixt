/*
 * Copyright 2004 The Apache Software Foundation.
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
 * @version $Revision: 1.1.2.8 $
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
	public void addComplexType(GlobalComplexType complexType) {
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
	public void addElement(GlobalElement element) {
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
    public void addGlobalElementType(TranscriptionConfiguration configuration, ElementDescriptor elementDescriptor) throws IntrospectionException {
        // need to create a global element declaration and a complex type 
        // use the fully qualified class name as the type name
        GlobalElement element = new GlobalElement(
                            elementDescriptor.getLocalName(), 
                            elementDescriptor.getPropertyType().getName());
        addElement(element);
        
        GlobalComplexType type = new GlobalComplexType(configuration, elementDescriptor, this);
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
