/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.apache.commons.betwixt;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Test harness for the Descriptors (ElementDescriptor and so on).
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.5 $
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
}

