/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.betwixt;

import java.util.List;

/**
 * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
 * @version $Revision$
 */
public class TestArrayMaps extends AbstractTestCase {

   private static final AddressBean[] EMPTY_ADDRESS_ARRAY = {};
   private static final Class ADDRESS_ARRAY_CLASS
         = EMPTY_ADDRESS_ARRAY.getClass();

   public TestArrayMaps(String testName) {
      super(testName);
   }

   public void testIntrospection() throws Exception {
      XMLIntrospector introspector = new XMLIntrospector();
      introspector.getConfiguration().setAttributesForPrimitives(true);

      XMLBeanInfo xmlBeanInfo
            = introspector.introspect(AddressBookWithMapArrayAdder.class);

      ElementDescriptor beanDescriptor = xmlBeanInfo.getElementDescriptor();
      List<ElementDescriptor> childDescriptors = beanDescriptor.getElementDescriptors();
      assertEquals("Only one child element", 1, childDescriptors.size());
      ElementDescriptor wrappingDescriptor = childDescriptors.get(0);
      List<ElementDescriptor> wrappingChildDescriptors = wrappingDescriptor.getElementDescriptors();
      assertEquals("One child descriptor", 1, wrappingChildDescriptors.size());
      ElementDescriptor entryDescriptor = wrappingChildDescriptors.get(0);
      List<ElementDescriptor> entryChildDescriptors = entryDescriptor.getElementDescriptors();
      assertEquals("Two child descriptors", 2, entryChildDescriptors.size());
      ElementDescriptor keyDescriptor = null;
      ElementDescriptor valueDescriptor = null;
      if ("key".equals(entryChildDescriptors.get(0).getQualifiedName())) {
         keyDescriptor = entryChildDescriptors.get(0);
      }
      if ("value".equals(entryChildDescriptors.get(0).getQualifiedName())) {
         valueDescriptor = entryChildDescriptors.get(0);
      }
      if ("key".equals(entryChildDescriptors.get(1).getQualifiedName())) {
         keyDescriptor = entryChildDescriptors.get(1);
      }
      if ("value".equals(entryChildDescriptors.get(1).getQualifiedName())) {
         valueDescriptor = entryChildDescriptors.get(1);
      }

      assertNotNull("Expected key descriptor", keyDescriptor);
      assertNotNull("Expected value descriptor", valueDescriptor);
      assertNotNull("Expected key property type", keyDescriptor.getPropertyType());
      assertNotNull("Expected value property type", valueDescriptor.getPropertyType());

      List<ElementDescriptor> childValueDescriptors = valueDescriptor.getElementDescriptors();
      assertEquals("One hollow child descriptor for array", 1, childValueDescriptors.size());
      ElementDescriptor hollowValueDescriptor = childValueDescriptors.get(0);
      assertEquals("Child descriptor is hollow", true, hollowValueDescriptor.isHollow());
      assertEquals(
            "Child descriptor has AddressBean[] property type",
            ADDRESS_ARRAY_CLASS,
            hollowValueDescriptor.getPropertyType());
      assertEquals(
            "Child descriptor has AddressBean[] singular property type",
            ADDRESS_ARRAY_CLASS,
            hollowValueDescriptor.getSingularPropertyType());
   }

}
