
/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/digester/TestXMLIntrospectorHelper.java,v 1.5 2003/10/05 13:53:43 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/10/05 13:53:43 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
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
package org.apache.commons.betwixt.digester;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.CustomerBean;
import org.apache.commons.betwixt.NodeDescriptor;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;

/** Test harness for the XMLIntrospectorHelper
  *
  * @author <a href="mailto:cyu77@yahoo.com">Calvin Yu</a>
  * @version $Revision: 1.5 $
  */
public class TestXMLIntrospectorHelper extends TestCase {

    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite(TestXMLIntrospectorHelper.class);
    }

    public TestXMLIntrospectorHelper(String testName) {
        super(testName);
    }

    /**
     * Test the helper's <code>createDescriptor</code> method when a hyphenated name
     * mapper is set.
     */
    public void testCreateDescriptorWithHyphenatedElementNameMapper() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.setAttributesForPrimitives(false);
        introspector.setElementNameMapper(new HyphenatedNameMapper());
        BeanInfo beanInfo = Introspector.getBeanInfo(CustomerBean.class);

        NodeDescriptor nickNameProperty = createDescriptor("nickName", beanInfo, introspector);
        assertNotNull("nickName property not found", nickNameProperty);
        assertEquals("nick name property", "nick-name", nickNameProperty.getLocalName());

        NodeDescriptor projectNamesProperty = createDescriptor("projectNames", beanInfo, introspector);
        assertNotNull("projectNames property not found", projectNamesProperty);
        assertEquals("project names property", "project-names", projectNamesProperty.getLocalName());
    }
    
    public void testNullParameters() throws Exception {
        XMLIntrospectorHelper.isLoopType(null);
    }

    /**
     * Find the specified property and convert it into a descriptor.
     */
    private NodeDescriptor createDescriptor(String propertyName, BeanInfo beanInfo, XMLIntrospector introspector)
        throws IntrospectionException {
        PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
        for (int i=0; i<properties.length; i++) {
            if (propertyName.equals(properties[i].getName())) {
                NodeDescriptor desc = (NodeDescriptor) introspector
                    .createDescriptor(properties[i],
                                      introspector.isAttributesForPrimitives());
                return desc;
            } 
        }
        return null;
    }

}
