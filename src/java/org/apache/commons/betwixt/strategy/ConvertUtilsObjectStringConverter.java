/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.lang.reflect.Array;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.betwixt.expression.Context;

/** 
 * String &lt;-&gt; object conversion strategy that delegates to ConvertUtils.
 *
 * @author Robert Burrell Donkin
 * @since 0.5
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
            String text = convertInternal( object );
            if ( text != null ) {
                return text;
            }
        }
        return "";
    }
    
    /**
     * Contains the code from the beanutils 1.7.0 ConvertUtils.convert( Object )
     * This is to prevent backward compatibility issues, which cannot be solved
     * in beanutils because of evolving functionality.<br/>
     * Since the advise here is the override the objectToString methods anyway
     * people can choose themselves to adhere to the new beanutils functionality.
     * 
     * @param value the value to convert to a String
     * @return the String representation or null.
     */
    private String convertInternal( Object value ) {
        if (value == null) {
            return ((String) null);
        } else if (value.getClass().isArray()) {
            if (Array.getLength(value) < 1) {
                return (null);
            }
            value = Array.get(value, 0);
            if (value == null) {
                return ((String) null);
            } else {
                Converter converter = ConvertUtils.lookup(String.class);
                return ((String) converter.convert(String.class, value));
            }
        } else {
            Converter converter = ConvertUtils.lookup(String.class);
            return ((String) converter.convert(String.class, value));
        }
    }
    
    /**
      * Converts an object to a string representation using ConvertUtils.
      * This implementation ignores null and empty string values (rather than converting them).
      * 
      * @param value the String to be converted, not null
      * @param type the property class to be returned (if possible), not null
      * @param flavour a string allow symantic differences in formatting 
      * to be communicated (ignored)
      * @param context not null
      * @return an Object converted from the String, not null
      */
    public Object stringToObject(String value, Class type, String flavour, Context context) {
        if (value == null || "".equals(value))
        {
            return null;    
        }
        
        return ConvertUtils.convert( value, type );
    }
}
