package org.apache.commons.betwixt.scarab;

/*
 * $Header: /home/cvs/jakarta-commons/beanutils/LICENSE.txt,v 1.3 2003/01/15 21:59:38 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/01/15 21:59:38 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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

import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;

/**
 * Test harness which round trips a Scarab's settings xml file
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id: TestScarabSettings.java,v 1.7 2002/06/05 07:57:07 jstrachan Exp $
 */
public class TestScarabSettings extends AbstractTestCase
{
    public static void main( String[] args )
    {
        TestRunner.run( suite() );
    }

    /**
     * A unit test suite for JUnit
     */
    public static Test suite()
    {
        return new TestSuite(TestScarabSettings.class);
    }

    /**
     * Constructor for the TestScarabSettings object
     *
     * @param testName
     */
    public TestScarabSettings(String testName)
    {
        super(testName);
    }

    /**
     * Tests we can round trip from the XML -> bean -> XML -> bean. Ideally this
     * method should test both Project objects are identical
     */
    public void testRoundTrip()
        throws Exception
    {
        BeanReader reader = createBeanReader();

        ScarabSettings ss = (ScarabSettings) reader.parse(
            new FileInputStream(getTestFile("src/test/org/apache/commons/betwixt/scarab/scarab-settings.xml")));

        // now lets output it to a buffer
        StringWriter buffer = new StringWriter();
        write(ss, buffer);

        // create a new BeanReader
        reader = createBeanReader();

        // now lets try parse the output sing the BeanReader
        String text = buffer.toString();

        System.out.println(text);

        /*
        ScarabSettings newScarabSettings = (ScarabSettings) reader.parse(new StringReader(text));

        // managed to parse it again!
        testScarabSettings(newScarabSettings);
        */
        testScarabSettings(ss);

        // #### should now test the old and new Project instances for equality.
    }


    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Description of the Method
     */
    protected BeanReader createBeanReader()
        throws Exception
    {
        BeanReader reader = new BeanReader();
        reader.setXMLIntrospector(createXMLIntrospector());
        reader.registerBeanClass(ScarabSettings.class);
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
        //introspector.setWrapCollectionsInElement(true);

        // turn bean elements into lower case
        introspector.setElementNameMapper(new HyphenatedNameMapper());

        return introspector;
    }

    /**
     * Tests the value of the Project object that has just been parsed
     */
    protected void testScarabSettings(ScarabSettings ss)
        throws Exception
    {
        List globalAttributes = ss.getGlobalAttributes();
        GlobalAttribute ga = (GlobalAttribute) globalAttributes.get(1);
        assertEquals("Functional area", ga.getName());

        List globalAttributeOptions = ga.getGlobalAttributeOptions();
        
        System.out.println( "GlobalAttribute: " + ga);
        System.out.println( "globalAttributeOptions: " + globalAttributeOptions);

        assertEquals(ga.getCreatedDate().getTimestamp(), "2002-05-31 13:29:27.0");
        
        assertEquals(globalAttributeOptions.size(), 2);
        GlobalAttributeOption gao = (GlobalAttributeOption) globalAttributeOptions.get(0);
        assertEquals("UI", gao.getChildOption());        
        gao = (GlobalAttributeOption) globalAttributeOptions.get(1);
        assertEquals("Code", gao.getChildOption());        

        List globalIssueTypes = ss.getGlobalIssueTypes();
        GlobalIssueType git = (GlobalIssueType) globalIssueTypes.get(0);
        assertEquals("Defect", git.getName());

        List modules = ss.getModules();
        Module m = (Module) modules.get(0);
        assertEquals("Source", m.getName());
    }

    /**
     * Description of the Method
     */
    protected void write(Object bean, Writer out)
        throws Exception
    {
        BeanWriter writer = new BeanWriter(out);
        writer.setXMLIntrospector(createXMLIntrospector());
        writer.enablePrettyPrint();
        writer.write(bean);
    }
}

