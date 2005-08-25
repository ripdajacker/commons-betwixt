/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.apache.commons.betwixt.strategy.impl.propertysuppression;

import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanWriter;

import junit.framework.TestCase;

public class TestPackageSuppressor extends TestCase {

    public void testExact() throws Exception {
        PackageSuppressor suppressor = new PackageSuppressor("org.apache.commons.betwixt");
        assertFalse("Unrelated class", suppressor.suppressProperty(String.class, String.class,"bogus"));
        assertFalse("Unrelated type", suppressor.suppressProperty(XMLIntrospector.class, String.class,"bogus"));
        assertTrue("Type in package", suppressor.suppressProperty( String.class, XMLIntrospector.class,"bogus"));
        assertFalse("Type in child package", suppressor.suppressProperty( String.class, BeanWriter.class,"bogus"));
    }
    
    public void testWild() throws Exception {
        PackageSuppressor suppressor = new PackageSuppressor("org.apache.commons.betwixt.*");
        assertFalse("Unrelated class", suppressor.suppressProperty(String.class, String.class,"bogus"));
        assertFalse("Unrelated type", suppressor.suppressProperty(XMLIntrospector.class, String.class,"bogus"));
        assertTrue("Type in package", suppressor.suppressProperty( String.class, XMLIntrospector.class,"bogus"));
        assertTrue("Type in child package", suppressor.suppressProperty( String.class, BeanWriter.class,"bogus"));
    }
}
