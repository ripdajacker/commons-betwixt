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

/**
 * A beanmapper which converts a type to start with an uppercase.
 * So eg elementName should return ElementName
 * 
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id$
 */
public class CapitalizeNameMapper implements NameMapper {

    /**
     * Capitalize first letter of type name.
     * 
     * @param typeName the string to convert
     * @return <code>typeName</code> after first letter has been converted to upper case
     */
    public String mapTypeToElementName(String typeName) {
        if (typeName == null || typeName.length() ==0) {
            return typeName;
        }
        StringBuffer sb = new StringBuffer(typeName);
        char upperChar = Character.toUpperCase(typeName.charAt(0));
        sb.delete(0,1);
        sb.insert(0, upperChar);
        return sb.toString();
    }

    /**
     * Outputs a brief description.
     */
    public String toString() {
        return "Capitalize Type Name Mapper";
    }
}

