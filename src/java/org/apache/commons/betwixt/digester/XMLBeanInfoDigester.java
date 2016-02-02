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

import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.digester.Digester;

import java.util.HashSet;
import java.util.Set;

/**
 * <p><code>XMLBeanInfoDigester</code> is a digester of XML files
 * containing XMLBeanInfo definitions for a JavaBean.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision$
 */
public class XMLBeanInfoDigester extends Digester {
    /**
     * the beans class for this XML descriptor
     */
    private Class beanClass;

    /**
     * should attributes or elements be used for primitive types
     */
    private boolean attributesForPrimitives;

    /**
     * the set of property names processed so far
     */
    private final Set<String> processedPropertyNameSet = new HashSet<>();

    /**
     * the introspector that is using me
     */
    private XMLIntrospector introspector;

    /**
     * Construct a new XMLBeanInfoDigester with default properties.
     */
    public XMLBeanInfoDigester() {
    }


    /**
     * Gets the class of the bean whose .betwixt file is being processed
     *
     * @return the beans class for this XML descriptor
     */
    public Class getBeanClass() {
        return beanClass;
    }

    /**
     * Sets the beans class for this XML descriptor
     *
     * @param beanClass the <code>Class</code> of the bean being processed
     */
    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }


    /**
     * Gets the property names already processed
     *
     * @return the set of property names that have been processed so far
     */
    public Set<String> getProcessedPropertyNameSet() {
        return processedPropertyNameSet;
    }

    /**
     * Set whether attributes (or elements) should be used for primitive types.
     *
     * @param attributesForPrimitives pass true if primitive properties should be
     *                                written as attributes
     */
    public void setAttributesForPrimitives(boolean attributesForPrimitives) {
        this.attributesForPrimitives = attributesForPrimitives;
        if (introspector != null) {
            introspector.getConfiguration()
                    .setAttributesForPrimitives(attributesForPrimitives);
        }
    }

    /**
     * Gets the XMLIntrospector that's using this digester.
     *
     * @return the introspector that is using me
     */
    public XMLIntrospector getXMLIntrospector() {
        return introspector;
    }

    /**
     * Sets the introspector that is using me
     *
     * @param introspector the <code>XMLIntrospector</code> that using this for .betwixt
     *                     digestion
     */
    public void setXMLIntrospector(XMLIntrospector introspector) {
        this.introspector = introspector;
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Reset configure for new digestion
     */
    protected void configure() {
        if (!configured) {
            configured = true;

            // add the various rules

            addRule("info", new InfoRule());
            addRuleSet(new CommonRuleSet());

        }

        // now initialize
        setAttributesForPrimitives(attributesForPrimitives);
        processedPropertyNameSet.clear();
    }

}
