/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/strategy/TestHyphenatedNameMapper.java,v 1.6 2003/08/27 01:50:07 dlr Exp $
 * $Revision: 1.6 $
 * $Date: 2003/08/27 01:50:07 $
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
 * $Id: TestHyphenatedNameMapper.java,v 1.6 2003/08/27 01:50:07 dlr Exp $
 */

package org.apache.commons.betwixt.strategy;

import java.beans.BeanDescriptor;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.betwixt.XMLIntrospector;

/** Test harness for the HyphenatedNameMapper
  *
  * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
  * @version $Revision: 1.6 $
  */
public class TestHyphenatedNameMapper extends TestCase {
    
    public static Test suite() {
        return new TestSuite(TestHyphenatedNameMapper.class);
    }
    
    public TestHyphenatedNameMapper(String testName) {
        super(testName);
    }
    
    public void testLowerCase()  {
        HyphenatedNameMapper mapper = new HyphenatedNameMapper();
        String result = mapper.mapTypeToElementName("FooBar");
        assertEquals("foo-bar", result);
    }
    
    public void testLowerCaseViaBeanDescriptor() {
        HyphenatedNameMapper mapper = new HyphenatedNameMapper(false, "_");
        BeanDescriptor bd = new BeanDescriptor(getClass());
        String result = mapper.mapTypeToElementName(bd.getName());
        assertEquals("test_hyphenated_name_mapper", result);
    }

    public void testUpperCase()  {
        HyphenatedNameMapper mapper = new HyphenatedNameMapper(true, "_");
        String result = mapper.mapTypeToElementName("FooBar");
        assertEquals("FOO_BAR", result);
    }
    
    public void testUpperCaseViaProperties()  {
        HyphenatedNameMapper mapper = new HyphenatedNameMapper();
        mapper.setUpperCase(true);
        mapper.setSeparator("_");
        String result = mapper.mapTypeToElementName("FooBar");
        assertEquals("FOO_BAR", result);
    }
    
    /**
     * A more "complicated" exmple
     */
    public void testUpperCaseLongViaProperties() {
        HyphenatedNameMapper mapper = new HyphenatedNameMapper(true, "__");
        String result = mapper.mapTypeToElementName("FooBarFooBar");
        assertEquals("FOO__BAR__FOO__BAR", result);

    }
     
    public void testBeanWithAdd() throws Exception {	
        //
        // simple test this one
        // a problem was reported when introspecting classes with 'add' properties
        // when using the HyphenatedNameMapper
        // basically, the test is that no exception is thrown
        //
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.setElementNameMapper(new HyphenatedNameMapper());
        introspector.introspect(new ArrayList());
    }
}
