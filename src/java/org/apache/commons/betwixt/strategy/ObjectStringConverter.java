/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/strategy/ObjectStringConverter.java,v 1.3 2003/08/21 22:41:50 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/08/21 22:41:50 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * $Id: ObjectStringConverter.java,v 1.3 2003/08/21 22:41:50 rdonkin Exp $
 */
package org.apache.commons.betwixt.strategy;

import java.io.Serializable;

import org.apache.commons.betwixt.expression.Context;

/** 
 * <p>Strategy class for string &lt;-&gt; object conversions.
 * Implementations of this interface are used by Betwixt to perform
 * string &lt;-&gt; object conversions.
 * This performs only the most basic conversions.
 * Most applications will use a subclass.
 * </p>
 * <p>It is strongly recommended that (in order to support round tripping)
 * that <code>objectToString</code> and <code>stringToObject</code>
 * are inverse functions.
 * In other words, given the same flavour, context and type the applying 
 * objectToString to the result of stringToObject should be equal to the 
 * original input.
 * </p>
 * @author Robert Burrell Donkin 
 * @version
 */
public class ObjectStringConverter implements Serializable {
    
    /**
      * Converts an object to a string representation.
      * This basic implementation returns object.toString() 
      * or an empty string if the given object is null.
      *
      * @param object the object to be converted, possibly null
      * @param type the property class of the object, not null
      * @param flavour a string allow symantic differences in formatting to be communicated
      * @return a String representation, not null
      */
    public String objectToString(Object object, Class type, String flavour, Context context) {
        if ( object != null ) {
            return object.toString();
        } 
        return "";
    }
    
    /**
      * Converts a string representation to an object.
      * It is acceptable for an implementation to return the string if it cannot convert 
      * the string to the given class type.
      * This basic implementation just returns a string.
      * 
      * @param value the String to be converted
      * @param type the property class to be returned (if possible), not null
      * @param flavour a string allow symantic differences in formatting to be communicated
      * @param context not null
      * @return an Object converted from the String, not null
      */
    public Object stringToObject(String value, Class type, String flavour, Context context) {
        return value;
    }
}
