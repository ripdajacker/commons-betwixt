/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 
package org.apache.commons.betwixt.strategy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test that harnasses the DecapitlizeNameMapper
 * 
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: TestDecapitalizeNameMapper.java,v 1.5 2004/02/28 13:38:36 yoavs Exp $
 */
public class TestDecapitalizeNameMapper extends TestCase
{

    public static Test suite() {
        return new TestSuite(TestDecapitalizeNameMapper.class);
    }
    
    public TestDecapitalizeNameMapper(String testName) {
        super(testName);
    }
    
    public void testDecapitalize() {
        DecapitalizeNameMapper mapper = new DecapitalizeNameMapper();
        String result = mapper.mapTypeToElementName("FOOBAR");
        assertEquals("FOOBAR", result);
        result = mapper.mapTypeToElementName("FooBar");
        assertEquals("fooBar", result);
        result = mapper.mapTypeToElementName("FOOBar");
        assertEquals("FOOBar", result);
    }
}

