/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.commons.betwixt.recursion;

import java.io.StringWriter;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.io.BeanWriter;

/**
 * @author <a href='http://jakarta.apache.org/commons'>Jakarta Commons Team</a>, <a href='http://www.apache.org'>Apache Software Foundation</a>
 */
public class TestSharedIDGeneration extends AbstractTestCase {

    public TestSharedIDGeneration(String testName) {
        super(testName);
    }

    public void testSharedChild() throws Exception {
        
        NameBean name = new NameBean("Me");
        
        HybridBean hybrid = new HybridBean(new AlienBean(name), new PersonBean(name));
        
        StringWriter out = new StringWriter();
        BeanWriter writer = new BeanWriter(out);
        writer.write(hybrid);
        
        String expected = "<?xml version='1.0'?><HybridBean id='1'>" +
        		"<alien id='2'><name id='3'><moniker>Me</moniker></name></alien>" +
        		"<person id='4'><name idref='3'/></person>" +
        		"</HybridBean>";
        
        xmlAssertIsomorphic(parseString(expected), parseString(out));
    }
    
}
