/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/schema/TestSchemaTranscriber.java,v 1.1.2.7 2004/02/23 21:41:13 rdonkin Exp $
 * $Revision: 1.1.2.7 $
 * $Date: 2004/02/23 21:41:13 $
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

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;

/**
 * Tests for the SchemaTranscriber.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.7 $
 */
public class TestSchemaTranscriber extends AbstractTestCase {
    
    public TestSchemaTranscriber(String testName) {
        super(testName);
    }
	
    public void testEmpty() {}
    
    public void testSimplestBeanAttribute() throws Exception {
        Schema expected = new Schema();
        
        GlobalComplexType simplestBeanType = new GlobalComplexType();
        simplestBeanType.setName("org.apache.commons.betwixt.schema.SimplestBean");
        simplestBeanType.addAttribute(new Attribute("name", "xsd:string"));
        
        GlobalElement root = new GlobalElement("SimplestBean", "org.apache.commons.betwixt.schema.SimplestBean");
        expected.addComplexType(simplestBeanType);
        expected.addElement(root);
        
        SchemaTranscriber transcriber = new SchemaTranscriber();
        transcriber.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
        Schema out = transcriber.generate(SimplestBean.class);
        
        assertEquals("Simplest bean schema", expected, out);
    }
    
    public void testSimplestBeanElement() throws Exception {
        Schema expected = new Schema();
        
        GlobalComplexType simplestBeanType = new GlobalComplexType();
        simplestBeanType.setName("org.apache.commons.betwixt.schema.SimplestElementBean");
        simplestBeanType.addElement(new SimpleLocalElement("name", "xsd:string"));
        
        GlobalElement root = new GlobalElement("SimplestBean", "org.apache.commons.betwixt.schema.SimplestElementBean");
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
		GlobalComplexType simpleBeanType = new GlobalComplexType();
		simpleBeanType.setName("org.apache.commons.betwixt.schema.SimpleBean");
		simpleBeanType.addAttribute(new Attribute("one", "xsd:string"));
		simpleBeanType.addAttribute(new Attribute("two", "xsd:string"));
		simpleBeanType.addElement(new SimpleLocalElement("three", "xsd:string"));
		simpleBeanType.addElement(new SimpleLocalElement("four", "xsd:string"));
		expected.addComplexType(simpleBeanType);
        expected.addElement(new GlobalElement("simple", "org.apache.commons.betwixt.schema.SimpleBean"));
        
        assertEquals("Simple bean schema", expected, out);
        
	}
    
    public void testOrderLine() throws Exception {
        SchemaTranscriber transcriber = new SchemaTranscriber();
        transcriber.getXMLIntrospector().getConfiguration().setAttributeNameMapper(new HyphenatedNameMapper());
        transcriber.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
        Schema out = transcriber.generate(OrderLineBean.class);
        
        Schema expected = new Schema();
        
        GlobalComplexType productBeanType = new GlobalComplexType();
        productBeanType.setName(ProductBean.class.getName());
        productBeanType.addAttribute(new Attribute("barcode", "xsd:string"));
        productBeanType.addAttribute(new Attribute("code", "xsd:string"));
        productBeanType.addAttribute(new Attribute("name", "xsd:string"));
        productBeanType.addAttribute(new Attribute("display-name", "xsd:string"));
        expected.addComplexType(productBeanType);
        
        GlobalComplexType orderLineType = new GlobalComplexType();       
        orderLineType.setName(OrderLineBean.class.getName());
        orderLineType.addAttribute(new Attribute("quantity", "xsd:string"));
        orderLineType.addElement(new ElementReference("product", productBeanType));
        expected.addComplexType(orderLineType);
        expected.addElement(new GlobalElement("OrderLineBean", OrderLineBean.class.getName()));
        
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
        
        
        GlobalComplexType customerBeanType = new GlobalComplexType();
        customerBeanType.setName(CustomerBean.class.getName());
        customerBeanType.addAttribute(new Attribute("code", "xsd:string"));
        customerBeanType.addAttribute(new Attribute("name", "xsd:string"));
        customerBeanType.addAttribute(new Attribute("street", "xsd:string"));
        customerBeanType.addAttribute(new Attribute("town", "xsd:string"));
        customerBeanType.addAttribute(new Attribute("country", "xsd:string"));
        customerBeanType.addAttribute(new Attribute("postcode", "xsd:string"));
        expected.addComplexType(customerBeanType);
        
        GlobalComplexType productBeanType = new GlobalComplexType();
        productBeanType.setName(ProductBean.class.getName());
        productBeanType.addAttribute(new Attribute("barcode", "xsd:string"));
        productBeanType.addAttribute(new Attribute("code", "xsd:string"));
        productBeanType.addAttribute(new Attribute("name", "xsd:string"));
        productBeanType.addAttribute(new Attribute("display-name", "xsd:string"));
        expected.addComplexType(productBeanType);
        
        GlobalComplexType orderLineType = new GlobalComplexType();       
        orderLineType.setName(OrderLineBean.class.getName());
        orderLineType.addAttribute(new Attribute("quantity", "xsd:string"));
        orderLineType.addElement(new ElementReference("product", productBeanType));
        expected.addComplexType(orderLineType);
        
        GlobalComplexType orderType = new GlobalComplexType();       
        orderType.setName(OrderBean.class.getName());
        orderType.addAttribute(new Attribute("code", "xsd:string"));
        orderType.addElement(new ElementReference("customer", customerBeanType));
        orderType.addElement(new ElementReference("line", orderLineType));
        expected.addComplexType(orderType);
        expected.addElement(new GlobalElement("order-bean", OrderBean.class.getName()));
        
        assertEquals("Transcriber schema", expected, out);   
    }

}