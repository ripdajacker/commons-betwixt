/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/TestXMLUtils.java,v 1.4 2003/10/09 20:52:07 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/10/09 20:52:07 $
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
 
package org.apache.commons.betwixt;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Test harness for the XMLUtils
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.4 $
  */
public class TestXMLUtils extends AbstractTestCase {
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestXMLUtils.class);
    }
    
    public TestXMLUtils(String testName) {
        super(testName);
    }
    
    /**
     * Test for some common xml naming 
     */
    public void testXMLNameTest() {
        // just go through some common cases to make sure code is working
        assertEquals("Testing name 'Name<'", false, XMLUtils.isWellFormedXMLName("Name<"));
        assertEquals("Testing name 'Name>'", false, XMLUtils.isWellFormedXMLName("Name>"));
        assertEquals("Testing name 'Name''", false, XMLUtils.isWellFormedXMLName("Name'"));
        assertEquals("Testing name 'Name_:-.'", true, XMLUtils.isWellFormedXMLName("Name_:-."));
        assertEquals("Testing name '.Name'", false, XMLUtils.isWellFormedXMLName(".Name"));
        assertEquals("Testing name '-Name'", false, XMLUtils.isWellFormedXMLName("-Name"));
        assertEquals("Testing name ':Name'", true, XMLUtils.isWellFormedXMLName(":Name"));
        assertEquals("Testing name '_Name'", true, XMLUtils.isWellFormedXMLName("_Name"));
        assertEquals("Testing name 'A0123456789Name", true, XMLUtils.isWellFormedXMLName("A0123456789Name"));
    }
    
    /** Test attribute escaping */
    public void testAttributeEscaping() {
        assertEquals("Escaping: <", "&lt;", XMLUtils.escapeAttributeValue("<"));
        assertEquals("Escaping: >", "&gt;", XMLUtils.escapeAttributeValue(">"));
        assertEquals("Escaping: '", "&apos;", XMLUtils.escapeAttributeValue("'"));
        assertEquals("Escaping: \"", "&quot;", XMLUtils.escapeAttributeValue("\""));
        assertEquals("Escaping: &", "&amp;", XMLUtils.escapeAttributeValue("&"));
        assertEquals("Escaping: 1<", "1&lt;", XMLUtils.escapeAttributeValue("1<"));
        assertEquals("Escaping: 1>", "1&gt;", XMLUtils.escapeAttributeValue("1>"));
        assertEquals("Escaping: 1'", "1&apos;", XMLUtils.escapeAttributeValue("1'"));
        assertEquals("Escaping: 1\"", "1&quot;", XMLUtils.escapeAttributeValue("1\""));
        assertEquals("Escaping: 1&", "1&amp;", XMLUtils.escapeAttributeValue("1&"));
        assertEquals("Escaping: <2", "&lt;2", XMLUtils.escapeAttributeValue("<2"));
        assertEquals("Escaping: >2", "&gt;2", XMLUtils.escapeAttributeValue(">2"));
        assertEquals("Escaping: '2", "&apos;2", XMLUtils.escapeAttributeValue("'2"));
        assertEquals("Escaping: \"2", "&quot;2", XMLUtils.escapeAttributeValue("\"2"));
        assertEquals("Escaping: &2", "&amp;2", XMLUtils.escapeAttributeValue("&2"));
        assertEquals("Escaping: a<b", "a&lt;b", XMLUtils.escapeAttributeValue("a<b"));
        assertEquals("Escaping: a>b", "a&gt;b", XMLUtils.escapeAttributeValue("a>b"));
        assertEquals("Escaping: a'b", "a&apos;b", XMLUtils.escapeAttributeValue("a'b"));
        assertEquals("Escaping: a\"b", "a&quot;b", XMLUtils.escapeAttributeValue("a\"b"));
        assertEquals("Escaping: a&b", "a&amp;b", XMLUtils.escapeAttributeValue("a&b"));
        assertEquals("Escaping: <<abba", "&lt;&lt;abba", XMLUtils.escapeAttributeValue("<<abba"));
        assertEquals("Escaping: >>abba", "&gt;&gt;abba", XMLUtils.escapeAttributeValue(">>abba"));
        assertEquals("Escaping: ''abba", "&apos;&apos;abba", XMLUtils.escapeAttributeValue("''abba"));
        assertEquals("Escaping: \"\"abba", "&quot;&quot;abba", XMLUtils.escapeAttributeValue("\"\"abba"));
        assertEquals("Escaping: &&abba", "&amp;&amp;abba", XMLUtils.escapeAttributeValue("&&abba"));
        assertEquals(
            "Escaping: a<>b''c\"e>f'&g", 
            "a&lt;&gt;b&apos;&apos;c&quot;e&gt;f&apos;&amp;g", 
            XMLUtils.escapeAttributeValue("a<>b''c\"e>f'&g"));
        
    }
}

