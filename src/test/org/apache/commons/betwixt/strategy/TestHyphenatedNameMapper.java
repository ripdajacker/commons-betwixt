/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: TestHyphenatedNameMapper.java,v 1.3 2002/09/26 21:20:52 rdonkin Exp $
 */
package org.apache.commons.betwixt.strategy;

import java.util.ArrayList;

import org.apache.commons.betwixt.XMLIntrospector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/** Test harness for the HyphenatedNameMapper
  *
  * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
  * @version $Revision: 1.3 $
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

