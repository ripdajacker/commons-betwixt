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

/**
 * Pluggable strategy specifying whether property's should be surpressed.
 * Implementations can be used to give rules about which properties 
 * should be ignored by Betwixt when introspecting.
 * @author <a href='http://jakarta.apache.org/commons'>Jakarta Commons Team</a>, <a href='http://www.apache.org'>Apache Software Foundation</a>
 */
public abstract class PropertySuppressionStrategy {

    /**
     * Default implementation supresses the class property
     * found on every object.
     */
    public static final PropertySuppressionStrategy DEFAULT = new PropertySuppressionStrategy() {
        public boolean suppressProperty(Class propertyType, String propertyName) {
            boolean result = false;
            // ignore class properties
            if ( Class.class.equals( propertyType) && "class".equals( propertyName ) ) {
                result = true;
            }
            return result;
        }
    };
    
    /**
     * Should the given property be supressed?
     * @param propertyType <code>Class</code> giving the type of the property, not null
     * @param propertyName the name of the property, not null
     * @return true when the given property should be suppressed
     */
    public abstract boolean suppressProperty(Class propertyType, String propertyName);
}
