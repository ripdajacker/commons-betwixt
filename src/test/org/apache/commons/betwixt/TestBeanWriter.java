/*
 * $Header: /home/cvs/jakarta-commons-sandbox/betwixt/src/test/org/apache/commons/betwixt/TestBeanWriter.java,v 1.13 2002/05/17 15:24:10 jstrachan Exp $
 * $Revision: 1.13 $
 * $Date: 2002/05/17 15:24:10 $
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
 * $Id: TestBeanWriter.java,v 1.13 2002/05/17 15:24:10 jstrachan Exp $
 */
package org.apache.commons.betwixt;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.io.CyclicReferenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.SimpleLog;


/** Test harness for the BeanWriter
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
  * @version $Revision: 1.13 $
  */
public class TestBeanWriter extends AbstractTestCase {
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestBeanWriter.class);
    }
    
    public TestBeanWriter(String testName) {
        super(testName);
    }
    
    public void testBeanWriter() throws Exception {
        Object bean = createBean();
        
        System.out.println( "Now trying pretty print" );
        
        BeanWriter writer = new BeanWriter();
        writer.enablePrettyPrint();
        writer.write( bean );
    }
    
    
    public void testLooping() throws Exception {
        BeanWriter writer = new BeanWriter();
        
        // logging for debugging jsut this method 
//        SimpleLog log = new SimpleLog("[testLooping:BeanWriter]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        writer.setLog(log);
        
//        log = new SimpleLog("[testLooping:AbstractBeanWriter]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        writer.setAbstractBeanWriterLog(log);
        
//        log = new SimpleLog("[testLooping]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
        writer.enablePrettyPrint();
        writer.write( LoopBean.createNoLoopExampleBean() );    
        writer.write( LoopBean.createLoopExampleBean() );   
        
        // test not writing IDs
        writer.setWriteIDs(false);
        
//        log.info("Writing LoopBean.createNoLoopExampleBean...");
        
        writer.write( LoopBean.createNoLoopExampleBean() );
        
//        log.info("Writing LoopBean.createIdOnlyLoopExampleBean...");
        
        writer.write( LoopBean.createIdOnlyLoopExampleBean() );
        
        try {   
//            log.info("Writing LoopBean.createLoopExampleBean...");
            writer.write( LoopBean.createLoopExampleBean() );   
            fail("CyclicReferenceException not thrown!");
            
        } catch (CyclicReferenceException e) {
            // everything's fine
        }
    }
    
    public void testEscaping() throws Exception {
        //XXX find a way to automatically verify test
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BeanWriter writer = new BeanWriter(out);
        writer.enablePrettyPrint(); 
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.setAttributesForPrimitives(true);
        writer.setXMLIntrospector(introspector);
        writer.write(new LoopBean("Escape<LessThan"));
        writer.write(new LoopBean("Escape>GreaterThan"));
        writer.write(new LoopBean("Escape&amphersand"));
        writer.write(new LoopBean("Escape'apostrophe"));
        writer.write(new LoopBean("Escape\"Quote"));
        
        CustomerBean bean = new CustomerBean();
        bean.setEmails( new String[] { 
                                        "Escape<LessThan",
                                        "Escape>GreaterThan",
                                        "Escape&amphersand",
                                        "Escape'apostrophe",
                                        "Escape\"Quote"} );
                                        
        // The attribute value escaping needs test too..
        bean.setName("Escape<LessThan");
        AddressBean address = new AddressBean();
        address.setCode("Escape>GreaterThan");
        address.setCountry("Escape&amphersand");
        address.setCity("Escape'apostrophe");
        address.setStreet("Escape\"Quote");
        bean.setAddress(address);
        
        writer.write(bean);
        out.flush();
        String result = out.toString();
        
        System.out.println( "Created..." );
        System.out.println( result );
        
        // check for the elemant content..
        assertTrue(result.indexOf("<email>Escape&lt;LessThan</email>") > -1 );
        assertTrue(result.indexOf("<email>Escape&gt;GreaterThan</email>") > -1);
        assertTrue(result.indexOf("<email>Escape&amp;amphersand</email>") != -1);
        assertTrue(result.indexOf("<email>Escape'apostrophe</email>") != -1);
        assertTrue(result.indexOf("<email>Escape\"Quote</email>") != -1);
        // check for the attributes..
        assertTrue(result.indexOf("name=\"Escape&lt;LessThan\"") > -1 );
        assertTrue(result.indexOf("code=\"Escape&gt;GreaterThan\"") > -1);
        assertTrue(result.indexOf("country=\"Escape&amp;amphersand\"") != -1);
        assertTrue(result.indexOf("city=\"Escape&apos;apostrophe\"") != -1);
        assertTrue(result.indexOf("street=\"Escape&quot;Quote\"") != -1);
    }
    /**
     * Testing valid endofline characters.
     * It tests if there is a warning on System.err
     */
    public void testValidEndOfLine() throws Exception {
        BeanWriter writer = new BeanWriter();
        
        // store the system err
        PrintStream errStream = System.err;
        ByteArrayOutputStream warning = new ByteArrayOutputStream();
        System.setErr(new PrintStream(warning));
        
        // force logging to go to System.err
        writer.setLog( new SimpleLog( "test.betwixt" ) );
        
        
        writer.setEndOfLine("X");
        warning.flush();
        assertTrue(warning.toString().startsWith("[WARN]"));
        warning.reset();
        writer.setEndOfLine("\tX");
        warning.flush();
        assertTrue(warning.toString().startsWith("[WARN]"));
        warning.reset();
        // now test a valid value..
        writer.setEndOfLine(" ");
        warning.flush();
        assertTrue(warning.toString().equals(""));
        warning.reset();
        // set the System.err back again..
        System.setErr(errStream);
    }
}

