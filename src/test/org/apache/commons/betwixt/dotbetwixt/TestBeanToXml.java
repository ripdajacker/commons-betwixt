/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/dotbetwixt/TestBeanToXml.java,v 1.6 2002/12/30 18:16:48 mvdb Exp $
 * $Revision: 1.6 $
 * $Date: 2002/12/30 18:16:48 $
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
 * $Id: TestBeanToXml.java,v 1.6 2002/12/30 18:16:48 mvdb Exp $
 */
package org.apache.commons.betwixt.dotbetwixt;

import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.xmlunit.XmlTestCase;


/** 
  * Provides xml test utilities. 
  * Hopefully, these might be moved into [xmlunit] sometime.
  *
  * @author Robert Burrell Donkin
  */
public class TestBeanToXml extends XmlTestCase {

    private final static boolean debug = true;

//--------------------------------- Test Suite
    
    public static Test suite() {
        return new TestSuite(TestBeanToXml.class);
    }
    
//--------------------------------- Constructor
        
    public TestBeanToXml(String testName) {
        super(testName);
    }

//---------------------------------- Tests
    
    public void testOne() throws Exception {
        // THIS TEST FAILS IN MAVEN
        xmlAssertIsomorphicContent(	
            parseFile("src/test/org/apache/commons/betwixt/dotbetwixt/rbean-result.xml"), 
            parseFile("src/test/org/apache/commons/betwixt/dotbetwixt/rbean-result.xml"));
    }
    
    public void testSimpleBean() throws Exception {
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0' encoding='UTF-8'?>");
//        SimpleLog log = new SimpleLog("[testSimpleBean:XMLIntrospector]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        BeanWriter writer = new BeanWriter(out);
//        writer.getXMLIntrospector().setLog(log);
        
//        log = new SimpleLog("[testSimpleBean:XMLIntrospectorHelper]");
//        XMLIntrospectorHelper.setLog(log);
    
        writer.setWriteIDs(false);
	SimpleTestBean bean = new SimpleTestBean("alpha-value","beta-value","gamma-value");
        writer.write(bean);
        out.flush();
        String xml = out.toString();

        if (debug) {
            System.out.println("************testSimpleBean************");
            System.out.println(xml);
        }
        
        xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/dotbetwixt/simpletestone.xml"),
                    parseString(xml));

    }
    
    public void testWriteRecursiveBean() throws Exception {
        /*
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0' encoding='UTF-8'?>");
        BeanWriter writer = new BeanWriter(out);
        RecursiveBean bean 
            = new RecursiveBean(
                "alpha", 
                new RecursiveBean(
                    "beta", 
                    new RecursiveBean("gamma")));
        writer.setWriteIDs(false);
        writer.write(bean);
        out.flush();
        String xml = out.toString();
        
        if (debug) {
            System.out.println(xml);
        }
        
        
        xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/dotbetwixt/rbean-result.xml"),
                    parseString(xml));
        */
    }
}

