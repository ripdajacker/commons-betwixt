/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/schema/DefaultDataTypeMapper.java,v 1.1.2.1 2004/02/23 21:48:40 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/02/23 21:48:40 $
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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Default <code>DataTypeMapper</code>implementation.
 * Provides a reasonably standard and compatible mapping.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class DefaultDataTypeMapper extends DataTypeMapper {

    /**
     * This implementation provides
     * @see org.apache.commons.betwixt.schema.DataTypeMapper#toXMLSchemaDataType(java.lang.Class)
     */
    public String toXMLSchemaDataType(Class type) {
        // default mapping is to string
        String result = "xsd:string";
        if (String.class.equals(type)) {
            result = "xsd:string";
            
        } else if (BigInteger.class.equals(type)) {
            result = "xsd:integer";
            
        } else if (Integer.TYPE.equals(type)) {
            result = "xsd:int";

        } else if (Integer.class.equals(type)) {
            result = "xsd:int";
            
        } else if (Long.TYPE.equals(type)) {
            result = "xsd:long";

        } else if (Long.class.equals(type)) {
            result = "xsd:long";

        } else if (Short.TYPE.equals(type)) {
            result = "xsd:short";

        } else if (Short.class.equals(type)) {
            result = "xsd:short";

        } else if (BigDecimal.class.equals(type)) {
            result = "xsd:decimal";

        } else if (Float.TYPE.equals(type)) {
            result = "xsd:float";

        } else if (Float.class.equals(type)) {
            result = "xsd:float";

        } else if (Double.TYPE.equals(type)) {
            result = "xsd:double";

        } else if (Double.class.equals(type)) {
            result = "xsd:double";

        } else if (Boolean.TYPE.equals(type)) {
            result = "xsd:boolean";

        } else if (Boolean.class.equals(type)) {
            result = "xsd:boolean";

        } else if (Byte.TYPE.equals(type)) {
            result = "xsd:byte";

        } else if (Byte.class.equals(type)) {
            result = "xsd:byte";

        } else if (java.util.Date.class.equals(type)) {
            result = "xsd:dateTime";
            
        } else if (java.sql.Date.class.equals(type)) {
            result = "xsd:date";

        } else if (java.sql.Time.class.equals(type)) {
            result = "xsd:time";
        }
        
        return result;
    }
    
    
}
