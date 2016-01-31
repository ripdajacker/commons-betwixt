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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.commons.betwixt.digester.XMLBeanInfoDigester;

import java.io.FileInputStream;
import java.io.InputStream;

/** Test harness for the Digester of XMLBeanInfo
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision$
 */
public class TestXMLBeanInfoDigester extends AbstractTestCase {

   public static void main(String[] args) {
      TestRunner.run(suite());
   }

   public static Test suite() {
      return new TestSuite(TestXMLBeanInfoDigester.class);
   }

   public TestXMLBeanInfoDigester(String testName) {
      super(testName);
   }

   public void testDigester() throws Exception {
      XMLBeanInfoDigester digester = new XMLBeanInfoDigester();
      // TODO the digestion probably won't work without an XMLIntrospector
      // so it might be better to enforce via a constructor
      // or create a default one
      digester.setXMLIntrospector(new XMLIntrospector());

      InputStream in = new FileInputStream(getTestFile("src/test/org/apache/commons/digester/rss/Channel.betwixt"));

      assertTrue("Found betwixt config file", in != null);

      XMLBeanInfo info = (XMLBeanInfo) digester.parse(in);

      assertTrue("Found XMLBeanInfo", info != null);

      ElementDescriptor descriptor = info.getElementDescriptor();

      assertTrue("Found root element descriptor", descriptor != null);
      assertEquals("Element name correct", "rss", descriptor.getLocalName());

      java.util.List<ElementDescriptor> elements = descriptor.getElementDescriptors();

      assertTrue("Found elements", elements != null && elements.size() > 0);

      descriptor = elements.get(0);
      assertEquals("Element name correct", "channel", descriptor.getLocalName());

   }
}

