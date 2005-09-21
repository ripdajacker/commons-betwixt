/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.commons.betwixt;


import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Tests the multi-mapping of collections with polymorphic entries.
 * 
 * @author Thomas Dudziak (tomdz@apache.org)
 */
public class TestMultipleCollectionMappings extends AbstractTestCase
{
    public static class Container
    {
        private List _elements = new ArrayList();
        private List _nodes    = new ArrayList();

        public Iterator getElements()
        {
            return _elements.iterator();
        }

        public void addElement(Element element)
        {
            _elements.add(element);
        }

        public Iterator getNodes()
        {
            return _nodes.iterator();
        }

        public void addNode(Node node)
        {
            _nodes.add(node);
        }
    }

    public static interface Element
    {}

    public static class ElementA implements Element
    {}

    public static class ElementB implements Element
    {}

    public static abstract class Node
    {}

    public static class Node1 extends Node
    {}

    public static class Node2 extends Node
    {}

    private static final String MAPPING =
        "<?xml version=\"1.0\"?>\n"+
        "<betwixt-config>\n"+
        "  <class name=\""+Container.class.getName()+"\">\n"+
        "    <element name=\"container\">\n"+
        "      <element property=\"elements\" updater=\"addElement\"/>\n"+
        "      <element property=\"nodes\"    updater=\"addNode\"/>\n"+
        "    </element>\n"+
        "  </class>\n"+
        "  <class name=\""+ElementA.class.getName()+"\">\n"+
        "    <element name=\"elementA\"/>\n"+
        "  </class>\n"+
        "  <class name=\""+ElementB.class.getName()+"\">\n"+
        "    <element name=\"elementB\"/>\n"+
        "  </class>\n"+
        "  <class name=\""+Node1.class.getName()+"\">\n"+
        "    <element name=\"node1\"/>\n"+
        "  </class>\n"+
        "  <class name=\""+Node2.class.getName()+"\">\n"+
        "    <element name=\"node2\"/>\n"+
        "  </class>\n"+
        "</betwixt-config>";
    private static final String EXPECTED1 =
        "<?xml version=\"1.0\" ?>\n"+
        "  <container>\n"+
        "    <elementB/>\n"+
        "    <elementA/>\n"+
        "  </container>\n";
    private static final String EXPECTED2 =
        "<?xml version=\"1.0\" ?>\n"+
        "  <container>\n"+
        "    <node1/>\n"+
        "    <node2/>\n"+
        "    <node2/>\n"+
        "  </container>\n";
    private static final String EXPECTED3 =
        "<?xml version=\"1.0\" ?>\n"+
        "  <container>\n"+
        "    <elementA/>\n"+
        "    <elementB/>\n"+
        "    <node2/>\n"+
        "    <node1/>\n"+
        "    <node2/>\n"+
        "  </container>\n";
    
    public TestMultipleCollectionMappings(String testName)
    {
        super(testName);
    }

    public void testOnlyElements() throws IOException, SAXException, IntrospectionException
    {
        Container container = new Container();

        container.addElement(new ElementB());
        container.addElement(new ElementA());

        StringWriter outputWriter = new StringWriter();

        outputWriter.write("<?xml version=\"1.0\" ?>\n");

        BeanWriter beanWriter = new BeanWriter(outputWriter);
        beanWriter.setEndOfLine("\n");
        beanWriter.enablePrettyPrint();
        beanWriter.setWriteEmptyElements(true);
        beanWriter.getBindingConfiguration().setMapIDs(false);
        beanWriter.getXMLIntrospector().register(new InputSource(new StringReader(MAPPING)));
        beanWriter.setEndOfLine("\n"); //force to \n so expected values match for sure
        beanWriter.write(container);

        String output = outputWriter.toString();

        assertEquals(EXPECTED1, output);
            
        BeanReader beanReader = new BeanReader();

        beanReader.registerMultiMapping(new InputSource(new StringReader(MAPPING)));

        StringReader xmlReader = new StringReader(output);

        container = (Container)beanReader.parse(xmlReader);

        Iterator it = container.getElements();

        assertTrue(it.next() instanceof ElementB);
        assertTrue(it.next() instanceof ElementA);
        assertFalse(it.hasNext());

        assertFalse(container.getNodes().hasNext());
    }
    
    public void testOnlyNodes() throws IOException, SAXException, IntrospectionException
    {
        Container container = new Container();

        container.addNode(new Node1());
        container.addNode(new Node2());
        container.addNode(new Node2());

        StringWriter outputWriter = new StringWriter();

        outputWriter.write("<?xml version=\"1.0\" ?>\n");

        BeanWriter beanWriter = new BeanWriter(outputWriter);
        beanWriter.setEndOfLine("\n");
        beanWriter.enablePrettyPrint();
        beanWriter.setWriteEmptyElements(true);
        beanWriter.getBindingConfiguration().setMapIDs(false);
        beanWriter.getXMLIntrospector().register(new InputSource(new StringReader(MAPPING)));
        beanWriter.setEndOfLine("\n"); //force to \n so expected values match for sure
        beanWriter.write(container);

        String output = outputWriter.toString();

        assertEquals(EXPECTED2, output);
            
        BeanReader beanReader = new BeanReader();

        beanReader.registerMultiMapping(new InputSource(new StringReader(MAPPING)));

        StringReader xmlReader = new StringReader(output);

        container = (Container)beanReader.parse(xmlReader);

        assertFalse(container.getElements().hasNext());

        Iterator it = container.getNodes();

        assertTrue(it.next() instanceof Node1);
        assertTrue(it.next() instanceof Node2);
        assertTrue(it.next() instanceof Node2);
        assertFalse(it.hasNext());
    }

    public void testMixed() throws IOException, SAXException, IntrospectionException
    {
        Container container = new Container();

        container.addNode(new Node2());
        container.addNode(new Node1());
        container.addNode(new Node2());
        container.addElement(new ElementA());
        container.addElement(new ElementB());

        StringWriter outputWriter = new StringWriter();

        outputWriter.write("<?xml version=\"1.0\" ?>\n");

        BeanWriter beanWriter = new BeanWriter(outputWriter);
        beanWriter.setEndOfLine("\n");
        beanWriter.enablePrettyPrint();
        beanWriter.setWriteEmptyElements(true);
        beanWriter.getBindingConfiguration().setMapIDs(false);
        beanWriter.getXMLIntrospector().register(new InputSource(new StringReader(MAPPING)));
        beanWriter.setEndOfLine("\n"); //force to \n so expected values match for sure
        beanWriter.write(container);

        String output = outputWriter.toString();

        assertEquals(EXPECTED3, output);
            
        BeanReader beanReader = new BeanReader();

        beanReader.registerMultiMapping(new InputSource(new StringReader(MAPPING)));

        StringReader xmlReader = new StringReader(output);

        container = (Container)beanReader.parse(xmlReader);

        Iterator it = container.getElements();

        assertTrue(it.next() instanceof ElementA);
        assertTrue(it.next() instanceof ElementB);
        assertFalse(it.hasNext());

        it = container.getNodes();

        assertTrue(it.next() instanceof Node2);
        assertTrue(it.next() instanceof Node1);
        assertTrue(it.next() instanceof Node2);
        assertFalse(it.hasNext());

    }
}
