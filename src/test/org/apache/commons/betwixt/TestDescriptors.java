/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/TestDescriptors.java,v 1.4.2.4 2004/04/27 20:00:04 rdonkin Exp $
 * $Revision: 1.4.2.4 $
 * $Date: 2004/04/27 20:00:04 $
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
package org.apache.commons.betwixt;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/** Test harness for the Descriptors (ElementDescriptor and so on).
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.4.2.4 $
  */
public class TestDescriptors extends AbstractTestCase {
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestDescriptors.class);
    }
    
    public TestDescriptors(String testName) {
        super(testName);
    }
    
    public void testElementDescriptorLazyInit() {
        ElementDescriptor descriptor = new ElementDescriptor();
        
        // check for NPEs
        assertTrue("Empty descriptor has no children", !descriptor.hasChildren());
        assertTrue("Empty descriptor has no content", !descriptor.hasContent());
        assertTrue("Empty descriptor has no attributes", !descriptor.hasAttributes());
        
        // add an attribute and make sure everything works
        descriptor.addAttributeDescriptor(new AttributeDescriptor("test:one"));
        assertTrue("Empty descriptor has no children", !descriptor.hasChildren());
        assertTrue("Empty descriptor has no content", !descriptor.hasContent());
        assertTrue("Descriptor has attributes (1)", descriptor.hasAttributes());        
                
        // add an element and make sure everything works
        descriptor.addElementDescriptor(new ElementDescriptor("test:two"));
        assertTrue("Descriptor has children (1)", descriptor.hasChildren());
        assertTrue("Descriptor has content (1)", descriptor.hasContent());
        assertTrue("Descriptor has attributes (2)", descriptor.hasAttributes());        
        
        // start again and test in reverse order
        descriptor = new ElementDescriptor();
        
        // add an element and make sure everything works
        descriptor.addElementDescriptor(new ElementDescriptor("test:one"));
        assertTrue("Descriptor has children (2)", descriptor.hasChildren());
        assertTrue("Descriptor has content (2)", descriptor.hasContent());
        assertTrue("Descriptor has no attributes (1)", !descriptor.hasAttributes());      
        
        // add an attribute and make sure everything works
        descriptor.addAttributeDescriptor(new AttributeDescriptor("test:two"));
        assertTrue("Descriptor has children (3)", descriptor.hasChildren());
        assertTrue("Descriptor has content (3)", descriptor.hasContent());
        assertTrue("Descriptor has attributes (2)", descriptor.hasAttributes());        
        
        // try adding content
        descriptor = new ElementDescriptor();
        descriptor.addContentDescriptor(new AttributeDescriptor("test:one"));
        assertTrue("Descriptor has no children (1)", !descriptor.hasChildren());
        assertTrue("Descriptor has content (3)", descriptor.hasContent());
        assertTrue("Descriptor has no attributes (2)", !descriptor.hasAttributes());        
        
        // add an element and make sure everything works
        descriptor.addElementDescriptor(new ElementDescriptor("test:two"));
        assertTrue("Descriptor has children (4)", descriptor.hasChildren());
        assertTrue("Descriptor has content (4)", descriptor.hasContent());
        assertTrue("Descriptor has no attributes (3)", !descriptor.hasAttributes());      
        
        // add an attribute and make sure everything works
        descriptor.addAttributeDescriptor(new AttributeDescriptor("test:three"));
        assertTrue("Descriptor has children (5)", descriptor.hasChildren());
        assertTrue("Descriptor has content (5)", descriptor.hasContent());
        assertTrue("Descriptor has attributes (3)", descriptor.hasAttributes());       
    }
    
    public void testGetElementDescriptorByName() 
    {
        ElementDescriptor descriptor = new ElementDescriptor("Flintstones");
        descriptor.addElementDescriptor(new ElementDescriptor("Freddy"));
        descriptor.addElementDescriptor(new ElementDescriptor("Wilma"));
        descriptor.addElementDescriptor(new ElementDescriptor("Pebbles"));
        
        ElementDescriptor returned = descriptor.getElementDescriptor("Freddy");
        assertTrue("Freddy is a Flintstone", returned != null);
        assertEquals("Freddy is the right flintstone", "Freddy", returned.getLocalName());
        
        returned = descriptor.getElementDescriptor("Wilma");
        assertTrue("Wilma is a Flintstone", returned != null);
        assertEquals("Wilma is the right flintstone", "Wilma", returned.getLocalName());
        
        returned = descriptor.getElementDescriptor("Barney");
        assertTrue("Barney is not a Flintstone", returned == null);
    }
    
    public void testGetElementDescriptorByNameNullMatch() 
    {
        ElementDescriptor descriptor = new ElementDescriptor("Flintstones");
        descriptor.addElementDescriptor(new ElementDescriptor("Freddy"));
        descriptor.addElementDescriptor(new ElementDescriptor("Wilma"));
        descriptor.addElementDescriptor(new ElementDescriptor("Pebbles"));
        descriptor.addElementDescriptor(new ElementDescriptor());
        
        ElementDescriptor returned = descriptor.getElementDescriptor("NotFreddy");
        assertTrue("NotFreddy matched", returned != null);
        assertEquals("NotFreddy match by null descriptor", null, returned.getLocalName());
    }
}

