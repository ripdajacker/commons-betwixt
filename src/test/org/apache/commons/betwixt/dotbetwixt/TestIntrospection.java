/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/dotbetwixt/TestIntrospection.java,v 1.1 2003/08/24 16:57:28 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/08/24 16:57:28 $
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
 * $Id: TestIntrospection.java,v 1.1 2003/08/24 16:57:28 rdonkin Exp $
 */
package org.apache.commons.betwixt.dotbetwixt;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.logging.impl.SimpleLog;

import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.ElementDescriptor;

import org.apache.commons.betwixt.xmlunit.XmlTestCase;
import org.apache.commons.betwixt.digester.ElementRule;

/** 
  * Test customization of xml to bean mapping using .betwixt files.
  *
  * @author Robert Burrell Donkin
  */
public class TestIntrospection extends XmlTestCase {

//--------------------------------- Test Suite
    
    public static Test suite() {
        return new TestSuite(TestIntrospection.class);
    }
    
//--------------------------------- Constructor
        
    public TestIntrospection(String testName) {
        super(testName);
    }

//---------------------------------- Tests

    public void testClassAttribute() throws Exception {
        //SimpleLog log = new SimpleLog("[testClassAttribute:ElementRule]");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        //ElementRule.setLog(log);
        
        XMLIntrospector introspector = new XMLIntrospector();
        XMLBeanInfo beanInfo = introspector.introspect(ExampleBean.class);
        ElementDescriptor[] elementDescriptors = beanInfo.getElementDescriptor().getElementDescriptors();
        ElementDescriptor elementsElementDescriptor = null;
        for ( int i=0, size = elementDescriptors.length; i<size ; i++ ) {
            if ( "examples".equals( elementDescriptors[i].getLocalName() ) ) {
                elementsElementDescriptor = elementDescriptors[i];
            }
        }
        
        assertNotNull("Element descriptor for elements not found", elementsElementDescriptor);
        assertEquals(
                    "Class property not set", 
                    ExampleImpl.class, 
                    elementsElementDescriptor.getImplementationClass());
    }
}
