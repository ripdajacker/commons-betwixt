/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/introspection/TestXMLIntrospector.java,v 1.10.2.4 2004/01/18 22:25:23 rdonkin Exp $
 * $Revision: 1.10.2.4 $
 * $Date: 2004/01/18 22:25:23 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 
package org.apache.commons.betwixt.introspection;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.registry.DefaultXMLBeanInfoRegistry;
import org.apache.commons.betwixt.registry.NoCacheRegistry;
import org.apache.commons.betwixt.strategy.ClassNormalizer;
import org.apache.commons.betwixt.strategy.ListedClassNormalizer;
import org.apache.commons.digester.rss.Channel;


/** Test harness for the XMLIntrospector
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.10.2.4 $
  */
public class TestXMLIntrospector extends AbstractTestCase {
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestXMLIntrospector.class);
    }
        
    public TestXMLIntrospector(String testName) {
        super(testName);
    }
    
    public void testIntrospector() throws Exception {
        //SimpleLog log = new SimpleLog("testIntrospector:introspector");
        //log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        XMLIntrospector introspector = new XMLIntrospector();
        //introspector.setLog(log);
        
        introspector.getConfiguration().setAttributesForPrimitives(true);
        
        Object bean = createBean();
        
        XMLBeanInfo info = introspector.introspect( bean );
        
        assertTrue( "Found XMLBeanInfo", info != null );
        
        ElementDescriptor descriptor = info.getElementDescriptor();
        
        assertTrue( "Found root element descriptor", descriptor != null );
        
        AttributeDescriptor[] attributes = descriptor.getAttributeDescriptors();
        
        assertTrue( "Found attributes", attributes != null && attributes.length > 0 );
        
        // test second introspection with caching on
        info = introspector.introspect( bean );
        
        assertTrue( "Found XMLBeanInfo", info != null );
        
        descriptor = info.getElementDescriptor();
        
        assertTrue( "Found root element descriptor", descriptor != null );
        
        attributes = descriptor.getAttributeDescriptors();
        
        assertTrue( "Found attributes", attributes != null && attributes.length > 0 );


        // test introspection with caching off      
        //introspector.setCachingEnabled(false);  
        introspector.setRegistry(new NoCacheRegistry());
        info = introspector.introspect( bean );
        
        assertTrue( "Found XMLBeanInfo", info != null );
        
        descriptor = info.getElementDescriptor();
        
        assertTrue( "Found root element descriptor", descriptor != null );
        
        attributes = descriptor.getAttributeDescriptors();
        
        assertTrue( "Found attributes", attributes != null && attributes.length > 0 );


        // test introspection after flushing cache
//        introspector.setCachingEnabled(true);
        introspector.setRegistry(new DefaultXMLBeanInfoRegistry()); 
        //introspector.flushCache();
        info = introspector.introspect( bean );
        
        assertTrue( "Found XMLBeanInfo", info != null );
        
        descriptor = info.getElementDescriptor();
        
        assertTrue( "Found root element descriptor", descriptor != null );
        
        attributes = descriptor.getAttributeDescriptors();
        
        assertTrue( "Found attributes", attributes != null && attributes.length > 0 );

    }
    
    public void testBeanWithBeanInfo() throws Exception {
        
        // let's check that bean info's ok
        BeanInfo bwbiBeanInfo = Introspector.getBeanInfo(BeanWithBeanInfoBean.class);
        
        PropertyDescriptor[] propertyDescriptors = bwbiBeanInfo.getPropertyDescriptors();

        assertEquals("Wrong number of properties", 2 , propertyDescriptors.length);
        
        // order of properties isn't guarenteed 
        if ("alpha".equals(propertyDescriptors[0].getName())) {
        
            assertEquals("Second property name", "gamma" , propertyDescriptors[1].getName());
            
        } else {
        
            assertEquals("First property name", "gamma" , propertyDescriptors[0].getName());
            assertEquals("Second property name", "alpha" , propertyDescriptors[1].getName());
        }
        
        // finished with the descriptors
        propertyDescriptors = null;

//        SimpleLog log = new SimpleLog("[testBeanWithBeanInfo:XMLIntrospector]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);

        XMLIntrospector introspector = new XMLIntrospector();
        introspector.getConfiguration().setAttributesForPrimitives(false);
//        introspector.setLog(log);
        
        XMLBeanInfo xmlBeanInfo = introspector.introspect(BeanWithBeanInfoBean.class);
        
        ElementDescriptor[] elementDescriptors = xmlBeanInfo.getElementDescriptor().getElementDescriptors();
        
//        log = new SimpleLog("[testBeanWithBeanInfo]");
//        log.setLevel(SimpleLog.LOG_LEVEL_DEBUG);
        
//        log.debug("XMLBeanInfo:");
//        log.debug(xmlBeanInfo);
//        log.debug("Elements:");
//        log.debug(elementDescriptors[0].getPropertyName());
//        log.debug(elementDescriptors[1].getPropertyName());
        
        assertEquals("Wrong number of elements", 2 , elementDescriptors.length);

        // order of properties isn't guarenteed 
        boolean alphaFirst = true;
        if ("alpha".equals(elementDescriptors[0].getPropertyName())) {
            
            assertEquals("Second element name", "gamma" , elementDescriptors[1].getPropertyName());
            
        } else {
            alphaFirst = false;
            assertEquals("First element name", "gamma" , elementDescriptors[0].getPropertyName());
            assertEquals("Second element name", "alpha" , elementDescriptors[1].getPropertyName());
        }
        
        // might as well give test output
        StringWriter out = new StringWriter();
        BeanWriter writer = new BeanWriter(out);
        writer.getBindingConfiguration().setMapIDs(false);
        BeanWithBeanInfoBean bean = new BeanWithBeanInfoBean("alpha value","beta value","gamma value");
        writer.write(bean);
        
        if (alphaFirst) {
        
            xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/introspection/test-bwbi-output-a.xml"),
                    parseString(out.toString()));
        
        } else {
            xmlAssertIsomorphicContent(
                    parseFile("src/test/org/apache/commons/betwixt/introspection/test-bwbi-output-g.xml"),
                    parseString(out.toString()));
        }
    }
    
    public void testDefaultClassNormalizer() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        
        FaceImpl face = new FaceImpl();
        XMLBeanInfo info = introspector.introspect( face );
        ElementDescriptor elementDescriptor = info.getElementDescriptor();
    
        AttributeDescriptor[] attributeDescriptor = elementDescriptor.getAttributeDescriptors();      
        ElementDescriptor[] children = elementDescriptor.getElementDescriptors();
        
        assertEquals("Expected no attributes", 0, attributeDescriptor.length);
        assertEquals("Expected two elements", 2, children.length);
    }	
    
    public void testClassNormalizer() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.getConfiguration().setClassNormalizer( new ClassNormalizer() {
                
                public Class normalize(Class clazz) {
                    if (IFace.class.isAssignableFrom( clazz )) {
                        return IFace.class;
                    }	
                    return super.normalize( clazz );
                }	
            });
        
        FaceImpl face = new FaceImpl();
        XMLBeanInfo info = introspector.introspect( face );
        ElementDescriptor elementDescriptor = info.getElementDescriptor();
        assertEquals("Expected only itself", 1, elementDescriptor.getElementDescriptors().length);
    
        AttributeDescriptor[] attributeDescriptor = elementDescriptor.getAttributeDescriptors();      
        ElementDescriptor[] children = elementDescriptor.getElementDescriptors();
        
        assertEquals("Expected no attributes", 0, attributeDescriptor.length);
        assertEquals("Expected one elements", 1, children.length);
        assertEquals("Expected element", "name", children[0].getLocalName());
    }	
    
    public void testListedClassNormalizer() throws Exception {
        ListedClassNormalizer classNormalizer = new ListedClassNormalizer();
        classNormalizer.addSubstitution( IFace.class );
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.getConfiguration().setClassNormalizer(classNormalizer);
        
        FaceImpl face = new FaceImpl();
        
        XMLBeanInfo info = introspector.introspect( face );
        ElementDescriptor elementDescriptor = info.getElementDescriptor();
        AttributeDescriptor[] attributeDescriptor = elementDescriptor.getAttributeDescriptors();      
        ElementDescriptor[] children = elementDescriptor.getElementDescriptors();
        
        assertEquals("Expected no attributes", 0, attributeDescriptor.length);
        assertEquals("Expected one elements", 1, children.length);
        assertEquals("Expected element", "name", children[0].getLocalName());
    }	    
    
    public void testListedClassNormalizerWrite() throws Exception {
        ListedClassNormalizer classNormalizer = new ListedClassNormalizer();
        classNormalizer.addSubstitution( IFace.class );
        
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter( out );
		writer.getBindingConfiguration().setMapIDs(false);
        writer.getXMLIntrospector().getConfiguration().setClassNormalizer( classNormalizer );
        FaceImpl bean = new FaceImpl();
        bean.setName("Old Tom Cobbly");
        writer.write(bean);
        
        String xml="<?xml version='1.0'?><IFace><name>Old Tom Cobbly</name></IFace>";
        xmlAssertIsomorphicContent(
                            parseString(out.getBuffer().toString()),
                            parseString(xml), 
                            true);
    }	
    
    public void testBetwixtFileType() throws Exception {
    	XMLIntrospector introspector = new XMLIntrospector();
    	XMLBeanInfo info = introspector.introspect( Channel.class );
    	
    	ElementDescriptor elementDescriptor = info.getElementDescriptor();
    	
		Class clazz = elementDescriptor.getSingularPropertyType();
		assertEquals( "Element type correct", Channel.class , clazz);
		
		assertEquals( "Element name correct", "rss", elementDescriptor.getLocalName());
    }
    

}

