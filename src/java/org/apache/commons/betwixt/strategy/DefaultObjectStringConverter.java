/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/strategy/DefaultObjectStringConverter.java,v 1.6 2003/10/05 14:10:29 rdonkin Exp $
 * $Revision: 1.6 $
 * $Date: 2003/10/05 14:10:29 $
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
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.Locale;

import org.apache.commons.beanutils.ConversionException;

import org.apache.commons.betwixt.expression.Context;

/** 
 * <p>Default string &lt;-&gt; object conversion strategy.</p>
 * <p>
 * This delegates to ConvertUtils except when the type 
 * is assignable from <code>java.util.Date</code>
 * but not from <code>java.sql.Date</code>.
 * In this case, the format used is (in SimpleDateFormat terms) 
 * <code>EEE MMM dd HH:mm:ss zzz yyyy</code>.
 * This is the same as the output of the toString method on java.util.Date.
 * </p>
 * <p>
 * This should preserve the existing symantic behaviour whilst allowing round tripping of dates
 * (given the default settings).
 * </p>
 * @author Robert Burrell Donkin
 * @version $Revision: 1.6 $
 */
public class DefaultObjectStringConverter extends ConvertUtilsObjectStringConverter {
    
    /** Formats Dates to Strings and Strings to Dates */
    private static final SimpleDateFormat formatter 
        = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.UK);
    
    /**
      * Converts an object to a string representation using ConvertUtils.
      * If the object is a java.util.Date and the type is java.util.Date 
      * but not java.sql.Date
      * then SimpleDateFormat formatting to 
      * <code>EEE MMM dd HH:mm:ss zzz yyyy</code>
      * will be used. 
      * (This is the same as java.util.Date toString would return.)
      *
      * @param object the object to be converted, possibly null
      * @param type the property class of the object, not null
      * @param flavour a string allow symantic differences in formatting 
      * to be communicated (ignored)
      * @param context convert against this context not null
      * @return a String representation, not null
      */
    public String objectToString(Object object, Class type, String flavour, Context context) {
        if ( object != null ) {
            if ( object instanceof java.util.Date && isUtilDate( type ) ) {
                
                return formatter.format( (java.util.Date) object );
                
            } else {
                // use ConvertUtils implementation
                return super.objectToString( object, type, flavour, context );
            }
        }
        return "";
    }
    
    /**
      * Converts an object to a string representation using ConvertUtils.
      * 
      * @param value the String to be converted, not null
      * @param type the property class to be returned (if possible), not null
      * @param flavour a string allow symantic differences 
      * in formatting to be communicated (ignored)
      * @param context not null
      * @return an Object converted from the String, not null
      */
    public Object stringToObject(String value, Class type, String flavour, Context context) {
            if ( isUtilDate( type ) ) {
                try {
                    
                    return formatter.parse( value );
                    
                } catch ( ParseException ex ) { 
                    handleException( ex );
                    // this supports any subclasses that do not which to throw exceptions
                    // probably will result in a problem when the method will be invoked
                    // but never mind
                    return value;
                }
            } else {
                // use ConvertUtils implementation
                return super.stringToObject( value, type, flavour, context );
            }
    }
    
    /** 
      * Allow subclasses to use a different exception handling strategy.
      * This class throws a <code>org.apache.commons.beanutils.ConversionException</code>
      * when conversion fails.
      * @param e the Exception to be handled
      * @throws org.apache.commons.beanutils.ConversionException when conversion fails
      */
    protected void handleException(Exception e) {
        throw new ConversionException( "String to object conversion failed: " + e.getMessage(), e );
    }
    
    /**
      * Is the given type a java.util.Date but not a java.sql.Date?
      * @param type test this class type
      * @return true is this is a until date but not a sql one
      */
    private boolean isUtilDate(Class type) {
        return ( java.util.Date.class.isAssignableFrom(type) 
             && !java.sql.Date.class.isAssignableFrom(type)
             && !java.sql.Time.class.isAssignableFrom(type) 
             && !java.sql.Timestamp.class.isAssignableFrom(type) );
    }
}
