/*
 * Copyright 2004 The Apache Software Foundation.
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

import org.apache.commons.betwixt.AttributeDescriptor;

/**
 * Determines whether the expression of an attribute with a values 
 * should be suppressed.
 *
 * @since 0.7 
 * @author <a href='http://jakarta.apache.org/commons'>Jakarta Commons Team</a>, <a href='http://www.apache.org'>Apache Software Foundation</a>
 */
public abstract class ValueSuppressionStrategy {
    
    /**
     * Strategy allows all values to be expressed for all attributes
     */
    public static final ValueSuppressionStrategy ALLOW_ALL_VALUES = new ValueSuppressionStrategy() {
        public boolean suppressAttribute(AttributeDescriptor attributeDescriptor, String value) {
            return true;
        }
    };

    /**
     * Suppresses all null values.
     */
    public static final ValueSuppressionStrategy SUPPRESS_EMPTY = new ValueSuppressionStrategy() {
        public boolean suppressAttribute(AttributeDescriptor attributeDescriptor, String value) {
            return "".equals(value);
        } 
    };
    
    /**
     * Default strategy is {@link #SUPPRESS_EMPTY}.
     */
    public static final ValueSuppressionStrategy DEFAULT = SUPPRESS_EMPTY;

    
    /**
     * Should the given attribute value be suppressed?
     * @param attributeDescriptor <code>AttributeDescriptor</code> describing the attribute, not null
     * @param value <code>Object</code> value, possibly null
     * @return true if the attribute should not be written for the given value
     */
    public abstract boolean suppressAttribute(AttributeDescriptor attributeDescriptor, String value);

}
