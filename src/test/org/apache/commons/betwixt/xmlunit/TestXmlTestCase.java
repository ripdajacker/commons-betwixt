package org.apache.commons.betwixt.xmlunit;

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

import junit.framework.AssertionFailedError;

/**
 * Test harness which test xml unit
 *
 * @author Robert Burrell Donkin
 * @version $Id: TestXmlTestCase.java,v 1.5 2004/02/28 13:38:37 yoavs Exp $
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
}