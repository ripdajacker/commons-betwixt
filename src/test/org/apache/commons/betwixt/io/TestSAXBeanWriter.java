/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/io/TestSAXBeanWriter.java,v 1.1 2002/11/08 22:09:01 mvdb Exp $
 * $Revision: 1.1 $
 * $Date: 2002/11/08 22:09:01 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * $Id: TestSAXBeanWriter.java,v 1.1 2002/11/08 22:09:01 mvdb Exp $
 */
package org.apache.commons.betwixt.io;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.PersonBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/** 
 * Test harness for SAXBeanWriter.
 * 
 * @author <a href="mailto:contact@hdietrich.net">Harald Dietrich</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: TestSAXBeanWriter.java,v 1.1 2002/11/08 22:09:01 mvdb Exp $
 */
public class TestSAXBeanWriter extends TestCase {
    
    public static final String XML = "<?xml version='1.0'?><PersonBean id='1'><age>35</age><name>John Smith</name></PersonBean>";

    public TestSAXBeanWriter(String name) {
        super(name);
    }

    public void testWrite() throws Exception {
        PersonBean bean = new PersonBean(35, "John Smith");

        // writer bean into string
        StringWriter out = new StringWriter();
        
        SAXBeanWriter writer = new SAXBeanWriter(new SAXContentHandler(out));
        writer.write(bean);
        String beanString = out.getBuffer().toString();
        
        // test the result
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        factory.setIgnoringElementContentWhitespace(true);
        InputSource in = new InputSource();
        StringReader reader = new StringReader(beanString);
        in.setCharacterStream(reader);
        Document doc = builder.parse(in);
        this.assertNotNull("Document missing", doc);        
        Element root = doc.getDocumentElement();
        this.assertNotNull("Document root missing", root);
        this.assertEquals("Document root name wrong", "PersonBean", root.getNodeName());
        NodeList children = root.getChildNodes();       
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals("age")) {
                this.assertNotNull("Person age missing", child.getFirstChild());
                this.assertEquals("Person age wrong", "35", child.getFirstChild().getNodeValue().trim());
            } else if (child.getNodeName().equals("name")) {
                this.assertNotNull("Person name missing", child.getFirstChild());
                this.assertEquals("Person name wrong", "John Smith", child.getFirstChild().getNodeValue().trim());
            } else {
                if (!child.getNodeName().equals("#text")) {
                    this.fail("Unknown attribute " + child.getNodeName());
                }
            }
        }
    }       
        
    public static Test suite() {
        return new TestSuite(TestSAXBeanWriter.class);
    }    
    
    public static void main(String[] args) {
        TestRunner.run(suite());
    }
}
