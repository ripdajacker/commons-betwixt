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

package org.apache.commons.betwixt.poly;

import org.apache.commons.betwixt.*;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class TestPolyListHolder extends AbstractTestCase {

   public TestPolyListHolder(String testName) {
      super(testName);
   }

   private static final String XML = "<AlphaListHolder>" +
         "    <AlphaList>" +
         "        <AlphaOneImpl>" +
         "            <one>1</one>" +
         "        </AlphaOneImpl>" +
         "        <AlphaTwoImpl>" +
         "            <two>2</two>" +
         "        </AlphaTwoImpl>" +
         "    </AlphaList>" +
         "</AlphaListHolder>";

   private static final String MAPPING = "<?xml version='1.0'?>"
         + "<betwixt-config>"
         + "  <class name='org.apache.commons.betwixt.poly.AlphaListHolder'>"
         + "    <element name='AlphaListHolder'>"
         + "       <element name='AlphaList' property='alphaList' updater='setAlphaList'/>"
         + "    </element>"
         + "  </class>"
         + "  <class name='org.apache.commons.betwixt.poly.AlphaOneImpl'>"
         + "    <element name='AlphaOneImpl'>"
         + "      <element name='one' property='one'/>"
         + "    </element>"
         + "  </class>"
         + "  <class name='org.apache.commons.betwixt.poly.AlphaTwoImpl'>"
         + "    <element name='AlphaTwoImpl'>"
         + "      <element name='two' property='two'/>"
         + "    </element>"
         + "  </class>"
         + "</betwixt-config>";

   public void testWrite() throws Exception {
      AlphaList list = new AlphaList();
      AlphaOneImpl one = new AlphaOneImpl("1");
      list.add(one);
      AlphaTwoImpl two = new AlphaTwoImpl("2");
      list.add(two);

      AlphaListHolder bean = new AlphaListHolder();
      bean.setAlphaList(list);

      StringWriter out = new StringWriter();
      BeanWriter writer = new BeanWriter(out);
      StringReader mapping = new StringReader(MAPPING);
      writer.getIntrospector().register(new InputSource(mapping));
      configure(writer.getBindingConfiguration());
      writer.write(bean);

      String written = out.getBuffer().toString();

      xmlAssertIsomorphicContent(
            parseString(XML),
            parseString(written),
            true);
   }

   public void testRead() throws Exception {

      StringReader in = new StringReader(XML);
      BeanReader reader = new BeanReader();

      StringReader mapping = new StringReader(MAPPING);
      reader.registerMultiMapping(new InputSource(mapping));
      reader.registerBeanClass(AlphaList.class);
      configure(reader.getBindingConfiguration());
      Object bean = reader.parse(in);

      assertTrue(bean instanceof AlphaListHolder);
      AlphaListHolder holder = (AlphaListHolder) bean;

      AlphaList list = holder.getAlphaList();
      assertNotNull(list);
      assertEquals(2, list.size());

      assertTrue(list.get(0) instanceof AlphaOneImpl);
      AlphaOneImpl one = (AlphaOneImpl) list.get(0);
      assertEquals("1", one.alpha());

      assertTrue(list.get(1) instanceof AlphaTwoImpl);
      AlphaTwoImpl two = (AlphaTwoImpl) list.get(1);
      assertEquals("2", two.alpha());
   }

   private void configure(BindingConfiguration configuration) {
      configuration.setMapIDs(false);
   }


   public void testIntrospection() throws Exception {
      XMLIntrospector introspector = new XMLIntrospector();

      StringReader mapping = new StringReader(MAPPING);
      introspector.register(new InputSource(mapping));

      XMLBeanInfo beanInfo = introspector.introspect(AlphaListHolder.class);
      ElementDescriptor descriptor = beanInfo.getElementDescriptor();
      assertNotNull(descriptor);
      List<ElementDescriptor> descriptors = descriptor.getElementDescriptors();
      assertNotNull(descriptors);
      assertEquals("Only one descriptor", 1, descriptors.size());
      assertNotNull("Expected updater", descriptors.get(0).getUpdater());
   }
}
