/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/io/read/TestMaps.java,v 1.1.2.1 2004/02/22 17:09:29 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/02/22 17:09:29 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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
 */ 

package org.apache.commons.betwixt.io.read;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

/**
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class TestMaps extends AbstractTestCase {

    public TestMaps(String testName) {
        super(testName);
    }
    
    public void testWriteConcreateMapImplementation() throws Exception {
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(false);
        writer.getBindingConfiguration().setMapIDs(false);
        BeanWithConcreteMap bean = new BeanWithConcreteMap();
        bean.addSomeThingy("Aethelred", "The Unready");
        bean.addSomeThingy("Swein", "Forkbeard");
        bean.addSomeThingy("Thorkell", "The Tall");
        writer.write(bean);
        String xml = out.getBuffer().toString();
        String expected = "<?xml version='1.0'?><BeanWithConcreteMap>" +            "<entry>" +            "<key>Swein</key>" +            "<value>Forkbeard</value>" +            "</entry>" +            "<entry>" +            "<key>Thorkell</key>" +            "<value>The Tall</value>" +            "</entry>" +            "<entry>" +            "<key>Aethelred</key>" +            "<value>The Unready</value>" +            "</entry>" +            "</BeanWithConcreteMap>";
        xmlAssertIsomorphicContent(parseString(expected), parseString(xml));
    }

    
    public void testReadConcreateMapImplementation() throws Exception {
        StringReader in =  new StringReader("<?xml version='1.0'?><BeanWithConcreteMap>" +
            "<entry>" +
            "<key>Swein</key>" +
            "<value>Forkbeard</value>" +
            "</entry>" +
            "<entry>" +
            "<key>Thorkell</key>" +
            "<value>The Tall</value>" +
            "</entry>" +
            "<entry>" +
            "<key>Aethelred</key>" +
            "<value>The Unready</value>" +
            "</entry>" +
            "</BeanWithConcreteMap>");

        BeanReader reader = new BeanReader();
        reader.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(false);
        reader.getBindingConfiguration().setMapIDs(false);
        reader.registerBeanClass(BeanWithConcreteMap.class);
        
        
        BeanWithConcreteMap bean = (BeanWithConcreteMap) reader.parse(in);
        assertNotNull("Parse failed", bean);
        
        Map map = bean.getSomeThingies();
        
        Set keyset = map.keySet();
        assertEquals("Three entries", 3, keyset.size());
        assertEquals("Aethelred The Unready", "The Unready", map.get("Aethelred"));
        assertEquals("Swein Forkbeardy", "Forkbeard", map.get("Swein"));
        assertEquals("Thorkell The Tall", "The Tall", map.get("Thorkell"));
 
    }

}
