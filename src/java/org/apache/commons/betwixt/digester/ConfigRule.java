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
package org.apache.commons.betwixt.digester;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Digester Rule to process config elements.
 * @since 0.6.1
 * @author Brian Pugh
 */
public class ConfigRule extends RuleSupport {
    /** Logger. */
    private static final Log log = LogFactory.getLog(ConfigRule.class);

    /** Base constructor. */
    public ConfigRule() {
    }
    // Rule interface
    //-------------------------------------------------------------------------
    /**
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
     * @throws org.xml.sax.SAXException if the primitiveTypes attribute contains an invalid value
     */
    public void begin(Attributes attributes) throws SAXException {
        String value = attributes.getValue("primitiveTypes");
        if (value != null) {
            if (value.equalsIgnoreCase("element")) {
                	getXMLInfoDigester().setAttributesForPrimitives(false);
            
            } else if (value.equalsIgnoreCase("attribute")) {
                getXMLInfoDigester().setAttributesForPrimitives(true);
            } else {
                throw new SAXException(
                        "Invalid value inside element <betwixt-config> for attribute 'primitiveTypes'."
                        + " Value should be 'element' or 'attribute'");
            }
        }
        MultiMappingBeanInfoDigester digester = (MultiMappingBeanInfoDigester) getDigester();
        getDigester().push(digester.getBeanInfoMap());
    }
    /**
     * Process the end of this element.
     */
    public void end() {
        Object top = getDigester().pop();
    }


}