/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/read/ElementMapping.java,v 1.4.2.1 2004/01/13 21:49:46 rdonkin Exp $
 * $Revision: 1.4.2.1 $
 * $Date: 2004/01/13 21:49:46 $
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
package org.apache.commons.betwixt.io.read;

import org.apache.commons.betwixt.ElementDescriptor;
import org.xml.sax.Attributes;

/**  
  * Describes a mapping between an xml element and a betwixt element.
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.4.2.1 $
  */
public class ElementMapping {
    
    /** Namespace of the xml element */
    private String namespace;
    /** Name of the element */
    private String name;
    /** Attributes associated with this element */
    private Attributes attributes;
    /** The base type of the mapped bean */
    private Class type;
    /** The mapped descriptor */
    private ElementDescriptor descriptor;
   
    /** Base constructor */ 
    public ElementMapping() {}
    
    /**
      * Gets the namespace URI or an empty string if the parser is not namespace aware 
      * or the element has no namespace.
      * @return namespace possibly null
      */
    public String getNamespace() {
        return namespace;
    }
    
    /** 
      * Sets the namespace URI for this element
      * @param namespace the namespace uri, possibly null
      */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    /** 
      * Gets the local name if the parser is namespace aware, otherwise the name.
      * @return the element name, possibly null
      */
    public String getName() {
        return name;
    }
    
    /**
      * Sets the local name for this element.
      * @param name the element name, possibly null
      */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
      * Gets the element's attributes.
      * @return the Attributes for this element, possibly null.
      */
    public Attributes getAttributes() {
        return attributes;
    }
    
    /** 
      * Sets the element's attributes 
      * @param attributes the element's attributes, possibly null
      */ 
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }
    
    /**
      * Gets the base type for this element.
      * The base type may - or may not - correspond to the created type.
      * @return the Class of the base type for this element
      */
    public Class getType() {
        return type;
    }
    
    /**
      * Sets the base type for this element.
      * The base type may - or may not - correspond to the created type.
      * @param type the Class of the base type for this element
      */
    public void setType(Class type) {
        this.type = type;
    }
    
    /**
      * Gets the mapped element descriptor.
      * @return the mapped ElementDescriptor
      */
    public ElementDescriptor getDescriptor() {
        return descriptor;
    }
    
    /** 
      * Sets the mapped element descriptor.
      * @param descriptor set this descriptor
      */
    public void setDescriptor(ElementDescriptor descriptor) {
        this.descriptor = descriptor;
    }
}
