/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/schema/TestSchemaTranscriber.java,v 1.1.2.5 2004/02/07 14:44:45 rdonkin Exp $
 * $Revision: 1.1.2.5 $
 * $Date: 2004/02/07 14:44:45 $
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

package org.apache.commons.betwixt.schema;

import java.util.Iterator;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;

/**
 * Tests for the SchemaTranscriber.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.5 $
 */
public class TestSchemaTranscriber extends AbstractTestCase {
    
    public TestSchemaTranscriber(String testName) {
        super(testName);
    }
	
    public void testEmpty() {}
    
    public void testSimplestBeanAttribute() throws Exception {
        Schema expected = new Schema();
        
        ComplexType simplestBeanType = new ComplexType();
        simplestBeanType.setName("org.apache.commons.betwixt.schema.SimplestBean");
        simplestBeanType.addAttribute(new Attribute("name", "xsd:string"));
        
        Element root = new Element("SimplestBean", "org.apache.commons.betwixt.schema.SimplestBean");
        expected.addComplexType(simplestBeanType);
        expected.addElement(root);
        
        SchemaTranscriber transcriber = new SchemaTranscriber();
        transcriber.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
        Schema out = transcriber.generate(SimplestBean.class);
        
        assertEquals("Simplest bean schema", expected, out);
    }
    
    public void testSimplestBeanElement() throws Exception {
        Schema expected = new Schema();
        
        ComplexType simplestBeanType = new ComplexType();
        simplestBeanType.setName("org.apache.commons.betwixt.schema.SimplestElementBean");
        simplestBeanType.addElement(new LocalElement("name", "xsd:string"));
        
        Element root = new Element("SimplestBean", "org.apache.commons.betwixt.schema.SimplestElementBean");
        expected.addComplexType(simplestBeanType);
        expected.addElement(root);
        
        SchemaTranscriber transcriber = new SchemaTranscriber();
        transcriber.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
        Schema out = transcriber.generate(SimplestElementBean.class);
        
        assertEquals("Simplest bean schema", expected, out);
    }
    
	public void testSimpleBean() throws Exception {
		SchemaTranscriber transcriber = new SchemaTranscriber();
		Schema out = transcriber.generate(SimpleBean.class);
		
		Schema expected = new Schema();
		ComplexType simpleBeanType = new ComplexType();
		simpleBeanType.setName("org.apache.commons.betwixt.schema.SimpleBean");
		simpleBeanType.addAttribute(new Attribute("one", "xsd:string"));
		simpleBeanType.addAttribute(new Attribute("two", "xsd:string"));
		simpleBeanType.addElement(new LocalElement("three", "xsd:string"));
		simpleBeanType.addElement(new LocalElement("four", "xsd:string"));
		expected.addComplexType(simpleBeanType);
        expected.addElement(new Element("simple", "org.apache.commons.betwixt.schema.SimpleBean"));
        
        assertEquals("Simple bean schema", expected, out);
        
	}
    
