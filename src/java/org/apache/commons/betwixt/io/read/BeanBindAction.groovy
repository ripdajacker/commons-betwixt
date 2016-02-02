/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License") you may not use this file except in compliance with
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
package org.apache.commons.betwixt.io.read

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.AttributeDescriptor
import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.TextDescriptor
import org.apache.commons.betwixt.XMLBeanInfo
import org.apache.commons.betwixt.expression.Updater
import org.apache.commons.logging.Log
import org.xml.sax.Attributes

/**
 * Action that creates and binds a new bean instance.
 *
 * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
 */
@SuppressWarnings("UnnecessaryQualifiedReference")
@TypeChecked
public class BeanBindAction extends MappingAction.Base {

    /**
     * Singleton instance
     */
    public static final BeanBindAction INSTANCE = new BeanBindAction()

    /**
     * Begins a new element which is to be bound to a bean.
     */
    public MappingAction begin(String namespace, String name, Attributes attributes, ReadContext context) {

        Log log = context.getLog()

        ElementDescriptor computedDescriptor = context.getCurrentDescriptor()

        if (log.isTraceEnabled()) {
            log.trace("Element Pushed: " + name)
        }

        // default to ignoring the current element
        MappingAction action = MappingAction.EMPTY


        Class beanClass = null
        if (computedDescriptor == null) {
            log.warn("No Descriptor")
        } else {
            beanClass = computedDescriptor.getSingularPropertyType()
        }
        Object instance
        if (beanClass != null && !Map.class.isAssignableFrom(beanClass)) {
            instance = createBean(namespace, name, attributes, computedDescriptor, context)

            if (instance != null) {
                action = this
                if (computedDescriptor.isUseBindTimeTypeForMapping()) {
                    beanClass = instance.getClass()
                }
                context.markClassMap(beanClass)
                context.pushBean(instance)

                ElementDescriptor typeDescriptor = getElementDescriptor(computedDescriptor, context)

                Iterable<AttributeDescriptor> attributeDescriptors = typeDescriptor.getAttributeDescriptors()
                context.populateAttributes(attributeDescriptors, attributes)

                // add bean for ID matching
                if (context.getMapIDs()) {
                    String id = attributes.getValue("id")
                    if (id != null) {
                        context.putBean(id, instance)
                    }
                }
            }
        }
        return action
    }


    public void body(String text, ReadContext context) {
        Log log = context.getLog()
        // Take the first content descriptor
        ElementDescriptor currentDescriptor = context.getCurrentDescriptor()
        if (currentDescriptor == null) {
            if (log.isTraceEnabled()) {
                log.trace("path descriptor is null:")
            }
        } else {
            TextDescriptor bodyTextdescriptor =
                    currentDescriptor.getPrimaryBodyTextDescriptor()
            if (bodyTextdescriptor != null) {
                if (log.isTraceEnabled()) {
                    log.trace("Setting mixed content for:")
                    log.trace(bodyTextdescriptor)
                }
                Updater updater = bodyTextdescriptor.getUpdater()
                if (log.isTraceEnabled()) {
                    log.trace("Updating mixed content with:")
                    log.trace(updater)
                }
                if (updater != null && text != null) {
                    updater.update(context, text)
                }
            }
        }
    }

    public void end(ReadContext context) {
        // force any setters of the parent bean to be called for this new bean instance
        Object instance = context.popBean()
        update(context, instance)
    }

    private static void update(ReadContext context, Object value) {
        Updater updater = context.getCurrentUpdater()

        if (updater == null) {
            context.getLog().warn("No updater for " + context.getCurrentElement())
        } else {
            updater.update(context, value)
        }

        context.popElement()
    }

    /**
     * Factory method to create new bean instances
     *
     * @param namespace the namespace for the element
     * @param name the local name
     * @param attributes the <code>Attributes</code> used to match <code>ID/IDREF</code>
     *
     * @return the created bean
     */
    static Object createBean(String namespace, String name, Attributes attributes, ElementDescriptor descriptor, ReadContext context) {
        ElementMapping mapping = new ElementMapping()
        Class beanClass = descriptor.getSingularPropertyType()
        if (beanClass != null && beanClass.isArray()) {
            beanClass = beanClass.getComponentType()
        }

        mapping.setType(beanClass)
        mapping.setNamespace(namespace)
        mapping.setName(name)
        mapping.setAttributes(attributes)
        mapping.setDescriptor(descriptor)

        return context.getBeanCreationChain().create(mapping, context)
    }

    /**
     * Allows the navigation from a reference to a property object to the
     * descriptor defining what the property is. i.e. doing the join from a reference
     * to a type to lookup its descriptor.
     * This could be done automatically by the NodeDescriptors.
     *
     * @param propertyDescriptor find descriptor for property object
     *                           referenced by this descriptor
     * @return descriptor for the singular property class type referenced.
     */
    private static ElementDescriptor getElementDescriptor(ElementDescriptor propertyDescriptor, ReadContext context) {
        Log log = context.getLog()

        Class beanClass = propertyDescriptor.getSingularPropertyType()
        if (propertyDescriptor.isUseBindTimeTypeForMapping()) {
            // use the actual bind time type
            Object current = context.getBean()
            if (current != null) {
                beanClass = current.getClass()
            }
        }
        if (beanClass != null && !Map.class.isAssignableFrom(beanClass)) {
            if (beanClass.isArray()) {
                beanClass = beanClass.getComponentType()
            }

            try {
                XMLBeanInfo xmlInfo = context.introspector.introspect(beanClass)
                return xmlInfo.getElementDescriptor()
            } catch (Exception e) {
                log.warn("Could not introspect class: " + beanClass, e)
            }
        }
        // could not find a better descriptor so use the one we've got
        return propertyDescriptor
    }

}
