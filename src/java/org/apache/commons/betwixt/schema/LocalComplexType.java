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
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.3 $
 */
public class LocalComplexType extends ComplexType {


    public LocalComplexType() {}

    public LocalComplexType(TranscriptionConfiguration configuration, ElementDescriptor elementDescriptor, Schema schema) throws IntrospectionException {
        super(configuration, elementDescriptor, schema);   
    }

    public boolean equals(Object obj) {
          boolean result = false;
          if (obj instanceof GlobalComplexType) {
              GlobalComplexType complexType = (GlobalComplexType) obj;
              result =  
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
          buffer.append("<xsd:complexType>");
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
