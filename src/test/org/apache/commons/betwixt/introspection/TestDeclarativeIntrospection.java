/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/introspection/TestDeclarativeIntrospection.java,v 1.1.2.1 2004/01/18 12:30:58 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/01/18 12:30:58 $
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

package org.apache.commons.betwixt.introspection;

import java.util.List;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;

/**
 * Tests for the new, more declarative style of introspection.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class TestDeclarativeIntrospection extends AbstractTestCase{
    public TestDeclarativeIntrospection(String name) {
        super(name);
    }
    
    public void _testWrappedCollective() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.setWrapCollectionsInElement(true);
        introspector.setAttributesForPrimitives(true);
        XMLBeanInfo out = introspector.introspect(PhoneBookBean.class);
        
        // with wrapped collective, we expect a spacer element descriptor 
        // (for the collective) containing a single collective descriptor
        ElementDescriptor phoneBookBeanDescriptor = out.getElementDescriptor();
        ElementDescriptor[] phoneBookChildDescriptors = phoneBookBeanDescriptor.getElementDescriptors();
        assertEquals("Expected single wrapping descriptor", 1, phoneBookChildDescriptors.length);
        
        ElementDescriptor wrappingDescriptor = phoneBookChildDescriptors[0];
        assertNull("Spacer should not have an updater", wrappingDescriptor.getUpdater());
        assertEquals("Wrapper element name should match getter", "numbers" , wrappingDescriptor.getQualifiedName());
        
        ElementDescriptor[] wrappingChildDescriptors = wrappingDescriptor.getElementDescriptors();
        assertEquals("Expected single child for wrapping descriptor", 1, wrappingChildDescriptors.length);
        
        ElementDescriptor hollowPhoneNumberDescriptor = wrappingChildDescriptors[0];
        assertTrue("Expected wrapped descriptor to be hollow", hollowPhoneNumberDescriptor.isHollow());
        assertEquals("Expected the collective property type to be a list", 
                    List.class, 
                    hollowPhoneNumberDescriptor.getPropertyType());
        assertEquals("Expected the singular property type to be the phone number", 
                    PhoneBookBean.class, 
                    hollowPhoneNumberDescriptor.getSingularPropertyType());
        
        assertEquals("Collective element name should match adder", "number" , hollowPhoneNumberDescriptor.getQualifiedName());

    }
    
    public void _testUnwrappedCollective() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.setWrapCollectionsInElement(false);
        introspector.setAttributesForPrimitives(true);
        XMLBeanInfo out = introspector.introspect(PhoneBookBean.class);
        
        // with wrapped collective, we expect a spacer element descriptor 
        // (for the collective) containing a single collective descriptor
        ElementDescriptor phoneBookBeanDescriptor = out.getElementDescriptor();
        ElementDescriptor[] phoneBookChildDescriptors = phoneBookBeanDescriptor.getElementDescriptors();
        assertEquals("Expected single child descriptor", 1, phoneBookChildDescriptors.length);
        
        ElementDescriptor hollowPhoneNumberDescriptor = phoneBookChildDescriptors[0];

        assertTrue("Expected collective element descriptor to be hollow", hollowPhoneNumberDescriptor.isHollow());
        assertEquals("Expected the collective property type to be a list", 
                    List.class, 
                    hollowPhoneNumberDescriptor.getPropertyType());
        assertEquals("Expected the singular property type to be the phone number", 
                    PhoneBookBean.class, 
                    hollowPhoneNumberDescriptor.getSingularPropertyType());
        assertEquals("Collective element name should match adder", "number" , hollowPhoneNumberDescriptor.getQualifiedName());
    }
    
    public void testIsSimpleForPrimitives() throws Exception {
        XMLIntrospector introspector = new XMLIntrospector();
        introspector.setWrapCollectionsInElement(true);
        introspector.setAttributesForPrimitives(false);
        XMLBeanInfo out = introspector.introspect(PhoneNumberBean.class);
        
        // the bean is mapped to a complex type structure and so should not be simple
        ElementDescriptor phoneNumberDescriptor = out.getElementDescriptor();
        
        assertFalse("Phone number descriptor is complex", phoneNumberDescriptor.isSimple());
        
        ElementDescriptor[] phoneNumberChildDescriptors = phoneNumberDescriptor.getElementDescriptors();
        assertEquals("Expected three child elements", 3, phoneNumberChildDescriptors.length);
         
        // all children should be simple
        assertTrue("Descriptor " + phoneNumberChildDescriptors[0] + " should be simple", 
                    phoneNumberChildDescriptors[0].isSimple());
        assertTrue("Descriptor " + phoneNumberChildDescriptors[1] + " should be simple", 
                    phoneNumberChildDescriptors[1].isSimple());
        assertTrue("Descriptor " + phoneNumberChildDescriptors[2] + " should be simple", 
                    phoneNumberChildDescriptors[2].isSimple());
    }
}
