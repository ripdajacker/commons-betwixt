/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/schema/GlobalComplexType.java,v 1.1.2.1 2004/02/08 12:13:41 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/02/08 12:13:41 $
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

package org.apache.commons.betwixt.schema;

import java.beans.IntrospectionException;
import java.util.Iterator;

import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.collections.CollectionUtils;

/**
 * Models a <code>complexType</code> from an XML schema.
 * A complex type may contain element content and may have attributes.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class GlobalComplexType extends ComplexType {
	
	private String name;
	
	public GlobalComplexType() {}
    
    /**
     * Constructs a new ComplexType from the descriptor given.
     * @param elementDescriptor
     */
    public GlobalComplexType(ElementDescriptor elementDescriptor, Schema schema) throws IntrospectionException {
        super(elementDescriptor, schema);    
    }

    protected void init(ElementDescriptor elementDescriptor, Schema schema) throws IntrospectionException {
        setName(elementDescriptor.getPropertyType().getName());
        super.init(elementDescriptor, schema);
    }

	/**
     * Gets the name of this type.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this type.
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

    public boolean equals(Object obj) {
          boolean result = false;
          if (obj instanceof GlobalComplexType) {
              GlobalComplexType complexType = (GlobalComplexType) obj;
              result =  isEqual(name, complexType.name) &&
                        CollectionUtils.isEqualCollection(attributes, complexType.attributes) &&
                        CollectionUtils.isEqualCollection(elements, complexType.elements);
                                   
          }
          return result;
      }

    public int hashCode() {
        return 0;
    }

      /**
       * Null safe equals method
       * @param one
       * @param two
       * @return
       */
      private boolean isEqual(String one, String two) {
          boolean result = false;
          if (one == null) {
              result = (two == null); 
          }
          else
          {
              result = one.equals(two);
          }
        
          return result;
      }
      
      public String toString() {
          StringBuffer buffer = new StringBuffer();
          buffer.append("<xsd:complexType name='");
          buffer.append(name);
          buffer.append("'>");
          buffer.append("<xsd:sequence>");
          for (Iterator it=elements.iterator(); it.hasNext();) {
                buffer.append(it.next());    
          }
          buffer.append("</xsd:sequence>");
          
          for (Iterator it=attributes.iterator(); it.hasNext();) {
                buffer.append(it.next());    
          }
          buffer.append("</xsd:complexType>");
          return buffer.toString();
      }
}
