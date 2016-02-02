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
package org.apache.commons.betwixt.strategy;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

import java.io.StringReader;
import java.io.StringWriter;

/**
 */
public class TestIdStorageStrategy extends AbstractTestCase {

    public TestIdStorageStrategy(String testName) {
        super(testName);
    }

    public void testWrite() throws Exception {

        final Element alpha = new Element("ONE");
        Element beta = new Element("TWO");
        ElementsList elements = new ElementsList();
        elements.addElement(alpha);
        elements.addElement(beta);

        IdStoringStrategy storingStrategy = new DefaultIdStoringStrategy() {

            public String getReferenceFor(Object bean) {
                String result;
                if (bean == alpha) {
                    result = "ALPHA";
                } else {
                    result = super.getReferenceFor(bean);
                }
                return result;
            }

            public void setReference(Object bean, String id) {
                if (bean != alpha) {
                    super.setReference(bean, id);
                }
            }
        };

        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.getBindingConfiguration().setIdMappingStrategy(storingStrategy);
        writer.write(elements);

        String expected = "<?xml version='1.0'?>" +
                "<ElementsList id='1'>" +
                "   <elements>" +
                "       <element idref='ALPHA'/>" +
                "       <element id='2'>" +
                "           <value>TWO</value>" +
                "       </element>" +
                "   </elements>" +
                "</ElementsList>";

        xmlAssertIsomorphicContent(parseString(expected), parseString(out));
    }

    public void testRead() throws Exception {

        String xml = "<?xml version='1.0'?>" +
                "<ElementsList id='1'>" +
                "   <elements>" +
                "       <element idref='ALPHA'/>" +
                "       <element id='2'>" +
                "           <value>TWO</value>" +
                "       </element>" +
                "   </elements>" +
                "</ElementsList>";

        final Element alpha = new Element("ONE");

        IdStoringStrategy storingStrategy = new DefaultIdStoringStrategy() {

            public void setReference(Object bean, String id) {
                if (bean != alpha) {
                    super.setReference(bean, id);
                }
            }

            public Object getReferenced(String id) {
                if ("ALPHA".equals(id)) {
                    return alpha;
                }
                return getReferenced(id);
            }

        };

        BeanReader reader = new BeanReader();
        reader.getBindingConfiguration().setIdMappingStrategy(storingStrategy);
        reader.registerBeanClass(ElementsList.class);
        ElementsList elements = (ElementsList) reader.parse(new StringReader(xml));
        assertNotNull(elements);
        Element one = elements.get(0);
        assertTrue(one == alpha);
        Element two = elements.get(1);
        assertNotNull(two);
    }


}
