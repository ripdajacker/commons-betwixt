/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/nowrap/TestNoWrap.java,v 1.9.2.3 2004/01/18 22:25:23 rdonkin Exp $
 * $Revision: 1.9.2.3 $
 * $Date: 2004/01/18 22:25:23 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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
 * @version $Id: TestNoWrap.java,v 1.9.2.3 2004/01/18 22:25:23 rdonkin Exp $
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
        beanWriter.getBindingConfiguration().setMapIDs(false);
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
        introspector.getConfiguration().setAttributesForPrimitives(false);

        // wrap collections in an XML element
        introspector.getConfiguration().setWrapCollectionsInElement(false);

        // turn bean elements first letter into lower case
        introspector.getConfiguration().setElementNameMapper( new DecapitalizeNameMapper() );

        // Set default plural stemmer.
        introspector.getConfiguration().setPluralStemmer( new DefaultPluralStemmer() );

        return introspector;
    }
}

