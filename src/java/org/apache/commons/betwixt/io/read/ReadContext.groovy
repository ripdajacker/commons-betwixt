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

import groovy.transform.CompileStatic
import org.apache.commons.betwixt.*
import org.apache.commons.betwixt.expression.Context
import org.apache.commons.betwixt.expression.Updater
import org.apache.commons.betwixt.registry.PolymorphicReferenceResolver
import org.apache.commons.betwixt.strategy.ActionMappingStrategy
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.xml.sax.Attributes

import java.beans.IntrospectionException

/**
 * <p>Extends <code>Context</code> to provide read specific functionality.</p>
 * <p>
 * Three stacks are used to manage the reading:
 * </p>
 * <ul>
 * <li><strong>Action mapping stack</strong> contains the {@link MappingAction}'s
 * used to execute the mapping of the current element and it's ancesters back to the
 * document root.</li>
 * <li><strong>Result stack</strong> contains the objects which are bound
 * to the current element and to each of it's ancester's back to the root</li>
 * <li><strong>Element mapping stack</strong> records the names of the element
 * and the classes to which they are bound</li>
 * </ul>
 *
 * @author Robert Burrell Donkina
 * @since 0.5
 */
@CompileStatic
public class ReadContext extends Context implements AbstractReadContext {
    /** Classloader to be used to load beans during reading */
    ClassLoader classLoader
    /** The read specific configuration */
    ReadConfiguration readConfiguration

    /** Records the element path together with the locations where classes were mapped */
    private LinkedList<Object> elementMappingStack = []

    /** Contains actions for each element */
    private LinkedList<MappingAction> actionMappingStack = []

    /** Stack contains all beans created */
    private LinkedList<Object> objectStack = []

    /** Stack contains element descriptors */
    private LinkedList<ElementDescriptor> descriptorStack = []

    /** Stack contains updaters */
    private LinkedList<Updater> updaterStack = []

    Class rootClass
    XMLIntrospector introspector = new XMLIntrospector()

    /**
     * Constructs a <code>ReadContext</code> with standard log.
     *
     * @param bindingConfiguration the dynamic configuration, not null
     * @param readConfiguration the extra read configuration not null
     */
    public ReadContext(BindingConfiguration bindingConfiguration, ReadConfiguration readConfiguration) {
        this(LogFactory.getLog(ReadContext.class), bindingConfiguration, readConfiguration)
    }

    /**
     * Base constructor
     *
     * @param log log to this Log
     * @param bindingConfiguration the dynamic configuration, not null
     * @param readConfiguration the extra read configuration not null
     */
    public ReadContext(Log log, BindingConfiguration bindingConfiguration, ReadConfiguration readConfiguration) {
        super(null, log, bindingConfiguration)
        this.readConfiguration = readConfiguration
    }

    /**
     * Constructs a <code>ReadContext</code>
     * with the same settings as an existing <code>Context</code>.
     *
     * @param readContext not null
     */
    public ReadContext(ReadContext readContext) {
        super(readContext)
        classLoader = readContext.classLoader
        readConfiguration = readContext.readConfiguration
    }

    /**
     * Puts a bean into storage indexed by an (xml) ID.
     *
     * @param id the ID string of the xml element associated with the bean
     * @param bean the Object to store, not null
     */
    public void putBean(String id, Object bean) {
        getIdMappingStrategy().setReference(this, bean, id)
    }

    /**
     * Gets a bean from storage by an (xml) ID.
     *
     * @param id the ID string of the xml element associated with the bean
     * @return the Object that the ID references, otherwise null
     */
    public Object getBean(String id) {
        return getIdMappingStrategy().getReferenced(this, id)
    }

    /**
     * Clears the beans indexed by id.
     */
    public void clearBeans() {
        getIdMappingStrategy().reset()
    }

    /**
     * Gets the classloader to be used.
     *
     * @return the classloader that should be used to load all classes, possibly null
     */
    public ClassLoader getClassLoader() {
        return classLoader
    }

    /**
     * Sets the classloader to be used.
     *
     * @param classLoader the ClassLoader to be used, possibly null
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader
    }

    /**
     * Gets the <code>BeanCreationChange</code> to be used to create beans
     * when an element is mapped.
     *
     * @return the BeanCreationChain not null
     */
    public BeanCreationChain getBeanCreationChain() {
        return readConfiguration.getBeanCreationChain()
    }

    /**
     * Gets the strategy used to define default mappings actions
     * for elements.
     *
     * @return <code>ActionMappingStrategy</code>. not null
     */
    public ActionMappingStrategy getActionMappingStrategy() {
        return readConfiguration.getActionMappingStrategy()
    }

