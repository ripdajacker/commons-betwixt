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
package org.apache.commons.betwixt.io.read;

import java.io.StringReader;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.io.BeanReader;

/**
 * @author <a href='http://jakarta.apache.org/commons'>Jakarta Commons Team</a>, <a href='http://www.apache.org'>Apache Software Foundation</a>
 */
public class TestReadData extends AbstractTestCase {

    public TestReadData(String testName) {
        super(testName);
    }

    public void testReadInvalidDate() throws Exception {
        
        String xmlWithInvalidDate = "<?xml version='1.0'?>" +
        		"<AlertBean>" +
        		"	<message>Whatever</message>" +
        		"   <summary>Sometime</summary>" +
        		"   <timestamp>2004-13-32 00:00:00.0</timestamp>" +
        		"</AlertBean>";
        StringReader invalidIn = new StringReader(xmlWithInvalidDate);
    
        
        String xmlWithValidDate = "<?xml version='1.0'?>" +
        		"<AlertBean>" +
        		"	<message>Whatever</message>" +
        		"   <summary>Sometime</summary>" +
        		"   <timestamp>1999-12-31 00:00:00.0</timestamp>" +
        		"</AlertBean>";
        StringReader validIn = new StringReader(xmlWithValidDate);
      
        
        BeanReader reader = new BeanReader();
        reader.registerBeanClass(AlertBean.class);
        try
        {
            AlertBean alterBean = (AlertBean) reader.parse(invalidIn);
            fail("Invalid date so expected exception");
        }
        catch (Exception e)
        {
            // expected
        }
        
        AlertBean alterBean = (AlertBean) reader.parse(validIn);
    }
}