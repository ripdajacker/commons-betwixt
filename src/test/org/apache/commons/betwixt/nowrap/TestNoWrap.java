package org.apache.commons.betwixt.nowrap;

import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.NoOpPluralStemmer;
import org.apache.commons.betwixt.strategy.DefaultPluralStemmer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Test harness for the base PO object
 *
 * @author <a href="mailto:john@zenplex.com">John Thorhauer</a>
 * @version $Id: TestNoWrap.java,v 1.1 2002/07/08 16:40:06 jvanzyl Exp $
 */
public class TestNoWrap
    extends TestCase
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
        String xmlLocation = "src/test/org/apache/commons/betwixt/nowrap/po_add_test.xml";

        FileInputStream in = new FileInputStream(new File(xmlLocation));

        // create a new BeanReader
        BeanReader reader = createBeanReader(POTest.class);
        po = (POTest) reader.parse(in);
    }

    /**
     * Description of the Method
     */
    public void write()
        throws Exception
    {
        // Let's try to write the bean
        BeanWriter beanWriter = new BeanWriter();
        beanWriter.setXMLIntrospector(createXMLIntrospector());
        beanWriter.setWriteIDs(false);
        beanWriter.enablePrettyPrint();
        System.out.println(po);
        beanWriter.write(po);
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
        introspector.setNameMapper( new DecapitalizeNameMapper() );

        // Set default plural stemmer.
        introspector.setPluralStemmer( new DefaultPluralStemmer() );

        return introspector;
    }
}

