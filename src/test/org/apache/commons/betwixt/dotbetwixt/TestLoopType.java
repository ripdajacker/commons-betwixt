/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/dotbetwixt/TestLoopType.java,v 1.1.2.1 2004/01/20 23:01:26 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/01/20 23:01:26 $
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

package org.apache.commons.betwixt.dotbetwixt;


import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

/**
 * @author Brian Pugh
 */
public class TestLoopType extends TestCase {
    public void testSimpleList() throws Exception {
        Father father = new Father();
        father.setSpouse("Julie");
        father.addKid("John");
        father.addKid("Jane");

        StringWriter outputWriter = new StringWriter();

        outputWriter.write("<?xml version='1.0' ?>\n");
        BeanWriter beanWriter = new BeanWriter(outputWriter);
        beanWriter.enablePrettyPrint();
        beanWriter.getBindingConfiguration().setMapIDs(true);
        beanWriter.write(father);

        BeanReader beanReader = new BeanReader();

        // Configure the reader
        beanReader.registerBeanClass(Father.class);
        StringReader xmlReader = new StringReader(outputWriter.toString());

        //Parse the xml
        Father result = (Father) beanReader.parse(xmlReader);

        assertNotNull("Unexpected null list of children!", result.getKids());
        assertEquals(
            "got wrong number of children",
            father.getKids().size(),
            result.getKids().size());
        assertNull(
            "Spouse should not get set because it is not in the .betwixt file",
            result.getSpouse());
    }

    public void testIgnoredProperty() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        XMLBeanInfo beanInfo = introspector.introspect(IgnoreBean.class);
        ElementDescriptor ignoreDescriptor = beanInfo.getElementDescriptor();
        
        assertEquals("element name matches", "ignore", ignoreDescriptor.getLocalName());
        ElementDescriptor[] childDescriptors = ignoreDescriptor.getElementDescriptors();
        assertEquals("number of child elements", 1, childDescriptors.length);
                    
    }
}

