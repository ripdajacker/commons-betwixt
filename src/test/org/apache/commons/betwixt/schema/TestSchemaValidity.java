/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/schema/TestSchemaValidity.java,v 1.1.2.6 2004/02/07 14:44:45 rdonkin Exp $
 * $Revision: 1.1.2.6 $
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

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;
import org.xml.sax.InputSource;

/**
 * Tests for the validity of the schema produced.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.6 $
 */
public class TestSchemaValidity extends AbstractTestCase {

    public TestSchemaValidity(String name) {
        super(name);
    }
    
    
    private String generateSchema(Class clazz) throws Exception {
        SchemaTranscriber transcriber = new SchemaTranscriber();
        transcriber.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
        Schema schema = transcriber.generate(clazz);
        
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.setBindingConfiguration(transcriber.createSchemaBindingConfiguration());
        writer.getXMLIntrospector().setConfiguration(transcriber.createSchemaIntrospectionConfiguration());
        writer.write(schema);
        
        String xsd = out.getBuffer().toString();
        return xsd;
    }
    
    public void testSimplestBeanWithAttributes() throws Exception {
       String xsd = generateSchema(SimplestBean.class);
            
       StringWriter out = new StringWriter();
       out.write("<?xml version='1.0'?>");
       BeanWriter writer = new BeanWriter(out);
       writer.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
       writer.getXMLIntrospector().getConfiguration().getPrefixMapper().setPrefix(SchemaTranscriber.W3C_SCHEMA_INSTANCE_URI, "xsi");
       writer.getBindingConfiguration().setMapIDs(false);
       SimplestBean bean = new SimplestBean("Simon");
       writer.write(bean);
       
       String xml = out.getBuffer().toString();
       
       xmlAssertIsValid(new InputSource(new StringReader(xml)), new InputSource(new StringReader(xsd)));
    }   
    
    
    public void testSimplestBeanWithElements() throws Exception {
       String xsd = generateSchema(SimplestElementBean.class);
            
       StringWriter out = new StringWriter();
       out.write("<?xml version='1.0'?>");
       BeanWriter writer = new BeanWriter(out);
       writer.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
       writer.getXMLIntrospector().getConfiguration().getPrefixMapper().setPrefix(SchemaTranscriber.W3C_SCHEMA_INSTANCE_URI, "xsi");
       writer.getBindingConfiguration().setMapIDs(false);
       SimplestElementBean bean = new SimplestElementBean("Simon");
       writer.write(bean);
       
       String xml = out.getBuffer().toString();
       
       xmlAssertIsValid(new InputSource(new StringReader(xml)), new InputSource(new StringReader(xsd)));
    }   
    
    
    public void testSimpleBean() throws Exception {
       String xsd = generateSchema(SimpleBean.class);
            
       StringWriter out = new StringWriter();
       out.write("<?xml version='1.0'?>");
       BeanWriter writer = new BeanWriter(out);
       writer.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
       writer.getXMLIntrospector().getConfiguration().getPrefixMapper().setPrefix(SchemaTranscriber.W3C_SCHEMA_INSTANCE_URI, "xsi");
       writer.getBindingConfiguration().setMapIDs(false);
       SimpleBean bean = new SimpleBean("One", "Two", "A", "One, Two, Three, Four");
       writer.write(bean);
       
       String xml = out.getBuffer().toString();
       
       xmlAssertIsValid(new InputSource(new StringReader(xml)), new InputSource(new StringReader(xsd)));
    }
   
    private String generateOrderLineSchema() throws Exception {
        SchemaTranscriber transcriber = new SchemaTranscriber();
        transcriber.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
        transcriber.getXMLIntrospector().getConfiguration().setAttributeNameMapper(new HyphenatedNameMapper());
        Schema schema = transcriber.generate(OrderLineBean.class);
        
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.setBindingConfiguration(transcriber.createSchemaBindingConfiguration());
        writer.getXMLIntrospector().setConfiguration(transcriber.createSchemaIntrospectionConfiguration());
        writer.write(schema);
        
        String xsd = out.getBuffer().toString();
        return xsd;
    }
   
    public void testOrderLine() throws Exception {

       String xsd = generateOrderLineSchema();
       StringWriter out = new StringWriter();
       out.write("<?xml version='1.0'?>");
       BeanWriter writer = new BeanWriter(out);
       writer.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
       writer.getXMLIntrospector().getConfiguration().setAttributeNameMapper(new HyphenatedNameMapper());
       writer.getXMLIntrospector().getConfiguration().getPrefixMapper().setPrefix(SchemaTranscriber.W3C_SCHEMA_INSTANCE_URI, "xsi");
       writer.getBindingConfiguration().setMapIDs(false);
       OrderLineBean bean = new OrderLineBean(3, new ProductBean("00112234", "A11", "Fat Fish", "A Fat Fish"));
       writer.write(bean);
       
       String xml = out.getBuffer().toString();
       
       xmlAssertIsValid(new InputSource(new StringReader(xml)), new InputSource(new StringReader(xsd)));
    }      
    
    private String generateOrderSchema() throws Exception {
        SchemaTranscriber transcriber = new SchemaTranscriber();
        transcriber.getXMLIntrospector().getConfiguration().setElementNameMapper(new HyphenatedNameMapper());
        transcriber.getXMLIntrospector().getConfiguration().setAttributeNameMapper(new HyphenatedNameMapper());
        transcriber.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
        transcriber.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(false);
        Schema schema = transcriber.generate(OrderBean.class);
        
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.setBindingConfiguration(transcriber.createSchemaBindingConfiguration());
        writer.getXMLIntrospector().setConfiguration(transcriber.createSchemaIntrospectionConfiguration());
        writer.write(schema);
        
        String xsd = out.getBuffer().toString();
        return xsd;
    }
    
    public void testOrder() throws Exception {
        String xsd = generateOrderSchema();
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.getXMLIntrospector().getConfiguration().setElementNameMapper(new HyphenatedNameMapper());
        writer.getXMLIntrospector().getConfiguration().setAttributeNameMapper(new HyphenatedNameMapper());
        writer.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(true);
        writer.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(false);
        writer.getBindingConfiguration().setMapIDs(false);
        
        OrderBean bean = new OrderBean("XA-2231", 
            new CustomerBean("PB34", "Mr Abbot", "1, Skipton Road","Shipley", "Merry England", "BD4 8KL"));
        bean.addLine(
              new OrderLineBean(4, new ProductBean("00112234", "A11", "Taylor's Landlord", "Taylor's Landlord")));
        bean.addLine(
              new OrderLineBean(5, new ProductBean("00112235", "A13", "Black Sheep Special", "Black Sheep Special")));
        writer.write(bean);
       
        String xml = out.getBuffer().toString();
       
        xmlAssertIsValid(new InputSource(new StringReader(xml)), new InputSource(new StringReader(xsd)));  
        
    }
}
