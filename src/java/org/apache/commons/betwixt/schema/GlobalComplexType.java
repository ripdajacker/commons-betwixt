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
import java.util.Iterator;

import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.collections.CollectionUtils;

/**
 * Models a <code>complexType</code> from an XML schema.
 * A complex type may contain element content and may have attributes.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.2 $
 */
public class GlobalComplexType extends ComplexType {
	
	private String name;
	
	public GlobalComplexType() {}
    
    /**
     * Constructs a new ComplexType from the descriptor given.
     * @param elementDescriptor
     */
    public GlobalComplexType(TranscriptionConfiguration configuration, ElementDescriptor elementDescriptor, Schema schema) throws IntrospectionException {
        super(configuration, elementDescriptor, schema);    
    }

    protected void init(TranscriptionConfiguration configuration, ElementDescriptor elementDescriptor, Schema schema) throws IntrospectionException {
        setName(elementDescriptor.getPropertyType().getName());
        super.init(configuration, elementDescriptor, schema);
    }

	/**
     * Gets the name of this type.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this type.
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

    public boolean equals(Object obj) {
          boolean result = false;
          if (obj instanceof GlobalComplexType) {
              GlobalComplexType complexType = (GlobalComplexType) obj;
              result =  isEqual(name, complexType.name) &&
                        CollectionUtils.isEqualCollection(attributes, complexType.attributes) &&
                        CollectionUtils.isEqualCollection(elements, complexType.elements);
                                   
          }
          return result;
      }

    public int hashCode() {
        return 0;
    }

      /**
       * Null safe equals method
       * @param one
       * @param two
       * @return
       */
      private boolean isEqual(String one, String two) {
          boolean result = false;
          if (one == null) {
              result = (two == null); 
          }
          else
          {
              result = one.equals(two);
          }
        
          return result;
      }
      
      public String toString() {
          StringBuffer buffer = new StringBuffer();
          buffer.append("<xsd:complexType name='");
          buffer.append(name);
          buffer.append("'>");
          buffer.append("<xsd:sequence>");
          for (Iterator it=elements.iterator(); it.hasNext();) {
                buffer.append(it.next());    
          }
          buffer.append("</xsd:sequence>");
          
          for (Iterator it=attributes.iterator(); it.hasNext();) {
                buffer.append(it.next());    
          }
          buffer.append("</xsd:complexType>");
          return buffer.toString();
      }
}
