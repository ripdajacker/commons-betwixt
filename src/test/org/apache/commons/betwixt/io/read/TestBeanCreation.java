/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/io/read/TestBeanCreation.java,v 1.1 2003/08/21 22:45:49 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/08/21 22:45:49 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * $Id: TestBeanCreation.java,v 1.1 2003/08/21 22:45:49 rdonkin Exp $
 */
package org.apache.commons.betwixt.io.read;

import java.util.ArrayList;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.AbstractTestCase;

/** 
 * Test harness for bean creation (during reading).
 * 
 * @author Robert Burrell Donkin
 * @version $Id: TestBeanCreation.java,v 1.1 2003/08/21 22:45:49 rdonkin Exp $
 */
public class TestBeanCreation extends AbstractTestCase {

    public TestBeanCreation(String name) {
        super(name);
    }
        
    public static Test suite() {
        return new TestSuite(TestBeanCreation.class);
    }    
    
    public void testCustomCreatorOne() throws Exception {
        HouseBeans houses = new HouseBeans();
        HouseBean houseOne = new HouseBean();
        houseOne.setFacing(CompassPoint.NORTH);
        houseOne.setAddress(new AddressBean("Black Bull, 46 Briggate", "Brighouse", "England", "HD6 1EF"));
        houseOne.setHouseholder(new PersonBean("Samual", "Smith"));
        houseOne.setTenant(false);
        houses.addHouse(houseOne);
        HouseBean houseTwo = new HouseBean();
        houseTwo.setFacing(CompassPoint.SOUTH);
        houseTwo.setAddress(new AddressBean("The Commerical Inn, 1 Gooder Lane", "Brighouse", "England", "HD6 1HT"));
        houseTwo.setHouseholder(new PersonBean("Timothy", "Tayler"));
        houseTwo.setTenant(true);
        houses.addHouse(houseTwo);
        
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.getXMLIntrospector().setAttributesForPrimitives(true);
        writer.getXMLIntrospector().setWrapCollectionsInElement(false);
        writer.write("houses", houses);
        
        String xml = "<?xml version='1.0'?><houses>"
            + "<house tenant='false'>"
            + "<address street='Black Bull, 46 Briggate' city='Brighouse' country='England' code='HD6 1EF'/>"
            + "<householder forename='Samual' surname='Smith'/>"
            + "<facing name='North'/>"
            + "</house>"
            + "<house tenant='true'>"
            + "<address street='The Commerical Inn, 1 Gooder Lane' city='Brighouse'" 
            + " country='England' code='HD6 1HT'/>"
            + "<householder forename='Timothy' surname='Tayler'/>"
            + "<facing name='South'/>"
            + "</house>"
            + "</houses>";
        
        xmlAssertIsomorphic(parseString(xml), parseString(out.toString()), true);

        BeanCreationList chain = BeanCreationList.createStandardChain();
        // add a filter that creates enums to the start
        
        class EnumCreator implements ChainedBeanCreator {
            
            public Object create(ElementMapping mapping, ReadContext context, BeanCreationChain chain) {
                if ("facing".equals(mapping.getName())) {
                    String value = mapping.getAttributes().getValue("name");
                    if ("North".equals(value)) {
                        return CompassPoint.NORTH;
                    }
                    if ("South".equals(value)) {
                        return CompassPoint.SOUTH;
                    }
                    if ("East".equals(value)) {
                        return CompassPoint.EAST;
                    }
                    if ("West".equals(value)) {
                        return CompassPoint.WEST;
                    }
                }
                return chain.create(mapping, context);
            }
        }
        chain.insertBeanCreator(1, new EnumCreator());
        
        BeanReader reader = new BeanReader();
        reader.getXMLIntrospector().setAttributesForPrimitives(true);
        reader.getXMLIntrospector().setWrapCollectionsInElement(false);
        reader.registerBeanClass("houses", HouseBeans.class);
        reader.getReadConfiguration().setBeanCreationChain(chain);
        
        StringReader in = new StringReader(xml);
        HouseBeans newHouses = (HouseBeans) reader.parse(in);
        assertNotNull("Parsing should return a bean", newHouses);
        
        ArrayList houseList = newHouses.houses;
        assertEquals("Should be two houses read", 2, houseList.size());
        HouseBean newOne = (HouseBean) houseList.get(0);
        HouseBean newTwo = (HouseBean) houseList.get(1);
        assertEquals("First house is equal",  houseOne, newOne);
        assertEquals("Second house is equal",  houseTwo, newTwo);
        
    }
    
