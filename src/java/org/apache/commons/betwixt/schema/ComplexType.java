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

package org.apache.commons.betwixt.schema;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;

/**
 * Models a <code>complexType</code>. Global (top level) complex types are
 * represented by {@link GlobalComplexType}. Locally defined or referenced
 * complex types are represented by {@link LocalComplexType}.
 *
 * @author <a href='http://commons.apache.org/'>Apache Commons Team </a>
 * @version $Revision$
 */
public abstract class ComplexType {

    protected List<Element> elements = new ArrayList<>();

    protected List<Attribute> attributes = new ArrayList<>();

    public ComplexType() {
    }

    public ComplexType(
            TranscriptionConfiguration configuration,
            ElementDescriptor elementDescriptor, Schema schema)
            throws IntrospectionException {
        elementDescriptor = fillDescriptor(elementDescriptor, schema);
        init(configuration, elementDescriptor, schema);
    }

    /**
     * Fills the given descriptor
     *
     * @param elementDescriptor
     * @param schema
     * @return @throws
     * IntrospectionException
     * @since 0.7
     */
    protected ElementDescriptor fillDescriptor(
            ElementDescriptor elementDescriptor, Schema schema)
            throws IntrospectionException {
        if (elementDescriptor.isHollow()) {
            // need to introspector for filled descriptor
            Class type = elementDescriptor.getSingularPropertyType();
            if (type == null) {
                type = elementDescriptor.getPropertyType();
            }
            //noinspection StatementWithEmptyBody
            if (type != null) {
                XMLBeanInfo filledBeanInfo = schema.introspect(type);
                elementDescriptor = filledBeanInfo.getElementDescriptor();
            } else {
                // no type!
                // TODO: handle this
                // TODO: add support for logging
                // TODO: maybe should try singular type?
            }
        }
        return elementDescriptor;
    }

    protected void init(
            TranscriptionConfiguration configuration,
            ElementDescriptor elementDescriptor, Schema schema)
            throws IntrospectionException {

        List<AttributeDescriptor> attributeDescriptors = elementDescriptor.getAttributeDescriptors();
        for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
            //TODO: need to think about computing schema types from descriptors
            // this will probably depend on the class mapped to
            String uri = attributeDescriptor.getURI();
            if (!SchemaTranscriber.W3C_SCHEMA_INSTANCE_URI.equals(uri)) {
                attributes.add(new Attribute(attributeDescriptor));
            }
        }

        //TODO: add support for spacing elements
        for (ElementDescriptor child : elementDescriptor.getElementDescriptors()) {
            if (child.isHollow()) {
                elements.add(new ElementReference(configuration, child, schema));
            } else if (child.isSimple()) {
                elements.add(new SimpleLocalElement(configuration, child, schema));
            } else {
                elements.add(new ComplexLocalElement(configuration, child, schema));
            }
        }
    }

    /**
     * Gets the elements contained by this type
     *
     * @return <code>List</code> of contained elements, not null
     */
    public List getElements() {
        return elements;
    }

    /**
     * Adds an element to those contained by this type
     *
     * @param element
     */
    public void addElement(ElementReference element) {
        elements.add(element);
    }

    /**
     * Adds an element to those contained by this type
     *
     * @param element
     */
    public void addElement(LocalElement element) {
        elements.add(element);
    }

    /**
     * Gets the attributes contained by this type.
     *
     * @return <code>List</code> of attributes
     */
    public List getAttributes() {
        return attributes;
    }

    /**
     * Adds an attribute to those contained by this type
     *
     * @param attribute
     */
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

}