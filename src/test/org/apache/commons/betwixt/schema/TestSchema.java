/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/schema/TestSchema.java,v 1.9.2.1 2004/01/13 21:49:47 rdonkin Exp $
 * $Revision: 1.9.2.1 $
 * $Date: 2004/01/13 21:49:47 $
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
 
package org.apache.commons.betwixt.schema;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.registry.DefaultXMLBeanInfoRegistry;
import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;

//import org.apache.commons.logging.impl.SimpleLog;
//import org.apache.commons.betwixt.io.BeanRuleSet;

/**
 * This will test betwixt on handling a different kind of xml file, without
 * a "collection" tag.
 * 
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: TestSchema.java,v 1.9.2.1 2004/01/13 21:49:47 rdonkin Exp $
 */
public class TestSchema extends AbstractTestCase
{
    
    public static Test suite()
    {
        return new TestSuite(TestSchema.class);
    }

    
    public TestSchema(String testName)
    {
        super(testName);
    }
    
    /**
     * Test the roundtrip with an xml file that doesn't have
     * collection elements, writes it with collection elements
     * and then compares the 2 object, which should end up
     * equal..
     */
    public void testCombinedRoundTrip()
    throws Exception
    {	
//        SimpleLog log = new SimpleLog("[CombinedRoundTrip:BeanRuleSet]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        BeanRuleSet.setLog(log);
        
//        log = new SimpleLog("[CombinedRoundTrip]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
        BeanReader reader = createBeanReader();
        
        PhysicalSchema schema = (PhysicalSchema) reader.parse(
            getTestFileURL("src/test/org/apache/commons/betwixt/schema/schema.xml"));
        StringWriter buffer = new StringWriter();
        write(schema, buffer, true);
        
//        log.debug(buffer.getBuffer().toString());
        
        StringReader in = new StringReader(buffer.getBuffer().toString());
        reader = createBeanReader();
        XMLIntrospector intro = createXMLIntrospector();
        DefaultXMLBeanInfoRegistry registry = new DefaultXMLBeanInfoRegistry();
        intro.setRegistry(registry);
        // we have written the xml file back with element collections,
        // so we have to say to the reader we want to use that now
        // (the default when creating in this test is not to use them)
        intro.setWrapCollectionsInElement(true);
        // first flush the cash, else setting other options, doesn't
        // end up in rereading / mapping the object model.
        registry.flush();
        // set the xmlIntrospector back to the reader
        reader.setXMLIntrospector(intro);
        reader.deregisterBeanClass(PhysicalSchema.class);
        reader.getRules().clear();
        reader.registerBeanClass(PhysicalSchema.class);
        PhysicalSchema schemaSecond = (PhysicalSchema) reader.parse(in);
        buffer.close();
        write(schema,buffer, true);
        assertEquals(schema, schemaSecond);
    }
    /**
     * Tests we can round trip from the XML -> bean -> XML -> bean.
     * It will test if both object are identical.
     * For this to actually work I implemented a details equals in my
     * Beans..
     */
    public void testRoundTripWithoutCollectionElement()
    throws Exception
    {
        BeanReader reader = createBeanReader();
        PhysicalSchema schema = (PhysicalSchema) reader.parse(
            getTestFileURL("src/test/org/apache/commons/betwixt/schema/schema.xml"));
        StringWriter buffer = new StringWriter();
        write(schema, buffer, false);
        StringReader in = new StringReader(buffer.getBuffer().toString());
        PhysicalSchema schemaSecond = (PhysicalSchema) reader.parse(in);
        assertEquals(schemaSecond, schema);
    }
    
    /**
     * Creates a beanReader
     */
    protected BeanReader createBeanReader()
    throws Exception
     {
        BeanReader reader = new BeanReader();
        reader.setXMLIntrospector(createXMLIntrospector());
        // register the class which maps to the root element
        // of the xml file (this depends on the NameMapper used.
        reader.registerBeanClass(PhysicalSchema.class);
        return reader;
    } 
    
    /**
     * Set up the XMLIntroSpector
     */
    protected XMLIntrospector createXMLIntrospector() {
        XMLIntrospector introspector = new XMLIntrospector();

        // set elements for attributes to true
        introspector.setAttributesForPrimitives(true);

        // Since we don't want to have collectionelements 
        // line <DBMSS>, we have to set this to false,
        // since the default is true.
        introspector.setWrapCollectionsInElement(false);

        // We have to use the HyphenatedNameMapper
        // Since we want the names to resolve from eg PhysicalSchema
        // to PHYSICAL_SCHEMA.
        // we pass to the mapper we want uppercase and use _ for name
        // seperation.
        // This will set our ElementMapper.
        introspector.setElementNameMapper(new HyphenatedNameMapper(true, "_"));
        // since our attribute names will use a different 
        // naming convention in our xml file (just all lowercase)
        // we set another mapper for the attributes
        introspector.setAttributeNameMapper(new DecapitalizeNameMapper());

        return introspector;
    }
    
    /**
     * Opens a writer and writes an object model according to the
     * retrieved bean
     */
    private void write(Object bean, Writer out, boolean wrapCollectionsInElement)
    throws Exception
    {
        BeanWriter writer = new BeanWriter(out);
        writer.setWriteEmptyElements( true );
        writer.setXMLIntrospector(createXMLIntrospector());
        // specifies weather to use collection elements or not.
        writer.getXMLIntrospector().setWrapCollectionsInElement(wrapCollectionsInElement);
        // we don't want to write Id attributes to every element
        // we just want our opbject model written nothing more..
        writer.setWriteIDs(false);
        // the source has 2 spaces indention and \n as line seperator.
        writer.setIndent("  ");
        writer.setEndOfLine("\n");
        writer.write(bean);
    }
}

