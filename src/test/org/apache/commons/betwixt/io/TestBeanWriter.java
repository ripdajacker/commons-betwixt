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
 
package org.apache.commons.betwixt.io;

import java.io.StringWriter;

import org.apache.commons.betwixt.ElementDescriptor;
import org.xml.sax.helpers.AttributesImpl;

import junit.framework.TestCase;

/**
 * Test for <code>BeanWriter</code>
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision$
 */
public class TestBeanWriter extends TestCase {

    public void testSetEndTagForEmptyElementTrue() throws Exception
    {        
        StringWriter out = new StringWriter();
        BeanWriter writer = new BeanWriter(out);
        writer.setEndTagForEmptyElement(true);
        WriteContext context = new WriteContext() {

            public ElementDescriptor getCurrentDescriptor() {
                return null;
            }
            
        };
        writer.startElement(
                context, 
                null, 
                null, 
                "element", 
                new AttributesImpl());
        writer.endElement(
                context,
                null, 
                null, 
                "element");
         assertEquals("<element></element>" + writer.getEndOfLine(), out.getBuffer().toString());       
    }


    public void testSetEndTagForEmptyElementFalse() throws Exception
    {        
        StringWriter out = new StringWriter();
        BeanWriter writer = new BeanWriter(out);
        writer.setEndTagForEmptyElement(false);
        WriteContext context = new WriteContext() {

            public ElementDescriptor getCurrentDescriptor() {
                return null;
            }
            
        };
        writer.startElement(
                context, 
                null, 
                null, 
                "element", 
                new AttributesImpl());
        writer.endElement(
                context,
                null, 
                null, 
                "element");
         assertEquals("<element/>" + writer.getEndOfLine(), out.getBuffer().toString());       
    }
}
