/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/recursion/TestRecursion.java,v 1.6 2002/12/30 18:16:48 mvdb Exp $
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
 * $Id: TestRecursion.java,v 1.6 2002/12/30 18:16:48 mvdb Exp $
 */
package org.apache.commons.betwixt.recursion;

import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.io.CyclicReferenceException;


/**
 * This will test the recursive behaviour of betwixt.
 *
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: TestRecursion.java,v 1.6 2002/12/30 18:16:48 mvdb Exp $
 */
public class TestRecursion extends AbstractTestCase
{
    

    
    public TestRecursion(String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        return new TestSuite(TestRecursion.class);
    }
    
    /**
     * This will test reading a simple recursive xml file
     * 
     */
    public void testReadwithCollectionsInElementRoundTrip()
    throws Exception
    {
        XMLIntrospector intro = createXMLIntrospector();
        //((SimpleLog) intro.getLog()).setLevel(SimpleLog.LOG_LEVEL_TRACE);
        intro.setWrapCollectionsInElement(true);
        BeanReader reader = new BeanReader();
        reader.registerBeanClass(ElementBean.class);
        reader.setXMLIntrospector(intro);
        Object object = reader.parse(getTestFileURL("src/test/org/apache/commons/betwixt/recursion/recursion.xml"));
        StringWriter buffer = new StringWriter();
        write (object, buffer, true);
        System.out.println("buffer : "+buffer);
    }
    /**
     * This will test reading a simple recursive xml file
     */
    public void testReadWithoutCollectionsInElementRoundTrip()
    throws Exception
    {
        System.out.println("\ntestReadWithoutCollectionsInElement()\n");
        XMLIntrospector intro = createXMLIntrospector();
        BeanReader reader = new BeanReader();
        reader.registerBeanClass(ElementBean.class);
        reader.setXMLIntrospector(intro);
        Object object = reader.parse(getTestFileURL("src/test/org/apache/commons/betwixt/recursion/recursion2.xml"));
        StringWriter buffer = new StringWriter();
        write (object, buffer, false);
        System.out.println("buffer : "+buffer);
    }
    
    /**
     * Opens a writer and writes an object model according to the
     * retrieved bean
     */
    private void write(Object bean, Writer out, boolean wrapIt)
    throws Exception
    {
        BeanWriter writer = new BeanWriter(out);
        writer.setXMLIntrospector(createXMLIntrospector());
        // specifies weather to use collection elements or not.
        writer.getXMLIntrospector().setWrapCollectionsInElement(wrapIt);
        // we don't want to write Id attributes to every element
        // we just want our opbject model written nothing more..
        writer.setWriteIDs(false);
        // the source has 2 spaces indention and \n as line seperator.
        writer.setIndent("  ");
        writer.setEndOfLine("\n");
        writer.write(bean);
    }
    /**
     * Set up the XMLIntroSpector
     */
    protected XMLIntrospector createXMLIntrospector() {
        XMLIntrospector introspector = new XMLIntrospector();

        // set elements for attributes to true
        introspector.setAttributesForPrimitives(true);
        introspector.setWrapCollectionsInElement(false);
        
        return introspector;
    }
    

    /**
     */
    public void testBeanWithIdProperty() throws Exception
    {
        IdBean bean = new IdBean("Hello, World");
        BeanWriter writer = new BeanWriter();
        writer.getXMLIntrospector().setAttributesForPrimitives(true);
        writer.setWriteIDs(true);
        writer.write(bean);
    }    
    
    /**
     * Check that a cyclic reference exception is not thrown in this case 
     */
    public void testCyclicReferenceStack1() throws Exception
    {
        Element alpha = new Element("Alpha");
        Element beta = new Element("Beta");
        Element gamma = new Element("Gamma");
        Element epsilon = new Element("Epsilon");
        
        alpha.addElement(beta);
        beta.addElement(gamma);
        gamma.addElement(epsilon);
        alpha.addElement(epsilon);
        
        StringWriter stringWriter = new StringWriter();
        BeanWriter writer = new BeanWriter(stringWriter);
        writer.setWriteIDs(false);
        writer.write(alpha);
    }    

    /**
     * This should throw a cyclic reference
     */
    public void testCyclicReferenceStack2() throws Exception
    {
        Element alpha = new Element("Alpha");
        Element beta = new Element("Beta");
        Element gamma = new Element("Gamma");
        Element epsilon = new Element("Epsilon");
        
        alpha.addElement(beta);
        beta.addElement(gamma);
        gamma.addElement(epsilon);
        epsilon.addElement(beta);
        
        StringWriter stringWriter = new StringWriter();
        BeanWriter writer = new BeanWriter(stringWriter);
        writer.setWriteIDs(false);
        
        //SimpleLog log = new SimpleLog("[testCyclicReferenceStack2:BeanWriter]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        //writer.setLog(log);
        
        //log = new SimpleLog("[testCyclicReferenceStack2:BeanWriter]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        //writer.setAbstractBeanWriterLog(log);
        
        try {
            writer.write(alpha);
            fail("Cycle was not detected!");
            
        } catch (CyclicReferenceException e) {
            // that's what we expected!
        }
    }  
}

