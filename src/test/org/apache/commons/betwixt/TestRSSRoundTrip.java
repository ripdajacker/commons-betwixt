/*
 * $Header: /home/cvs/jakarta-commons-sandbox/betwixt/src/test/org/apache/commons/betwixt/TestRSSRoundTrip.java,v 1.5 2002/05/28 11:49:29 jstrachan Exp $
 * $Revision: 1.5 $
 * $Date: 2002/05/28 11:49:29 $
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
 * $Id: TestRSSRoundTrip.java,v 1.5 2002/05/28 11:49:29 jstrachan Exp $
 */
package org.apache.commons.betwixt;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

import org.apache.commons.digester.rss.Channel;
import org.apache.commons.digester.rss.RSSDigester;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.SimpleLog;


/** Test harness which parses an RSS document using Digester
  * then outputs it using Betwixt, then parses it again with Digester
  * to check that the document is parseable again.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.5 $
  */
public class TestRSSRoundTrip extends AbstractTestCase {
    
    /**
     * The set of public identifiers, and corresponding resource names,
     * for the versions of the DTDs that we know about.
     */
    protected static final String registrations[] = {
        "-//Netscape Communications//DTD RSS 0.9//EN",
        "/org/apache/commons/digester/rss/rss-0.9.dtd",
        "-//Netscape Communications//DTD RSS 0.91//EN",
        "/org/apache/commons/digester/rss/rss-0.91.dtd",
    };
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestRSSRoundTrip.class);
    }
    
    public TestRSSRoundTrip(String testName) {
        super(testName);
    }
    
    

    public void testRoundTrip() throws Exception {
        // lets parse the example 
        RSSDigester digester = new RSSDigester();
        
        InputStream in = new FileInputStream( "src/test/org/apache/commons/betwixt/rss-example.xml" );
        Object bean = digester.parse( in ); 
        in.close();
        
        // now lets output it to a buffer
        StringWriter buffer = new StringWriter();
        write( bean, buffer );
        
        // now lets try parse again
        String text = buffer.toString();        
        bean = digester.parse( new StringReader( text ) );
        
        // managed to parse it again!
        
        // now lets write it to another buffer
        buffer = new StringWriter();
        write( bean, buffer );
        
        String text2 = buffer.toString();

        // if the two strings are equal then we've done a full round trip
        // with the XML staying the same. Though the original source XML
        // could well be different
        assertEquals( "Round trip value should remain unchanged", text, text2 );
    }
    
    /** 
     * This tests using the both the RSSDigester 
     * and the BeanReader to parse an RSS and output it
     * using the BeanWriter
     */
    public void testBeanWriterRoundTrip() throws Exception {
        // lets parse the example using the RSSDigester
        RSSDigester digester = new RSSDigester();
        
        InputStream in = new FileInputStream( "src/test/org/apache/commons/betwixt/rss-example.xml" );
        Object bean = digester.parse( in ); 
        in.close();
        
        // now lets output it to a buffer
        StringWriter buffer = new StringWriter();
        write( bean, buffer );
        

        // create a BeanReader
        BeanReader reader = new BeanReader();
        reader.registerBeanClass( Channel.class );

        // Register local copies of the DTDs we understand
        for (int i = 0; i < registrations.length; i += 2) {
            URL url = RSSDigester.class.getResource(registrations[i + 1]);
            if (url != null) {
                reader.register(registrations[i], url.toString());
            }
        }
        
        // now lets try parse the output sing the BeanReader 
        String text = buffer.toString();        
        bean = reader.parse( new StringReader( text ) );
        
        // managed to parse it again!
        
        // now lets write it to another buffer
        buffer = new StringWriter();
        write( bean, buffer );
        
        String text2 = buffer.toString();

        // if the two strings are equal then we've done a full round trip
        // with the XML staying the same. Though the original source XML
        // could well be different
        assertEquals( "Round trip value should remain unchanged", text, text2 );
    }
    
    protected void write(Object bean, Writer out) throws Exception {
        BeanWriter writer = new BeanWriter(out);
        writer.getXMLIntrospector().setAttributesForPrimitives(false);
        writer.enablePrettyPrint();
        writer.write( bean );
    }
}

