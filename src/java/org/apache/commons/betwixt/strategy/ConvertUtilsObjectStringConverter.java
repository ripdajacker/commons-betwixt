/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/strategy/ConvertUtilsObjectStringConverter.java,v 1.4.2.1 2004/01/13 21:49:46 rdonkin Exp $
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
package org.apache.commons.betwixt.strategy;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.betwixt.expression.Context;

/** 
 * String &lt;-&gt; object conversion strategy that delegates to ConvertUtils.
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.4.2.1 $
 */
public class ConvertUtilsObjectStringConverter extends ObjectStringConverter {
    
    /**
      * Converts an object to a string representation using ConvertUtils.
      *
      * @param object the object to be converted, possibly null
      * @param type the property class of the object, not null
      * @param flavour a string allow symantic differences in formatting 
      * to be communicated (ignored)
      * @param context not null
      * @return a String representation, not null
      */
    public String objectToString(Object object, Class type, String flavour, Context context) {
        if ( object != null ) {
            String text = ConvertUtils.convert( object );
            if ( text != null ) {
                return text;
            }
        }
        return "";
    }
    
    /**
      * Converts an object to a string representation using ConvertUtils.
      * 
      * @param value the String to be converted, not null
      * @param type the property class to be returned (if possible), not null
      * @param flavour a string allow symantic differences in formatting 
      * to be communicated (ignored)
      * @param context not null
      * @return an Object converted from the String, not null
      */
    public Object stringToObject(String value, Class type, String flavour, Context context) {
        return ConvertUtils.convert( value, type );
    }
}
