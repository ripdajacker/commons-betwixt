/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/schema/Element.java,v 1.1.2.5 2004/02/08 12:11:17 rdonkin Exp $
 * $Revision: 1.1.2.5 $
 * $Date: 2004/02/08 12:11:17 $
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



/**
 * Models the Element tag in the XML schema.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.5 $
 */
public class Element {
	//TODO: going to ignore the issue of namespacing for the moment
	public static final String STRING_SIMPLE_TYPE="xsd:string";
	
	private String name;
	private String type;

    private GlobalComplexType complexType;
	
	public Element() {}
    
    public Element(String name, String type) {
        setName(name);
        setType(type);
    }
    
    public Element(String name, GlobalComplexType complexType) {
        setName(name);
        setComplexType(complexType);
    }
    

    

    /**
     * Gets the element name
     * @return element name, not null
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the element name
     * @param string not null
     */
    public void setName(String string) {
        name = string;
    }

    /**
     * Gets the element type
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the element type
     * @param string
     */
    public void setType(String string) {
        type = string;
    }


    /**
     * Gets the anonymous type definition for this element, if one exists.
     * @return ComplexType, null if there is no associated anonymous type definition
     */
    public GlobalComplexType getComplexType() {
        return complexType;
    }

    /**
     * Sets the anonymous type definition for this element
     * @param type ComplexType to be set as the anonymous type definition, 
     * null if the type is to be referenced
     */
    public void setComplexType(GlobalComplexType type) {
        this.type = type.getName();
        complexType = type;
    }    

	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof Element) {
            Element element = (Element) obj;
            result = isEqual(type, element.type) &&
                     isEqual(name, element.name);   		
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
        buffer.append("<xsd:element name='");
        buffer.append(name);
        buffer.append("' type='");
        buffer.append(type);
        buffer.append("'>");
        
        if (complexType != null) {
            buffer.append(complexType);
        }
        buffer.append("</xsd:element>");
        return buffer.toString();
    }


}
