/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/io/read/TestReadContext.java,v 1.1.2.1 2004/01/13 21:53:18 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/01/13 21:53:18 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * $Id: TestReadContext.java,v 1.1.2.1 2004/01/13 21:53:18 rdonkin Exp $
 */
package org.apache.commons.betwixt.io.read;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.collections.CollectionUtils;

/** 
 * Test harness for ReadContext
 * 
 * @author Robert Burrell Donkin
 * @version $Id: TestReadContext.java,v 1.1.2.1 2004/01/13 21:53:18 rdonkin Exp $
 */
public class TestReadContext extends AbstractTestCase {

    public TestReadContext(String name) {
        super(name);
    }
        
    public static Test suite() {
        return new TestSuite(TestReadContext.class);
    }    
    
    public void testElementStackPushPop() {
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());
        context.pushElement("alpha");
        assertEquals("Push then pop", "alpha", context.popElement());
        assertEquals("Push then pop at bottom", null, context.popElement());
        
        context.pushElement("beta");
        context.pushElement("delta");
        context.pushElement("gamma");
        assertEquals("Triple push (1)", "gamma", context.popElement());
        assertEquals("Triple push (2)", "delta", context.popElement());
        assertEquals("Triple push (3)", "beta", context.popElement());
        assertEquals("Triple push at bottom", null, context.popElement());
           
    }
       
    public void testElementStackMarkedPushPop() {
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());

        context.pushElement("beta");
        context.pushElement("delta");
        context.markClassMap(Object.class);
        context.pushElement("gamma");
        assertEquals("One mark (1)", "gamma", context.popElement());
        assertEquals("One mark (2)", "delta", context.popElement());
        assertEquals("One mark (3)", "beta", context.popElement());
        assertEquals("One mark at bottom", null, context.popElement());
           
        context.markClassMap(Object.class);
        context.pushElement("beta");
        context.pushElement("delta");
        context.markClassMap(Object.class);
        context.pushElement("gamma");
        context.markClassMap(Object.class);
        assertEquals("Three marks (1)", "gamma", context.popElement());
        assertEquals("Three marks (2)", "delta", context.popElement());
        assertEquals("Three marks (3)", "beta", context.popElement());
        assertEquals("Three marks at bottom", null, context.popElement());
    }
    
    public void testLastMappedClassNoClass()
    {
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());
        context.pushElement("beta");
        context.pushElement("delta");
        context.pushElement("gamma");
        assertEquals("No class", null, context.getLastMappedClass());
    }
    
    public void testLastMappedClassBottomClass()
    {
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());

        context.markClassMap(Object.class);
        context.pushElement("beta");
        context.pushElement("delta");
        context.pushElement("gamma");
        assertEquals("One classes", Object.class, context.getLastMappedClass());
    }
    
    public void testLastMappedClassTwoClasses()
    {
        
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());    
        context.markClassMap(Object.class);
        context.pushElement("beta");
        context.pushElement("delta");
        context.markClassMap(String.class);
        context.pushElement("gamma");
        assertEquals("Two classes", String.class, context.getLastMappedClass());
    }
    
    public void testLastMappedClassTopClass()
    {
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());    
        context.markClassMap(Object.class);
        context.pushElement("beta");
        context.pushElement("delta");
        context.markClassMap(String.class);
        context.pushElement("gamma");
        context.markClassMap(Integer.class);
        assertEquals("Top class", Integer.class, context.getLastMappedClass());
    }

    public void testRelativeElementPathBase()
    {
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());
        ArrayList elements = new ArrayList();
        
        context.pushElement("alpha");
        context.markClassMap(Object.class);
        context.pushElement("beta");
        context.pushElement("delta");
        context.pushElement("gamma");
    	CollectionUtils.addAll(elements, context.getRelativeElementPathIterator());
        
        assertEquals("Path element count (1)", 3 , elements.size());
        assertEquals("Element name 0", "beta", elements.get(0));
        assertEquals("Element name 1", "delta", elements.get(1));
        assertEquals("Element name 2", "gamma", elements.get(2));
    }
    

    public void testRelativeElementPathTwoMarks()
    {
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());
        ArrayList elements = new ArrayList();
        
        context.pushElement("alpha");
        context.markClassMap(Object.class);
        context.pushElement("beta");
        context.pushElement("delta");
        context.markClassMap(Object.class);
        context.pushElement("gamma");
    	CollectionUtils.addAll(elements, context.getRelativeElementPathIterator());
        
        assertEquals("Path element count (1)", 1 , elements.size());
        assertEquals("Element name", "gamma", elements.get(0));
    }


    public void testRelativeElementPathTopMark()
    {
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());
        ArrayList elements = new ArrayList();
        
        context.pushElement("alpha");
        context.pushElement("beta");
        context.pushElement("delta");
        context.pushElement("gamma");
        context.markClassMap(Object.class);
    	CollectionUtils.addAll(elements, context.getRelativeElementPathIterator());
        
        assertEquals("Path element count (0)", 0 , elements.size());
    }

    public void testRelativeElementPathRootMark()
    {
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());
        ArrayList elements = new ArrayList();
 
        context.markClassMap(Object.class);
        context.pushElement("alpha");
        context.pushElement("beta");
        context.pushElement("delta");
        context.pushElement("gamma");
    	CollectionUtils.addAll(elements, context.getRelativeElementPathIterator());
        
        assertEquals("Path element count (4)", 4 , elements.size());
        assertEquals("Element name (0)", "alpha", elements.get(0));
        assertEquals("Element name (1)", "beta", elements.get(1));
        assertEquals("Element name (2)", "delta", elements.get(2));
        assertEquals("Element name (3)", "gamma", elements.get(3));

    }
    
    public void testRelativeElementPathNoMark()
    {
        ReadContext context = new ReadContext(
                    new BindingConfiguration(), 
                    new ReadConfiguration());
        ArrayList elements = new ArrayList();
 
        context.pushElement("alpha");
        context.pushElement("beta");
        context.pushElement("delta");
        context.pushElement("gamma");
    	CollectionUtils.addAll(elements, context.getRelativeElementPathIterator());
        
        assertEquals("Path element count (4)", 4 , elements.size());
        assertEquals("Element name (0)", "alpha", elements.get(0));
        assertEquals("Element name (1)", "beta", elements.get(1));
        assertEquals("Element name (2)", "delta", elements.get(2));
        assertEquals("Element name (3)", "gamma", elements.get(3));

    }
}