    /**
     * Pops the top element from the element mapping stack.
     * Also removes any mapped class marks below the top element.
     *
     * @return the name of the element popped
     * if there are any more elements on the stack, otherwise null.
     * This is the local name if the parser is namespace aware, otherwise the name
     */
    public String popElement() {
        // since the descriptor stack is populated by pushElement,
        // need to ensure that it's correct popped by popElement
        if (!descriptorStack.empty) {
            descriptorStack.pop()
        }

        if (!updaterStack.isEmpty()) {
            popUpdater()
        }

        popOptions()

        if (elementMappingStack.empty) {
            return null
        }

        def pop = elementMappingStack.pop()
        if (pop instanceof String) {
            return pop
        }
        return popElement()
    }

    /**
     * Gets the element name for the currently mapped element.
     *
     * @return the name of the currently mapped element,
     * or null if there has been no element mapped
     */
    public String getCurrentElement() {
        def found = elementMappingStack.findAll { it instanceof String }
        if (found.empty) {
            return null
        }
        return (String) found.last()
    }

    /**
     * Gets the Class that was last mapped, if there is one.
     *
     * @return the Class last marked as mapped
     * or null if no class has been mapped
     */
    public Class getLastMappedClass() {
        elementMappingStack.find { it instanceof Class } as Class
    }

    private ElementDescriptor getParentDescriptor() throws IntrospectionException {
        if (descriptorStack.size() > 1) {
            return descriptorStack.get(1)
        }
        return null
    }

    /**
     * Pushes the given element onto the element mapping stack.
     *
     * @param elementName the local name if the parser is namespace aware,
     *                    otherwise the full element name. Not null
     */
    public void pushElement(String elementName) throws Exception {
        elementMappingStack.push(elementName)

        ElementDescriptor nextDescriptor = null
        if (elementMappingStack.size() == 1 && rootClass != null) {
            markClassMap(rootClass)
            XMLBeanInfo rootClassInfo = introspector.introspect(rootClass)
            nextDescriptor = rootClassInfo.getElementDescriptor()
        } else {
            ElementDescriptor currentDescriptor = getCurrentDescriptor()
            if (currentDescriptor != null) {
                nextDescriptor = currentDescriptor.getElementDescriptor(elementName)
            }
        }
        Updater updater = null
        Options options = null
        if (nextDescriptor != null) {
            updater = nextDescriptor.getUpdater()
            options = nextDescriptor.getOptions()
        }
        pushUpdater(updater)
        descriptorStack.push(nextDescriptor)
        pushOptions(options)

    }

    /**
     * Marks the element name stack with a class mapping.
     * Relative paths and last mapped class are calculated using these marks.
     *
     * @param mappedClazz the Class which has been mapped at the current path, not null
     */
    public void markClassMap(Class mappedClazz) throws IntrospectionException {
        if (mappedClazz.isArray()) {
            mappedClazz = mappedClazz.getComponentType()
        }
        elementMappingStack.push(mappedClazz)

        XMLBeanInfo mappedClassInfo = introspector.introspect(mappedClazz)
        ElementDescriptor mappedElementDescriptor = mappedClassInfo.getElementDescriptor()
        descriptorStack.push(mappedElementDescriptor)

        Updater updater = mappedElementDescriptor.getUpdater()
        updaterStack.push(updater)
    }

    /**
     * Pops an action mapping from the stack
     *
     * @return <code>MappingAction</code>, not null
     */
    public MappingAction popMappingAction() {
        return actionMappingStack.pop()
    }

    /**
     * Pushs an action mapping onto the stack
     *
     * @param mappingAction action
     */
    public void pushMappingAction(MappingAction mappingAction) {
        actionMappingStack.push(mappingAction)
    }

    /**
     * Gets the current mapping action
     *
     * @return MappingAction
     */
    public MappingAction currentMappingAction() {
        return actionMappingStack.peek()
    }

    public Object getBean() {
        return objectStack.peek()
    }

    public void setBean(Object bean) {
    }

    /**
     * Pops the last mapping <code>Object</code> from the
     * stack containing beans that have been mapped.
     *
     * @return the last bean pushed onto the stack
     */
    public Object popBean() {
        return objectStack.pop()
    }

    /**
     * Pushs a newly mapped <code>Object</code> onto the mapped bean stack.
     *
     * @param bean the bean
     */
    public void pushBean(Object bean) {
        objectStack.push(bean)
    }

    /**
     * Gets the <code>ElementDescriptor</code> that describes the
     * mapping for the current element.
     *
     * @return <code>ElementDescriptor</code> or null if there is no
     * current mapping
     * @throws Exception
     */
    public ElementDescriptor getCurrentDescriptor() throws Exception {
        return descriptorStack.peek()
    }

