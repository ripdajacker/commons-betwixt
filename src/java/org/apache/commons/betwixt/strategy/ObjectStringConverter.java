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
      * @param context the context, not null
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
      * @param context the context, not null
      * @return an Object converted from the String, not null
      */
    public Object stringToObject(String value, Class type, String flavour, Context context) {
        return value;
    }
}
