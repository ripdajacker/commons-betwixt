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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;


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

            testCustomer(bean);
            
            System.out.println( "Read bean: " + bean );
            System.out.println();
            System.out.println( "Lets turn it back into XML" );
            
            writeBean( bean );
        }
        finally {
            in.close();
        }
    }
    
    public void testWriteThenRead() throws Exception {
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

    public void writeBean(Object bean) throws Exception {
        BeanWriter writer = new BeanWriter();
        writer.enablePrettyPrint();
        writer.write( bean );
    }
    
    /** @return the bean class to use as the root */
    public Class getBeanClass() {
        return CustomerBean.class;
    }
    
    /** 
     * Asserts that the parsed CustomerBean looks fine
     */
    protected void testCustomer(Object bean) throws Exception {
        assertTrue( "Is a CustomerBean", bean instanceof CustomerBean );
        CustomerBean customer = (CustomerBean) bean;
     
        assertEquals( "name", "James", customer.getName() );
        
        String[] emails = customer.getEmails();
        assertTrue( "contains some emails", emails != null );
        assertEquals( "emails.length", 2, emails.length );
        assertEquals( "emails[0]", "jstrachan@apache.org", emails[0] );
        assertEquals( "emails[1]", "james_strachan@yahoo.co.uk", emails[1] );
        
        int[] numbers = customer.getNumbers();
        assertTrue( "contains some numbers", numbers != null );
        assertEquals( "numbers.length", 3, numbers.length );
        assertEquals( "numbers[0]", 3, numbers[0] );
        assertEquals( "numbers[1]", 4, numbers[1] );
        assertEquals( "numbers[2]", 5, numbers[2] );
        
        List locations = customer.getLocations();
        assertTrue( "contains some locations", locations != null );
        assertEquals( "locations.size()", 2, locations.size() );
        assertEquals( "locations[0]", "London", locations.get(0) );
        assertEquals( "locations[1]", "Bath", locations.get(1) );
        
        assertEquals( ConvertUtils.convert("2002-03-17", Date.class), customer.getDate());
        assertEquals( ConvertUtils.convert("20:30:40", Time.class), customer.getTime());
        assertEquals( ConvertUtils.convert("2002-03-17 20:30:40.0", Timestamp.class), customer.getTimestamp());

        assertEquals( new BigDecimal("1234567890.12345"), customer.getBigDecimal() );
        assertEquals( new BigInteger("1234567890"), customer.getBigInteger() );
    }
    
    protected InputStream getXMLInput() throws IOException {
        return new FileInputStream( getTestFile("src/test/org/apache/commons/betwixt/customer.xml") );
    }
 
    /** 
     * This tests that you can read a bean which has an adder but not a property
     */ 
    public void testAdderButNoProperty() throws Exception {
        /*
        // 
        // This is a test for an unfixed issue that might - or might not - be a bug
        // a developer submitted a patch but this broke the other unit test
        // a proper fix would require quite a lot of work including some refactoring
        // of various interfaces
        //
        
        // check bean's still working
        AdderButNoPropertyBean bean = new AdderButNoPropertyBean();
        bean.addString("one");
        bean.addString("two");
        bean.addString("three");
        checkBean(bean);
        
        BeanReader reader = new BeanReader();
        reader.registerBeanClass( AdderButNoPropertyBean.class );
        
        InputStream in =  
            new FileInputStream( getTestFile("src/test/org/apache/commons/betwixt/adder-np.xml") );
        try {
        
            checkBean((AdderButNoPropertyBean) reader.parse( in ));
            
        }
        finally {
            in.close();
        }        
        */
    }
    
    private void checkBean(AdderButNoPropertyBean bean) throws Exception {
        assertEquals("Bad addString call count", 3, bean.stringCallCount());
    }
    
    private void checkBean(PersonListBean bean) throws Exception {
        assertEquals("PersonList size", 4, bean.getPersonList().size());
        assertEquals("PersonList value (1)", "Athos", ((PersonBean) bean.getPersonList().get(0)).getName());
        assertEquals("PersonList value (2)", "Porthos", ((PersonBean) bean.getPersonList().get(1)).getName());
        assertEquals("PersonList value (3)", "Aramis", ((PersonBean) bean.getPersonList().get(2)).getName());
        assertEquals("PersonList value (4)", "D'Artagnan", ((PersonBean) bean.getPersonList().get(3)).getName());
    }
    
    public void testPersonList() throws Exception {

        PersonListBean people = new PersonListBean();
        people.addPerson(new PersonBean(22, "Athos"));
        people.addPerson(new PersonBean(25, "Porthos"));
        people.addPerson(new PersonBean(23, "Aramis"));
        people.addPerson(new PersonBean(18, "D'Artagnan"));
        
        checkBean(people);
//
// Logging and debugging code for this method commented out
//
//        writeBean(people);

//        SimpleLog log = new SimpleLog("[TestPersonList:XMLIntrospectorHelper]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        XMLIntrospectorHelper.setLog(log);
        
        
//        log = new SimpleLog("[TestPersonList:BeanCreateRule]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        BeanCreateRule.setLog(log);
        
//        log = new SimpleLog("[TestPersonList:XMLIntrospector]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
        BeanReader reader = new BeanReader();
//        reader.getXMLIntrospector().setLog(log);
              
//        log = new SimpleLog("[TestPersonList:BeanReader]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
//        reader.setLog(log);
        reader.registerBeanClass( PersonListBean.class );
        
        InputStream in =  
            new FileInputStream( getTestFile("src/test/org/apache/commons/betwixt/person-list.xml") );
        try {
        
            checkBean((PersonListBean) reader.parse( in ));
            
        }
        finally {
            in.close();
        }   
    }
}

