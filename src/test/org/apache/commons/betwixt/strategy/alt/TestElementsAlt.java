/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/strategy/alt/TestElementsAlt.java,v 1.4 2003/10/05 13:52:01 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/10/05 13:52:01 $
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

package org.apache.commons.betwixt.strategy.alt;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.strategy.CapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.DefaultNameMapper;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;
import org.apache.commons.betwixt.strategy.NameMapper;


/** 
 * Tests streaming/destreaming of an <code>Elements</code> bean, 
 * a container for <code>Element</code> instances, using various name mappers
 * The objective of this is to verify that containers whose names
 * are plurals of their contents can be written and read back successfully.
 * 
 * @author <a href="mailto:tima@intalio.com">Tim Anderson</a>
 */
public class TestElementsAlt extends TestCase {

//    private SimpleLog testLog;

    public TestElementsAlt(String name) {
        super(name);
//        testLog = new SimpleLog("[TestElementsAlt]");
//        testLog.setLevel(SimpleLog.LOG_LEVEL_TRACE);
    }

    public void testCapitalizeNameMapper() throws Exception {
//        testLog.debug("Testing capitalize name mapper");
        doTest(new CapitalizeNameMapper(), "capitalize name mapper");
    }

    public void testDecapitalizeNameMapper() throws Exception {
//        testLog.debug("Testing decapitalize name mapper");
        doTest(new DecapitalizeNameMapper(), "decapitalize name mapper");
    }

    public void testDefaultElementMapper() throws Exception {
//        testLog.debug("Testing default name mapper");
        doTest(new DefaultNameMapper(), "default name mapper");
    }

    public void testHyphenatedNameMapper() throws Exception {
//        testLog.debug("Testing hyphenated name mapper");
        doTest(new HyphenatedNameMapper(), "hyphenated name mapper");
    }

    private void doTest(NameMapper mapper, String testName) throws Exception {
        Elements elements = new Elements();
        elements.addElement(new Element("a"));
        elements.addElement(new Element("b"));
        elements.addElement(new Element("c"));

        StringWriter out = new StringWriter();
        BeanWriter writer = newBeanWriter(out, mapper);
        writer.setWriteEmptyElements( true );
        writer.write(elements);
        writer.flush();
        
        String xmlOut = out.toString();
        
//        testLog.debug(xmlOut);

        StringReader in = new StringReader(xmlOut);
        
//        SimpleLog log = new SimpleLog("[TestElementsAlt:BeanReader]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
        BeanReader reader = new BeanReader();
//        reader.setLog(log);

//        log = new SimpleLog("[TestElementsAlt:BeanReader]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        BeanCreateRule.setLog(log);
        
        reader.setXMLIntrospector(newXMLIntrospector(mapper));
        reader.registerBeanClass(Elements.class);
        Elements result = (Elements) reader.parse(in);

        assertNotNull("Element 'a' is null (" + testName + ")", result.getElement("a"));
        assertNotNull("Element 'b' is null (" + testName + ")", result.getElement("b"));
        assertNotNull("Element 'c' is null (" + testName + ")", result.getElement("c"));
    }

    private BeanWriter newBeanWriter(Writer writer, NameMapper mapper) {
//        SimpleLog log = new SimpleLog("[TestElementsAlt:BeanWriter]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
        BeanWriter result = new BeanWriter(writer);
        result.setWriteEmptyElements( true );
//        result.setLog(log);
        
//        log = new SimpleLog("[TestElementsAlt:AbstractBeanWriter]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        result.setAbstractBeanWriterLog(log);
        
        result.setXMLIntrospector(newXMLIntrospector(mapper));
        result.enablePrettyPrint();
        result.setWriteIDs(false);
        return result;
    }

    private XMLIntrospector newXMLIntrospector(NameMapper mapper) {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.setAttributesForPrimitives(true);
        introspector.setWrapCollectionsInElement(false);
        introspector.setElementNameMapper(mapper);
        return introspector;
    }
}

