/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/schema/SchemaTranscriber.java,v 1.1.2.1 2004/01/18 12:35:23 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/01/18 12:35:23 $
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

import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;

/**
 * <p>Generates XML Schemas for Betwixt mappings.
 * 
 * </p><p>
 * The basic idea is that an object model for the schema will be created
 * and Betwixt can be used to output this to xml.
 * This should allow both SAX and text.
 * </p>
 * TODO: it's very likely that strategies will be needed to allow flexibility in mapping later
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class SchemaTranscriber {
	
	/** Used to introspect beans in order to generate XML */
	private XMLIntrospector introspector = new XMLIntrospector();
	
	public SchemaTranscriber() {}
	 
	/**
	 * Gets the XMLIntrospector used to create XMLInfoBean's.
	 * @return XMLIntrospector used to create XMLInfoBean's used to generate schema, not null
	 */
	public XMLIntrospector getXMLIntrospector() {
		return introspector;
	}
	
	/**
	 * Sets the XMLIntrospector used to create XMLInfoBeans'
	 * @param introspector XMLIntrospector used to create XMLInfoBean's used to generate schema, not null
	 */
	public void setXMLIntrospector(XMLIntrospector introspector) {
		this.introspector = introspector;
	}
	
	
	/**
	 * Generates an XML Schema model for the given class.
	 * @param clazz not null
	 * @return Schema model, not null
	 */
	public Schema generate(Class clazz) throws IntrospectionException {
        XMLBeanInfo beanInfo = introspector.introspect(clazz);
		return generate(beanInfo);
	}
	
	/**
	 * Generates an XML Schema model from the given XMLBeanInfo
	 * @param xmlBeanInfo not null
	 * @return Schema model, not null
	 */
	public Schema generate(XMLBeanInfo xmlBeanInfo) {
       ElementDescriptor elementDescriptor = xmlBeanInfo.getElementDescriptor(); 
	   Schema schema = new Schema();
       schema.addGlobalElementType(elementDescriptor);
       return schema;
	}
}
