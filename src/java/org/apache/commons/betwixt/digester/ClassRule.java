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
package org.apache.commons.betwixt.digester;

import org.apache.commons.betwixt.XMLBeanInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Map;

/**
 * Digester Rule to process class elements.
 *
 * @author Brian Pugh
 * @since 0.7
 */
class ClassRule extends RuleSupport {
    /**
     * Base constructor.
     */
    public ClassRule() {
    }

    // Rule interface
    //-------------------------------------------------------------------------

    /**
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
     * @throws org.xml.sax.SAXException if the primitiveTypes attribute contains an invalid value
     */
    @SuppressWarnings("deprecation")
    public void begin(Attributes attributes) throws SAXException {
        String className = attributes.getValue("name");
        if (className == null || "".equals(className)) {
            throw new SAXException("Invalid 'class' element.Attribute 'name' is required but was not found but was not found.");
        }

        try {

            Class beanClass = loadClass(className);
            XMLBeanInfo xmlBeanInfo = new XMLBeanInfo(beanClass);
            XMLBeanInfoDigester xmlBeanInfoDigester = (XMLBeanInfoDigester) getDigester();
            xmlBeanInfoDigester.setBeanClass(beanClass);
            xmlBeanInfoDigester.push(xmlBeanInfo);

        } catch (ClassNotFoundException e) {
            throw new SAXException("Invalid 'class' element.  Unable to find class: " + className, e);
        }
    }

    /**
     * Process the end of this element.
     */
    @SuppressWarnings("deprecation")
    public void end() {
        XMLBeanInfo xmlBeanInfo = (XMLBeanInfo) getDigester().pop();
        MultiMappingBeanInfoDigester digester = (MultiMappingBeanInfoDigester) getDigester();
        Map xmlBeanInfoMapping = digester.getBeanInfoMap();
        //noinspection unchecked
        xmlBeanInfoMapping.put(xmlBeanInfo.getBeanClass(), xmlBeanInfo);
        digester.setBeanClass(null);
    }
}

