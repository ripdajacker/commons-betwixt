/*
 * $Header: /home/cvs/jakarta-commons-sandbox/betwixt/src/test/org/apache/commons/betwixt/TestXMLBeanInfoDigester.java,v 1.5 2002/05/28 11:49:29 jstrachan Exp $
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
 * $Id: TestXMLBeanInfoDigester.java,v 1.5 2002/05/28 11:49:29 jstrachan Exp $
 */
package org.apache.commons.betwixt;

import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.digester.XMLBeanInfoDigester;
import org.apache.commons.digester.rss.Channel;

/** Test harness for the Digester of XMLBeanInfo
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.5 $
  */
public class TestXMLBeanInfoDigester extends AbstractTestCase {
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestXMLBeanInfoDigester.class);
    }
        
    public TestXMLBeanInfoDigester(String testName) {
        super(testName);
    }
    
    public void testDigester() throws Exception {
        XMLBeanInfoDigester digester = new XMLBeanInfoDigester();

        InputStream in = new FileInputStream( getTestFile("src/test/org/apache/commons/digester/rss/Channel.betwixt") );
        
        assertTrue( "Found betwixt config file", in != null );
        
        XMLBeanInfo info = (XMLBeanInfo) digester.parse( in );
        
        assertTrue( "Found XMLBeanInfo", info != null );
        
        ElementDescriptor descriptor = info.getElementDescriptor();
        
        assertTrue( "Found root element descriptor", descriptor != null );
        assertEquals( "Element name correct", "rss", descriptor.getLocalName() );
        
        ElementDescriptor[] elements = descriptor.getElementDescriptors();
        
        assertTrue( "Found elements", elements != null && elements.length > 0 );
        
        descriptor = elements[0];
        assertEquals( "Element name correct", "channel", descriptor.getLocalName() );
    }
}

