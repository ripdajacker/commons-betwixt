/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/io/read/TestMappingActions.java,v 1.1.2.1 2004/01/13 22:08:04 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/01/13 22:08:04 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * $Id: TestMappingActions.java,v 1.1.2.1 2004/01/13 22:08:04 rdonkin Exp $
 */
package org.apache.commons.betwixt.io.read;

import java.io.StringReader;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanReader;

/** 
 * Test harness for Mapping Actions.
 * 
 * @author Robert Burrell Donkin
 * @version $Id: TestMappingActions.java,v 1.1.2.1 2004/01/13 22:08:04 rdonkin Exp $
 */
public class TestMappingActions extends AbstractTestCase {


    public TestMappingActions(String name) {
        super(name);
    }
        
    public static Test suite() {
        return new TestSuite(TestMappingActions.class);
    }    
    
    public void testSimpleRead() throws Exception {
    
        String xml="<?xml version='1.0'?><AddressBean><street>1 Main Street</street><city>New Town</city>"
                + "<code>NT1 1AA</code><country>UK</country></AddressBean>";
                
        //SimpleLog log = new SimpleLog("[test]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        //BeanRuleSet.setLog(log);
        BeanReader reader = new BeanReader();
        reader.registerBeanClass(AddressBean.class);
        AddressBean address = (AddressBean) reader.parse(new StringReader(xml));
        
        assertFalse("Address is mapped", address == null);
        assertEquals("Street", "1 Main Street", address.getStreet());
        assertEquals("City", "New Town", address.getCity());
        assertEquals("Code", "NT1 1AA", address.getCode());
        assertEquals("Country", "UK", address.getCountry());
    }
    
    public void testPrimitiveCollective() throws Exception{
    
        String xml="<?xml version='1.0'?><SimpleStringCollective><strings>"
                    + "<string>one</string><string>two</string><string>three</string>"
                    + "</strings></SimpleStringCollective>";
                
        //SimpleLog log = new SimpleLog("[test]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
       // BeanRuleSet.setLog(log);
        BeanReader reader = new BeanReader();
        reader.registerBeanClass(SimpleStringCollective.class);
        SimpleStringCollective collective = (SimpleStringCollective) reader.parse(new StringReader(xml));
        
        assertFalse("SimpleStringCollective mapped", collective == null);
        List strings = collective.getStrings();
        assertEquals("String count", 3, strings.size());
        assertEquals("First string", "one", strings.get(0));
        assertEquals("Second string", "two", strings.get(1));
        assertEquals("Third string", "three", strings.get(2));
    }
    

    
    public void testBodyUpdateActionNoMatch() throws Exception {
        AddressBean bean = new AddressBean();
        bean.setStreet("DEFAULT");
        bean.setCode("DEFAULT");
        bean.setCountry("DEFAULT");
        
        XMLIntrospector introspector = new XMLIntrospector();
        ElementDescriptor elementDescriptor = introspector.introspect(AddressBean.class).getElementDescriptor();
        
        ReadContext context = new ReadContext(new BindingConfiguration(), new ReadConfiguration());
        context.setBean(bean);
        context.markClassMap(AddressBean.class);
        context.pushElement("NoMatch");
        context.setXMLIntrospector(introspector);
        BodyUpdateAction action = new BodyUpdateAction();
        action.body("Street value", context);
        assertEquals("Street is unset", "DEFAULT", bean.getStreet());
        assertEquals("Country is unset", "DEFAULT", bean.getCountry());
        assertEquals("Code is unset", "DEFAULT", bean.getCode());
    }
    
    
    public void testBodyUpdateActionMatch() throws Exception {
        AddressBean bean = new AddressBean();
        bean.setStreet("DEFAULT");
        bean.setCode("DEFAULT");
        bean.setCountry("DEFAULT");
        
        XMLIntrospector introspector = new XMLIntrospector();
        ReadContext context = new ReadContext(new BindingConfiguration(), new ReadConfiguration());
        context.pushBean(bean);
        context.markClassMap(AddressBean.class);
        context.pushElement("street");
        context.setXMLIntrospector(introspector);
        BodyUpdateAction action = new BodyUpdateAction();
        action.body("Street value", context);
        assertEquals("Street is set", "Street value", bean.getStreet());
        assertEquals("Country is unset", "DEFAULT", bean.getCountry());
        assertEquals("Code is unset", "DEFAULT", bean.getCode());
    } 
    
    public void testCollection() throws Exception {
        String xml = "<?xml version='1.0'?>"
                + "<elements><element><value>alpha</value></element></elements>";
        StringReader in = new StringReader(xml);
        BeanReader reader = new BeanReader();
        reader.registerBeanClass(Elements.class);
        Elements result = (Elements) reader.parse(in);
        assertNotNull("Element alpha exists", result.getElement("alpha"));
    }
}
