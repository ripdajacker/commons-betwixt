package org.apache.commons.betwixt.xmlunit;

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/xmlunit/TestXmlTestCase.java,v 1.4.2.2 2004/02/02 22:21:44 rdonkin Exp $
 * $Revision: 1.4.2.2 $
 * $Date: 2004/02/02 22:21:44 $
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

import java.io.File;
import java.io.FileInputStream;

import junit.framework.AssertionFailedError;

import org.xml.sax.InputSource;

/**
 * Test harness which test xml unit
 *
 * @author Robert Burrell Donkin
 * @version $Id: TestXmlTestCase.java,v 1.4.2.2 2004/02/02 22:21:44 rdonkin Exp $
 */
 public class TestXmlTestCase extends XmlTestCase {
 
    public TestXmlTestCase(String name) {
        super(name);
    }
 
    public void testXMLUnit() throws Exception {
        xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/rss-example.xml"),
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/rss-example.xml"));
    }
    
    public void testXMLUnit2() throws Exception {
        boolean failed = false;
        try {
            xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/rss-example.xml"),
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/rss-example-morphed.xml"),
                    false);
            failed = true;
        } catch (AssertionFailedError er) {
            // this is expected
        }
        if (failed) {
            fail("Expected unit test to fail!");
        }
    }
    
    public void testXMLUnit3() throws Exception {
        boolean failed = false;
        try {
            xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/rss-example.xml"),
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/rss-example-not.xml"));
            failed = true;
        } catch (AssertionFailedError er) {
            // this is expected
        }
        if (failed) {
            fail("Expected unit test to fail!");
        }
    }

    
    public void testXMLUnit4() throws Exception {
        xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/rss-example.xml"),
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/rss-example-morphed.xml"),
                    true);
    }
    
    
    public void testXMLUnit5() throws Exception {
        boolean failed = false;
        try {
            xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/rss-example.xml"),
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/rss-example-not.xml"),
                    true);
            failed = true;
        } catch (AssertionFailedError er) {
            // this is expected
        }
        if (failed) {
            fail("Expected unit test to fail!");
        }
    }
    
    
    public void testXMLUnit6() throws Exception {
        boolean failed = false;
        try {
            xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/scarab-one.xml"),
                    parseFile("src/test/org/apache/commons/betwixt/xmlunit/scarab-two.xml"),
                    true);
            failed = true;
        } catch (AssertionFailedError er) {
            // this is expected
        }
        if (failed) {
            fail("Expected unit test to fail!");
        }
    }
    
    public void testValidateSchemaValidOne() throws Exception {
        String basedir = System.getProperty("basedir");
        InputSource document = new InputSource(new FileInputStream(
            new File(basedir,"src/test/org/apache/commons/betwixt/xmlunit/valid.xml")));
        InputSource schema = new InputSource(new FileInputStream(
            new File(basedir,"src/test/org/apache/commons/betwixt/xmlunit/test.xsd")));
        assertTrue(isValid(document, schema));
    }
  
   
    public void testValidateSchemaInvalidOne() throws Exception {
        String basedir = System.getProperty("basedir");
        InputSource document = new InputSource(new FileInputStream(
            new File(basedir,"src/test/org/apache/commons/betwixt/xmlunit/invalid.xml")));
        InputSource schema = new InputSource(new FileInputStream( 
            new File(basedir,"src/test/org/apache/commons/betwixt/xmlunit/test.xsd")));
        assertFalse(isValid(document, schema));
    }
    
    public void testValidateSchemaValidTwo() throws Exception {
        String basedir = System.getProperty("basedir");
        InputSource document = new InputSource(new FileInputStream(
            new File(basedir,"src/test/org/apache/commons/betwixt/xmlunit/valid-personnel-schema.xml")));
        InputSource schema = new InputSource(new FileInputStream(
            new File(basedir,"src/test/org/apache/commons/betwixt/xmlunit/personnel.xsd")));
        assertTrue(isValid(document, schema));
    }
  
   
    public void testValidateSchemaInvalidTwo() throws Exception {
        String basedir = System.getProperty("basedir");
        InputSource document = new InputSource(new FileInputStream(
            new File(basedir,"src/test/org/apache/commons/betwixt/xmlunit/invalid-personnel-schema.xml")));
        InputSource schema = new InputSource(new FileInputStream( 
            new File(basedir,"src/test/org/apache/commons/betwixt/xmlunit/personnel.xsd")));
        assertFalse(isValid(document, schema));
    }
    
}