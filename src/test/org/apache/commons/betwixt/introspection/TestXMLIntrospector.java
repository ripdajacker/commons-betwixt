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

package org.apache.commons.betwixt.introspection;

import dk.mehmedbasic.betwixt.BeanIntrospector;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.commons.betwixt.*;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.registry.DefaultXMLBeanInfoRegistry;
import org.apache.commons.betwixt.registry.NoCacheRegistry;
import org.apache.commons.betwixt.strategy.ClassNormalizer;
import org.apache.commons.betwixt.strategy.ListedClassNormalizer;
import org.apache.commons.digester.rss.Channel;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Test harness for the XMLIntrospector
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision$
 */
public class TestXMLIntrospector extends AbstractTestCase {

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestXMLIntrospector.class);
    }

    public TestXMLIntrospector(String testName) {
        super(testName);
    }

    public void testIntrospector() throws Exception {
        //SimpleLog log = new SimpleLog("testIntrospector:introspector");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        XMLIntrospector introspector = new XMLIntrospector();
        //introspector.setLog(log);

        introspector.getConfiguration().setAttributesForPrimitives(true);

        Object bean = createBean();

        XMLBeanInfo info = introspector.introspect(bean);

        assertTrue("Found XMLBeanInfo", info != null);

        ElementDescriptor descriptor = info.getElementDescriptor();

        assertTrue("Found root element descriptor", descriptor != null);

        List<AttributeDescriptor> attributes = descriptor.getAttributeDescriptors();

        assertTrue("Found attributes", attributes != null && attributes.size() > 0);

        // test second introspection with caching on
        info = introspector.introspect(bean);

        assertTrue("Found XMLBeanInfo", info != null);

        descriptor = info.getElementDescriptor();

        assertTrue("Found root element descriptor", descriptor != null);

        attributes = descriptor.getAttributeDescriptors();

        assertTrue("Found attributes", attributes != null && attributes.size() > 0);


        // test introspection with caching off
        //introspector.setCachingEnabled(false);
        introspector.setRegistry(new NoCacheRegistry());
        info = introspector.introspect(bean);

        assertTrue("Found XMLBeanInfo", info != null);

        descriptor = info.getElementDescriptor();

        assertTrue("Found root element descriptor", descriptor != null);

        attributes = descriptor.getAttributeDescriptors();

        assertTrue("Found attributes", attributes != null && attributes.size() > 0);


        // test introspection after flushing cache
//        introspector.setCachingEnabled(true);
        introspector.setRegistry(new DefaultXMLBeanInfoRegistry());
        //introspector.flushCache();
        info = introspector.introspect(bean);

        assertTrue("Found XMLBeanInfo", info != null);

        descriptor = info.getElementDescriptor();

        assertTrue("Found root element descriptor", descriptor != null);

        attributes = descriptor.getAttributeDescriptors();

        assertTrue("Found attributes", attributes != null && attributes.size() > 0);

    }

    public void testBeanWithBeanInfo() throws Exception {

        // let's check that bean info's ok
        BeanInfo bwbiBeanInfo = BeanIntrospector.getBeanInfo(BeanWithBeanInfoBean.class);

        List<PropertyDescriptor> propertyDescriptors = new LinkedList<>();
        Collections.addAll(propertyDescriptors, bwbiBeanInfo.getPropertyDescriptors());

        assertEquals("Wrong number of properties", 3, propertyDescriptors.size());

        // order of properties isn't guarenteed
        if ("alpha".equals(propertyDescriptors.get(0).getName())) {
            assertEquals("Second property name", "beta", propertyDescriptors.get(1).getName());

        } else {

            assertEquals("First property name", "gamma", propertyDescriptors.get(0).getName());
            assertEquals("Second property name", "alpha", propertyDescriptors.get(1).getName());
        }

        // finished with the descriptors
        propertyDescriptors = null;

//        SimpleLog log = new SimpleLog("[testBeanWithBeanInfo:XMLIntrospector]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);

        XMLIntrospector introspector = new XMLIntrospector();
        introspector.getConfiguration().setAttributesForPrimitives(false);
//        introspector.setLog(log);

        XMLBeanInfo xmlBeanInfo = introspector.introspect(BeanWithBeanInfoBean.class);

        List<ElementDescriptor> elementDescriptors = xmlBeanInfo.getElementDescriptor().getElementDescriptors();

        assertEquals("Wrong number of elements", 2, elementDescriptors.size());

        // order of properties isn't guarenteed
        boolean alphaFirst = true;
        if ("alpha".equals(elementDescriptors.get(0).getPropertyName())) {
            assertEquals("Second element name", "beta", elementDescriptors.get(1).getPropertyName());
        } else {
            alphaFirst = false;
            assertEquals("First element name", "beta", elementDescriptors.get(0).getPropertyName());
            assertEquals("Second element name", "alpha", elementDescriptors.get(1).getPropertyName());
        }

        // might as well give test output
        StringWriter out = new StringWriter();
        BeanWriter writer = new BeanWriter(out);
        writer.getBindingConfiguration().setMapIDs(false);
        BeanWithBeanInfoBean bean = new BeanWithBeanInfoBean("alpha value", "beta value", "gamma value");
        writer.write(bean);

        if (alphaFirst) {

            xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/introspection/test-bwbi-output-a.xml"),
                    parseString(out.toString()));

        } else {
            xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/introspection/test-bwbi-output-g.xml"),
                    parseString(out.toString()));
        }
    }

    public void testDefaultClassNormalizer() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();

        FaceImpl face = new FaceImpl();
        XMLBeanInfo info = introspector.introspect(face);
        ElementDescriptor elementDescriptor = info.getElementDescriptor();

        List<AttributeDescriptor> attributeDescriptor = elementDescriptor.getAttributeDescriptors();
        List<ElementDescriptor> children = elementDescriptor.getElementDescriptors();

        assertEquals("Expected no attributes", 0, attributeDescriptor.size());
        assertEquals("Expected two elements", 2, children.size());
    }

    public void testClassNormalizer() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.getConfiguration().setClassNormalizer(
                new ClassNormalizer() {

                    public Class normalize(Class clazz) {
                        if (IFace.class.isAssignableFrom(clazz)) {
                            return IFace.class;
                        }
                        return super.normalize(clazz);
                    }
                });

        FaceImpl face = new FaceImpl();
        XMLBeanInfo info = introspector.introspect(face);
        ElementDescriptor elementDescriptor = info.getElementDescriptor();
        assertEquals("Expected only itself", 1, elementDescriptor.getElementDescriptors().size());

        List<AttributeDescriptor> attributeDescriptor = elementDescriptor.getAttributeDescriptors();
        List<ElementDescriptor> children = elementDescriptor.getElementDescriptors();

        assertEquals("Expected no attributes", 0, attributeDescriptor.size());
        assertEquals("Expected one elements", 1, children.size());
        assertEquals("Expected element", "name", children.get(0).getLocalName());
    }

    public void testListedClassNormalizer() throws Exception {
        ListedClassNormalizer classNormalizer = new ListedClassNormalizer();
        classNormalizer.addSubstitution(IFace.class);
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.getConfiguration().setClassNormalizer(classNormalizer);

        FaceImpl face = new FaceImpl();

        XMLBeanInfo info = introspector.introspect(face);
        ElementDescriptor elementDescriptor = info.getElementDescriptor();
        List<AttributeDescriptor> attributeDescriptor = elementDescriptor.getAttributeDescriptors();
        List<ElementDescriptor> children = elementDescriptor.getElementDescriptors();

        assertEquals("Expected no attributes", 0, attributeDescriptor.size());
        assertEquals("Expected one elements", 1, children.size());
        assertEquals("Expected element", "name", children.get(0).getLocalName());
    }

    public void testListedClassNormalizerWrite() throws Exception {
        ListedClassNormalizer classNormalizer = new ListedClassNormalizer();
        classNormalizer.addSubstitution(IFace.class);

        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.getBindingConfiguration().setMapIDs(false);
        writer.getXMLIntrospector().getConfiguration().setClassNormalizer(classNormalizer);
        FaceImpl bean = new FaceImpl();
        bean.setName("Old Tom Cobbly");
        writer.write(bean);

        String xml = "<?xml version='1.0'?><IFace><name>Old Tom Cobbly</name></IFace>";
        xmlAssertIsomorphicContent(
                parseString(out.getBuffer().toString()),
                parseString(xml),
                true);
    }

    public void testBetwixtFileType() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        XMLBeanInfo info = introspector.introspect(Channel.class);

        ElementDescriptor elementDescriptor = info.getElementDescriptor();

        Class clazz = elementDescriptor.getSingularPropertyType();
        assertEquals("Element type correct", Channel.class, clazz);

        assertEquals("Element name correct", "rss", elementDescriptor.getLocalName());
    }

    public void testIgnoreAllBeanInfo() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.getConfiguration().setIgnoreAllBeanInfo(false);
        introspector.setRegistry(new NoCacheRegistry());
        XMLBeanInfo info = introspector.introspect(BeanWithBeanInfoBean.class);
        List<ElementDescriptor> elementDescriptors = info.getElementDescriptor().getElementDescriptors();
        // When BeanInfo is used the properties alpha and gamma will be found
        if ("alpha".equals(elementDescriptors.get(0).getPropertyName())) {
            assertEquals("Second element name", "beta", elementDescriptors.get(1).getPropertyName());
        } else {
            assertEquals("First element name", "gamma", elementDescriptors.get(0).getPropertyName());
            assertEquals("Second element name", "alpha", elementDescriptors.get(1).getPropertyName());
        }

        introspector.getConfiguration().setIgnoreAllBeanInfo(true);
        info = introspector.introspect(BeanWithBeanInfoBean.class);
        elementDescriptors = info.getElementDescriptor().getElementDescriptors();
        // When BeanInfo is ignored the properties alpha and beta will be found
        if ("alpha".equals(elementDescriptors.get(0).getPropertyName())) {
            assertEquals("Second element name", "beta", elementDescriptors.get(1).getPropertyName());
        } else {
            assertEquals("First element name", "beta", elementDescriptors.get(0).getPropertyName());
            assertEquals("Second element name", "alpha", elementDescriptors.get(1).getPropertyName());
        }
    }


}