    public void testCustomCreatorTwo() throws Exception {
        HouseBeans houses = new HouseBeans();
        HouseBean houseOne = new HouseBean();
        houseOne.setFacing(CompassPoint.NORTH);
        houseOne.setAddress(new AddressBean("Black Bull, 46 Briggate", "Brighouse", "England", "HD6 1EF"));
        houseOne.setHouseholder(new PersonBean("Samual", "Smith"));
        houseOne.setTenant(false);
        houses.addHouse(houseOne);
        HouseBean houseTwo = new HouseBean();
        houseTwo.setFacing(CompassPoint.SOUTH);
        houseTwo.setAddress(new AddressBean("The Commerical Inn, 1 Gooder Lane", "Brighouse", "England", "HD6 1HT"));
        houseTwo.setHouseholder(new PersonBean("Timothy", "Tayler"));
        houseTwo.setTenant(true);
        houses.addHouse(houseTwo);
        
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.getXMLIntrospector().setAttributesForPrimitives(true);
        writer.getXMLIntrospector().setWrapCollectionsInElement(false);
        writer.write("houses", houses);
        
        String xml = "<?xml version='1.0'?><houses>"
            + "<house tenant='false'>"
            + "<address street='Black Bull, 46 Briggate' city='Brighouse' country='England' code='HD6 1EF'/>"
            + "<householder forename='Samual' surname='Smith'/>"
            + "<facing name='North'/>"
            + "</house>"
            + "<house tenant='true'>"
            + "<address street='The Commerical Inn, 1 Gooder Lane' city='Brighouse'" 
            + " country='England' code='HD6 1HT'/>"
            + "<householder forename='Timothy' surname='Tayler'/>"
            + "<facing name='South'/>"
            + "</house>"
            + "</houses>";
        
        xmlAssertIsomorphic(parseString(xml), parseString(out.toString()), true);

        BeanCreationList chain = BeanCreationList.createStandardChain();
        // add a filter that creates enums to the start
        
        class EnumCreator implements ChainedBeanCreator {
            // match by class this time
            public Object create(ElementMapping mapping, ReadContext context, BeanCreationChain chain) {
                if (CompassPoint.class.equals(mapping.getType())) {
                    String value = mapping.getAttributes().getValue("name");
                    if ("North".equals(value)) {
                        return CompassPoint.NORTH;
                    }
                    if ("South".equals(value)) {
                        return CompassPoint.SOUTH;
                    }
                    if ("East".equals(value)) {
                        return CompassPoint.EAST;
                    }
                    if ("West".equals(value)) {
                        return CompassPoint.WEST;
                    }
                }
                return chain.create(mapping, context);
            }
        }
        chain.insertBeanCreator(1, new EnumCreator());
        
        BeanReader reader = new BeanReader();
        reader.getXMLIntrospector().setAttributesForPrimitives(true);
        reader.getXMLIntrospector().setWrapCollectionsInElement(false);
        reader.registerBeanClass("houses", HouseBeans.class);
        reader.getReadConfiguration().setBeanCreationChain(chain);
        
        StringReader in = new StringReader(xml);
        HouseBeans newHouses = (HouseBeans) reader.parse(in);
        assertNotNull("Parsing should return a bean", newHouses);
        
        ArrayList houseList = newHouses.houses;
        assertEquals("Should be two houses read", 2, houseList.size());
        HouseBean newOne = (HouseBean) houseList.get(0);
        HouseBean newTwo = (HouseBean) houseList.get(1);
        assertEquals("First house is equal",  houseOne, newOne);
        assertEquals("Second house is equal",  houseTwo, newTwo);
    }
}
