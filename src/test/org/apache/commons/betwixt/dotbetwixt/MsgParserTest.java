/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/dotbetwixt/Attic/MsgParserTest.java,v 1.1 2003/11/24 01:58:24 mvdb Exp $
 * $Revision: 1.1 $
 * $Date: 2003/11/24 01:58:24 $
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
 package org.apache.commons.betwixt.dotbetwixt;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

import junit.framework.TestCase;
/**
 * Tests the marshalling and unmarshalling of MsgBeans with Betwixt.
 * The problem tested here is that an element without an updater would
 * not process it's attributes correctly even though they had updaters.
 * 
 * @author <a href="mstanley@cauldronsolutions.com">Mike Stanley</a>
 * @version $Id: MsgParserTest.java,v 1.1 2003/11/24 01:58:24 mvdb Exp $
 */
public class MsgParserTest extends TestCase
{
    private static final String XML_PROLOG = "<?xml version='1.0' ?>\n";
    private MsgBean msg;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        msg = new MsgBean();
        msg.setDescription("Some simple descriptive text");
        msg.setToAddress("mike@somewhere.com");
        msg.setFromAddress("debbie@somwhere.com");
        msg.setName("basicMsg");
        msg.setOptionalField1("7-12-99");
        msg.setOptionalField2("true");
        msg.setStatus("sent");
        msg.setType("spam");
    }

    public void testGetAsXml() throws Exception
    {
        String xmlMsg = null;
        xmlMsg = getAsXml(msg);            
        assertNotNull("XML String should not be null", xmlMsg);
        
    }

    public void testParseMsg() throws Exception
    {
        MsgBean newMsg = null;
       // install request marshall/unmarshall
       String xmlMsg = getAsXml(msg);
       newMsg = parseMsg(xmlMsg);

       assertNotNull("new MsgBean should not be null.", newMsg);
       assertEquals( msg.getDescription(), newMsg.getDescription() );
       assertEquals( msg.getFromAddress(), newMsg.getFromAddress() );
       assertEquals( msg.getName(), newMsg.getName() );
       assertEquals( msg.getOptionalField1(), newMsg.getOptionalField1() );
       assertEquals( msg.getOptionalField2(), newMsg.getOptionalField2() );
       assertEquals( msg.getStatus(), newMsg.getStatus() );
       assertEquals( msg.getToAddress(), newMsg.getToAddress() );
       assertEquals( msg.getType(), newMsg.getType() );
    }
    
    /**
     * Returns the bean as an xml string.
     * 
     * @param msg
     * @return
     * @throws Exception
     */
    public static final String getAsXml(MsgBean msg) 
    throws Exception
    {
        StringWriter writer = new StringWriter();

        // Betwixt just writes out the bean as a fragment
        // we want well-formed xml, we need to add the prolog
        writer.write(XML_PROLOG);

        // Create a BeanWriter which writes to our prepared stream
        BeanWriter beanWriter = new BeanWriter(writer);

        // Configure betwixt
        // For more details see java docs or later in the main documentation
        beanWriter.getXMLIntrospector().setAttributesForPrimitives(true);
        beanWriter.setWriteIDs(false);
        beanWriter.enablePrettyPrint();

        // Write example bean as base element 'person'
        beanWriter.write("message", msg);
        beanWriter.flush();

        return writer.toString();
    }
    
    /**
     * Parses the passed in message xml
     * 
     * @param xmlMessage
     * @return
     * @throws Exception
     */
    public static final MsgBean parseMsg(String xmlMessage)
        throws Exception
    {
        MsgBean msg = null;
        BeanReader beanReader = new BeanReader();
        // Configure the reader
        beanReader.getXMLIntrospector().setAttributesForPrimitives(true);
        // Register beans so that betwixt knows what the xml is 
        beanReader.registerBeanClass("message", MsgBean.class);
        StringReader stringReader = new StringReader(xmlMessage);
        return  (MsgBean) beanReader.parse(stringReader);
    }
    
    

}
