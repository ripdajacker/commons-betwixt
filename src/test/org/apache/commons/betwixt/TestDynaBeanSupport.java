/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/TestDynaBeanSupport.java,v 1.2 2003/10/05 13:56:45 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/05 13:56:45 $
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
package org.apache.commons.betwixt;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.BasicDynaClass;

import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.io.CyclicReferenceException;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.commons.betwixt.digester.XMLIntrospectorHelper;
import org.apache.commons.betwixt.strategy.CapitalizeNameMapper;
import org.apache.commons.betwixt.strategy.HyphenatedNameMapper;
import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;

/** Test harness for the DynaBeans support
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @version $Revision: 1.2 $
  */
public class TestDynaBeanSupport extends AbstractTestCase {
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestDynaBeanSupport.class);
    }
    
    public TestDynaBeanSupport(String testName) {
        super(testName);
    }
        
    public void testIntrospectDynaBean() throws Exception
    {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.setAttributesForPrimitives(false);
        XMLBeanInfo beanInfo = introspector.introspect(createDynasaurClass());
        ElementDescriptor baseElement = beanInfo.getElementDescriptor();
        // no attributes
        assertEquals("Correct number of attributes", 0, baseElement.getAttributeDescriptors().length);
        ElementDescriptor[] descriptors = baseElement.getElementDescriptors();
        assertEquals("Correct number of elements", 3, descriptors.length);
        
        boolean matchedSpecies = false;
        boolean matchedIsRaptor = false;
        boolean matchedPeriod = false;
        
        for (int i=0, size = descriptors.length; i< size; i++) {
            if ("Species".equals(descriptors[i].getPropertyName())) {
                matchedSpecies = true;
            }
            
            if ("isRaptor".equals(descriptors[i].getPropertyName())) {
                matchedIsRaptor = true;
            }
            
            if ("Period".equals(descriptors[i].getPropertyName())) {
                matchedPeriod = true;
            }
        }
        
        assertTrue("Species descriptor not found", matchedSpecies);
        assertTrue("isRaptor descriptor not found", matchedIsRaptor);
        assertTrue("Period descriptor not found", matchedPeriod);
    }
    
    public void testWriteDynaBean() throws Exception {
        DynaBean dynasaur = createDynasaurClass().newInstance();
        dynasaur.set("Species", "Allosaurus");
        dynasaur.set("isRaptor", Boolean.TRUE);
        dynasaur.set("Period", "Jurassic");
        
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.getXMLIntrospector().setElementNameMapper(new DecapitalizeNameMapper());
        writer.write(dynasaur);
        
        String xml = "<?xml version='1.0'?><dynasaur><species>Allosaurus</species>"
            + "<isRaptor>true</isRaptor><period>Jurassic</period></dynasaur>";
        
        xmlAssertIsomorphicContent(	
                            "Test write dyna beans",
                            parseString(xml), 
                            parseString(out.getBuffer().toString()),
                            true); 
    }
    
    public void testOverrideWithDotBetwixt() throws Exception {
        DynaWithDotBetwixt bean = new DynaWithDotBetwixt("Tweedledum","Tweedledee");
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.getXMLIntrospector().setElementNameMapper(new DecapitalizeNameMapper());
        writer.write("bean", bean);
        
        String xml = "<?xml version='1.0'?><bean><ndp>Tweedledum</ndp></bean>";
        xmlAssertIsomorphicContent(	
                            "Test write dyna beans with dt betwixt",
                            parseString(xml), 
                            parseString(out.getBuffer().toString()),
                            true); 
        
    }
    
    private DynaClass createDynasaurClass() throws Exception {
        DynaClass dynaClass = new BasicDynaClass
                ("Dynasaur", null,
                        new DynaProperty[]{
                            new DynaProperty("Species", String.class),
                            new DynaProperty("isRaptor", Boolean.TYPE),
                            new DynaProperty("Period", String.class),
                        });
        return (dynaClass);
    }
}


