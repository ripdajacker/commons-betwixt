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
package org.apache.commons.betwixt.dotbetwixt;

import org.apache.commons.betwixt.*;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.List;

/**
 * @author <a href='http://commons.apache.org'>Apache Commons Team</a>, <a href='http://www.apache.org'>Apache Software Foundation</a>
 */
public class TestMultiMap extends AbstractTestCase {

   public TestMultiMap(String testName) {
      super(testName);
   }

   private static final String MAPPING = "<?xml version='1.0'?>" +
         "     <betwixt-config>" +
         "            <class name='org.apache.commons.betwixt.PartyBean'>" +
         "    		    	<element name='party'>" +
         "                <element name='the-excuse' property='excuse'/>" +
         "                <element name='location' property='venue'/>      " +
         "                <element name='time' property='fromHour'/>" +
         "              </element>" +
         "            </class>" +
         "            <class name='org.apache.commons.betwixt.AddressBean'>" +
         "              <element name='not-address'>" +
         "                <element name='not-street' property='street'/>" +
         "                <element name='not-city' property='city'/>" +
         "                <element name='not-code' property='code'/>" +
         "                <element name='not-country' property='country'/>" +
         "              </element>" +
         "            </class>" +
         "            <class name='org.apache.commons.betwixt.dotbetwixt.SimpleTestBean'>" +
         "                <element name='jelly'>" +
         "                    <element name='wibble' property='alpha'/>" +
         "                    <element name='wobble' property='beta'/>" +
         "                </element>" +
         "            </class>" +
         "     </betwixt-config>";

   public void testRegisterMultiMapping() throws Exception {
      XMLIntrospector xmlIntrospector = new XMLIntrospector();
      List<Class> mapped = xmlIntrospector.register(new InputSource(new StringReader(MAPPING)));

      assertEquals("Mapped classes", 3, mapped.size());

      XMLBeanInfo beanInfo = xmlIntrospector.introspect(AddressBean.class);
      assertNotNull("Bean info mapping", beanInfo);
      ElementDescriptor descriptor = beanInfo.getElementDescriptor();
      assertEquals("Root element name", "not-address", descriptor.getLocalName());
      java.util.List<ElementDescriptor> childDescriptors = descriptor.getElementDescriptors();
      assertEquals("4 child elements", 4, childDescriptors.size());
      assertEquals("First element", "not-street", childDescriptors.get(0).getLocalName());
      assertEquals("Second element", "not-city", childDescriptors.get(1).getLocalName());
      assertEquals("Third element", "not-code", childDescriptors.get(2).getLocalName());
      assertEquals("Forth element", "not-country", childDescriptors.get(3).getLocalName());

      beanInfo = xmlIntrospector.introspect(SimpleTestBean.class);
      assertNotNull("Bean info mapping", beanInfo);
      descriptor = beanInfo.getElementDescriptor();
      assertEquals("Root element name", "jelly", descriptor.getLocalName());
      childDescriptors = descriptor.getElementDescriptors();
      assertEquals("Child elements", 2, childDescriptors.size());
      assertEquals("First element", "wibble", childDescriptors.get(0).getLocalName());
      assertEquals("Second element", "wobble", childDescriptors.get(1).getLocalName());

   }
}
