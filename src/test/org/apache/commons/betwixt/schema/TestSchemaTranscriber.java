/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/schema/TestSchemaTranscriber.java,v 1.1.2.1 2004/01/18 12:35:42 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/01/18 12:35:42 $
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

/**
 * Tests for the SchemaTranscriber.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class TestSchemaTranscriber extends AbstractTestCase {
	
	private static final String SIMPLE_BEAN_SCHEMA =
	  "<?xml version='1.0'?>" +	  "<xsd:schema xmlns:xsd='http://www.w3c.org/2001/XMLSchema'>" +
	  "<xsd:element name='simple' type='SimpleBean'/>'" +
	  "<xsd:complexType name='SimpleBean'>" +
	  "<xsd:sequence>" +
	  "<xsd:element name='three' type='xsd:string'/>" +	  "<xsd:element name='four' type='xsd:string'/>" +	  "</xsd:sequence>" + 
	  "<xsd:attribute name='one' type='xsd:string'/>" +
	  "<xsd:attribute name='two' type='xsd:string'/>" +	  "</xsd:complexType>" +	  "</xsd:schema>";
	
    public TestSchemaTranscriber(String testName) {
        super(testName);
    }
	
    public void testEmpty() {}
    
	public void _testSimpleBean() throws Exception {
		SchemaTranscriber transcriber = new SchemaTranscriber();
		Schema out = transcriber.generate(SimpleBean.class);
		
		Schema expected = new Schema();
		ComplexType simpleBeanType = new ComplexType();
		simpleBeanType.setName("SimpleBean");
		simpleBeanType.addAttribute(new Attribute("one", "xsd:string"));
		simpleBeanType.addAttribute(new Attribute("one", "xsd:string"));
		simpleBeanType.addElement(new Element("three", "xsd:string"));
		simpleBeanType.addElement(new Element("four", "xsd:string"));
		expected.addComplexType(simpleBeanType);
        
        assertEquals("Simple bean schema", expected, out);
        
	}
}
