/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/io/read/TestMaps.java,v 1.1.2.2 2004/05/01 09:42:22 rdonkin Exp $
 * $Revision: 1.1.2.2 $
 * $Date: 2004/05/01 09:42:22 $
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
 * @version $Revision: 1.1.2.2 $
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

    public void testMapWithArray() throws Exception {

        AddressBook addressBook = new AddressBook();
        AddressBean[] johnsAddresses = new AddressBean[2];
        johnsAddresses[0] = new AddressBean("12 here", "Chicago", "USA", "1234");
        johnsAddresses[1] =
            new AddressBean("333 there", "Los Angeles", "USA", "99999");
        String name = "John";
        addressBook.addAddressBookItem(name, johnsAddresses);
        StringWriter outputWriter = new StringWriter();
        outputWriter.write("<?xml version='1.0' ?>\n");
        BeanWriter beanWriter = new BeanWriter(outputWriter);
        beanWriter.enablePrettyPrint();
        beanWriter.write(addressBook);
    
        String xml =
            "<?xml version='1.0' ?>\n"
                + "  <AddressBook id=\"1\">\n"
                + "    <addressBookItems>\n"
                + "      <entry id=\"2\">\n"
                + "        <key>John</key>\n"
                + "        <value id=\"3\">\n"
                + "          <AddressBean id=\"4\">\n"
                + "            <city>Chicago</city>\n"
                + "            <code>1234</code>\n"
                + "            <country>USA</country>\n"
                + "            <street>12 here</street>\n"
                + "          </AddressBean>\n"
                + "          <AddressBean id=\"5\">\n"
                + "            <city>Los Angeles</city>\n"
                + "            <code>99999</code>\n"
                + "            <country>USA</country>\n"
                + "            <street>333 there</street>\n"
                + "          </AddressBean>\n"
                + "        </value>\n"
                + "      </entry>\n"
                + "    </addressBookItems>\n"
                + "  </AddressBook>\n";
    
        assertEquals(xml, outputWriter.toString());
        BeanReader reader = new BeanReader();
        reader.registerBeanClass(AddressBook.class);
        StringReader xmlReader = new StringReader(outputWriter.toString());
        AddressBook result = (AddressBook) reader.parse(xmlReader);
        assertNotNull("Expected to get an AddressBook!", result);
        assertNotNull(
            "Expected AddressBook to have some address entryitems!",
            result.getAddressBookItems());
        AddressBean[] resultAddresses =
            (AddressBean[]) result.getAddressBookItems().get(name);
        assertNotNull(
            "Expected to have some addresses for " + name,
            resultAddresses);
        assertEquals(
            "Got wrong city in first address for " + name,
            johnsAddresses[0].getCity(),
            resultAddresses[0].getCity());
        assertEquals(
            "Got wrong city in second address for " + name,
            johnsAddresses[1].getCity(),
            resultAddresses[1].getCity());
    }
}
