/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/strategy/TestSimpleTypeMapper.java,v 1.1.2.1 2004/02/03 22:31:35 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/02/03 22:31:35 $
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

package org.apache.commons.betwixt.strategy;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.IntrospectionConfiguration;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

/**
 * Tests for SimpleTypeMapper and the associated strategy.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class TestSimpleTypeMapper extends AbstractTestCase {

    public TestSimpleTypeMapper(String name) {
        super(name);
    }
    
    public void testNewStrategy() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.getConfiguration().setSimpleTypeMapper(new StringsAsElementsSimpleTypeMapper());
        introspector.getConfiguration().setWrapCollectionsInElement(true);
        
        XMLBeanInfo beanInfo = introspector.introspect(TuneBean.class);
        ElementDescriptor tuneBeanDescriptor = beanInfo.getElementDescriptor();
        
        AttributeDescriptor[] tuneBeanAttributes = tuneBeanDescriptor.getAttributeDescriptors();
        assertEquals("Only expect one attribute", 1, tuneBeanAttributes.length);
        AttributeDescriptor recordedAttribute = tuneBeanAttributes[0];
        assertEquals("Expected recorded to be bound as an attribute", "recorded", recordedAttribute.getLocalName());
        
        ElementDescriptor[] tuneBeanChildElements = tuneBeanDescriptor.getElementDescriptors();
        assertEquals("Expected three child elements", 3 , tuneBeanChildElements.length);
        
        int bits = 0;
        for (int i=0, size=tuneBeanChildElements.length; i<size; i++) {
            String localName = tuneBeanChildElements[i].getLocalName();
            if ("composers".equals(localName)) {
                bits = bits | 1;
            }
            if ("artist".equals(localName)) {
                bits = bits | 2;
            }      
            if ("name".equals(localName)) {
                bits = bits | 4;
            }          
        }
        
        assertEquals("Every element present", 7, bits);
    }
    
    public void testWrite() throws Exception {
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.getXMLIntrospector().getConfiguration().setSimpleTypeMapper(new StringsAsElementsSimpleTypeMapper());
        writer.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(true);
        writer.getBindingConfiguration().setMapIDs(false);
        
        TuneBean bean = new TuneBean("On The Run", "Pink Floyd", 1972);
        bean.addComposer(new ComposerBean("David", "Gilmour", 1944));
        bean.addComposer(new ComposerBean("Roger", "Waters", 1944));
        
        writer.write(bean);
        
        String xml = out.getBuffer().toString();
        String expected = "<?xml version='1.0'?>" +            "<TuneBean recorded='1972'>" +            "    <name>On The Run</name>" +            "    <artist>Pink Floyd</artist>" +            "    <composers>" +            "       <composer born='1944'>" +            "           <forename>David</forename>" +            "           <surname>Gilmour</surname>" +            "       </composer>" +            "       <composer born='1944'>" +
            "           <forename>Roger</forename>" +
            "           <surname>Waters</surname>" +
            "       </composer>" +            "   </composers>" +            "</TuneBean>";
        
        xmlAssertIsomorphicContent(parseString(xml), parseString(expected), true);
    }
    
    public void testRead() throws Exception {
        
        String xml = "<?xml version='1.0'?>" +
            "<TuneBean recorded='1972'>" +
            "    <name>On The Run</name>" +
            "    <artist>Pink Floyd</artist>" +
            "    <composers>" +
            "       <composer born='1944'>" +
            "           <forename>David</forename>" +
            "           <surname>Gilmour</surname>" +
            "       </composer>" +
            "       <composer born='1944'>" +
            "           <forename>Roger</forename>" +
            "           <surname>Waters</surname>" +
            "       </composer>" +
            "   </composers>" +
            "</TuneBean>";
       StringReader in = new StringReader(xml);
       
       BeanReader reader = new BeanReader();
       reader.getXMLIntrospector().getConfiguration().setSimpleTypeMapper(new StringsAsElementsSimpleTypeMapper());
       reader.getXMLIntrospector().getConfiguration().setWrapCollectionsInElement(true);
       reader.getBindingConfiguration().setMapIDs(false);
       
       reader.registerBeanClass(TuneBean.class);
       
       TuneBean bean = (TuneBean) reader.parse(in);
       
       assertNotNull("Parsing failed", bean);
       assertEquals("Name value", "On The Run", bean.getName());
       assertEquals("Artist value", "Pink Floyd", bean.getArtist());
       assertEquals("Recorded value", 1972, bean.getRecorded());
       
       Collection expectedComposers = new ArrayList();
       expectedComposers.add(new ComposerBean("David", "Gilmour", 1944));
       expectedComposers.add(new ComposerBean("Roger", "Waters", 1944));
       
       assertTrue("Right composers", bean.sameComposers(expectedComposers));
    }
        
    /** Implementation binds strings to elements but everything else to attributes */
    class StringsAsElementsSimpleTypeMapper extends SimpleTypeMapper {

        /**
         * Binds strings to elements but everything else to attributes
         */
        public Binding bind(
                            String propertyName, 
                            Class propertyType, 
                            IntrospectionConfiguration configuration) {
            if (String.class.equals(propertyType)) {
                return SimpleTypeMapper.Binding.ELEMENT;
            }
            return SimpleTypeMapper.Binding.ATTRIBUTE;
        }
               
    }
}
