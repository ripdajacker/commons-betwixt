/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: TestBeanReader.java,v 1.7 2002/05/28 23:01:08 jstrachan Exp $
 */
package org.apache.commons.betwixt;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.SimpleLog;


/** Test harness for the BeanReader
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.7 $
  */
public class TestBeanReader extends AbstractTestCase {
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestBeanReader.class);
    }
    
    public TestBeanReader(String testName) {
        super(testName);
    }
    
    public void testBeanWriter() throws Exception {
        BeanReader reader = new BeanReader();
        reader.registerBeanClass( getBeanClass() );

        InputStream in = getXMLInput();
        try {
            Object bean = reader.parse( in );

            System.out.println( "Read bean: " + bean );
            System.out.println();
            System.out.println( "Lets turn it back into XML" );
            
            writeBean( bean );
        }
        finally {
            in.close();
        }
    }
    
    public void writeBean(Object bean) throws Exception {
        BeanWriter writer = new BeanWriter();
        writer.enablePrettyPrint();
        writer.write( bean );
    }
    
    /** @return the bean class to use as the root */
    public Class getBeanClass() {
        return CustomerBean.class;
    }
    
    protected InputStream getXMLInput() throws IOException {
        return new FileInputStream( getTestFile("src/test/org/apache/commons/betwixt/customer.xml") );
    }
    
    public void testWriteThenRead() throws Exception
    {
        // test defaults
        PersonBean bean = new PersonBean(21, "Samual Smith");
        StringWriter stringWriter = new StringWriter();
        BeanWriter beanWriter = new BeanWriter(stringWriter);
        beanWriter.write(bean);
        stringWriter.flush();
        String xml = "<?xml version='1.0'?>" + stringWriter.toString();
        
        BeanReader reader = new BeanReader();
        reader.registerBeanClass( PersonBean.class );
        bean = (PersonBean) reader.parse(new StringReader(xml));
        
        assertEquals("Person age wrong", 21 , bean.getAge());
        assertEquals("Person name wrong", "Samual Smith" , bean.getName());
        
        // test now with attributes for primitives
        bean = new PersonBean(19, "John Smith");
        stringWriter = new StringWriter();
        beanWriter = new BeanWriter(stringWriter);
        beanWriter.getXMLIntrospector().setAttributesForPrimitives(true);
        beanWriter.write(bean);
        stringWriter.flush();
        xml = "<?xml version='1.0'?>" + stringWriter.toString();
        
        reader = new BeanReader();
        reader.getXMLIntrospector().setAttributesForPrimitives(true);
        reader.registerBeanClass( PersonBean.class );
        bean = (PersonBean) reader.parse(new StringReader(xml));
        
        assertEquals("[Attribute] Person age wrong", 19 , bean.getAge());
        assertEquals("[Attribute] Person name wrong", "John Smith" , bean.getName());
    }
}

