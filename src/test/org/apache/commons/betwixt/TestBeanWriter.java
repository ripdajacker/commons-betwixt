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

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.io.CyclicReferenceException;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.commons.betwixt.strategy.CapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;

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
        writer.setWriteEmptyElements(true);
        writer.enablePrettyPrint();
        writer.write( bean );
    }
    
    
    public void testLooping() throws Exception {
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.setWriteEmptyElements( true );
        
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
        
        String xml ="<?xml version='1.0'?><LoopBean><name>Root</name><friend><name>level1</name>"
                + "<friend><name>level2</name><friend><name>level3</name><friend><name>level4</name>"
                + "<friend><name>level5</name></friend></friend></friend></friend></friend></LoopBean>";
      
        xmlAssertIsomorphicContent(
                            "Test no loop",
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);        
        
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        writer = new BeanWriter(out);
        writer.setWriteEmptyElements( true );
        writer.write( LoopBean.createLoopExampleBean() );  
        xml ="<?xml version='1.0'?><LoopBean><name>Root</name><friend><name>level1</name>"
                + "<friend><name>level2</name><friend><name>level3</name><friend><name>level4</name>"
                + "<friend><name>level5</name><friend idref='1'/></friend></friend></friend>"
                + "</friend></friend></LoopBean>";
        xmlAssertIsomorphicContent(
                            "Test loop",
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);  
        
        // test not writing IDs

        
//        log.info("Writing LoopBean.createNoLoopExampleBean...");
        
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        writer = new BeanWriter(out);
        writer.setWriteEmptyElements( true );
        writer.setWriteIDs(false);
        writer.write( LoopBean.createNoLoopExampleBean() );
        xml ="<?xml version='1.0'?><LoopBean><name>Root</name><friend><name>level1</name><friend>"
            + "<name>level2</name><friend><name>level3</name><friend><name>level4</name><friend>"
            + "<name>level5</name></friend></friend>"
            + "</friend></friend></friend></LoopBean>";
                
        xmlAssertIsomorphicContent(	
                            "Test no loop, no ids",
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true); 
        
//        log.info("Writing LoopBean.createIdOnlyLoopExampleBean...");
        
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        writer = new BeanWriter(out);
        writer.setWriteEmptyElements( true );
        writer.setWriteIDs(false);
        writer.write( LoopBean.createIdOnlyLoopExampleBean() );
        xml = "<?xml version='1.0'?><LoopBean><name>Root</name><friend><name>level1</name>"
            + "<friend><name>level2</name><friend><name>level3</name><friend><name>level4</name>"
            + "<friend><name>level5</name><friend><name>Root</name></friend></friend>"
            + "</friend></friend></friend></friend></LoopBean>";
                
        xmlAssertIsomorphicContent(	
                            "Test id only loop",
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true); 
        
        try {   
//            log.info("Writing LoopBean.createLoopExampleBean...")
            out = new StringWriter();
            out.write("<?xml version='1.0'?>");
            writer = new BeanWriter(out);
            writer.setWriteEmptyElements( true );
            writer.setWriteIDs(false);
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
        writer.setWriteEmptyElements( true );
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
        String result = "<?xml version='1.0'?><beans>" + out.toString() + "</beans>";
        
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
        
        String xml="<?xml version='1.0'?><beans>  <LoopBean name='Escape&lt;LessThan'/>"
            + "<LoopBean name='Escape&gt;GreaterThan'/><LoopBean name='Escape&amp;amphersand'/>"
            + "<LoopBean name='Escape&apos;apostrophe'/><LoopBean name='Escape&quot;Quote'/>"
            + "<CustomerBean name='Escape&lt;LessThan' time='' date='' bigDecimal='' "
            + "bigInteger='' timestamp='' ID='' nickName=''>"
            + "<projectMap/><projectNames/><emails><email>Escape&lt;LessThan</email>"
            + "<email>Escape&gt;GreaterThan</email><email>Escape&amp;amphersand</email>"
            + "<email>Escape'apostrophe</email><email>Escape\"Quote</email></emails>"
            + "<locations/><projectURLs/>"
            + "<address code='Escape&gt;GreaterThan' city='Escape&apos;apostrophe' "
            + "country='Escape&amp;amphersand' street='Escape&quot;Quote'/>"
            + "<numbers/></CustomerBean></beans>";
            
        xmlAssertIsomorphicContent(
                            "Test escaping ",
                            parseString(result),
                            parseString(xml), 
                            true);  
    }
    /**
     * Testing valid endofline characters.
     * It tests if there is a warning on System.err
     */
    public void testValidEndOfLine() throws Exception {
        BeanWriter writer = new BeanWriter();
        writer.setWriteEmptyElements(true);
        
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
    
    /** Test simplest case for writing empty elements */
    public void testSimpleWriteEmptyElements() throws Exception{
        // use same bean for both tests
        AddressBean bean = new AddressBean();
        bean.setStreet("Pasture Lane");
        bean.setCity("Bradford");
        
//        SimpleLog log = new SimpleLog( "[SimpleEmpty:AbstractBeanWriter]" );
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
//        SimpleLog baseLog = new SimpleLog( "[SimpleEmpty]" );
//        baseLog.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
        // test output when writing empty elements
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.setWriteIDs(false);
        writer.write(bean);
//        baseLog.debug("SIMPLE EMPTY");
//        baseLog.debug(out.getBuffer().toString());
        String xml = "<?xml version='1.0'?><AddressBean><street>Pasture Lane</street><city>Bradford</city>"
                    + "<code/><country/></AddressBean>";
//        baseLog.debug(xml);

        xmlAssertIsomorphicContent(parseString(out.getBuffer().toString()),parseString(xml), true);
        
        // test output when not writing empty elements
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        writer = new BeanWriter(out);
        writer.setWriteEmptyElements(false);
        writer.setWriteIDs(false);
//        writer.setAbstractBeanWriterLog(log);
        writer.write(bean);
        xml = "<?xml version='1.0'?><AddressBean><street>Pasture Lane</street><city>Bradford</city>"
                    + "</AddressBean>";
//        baseLog.debug("SIMPLE NOT EMPTY");
//        baseLog.debug(out.getBuffer().toString());
        xmlAssertIsomorphicContent(parseString(out.getBuffer().toString()),parseString(xml), true);
    }
    
    /** Test nested case for writing empty elements */
    public void testNestedWriteEmptyElements() throws Exception{
        
        // write same bean both times
        LoopBean root = new LoopBean("base");
        LoopBean middle = new LoopBean(null);
        root.setFriend(middle);
        middle.setFriend(new LoopBean(null));
        
//        SimpleLog baseLog = new SimpleLog( "[NestedEmpty]" );
//        baseLog.setLevel(SimpleLog.LOG_LEVEL_TRACE);
    
        // test output when writing empty elements
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.setWriteIDs(false);
        writer.write(root);
//        baseLog.debug("NESTED EMPTY");
//        baseLog.debug(out.getBuffer().toString());
        String xml = "<?xml version='1.0'?><LoopBean><name>base</name><friend><name/><friend><name/></friend>"
                    + "</friend></LoopBean>";
        xmlAssertIsomorphicContent(parseString(out.getBuffer().toString()),parseString(xml), true);
        
        // test output when not writing empty elements
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        writer = new BeanWriter(out);
        writer.setWriteEmptyElements(false);
        writer.setWriteIDs(false);
        writer.write(root);
//        baseLog.debug("NESTED NOT EMPTY");
//        baseLog.debug(out.getBuffer().toString());
        xml = "<?xml version='1.0'?><LoopBean><name>base</name></LoopBean>";
        xmlAssertIsomorphicContent(parseString(out.getBuffer().toString()),parseString(xml), true);
        
    }
    
    public void testArrayWrite() throws Exception {
        ArrayBean bean = new ArrayBean("Rob");
        bean.addHobby("Hacking open source software");
        bean.addHobby("Playing cricket");
        bean.addHobby("Watching rugby league");
        bean.addHobby("Havin' it large");
    
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.setWriteIDs(false);
        writer.write(bean);
        
        String xml = "<?xml version='1.0'?><ArrayBean><name>Rob</name><hobbies>"
         + "<hobby>Hacking open source software</hobby>" 
         + "<hobby>Playing cricket</hobby>" 
         + "<hobby>Watching rugby league</hobby>" 
         + "<hobby>Havin' it large</hobby>"
         +"</hobbies></ArrayBean>";
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
                            
        String [] array = {"This", "That", "The Other"};
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.setWriteIDs(false);
        writer.write(array);
        
        xml = "<?xml version='1.0'?><Array>"
         + "<String>This</String>" 
         + "<String>That</String>" 
         + "<String>The Other</String>" 
         +"</Array>";
         
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
    }
    
    
    /** Test nested case for writing empty elements */
    public void testListedWriteEmptyElements() throws Exception {
        ListOfNames names = new ListOfNames();
        names.addName(new NameBean("Tom"));
        names.addName(new NameBean("Dick"));
        names.addName(new NameBean("Harry"));
        names.addName(new NameBean(""));
    
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");

        BeanWriter writer = new BeanWriter(out);
        
        //SimpleLog log = new SimpleLog("[testListedWriteEmptyElements:AbstractBeanWriter]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        //writer.setAbstractBeanWriterLog(log);
        
        //log = new SimpleLog("[testListedWriteEmptyElements:XMLIntrospector]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        //writer.getXMLIntrospector().setLog(log);
        
        //log = new SimpleLog("[testListedWriteEmptyElements:XMLIntrospectorHelper]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        //XMLIntrospectorHelper.setLog(log);
        
        writer.setWriteEmptyElements(false);
        writer.getXMLIntrospector().setWrapCollectionsInElement(false);
        writer.setWriteIDs(false);
        writer.write("Names", names);
        
        String xml = "<?xml version='1.0'?><Names>"
         + "<name><name>Tom</name></name>" 
         + "<name><name>Dick</name></name>" 
         + "<name><name>Harry</name></name>" 
         +"</Names>";
         
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
                            
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");

        writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.getXMLIntrospector().setWrapCollectionsInElement(false);
        writer.setWriteIDs(false);
        writer.write("Names", names);
        
        xml = "<?xml version='1.0'?><Names>"
         + "<name><name>Tom</name></name>" 
         + "<name><name>Dick</name></name>" 
         + "<name><name>Harry</name></name>" 
         + "<name><name/></name>"
         +"</Names>";
         
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
                            
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");

        writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.getXMLIntrospector().setWrapCollectionsInElement(true);
        writer.setWriteIDs(false);
        writer.write("Names", names);
        
        xml = "<?xml version='1.0'?><Names><names>"
         + "<name><name>Tom</name></name>" 
         + "<name><name>Dick</name></name>" 
         + "<name><name>Harry</name></name>" 
         + "<name><name/></name></names>"
         +"</Names>";
         
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
                            
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");

        writer = new BeanWriter(out);
        writer.setWriteEmptyElements(false);
        writer.getXMLIntrospector().setWrapCollectionsInElement(true);
        writer.setWriteIDs(false);
        writer.write("Names", names);
        
        xml = "<?xml version='1.0'?><Names><names>"
         + "<name><name>Tom</name></name>" 
         + "<name><name>Dick</name></name>" 
         + "<name><name>Harry</name></name>" 
         + "</names>"
         +"</Names>";
         
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
                            
    }
    
    public void testWriteNameMapperStrategy() throws Exception {
        ListOfNames names = new ListOfNames();
        names.addName(new NameBean("Sid James"));
        names.addName(new NameBean("Kenneth Williams"));
        names.addName(new NameBean("Joan Simms"));
        names.addName(new NameBean("Charles Hawtrey"));
        
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");

        BeanWriter writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.getXMLIntrospector().setWrapCollectionsInElement(true);
        writer.setWriteIDs(false);
        writer.write("CarryOn", names);
        
        String xml = "<?xml version='1.0'?><CarryOn><names>"
         + "<name><name>Sid James</name></name>" 
         + "<name><name>Kenneth Williams</name></name>" 
         + "<name><name>Joan Simms</name></name>" 
         + "<name><name>Charles Hawtrey</name></name>" 
         + "</names>"
         +"</CarryOn>";
         
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
                            
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");

        writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.getXMLIntrospector().setWrapCollectionsInElement(true);
        writer.setWriteIDs(false);
        writer.getXMLIntrospector().setElementNameMapper(new CapitalizeNameMapper());
        writer.write("CarryOn", names);
        
        xml = "<?xml version='1.0'?><CarryOn><Names>"
         + "<Name><Name>Sid James</Name></Name>" 
         + "<Name><Name>Kenneth Williams</Name></Name>" 
         + "<Name><Name>Joan Simms</Name></Name>" 
         + "<Name><Name>Charles Hawtrey</Name></Name>" 
         + "</Names>"
         +"</CarryOn>";
         
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
                            
        ArrayList things = new ArrayList();
        things.add(new NameBean("Sugar"));
        things.add(new NameBean("Spice"));
        things.add(new NameBean("All Things Nice"));
        
        NoAdderBean bean = new NoAdderBean();
        bean.setThings(things);
        
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.getXMLIntrospector().setWrapCollectionsInElement(true);
        writer.setWriteIDs(false);
        writer.write(bean);
        
        xml = "<?xml version='1.0'?><NoAdderBean><things>"
         + "<NameBean><name>Sugar</name></NameBean>" 
         + "<NameBean><name>Spice</name></NameBean>" 
         + "<NameBean><name>All Things Nice</name></NameBean>" 
         + "</things>"
         +"</NoAdderBean>";
         
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
        
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.getXMLIntrospector().setWrapCollectionsInElement(true);
        writer.setWriteIDs(false);
        writer.getXMLIntrospector().setElementNameMapper(new CapitalizeNameMapper());
        writer.write(bean);
        
        xml = "<?xml version='1.0'?><NoAdderBean><Things>"
         + "<NameBean><Name>Sugar</Name></NameBean>" 
         + "<NameBean><Name>Spice</Name></NameBean>" 
         + "<NameBean><Name>All Things Nice</Name></NameBean>" 
         + "</Things>"
         +"</NoAdderBean>";
         
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
                            
        out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        writer = new BeanWriter(out);
        writer.setWriteEmptyElements(true);
        writer.getXMLIntrospector().setWrapCollectionsInElement(true);
        writer.setWriteIDs(false);
        writer.getXMLIntrospector().setElementNameMapper(new HyphenatedNameMapper(false));
        writer.write(bean);
        
        xml = "<?xml version='1.0'?><no-adder-bean><things>"
         + "<name-bean><name>Sugar</name></name-bean>" 
         + "<name-bean><name>Spice</name></name-bean>" 
         + "<name-bean><name>All Things Nice</name></name-bean>" 
         + "</things>"
         +"</no-adder-bean>";
         
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
    }
    
    public void testBeanWriterWorksWithAnAddMethodAndACollection() throws Exception {

        BeanWriter bw = new BeanWriter();
        try {
            bw.write(new BeanWithAddMethod());
        } catch (IllegalArgumentException e) {
            fail("BeanWriter fails when a method is just called add(<type>) and there is also a collection");
        }
    }
    
    // used in testBeanWriterWorksWithAnAddMethodAndACollection
    public static class BeanWithAddMethod {
        private Collection x;
        public void add(Object x)
        {
            // do nothing
        }

        public Collection getX() {
            return x;
        }

        public void setX(Collection x) {
            this.x = x;
        }        
    }
}

