/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 
package org.apache.commons.betwixt.nowrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.DefaultPluralStemmer;

/**
 * Test harness for the base PO object
 *
 * @author <a href="mailto:john@zenplex.com">John Thorhauer</a>
 * @version $Id: TestNoWrap.java,v 1.10 2004/02/28 13:38:36 yoavs Exp $
 */
public class TestNoWrap
    extends AbstractTestCase
{
    private POTest po;

    /**
     * A unit test suite for JUnit
     */
    public static Test suite()
    {
        return new TestSuite(TestNoWrap.class);
    }

    /**
     * Constructor for the TestScarabSettings object
     *
     * @param testName
     */
    public TestNoWrap(String testName)
    {
        super(testName);
    }

    /**
     * Description of the Method
     */
    public void testRoundTrip()
        throws Exception
    {
        load();
        write();
    }

    /**
     * Description of the Method
     */
    public void load()
        throws Exception
    {
        String xmlLocation = getTestFile("src/test/org/apache/commons/betwixt/nowrap/po_add_test.xml");

        FileInputStream in = new FileInputStream(new File(xmlLocation));

        // create a new BeanReader
        BeanReader reader = createBeanReader(POTest.class);
        po = (POTest) reader.parse(in);
        assertEquals("PO Printing No", "555008805581", po.getPrintingNumber());
        List componentTests = po.getComponenttests();
        assertEquals("#Component tests", 3, componentTests.size());
        Componenttest testOne = (Componenttest) componentTests.get(0);
        assertEquals("Component Test One", "Text", testOne.getCompDescription());
        Componenttest testTwo = (Componenttest) componentTests.get(1);
        assertEquals("Component Test Two", "Binding", testTwo.getCompDescription());
        Componenttest testThree = (Componenttest) componentTests.get(2);
        assertEquals("Component Test Three", "Paper Cover", testThree.getCompDescription());
    }

    /**
     * Description of the Method
     */
    public void write()
        throws Exception
    {
        // Let's try to write the bean
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter beanWriter = new BeanWriter(out);
        beanWriter.setXMLIntrospector(createXMLIntrospector());
        beanWriter.setWriteIDs(false);
        beanWriter.enablePrettyPrint();
        
        beanWriter.write(po);
        String xml = "<?xml version='1.0'?><content><printingno>555008805581</printingno>"
                + "<componenttest><compdescription>Text</compdescription></componenttest>"
                + "<componenttest><compdescription>Binding</compdescription></componenttest>"
                + "<componenttest><compdescription>Paper Cover</compdescription>"
                + "</componenttest></content>";
                
        xmlAssertIsomorphicContent(
                    parseString(xml),
                    parseString(out.getBuffer().toString()),
                    true);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Description of the Method
     */
    protected BeanReader createBeanReader(Class beanClass)
        throws Exception
    {
        BeanReader reader = new BeanReader();
        reader.setXMLIntrospector(createXMLIntrospector());
        reader.registerBeanClass(beanClass);
        return reader;
    }

    /**
     * ### it would be really nice to move this somewhere shareable across Maven
     * / Turbine projects. Maybe a static helper method - question is what to
     * call it???
     */
    protected XMLIntrospector createXMLIntrospector()
    {
        XMLIntrospector introspector = new XMLIntrospector();

        // set elements for attributes to true
        introspector.setAttributesForPrimitives(false);

        // wrap collections in an XML element
        introspector.setWrapCollectionsInElement(false);

        // turn bean elements first letter into lower case
        introspector.setElementNameMapper( new DecapitalizeNameMapper() );

        // Set default plural stemmer.
        introspector.setPluralStemmer( new DefaultPluralStemmer() );

        return introspector;
    }
}