    /**
     * Populates the object mapped by the <code>AttributeDescriptor</code>s
     * with the values in the given <code>Attributes</code>.
     *
     * @param attributeDescriptors <code>AttributeDescriptor</code>s, not null
     * @param attributes <code>Attributes</code>, not null
     */
    public void populateAttributes(Iterable<AttributeDescriptor> attributeDescriptors, Attributes attributes) {
        if (attributeDescriptors != null) {
            for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
                String value
                value = attributes.getValue(attributeDescriptor.getURI(), attributeDescriptor.getLocalName())

                if (value == null) {
                    value = attributes.getValue(attributeDescriptor.getQualifiedName())
                }

                Updater updater = attributeDescriptor.getUpdater()
                if (updater != null && value != null) {
                    updater.update(this, value)
                }
            }
        }
    }

    /**
     * <p>Pushes an <code>Updater</code> onto the stack.</p>
     * <p>
     * <strong>Note</strong>Any action pushing an <code>Updater</code> onto
     * the stack should take responsibility for popping
     * the updater from the stack at an appropriate time.
     * </p>
     * <p>
     * <strong>Usage:</strong> this may be used by actions
     * which require a temporary object to be updated.
     * Pushing an updater onto the stack allow actions
     * downstream to transparently update the temporary proxy.
     * </p>
     *
     * @param updater Updater, possibly null
     */
    public void pushUpdater(Updater updater) {
        updaterStack.push(updater)
    }

    /**
     * Pops the top <code>Updater</code> from the stack.
     * <p>
     * <strong>Note</strong>Any action pushing an <code>Updater</code> onto
     * the stack should take responsibility for popping
     * the updater from the stack at an appropriate time.
     * </p>
     *
     * @return <code>Updater</code>, possibly null
     */
    public Updater popUpdater() {
        return (Updater) updaterStack.pop()
    }

    /**
     * Gets the current <code>Updater</code>.
     * This may (or may not) be the updater for the current
     * descriptor.
     * If the current descriptor is a bean child,
     * the the current updater will (most likely)
     * be the updater for the property.
     * Actions (that, for example, use proxy objects)
     * may push updaters onto the stack.
     *
     * @return Updater , possibly null
     */
    public Updater getCurrentUpdater() {
        Updater result = (Updater) updaterStack.peek()
        if (result == null && updaterStack.size() > 1) {
            result = (Updater) updaterStack.get(1)
        }
        return result
    }

    public Class resolvePolymorphicType(ElementMapping mapping) {
        Class result = null
        Log log = getLog()
        try {
            ElementDescriptor currentDescriptor = getCurrentDescriptor()
            if (currentDescriptor != null) {
                if (currentDescriptor.isPolymorphic()) {
                    PolymorphicReferenceResolver resolver = introspector.getPolymorphicReferenceResolver()
                    result = resolver.resolveType(mapping, this)
                    if (result == null) {
                        // try the other polymorphic descriptors
                        ElementDescriptor parent = getParentDescriptor()
                        if (parent != null) {
                            ElementDescriptor originalDescriptor = mapping.getDescriptor()
                            boolean resolved = false
                            for (ElementDescriptor descriptor : parent.getElementDescriptors()) {
                                if (descriptor.isPolymorphic()) {
                                    mapping.setDescriptor(descriptor)
                                    result = resolver.resolveType(mapping, this)
                                    if (result != null) {
                                        resolved = true
                                        descriptorStack.pop()
                                        popOptions()
                                        descriptorStack.push(descriptor)

                                        pushOptions(descriptor.getOptions())
                                        Updater originalUpdater = originalDescriptor.getUpdater()
                                        Updater newUpdater = descriptor.getUpdater()
                                        substituteUpdater(originalUpdater, newUpdater)
                                        break
                                    }
                                }
                            }
                            if (resolved) {
                                log.debug("Resolved polymorphic type")
                            } else {
                                log.debug("Failed to resolve polymorphic type")
                                mapping.setDescriptor(originalDescriptor)
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("Failed to resolved polymorphic type")
            log.debug(mapping, e)
        }
        return result
    }

    /**
     * Substitutes one updater in the stack for another.
     *
     * @param originalUpdater <code>Updater</code> possibly null
     * @param newUpdater <code>Updater</code> possibly null
     */
    private void substituteUpdater(Updater originalUpdater, Updater newUpdater) {
        if (!updaterStack.isEmpty()) {
            Updater updater = popUpdater()
            if (originalUpdater == null && updater == null) {
                pushUpdater(newUpdater)
            } else if (originalUpdater != null) {
                if (originalUpdater.equals(updater)) {
                    pushUpdater(newUpdater)
                } else {
                    substituteUpdater(originalUpdater, newUpdater)
                    pushUpdater(updater)
                }
            }
        }
    }

}
