/*
 * $Header: /home/cvs/jakarta-commons-sandbox/betwixt/src/test/org/apache/commons/betwixt/TestXMLIntrospector.java,v 1.8 2002/05/17 15:24:10 jstrachan Exp $
 * $Revision: 1.8 $
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
 * $Id: TestXMLIntrospector.java,v 1.8 2002/05/17 15:24:10 jstrachan Exp $
 */
package org.apache.commons.betwixt;

import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.registry.NoCacheRegistry;
import org.apache.commons.betwixt.registry.DefaultXMLBeanInfoRegistry;


/** Test harness for the XMLIntrospector
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.8 $
  */
public class TestXMLIntrospector extends AbstractTestCase {
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestXMLIntrospector.class);
    }
        
    public TestXMLIntrospector(String testName) {
        super(testName);
    }
    
    public void testIntrospector() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.setAttributesForPrimitives(true);
        
        Object bean = createBean();
        
        XMLBeanInfo info = introspector.introspect( bean );
        
        assertTrue( "Found XMLBeanInfo", info != null );
        
        ElementDescriptor descriptor = info.getElementDescriptor();
        
        assertTrue( "Found root element descriptor", descriptor != null );
        
        AttributeDescriptor[] attributes = descriptor.getAttributeDescriptors();
        
        assertTrue( "Found attributes", attributes != null && attributes.length > 0 );
        
        // test second introspection with caching on
        info = introspector.introspect( bean );
        
        assertTrue( "Found XMLBeanInfo", info != null );
        
        descriptor = info.getElementDescriptor();
        
        assertTrue( "Found root element descriptor", descriptor != null );
        
        attributes = descriptor.getAttributeDescriptors();
        
        assertTrue( "Found attributes", attributes != null && attributes.length > 0 );


        // test introspection with caching off      
        //introspector.setCachingEnabled(false);  
        introspector.setRegistry(new NoCacheRegistry());
        info = introspector.introspect( bean );
        
        assertTrue( "Found XMLBeanInfo", info != null );
        
        descriptor = info.getElementDescriptor();
        
        assertTrue( "Found root element descriptor", descriptor != null );
        
        attributes = descriptor.getAttributeDescriptors();
        
        assertTrue( "Found attributes", attributes != null && attributes.length > 0 );


        // test introspection after flushing cache
//        introspector.setCachingEnabled(true);
        introspector.setRegistry(new DefaultXMLBeanInfoRegistry()); 
        //introspector.flushCache();
        info = introspector.introspect( bean );
        
        assertTrue( "Found XMLBeanInfo", info != null );
        
        descriptor = info.getElementDescriptor();
        
        assertTrue( "Found root element descriptor", descriptor != null );
        
        attributes = descriptor.getAttributeDescriptors();
        
        assertTrue( "Found attributes", attributes != null && attributes.length > 0 );

    }
}

