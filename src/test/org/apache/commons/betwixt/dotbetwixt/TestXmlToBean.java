/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/dotbetwixt/TestXmlToBean.java,v 1.1 2003/04/08 13:42:48 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/04/08 13:42:48 $
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
 * $Id: TestXmlToBean.java,v 1.1 2003/04/08 13:42:48 rdonkin Exp $
 */
package org.apache.commons.betwixt.dotbetwixt;

import java.util.List;

import java.io.StringWriter;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.betwixt.digester.ElementRule;
import org.apache.commons.betwixt.digester.XMLIntrospectorHelper;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanRuleSet;
import org.apache.commons.betwixt.xmlunit.XmlTestCase;

import org.apache.commons.logging.impl.SimpleLog;

/** 
  * Test customization of xml to bean mapping using .betwixt files.
  *
  * @author Robert Burrell Donkin
  */
public class TestXmlToBean extends XmlTestCase {

//--------------------------------- Test Suite
    
    public static Test suite() {
        return new TestSuite(TestXmlToBean.class);
    }
    
//--------------------------------- Constructor
        
    public TestXmlToBean(String testName) {
        super(testName);
    }

//---------------------------------- Tests
    
    public void testCustomUpdaters() throws Exception {
        // might as well check writer whilst we're at it
        MixedUpdatersBean bean = new MixedUpdatersBean("Lov");
        bean.badNameSetter("Hate");
        bean.addItem("White");
        bean.badItemAdder("Black");
        bean.addItem("Life");
        bean.badItemAdder("Death");
        
//        SimpleLog log = new SimpleLog("[testCustomUpdaters:XMLIntrospector]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
//        writer.getXMLIntrospector().setLog(log);

//        log = new SimpleLog("[testCustomUpdaters:XMLIntrospectorHelper]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        XMLIntrospectorHelper.setLog(log);
        
        writer.setWriteIDs(false);
        writer.write(bean);

    	String xml = "<?xml version='1.0'?><mixed><name>Lov</name><bad-name>Hate</bad-name>"
          + "<items><item>White</item><item>Life</item></items>"
          + "<bad-items><bad-item>Black</bad-item><bad-item>Death</bad-item></bad-items></mixed>";
        
        xmlAssertIsomorphicContent(
                    parseString(xml),
                    parseString(out.toString()),
                    true);
        
//        SimpleLog log = new SimpleLog("[testCustomUpdaters:XMLIntrospectorHelper]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        XMLIntrospectorHelper.setLog(log);
        
//        log = new SimpleLog("[testCustomUpdaters:BeanRuleSet]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        BeanRuleSet.setLog(log);  

//        log = new SimpleLog("[testCustomUpdaters:ElementRule]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        ElementRule.setLog(log);   
        
        // now we'll test reading via round tripping
        BeanReader reader = new BeanReader();
        reader.setMatchIDs(false);
        reader.registerBeanClass("mixed", MixedUpdatersBean.class);
        bean = (MixedUpdatersBean) reader.parse(new StringReader(xml));
        
        assertEquals("Name incorrect", "Lov", bean.getName());
        assertEquals("BadName incorrect", "Hate", bean.getBadName());
        List items = bean.getItems();
        assertEquals("Wrong number of items", 2, items.size());
        assertEquals("Item one wrong", "White", items.get(0));
        assertEquals("Item two wrong", "Life", items.get(1));
        List badItems = bean.getBadItems();
        assertEquals("Wrong number of bad items", 2, badItems.size());
        assertEquals("Bad item one wrong", "Black", badItems.get(0));
        assertEquals("Bad item two wrong", "Death", badItems.get(1));       
        
    }
    
}
