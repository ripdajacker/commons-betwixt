/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/schema/TestDataTypeMapper.java,v 1.1.2.1 2004/02/23 21:56:36 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/02/23 21:56:36 $
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

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.TestCase;

/**
 * Tests for <code>DataTypeMapper</code>
 * both usages and implementations.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class TestDataTypeMapper extends TestCase {

    public TestDataTypeMapper(String testName) {
        super(testName);
    }

    public void testDefaultDataTypeMapping() throws Exception {
        DefaultDataTypeMapper mapper = new DefaultDataTypeMapper();
        assertEquals("java.lang.String", "xsd:string", mapper.toXMLSchemaDataType(String.class));
        assertEquals("java.math.BigInteger", "xsd:integer", mapper.toXMLSchemaDataType(BigInteger.class));
        assertEquals("java.math.BigDecimal", "xsd:decimal", mapper.toXMLSchemaDataType(BigDecimal.class));
        assertEquals("Integer", "xsd:int", mapper.toXMLSchemaDataType(Integer.TYPE));
        assertEquals("int", "xsd:int", mapper.toXMLSchemaDataType(Integer.class));
        assertEquals("Long", "xsd:long", mapper.toXMLSchemaDataType(Long.TYPE));
        assertEquals("long", "xsd:long", mapper.toXMLSchemaDataType(Long.class));
        assertEquals("Short", "xsd:short", mapper.toXMLSchemaDataType(Short.TYPE));
        assertEquals("short", "xsd:short", mapper.toXMLSchemaDataType(Short.class));
        assertEquals("Float", "xsd:float", mapper.toXMLSchemaDataType(Float.TYPE));
        assertEquals("float", "xsd:float", mapper.toXMLSchemaDataType(Float.class));
        assertEquals("Double", "xsd:double", mapper.toXMLSchemaDataType(Double.TYPE));
        assertEquals("double", "xsd:double", mapper.toXMLSchemaDataType(Double.class));
        assertEquals("Boolean", "xsd:boolean", mapper.toXMLSchemaDataType(Boolean.TYPE));
        assertEquals("boolean", "xsd:boolean", mapper.toXMLSchemaDataType(Boolean.class));
        assertEquals("Byte", "xsd:byte", mapper.toXMLSchemaDataType(Byte.TYPE));
        assertEquals("byte", "xsd:byte", mapper.toXMLSchemaDataType(byte.class));
        assertEquals("java.util.Date", "xsd:dateTime", mapper.toXMLSchemaDataType(java.util.Date.class));
        assertEquals("java.sql.Date", "xsd:date", mapper.toXMLSchemaDataType(java.sql.Date.class));
        assertEquals("java.sql.Time", "xsd:time", mapper.toXMLSchemaDataType(java.sql.Time.class));
    }
    
    public void testDefaultDataTypeTransciption() throws Exception {
        Schema expected = new Schema();
        
        GlobalComplexType allSimplesBeanType = new GlobalComplexType();
        allSimplesBeanType.setName("org.apache.commons.betwixt.schema.AllSimplesBean");
        allSimplesBeanType.addElement(new SimpleLocalElement("string", "xsd:string"));
        allSimplesBeanType.addElement(new SimpleLocalElement("bigInteger", "xsd:integer"));
        allSimplesBeanType.addElement(new SimpleLocalElement("primitiveInt", "xsd:int"));
        allSimplesBeanType.addElement(new SimpleLocalElement("objectInt", "xsd:int"));
        allSimplesBeanType.addElement(new SimpleLocalElement("primitiveLong", "xsd:long"));
        allSimplesBeanType.addElement(new SimpleLocalElement("objectLong", "xsd:long"));
        allSimplesBeanType.addElement(new SimpleLocalElement("primitiveShort", "xsd:short"));
        allSimplesBeanType.addElement(new SimpleLocalElement("objectShort", "xsd:short"));
        allSimplesBeanType.addElement(new SimpleLocalElement("bigDecimal", "xsd:decimal"));
        allSimplesBeanType.addElement(new SimpleLocalElement("primitiveFloat", "xsd:float"));
        allSimplesBeanType.addElement(new SimpleLocalElement("objectFloat", "xsd:float"));
        allSimplesBeanType.addElement(new SimpleLocalElement("primitiveDouble", "xsd:double"));
        allSimplesBeanType.addElement(new SimpleLocalElement("objectDouble", "xsd:double"));
        allSimplesBeanType.addElement(new SimpleLocalElement("primitiveBoolean", "xsd:boolean"));
        allSimplesBeanType.addElement(new SimpleLocalElement("objectBoolean", "xsd:boolean"));
        allSimplesBeanType.addElement(new SimpleLocalElement("primitiveByte", "xsd:byte"));
        allSimplesBeanType.addElement(new SimpleLocalElement("objectByte", "xsd:byte"));
        allSimplesBeanType.addElement(new SimpleLocalElement("utilDate", "xsd:dateTime"));
        allSimplesBeanType.addElement(new SimpleLocalElement("sqlDate", "xsd:date"));
        allSimplesBeanType.addElement(new SimpleLocalElement("sqlTime", "xsd:time"));

        GlobalElement root = new GlobalElement("AllSimplesBean", "org.apache.commons.betwixt.schema.AllSimplesBean");
        expected.addComplexType(allSimplesBeanType);
        expected.addElement(root);
        
        SchemaTranscriber transcriber = new SchemaTranscriber();
        transcriber.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
        Schema out = transcriber.generate(AllSimplesBean.class);
        
        assertEquals("AllSimplesBean schema", expected, out);        
    }
}