    public void testOrderLine() throws Exception {
        SchemaTranscriber transcriber = new SchemaTranscriber();
        transcriber.getXMLIntrospector().getConfiguration().setAttributeNameMapper(new HyphenatedNameMapper());
        transcriber.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
        Schema out = transcriber.generate(OrderLineBean.class);
        
        Schema expected = new Schema();
        
        ComplexType productBeanType = new ComplexType();
        productBeanType.setName(ProductBean.class.getName());
        productBeanType.addAttribute(new Attribute("barcode", "xsd:string"));
        productBeanType.addAttribute(new Attribute("code", "xsd:string"));
        productBeanType.addAttribute(new Attribute("name", "xsd:string"));
        productBeanType.addAttribute(new Attribute("display-name", "xsd:string"));
        expected.addComplexType(productBeanType);
        
        ComplexType orderLineType = new ComplexType();       
        orderLineType.setName(OrderLineBean.class.getName());
        orderLineType.addAttribute(new Attribute("quantity", "xsd:string"));
        orderLineType.addElement(new LocalElement("product", productBeanType));
        expected.addComplexType(orderLineType);
        expected.addElement(new Element("OrderLineBean", OrderLineBean.class.getName()));
        
        assertEquals("Transcriber schema", expected, out);   
    }
    
    
    public void testOrder() throws Exception {
        SchemaTranscriber transcriber = new SchemaTranscriber();
        transcriber.getXMLIntrospector().getConfiguration().setElementNameMapper(new HyphenatedNameMapper());
        transcriber.getXMLIntrospector().getConfiguration().setAttributeNameMapper(new HyphenatedNameMapper());
        transcriber.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
        transcriber.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(false);
        Schema out = transcriber.generate(OrderBean.class);
        
        Schema expected = new Schema();
        
        
        ComplexType customerBeanType = new ComplexType();
        customerBeanType.setName(CustomerBean.class.getName());
        customerBeanType.addAttribute(new Attribute("code", "xsd:string"));
        customerBeanType.addAttribute(new Attribute("name", "xsd:string"));
        customerBeanType.addAttribute(new Attribute("street", "xsd:string"));
        customerBeanType.addAttribute(new Attribute("town", "xsd:string"));
        customerBeanType.addAttribute(new Attribute("country", "xsd:string"));
        customerBeanType.addAttribute(new Attribute("postcode", "xsd:string"));
        expected.addComplexType(customerBeanType);
        
        ComplexType productBeanType = new ComplexType();
        productBeanType.setName(ProductBean.class.getName());
        productBeanType.addAttribute(new Attribute("barcode", "xsd:string"));
        productBeanType.addAttribute(new Attribute("code", "xsd:string"));
        productBeanType.addAttribute(new Attribute("name", "xsd:string"));
        productBeanType.addAttribute(new Attribute("display-name", "xsd:string"));
        expected.addComplexType(productBeanType);
        
        ComplexType orderLineType = new ComplexType();       
        orderLineType.setName(OrderLineBean.class.getName());
        orderLineType.addAttribute(new Attribute("quantity", "xsd:string"));
        orderLineType.addElement(new LocalElement("product", productBeanType));
        expected.addComplexType(orderLineType);
        
        ComplexType orderType = new ComplexType();       
        orderType.setName(OrderBean.class.getName());
        orderType.addAttribute(new Attribute("code", "xsd:string"));
        orderType.addElement(new LocalElement("customer", customerBeanType));
        orderType.addElement(new LocalElement("line", orderLineType));
        expected.addComplexType(orderType);
        expected.addElement(new Element("order-bean", OrderBean.class.getName()));
        
        assertEquals("Transcriber schema", expected, out);   
    }
    
    private void printDifferences(Schema one, Schema two) {
        for( Iterator it=one.getComplexTypes().iterator();it.hasNext(); ) {
            ComplexType complexType = (ComplexType)it.next();
            if (!two.getComplexTypes().contains(complexType)) {
                boolean matched = false;
                for (Iterator otherIter=two.getComplexTypes().iterator(); it.hasNext();) {
                    ComplexType otherType = (ComplexType) otherIter.next();
                    if (otherType.getName().equals(complexType.getName())) {
                        printDifferences(complexType, otherType);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    System.err.println("Missing Complex type: " + complexType);
                }
            }
        }          
        
    }
    
    private void printDifferences(ComplexType one, ComplexType two) {
        System.err.println("Type " + one + " is not equal to " + two);
        for (Iterator it = one.getElements().iterator(); it.hasNext();) {
            Element elementOne = (Element) it.next();
            if (!two.getElements().contains(elementOne)) {
                boolean matched = false;
                for (Iterator otherIter=two.getElements().iterator(); it.hasNext();) {
                    Element elementTwo = (Element) otherIter.next();
                    if (elementTwo.getName().equals(elementTwo.getName())) {
                        printDifferences(elementOne, elementTwo);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    System.err.println("Missing Element: " + elementOne);
                }                
            }
        }
        for (Iterator it = one.getAttributes().iterator(); it.hasNext();) {
            Attribute attributeOne = (Attribute) it.next();
            if (!two.getAttributes().contains(attributeOne)) {
                boolean matched = false;
                for (Iterator otherIter=two.getAttributes().iterator(); it.hasNext();) {
                    Attribute attributeTwo = (Attribute) otherIter.next();
                    if (attributeTwo.getName().equals(attributeTwo.getName())) {
                        printDifferences(attributeOne, attributeTwo);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    System.err.println("Missing Attribute: " + attributeOne);
                }                
            }
        }
    }
    
    private void printDifferences(Attribute one , Attribute two) {
        System.err.println("Attribute " + one + " is not equals to " + two);
    }
    
    private void printDifferences(Element one , Element two) {
        System.err.println("Element " + one + " is not equals to " + two);
    }
}
