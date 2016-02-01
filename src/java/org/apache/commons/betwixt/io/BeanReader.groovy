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
package org.apache.commons.betwixt.io

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.betwixt.BindingConfiguration
import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.XMLBeanInfo
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.betwixt.io.read.ReadConfiguration
import org.apache.commons.betwixt.io.read.ReadContext
import org.apache.commons.digester.Digester
import org.apache.commons.digester.ExtendedBaseRules
import org.apache.commons.digester.RuleSet
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.XMLReader

import javax.xml.parsers.SAXParser
import java.beans.IntrospectionException

/**
 * <p><code>BeanReader</code> reads a tree of beans from an XML document.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 */
@CompileStatic
@TypeChecked
public class BeanReader extends Digester {
    Log log = LogFactory.getLog(getClass())
    /**
     * Introspector used
     */
    XMLIntrospector introspector = new XMLIntrospector()
    /**
     * The registered classes
     */
    private Set<Class> registeredClasses = new HashSet<>()
    /**
     * Dynamic binding configuration settings
     */
    BindingConfiguration bindingConfiguration = new BindingConfiguration()
    /**
     * Reading specific configuration settings
     */
    ReadConfiguration readConfiguration = new ReadConfiguration()

    /**
     * Construct a new BeanReader with default properties.
     */
    public BeanReader() {
        setRules(new ExtendedBaseRules())
    }

    /**
     * Construct a new BeanReader, allowing a SAXParser to be passed in.  This
     * allows BeanReader to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Thanks for the request to change go to
     * James House (james@interobjective.com).  This may help in places where
     * you are able to load JAXP 1.1 classes yourself.
     *
     * @param parser use this <code>SAXParser</code>
     */
    public BeanReader(SAXParser parser) {
        super(parser)
        setRules(new ExtendedBaseRules())
    }

    /**
     * Construct a new BeanReader, allowing an XMLReader to be passed in.  This
     * allows BeanReader to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Note that if you use this option you
     * have to configure namespace and validation support yourself, as these
     * properties only affect the SAXParser and emtpy constructor.
     *
     * @param reader use this <code>XMLReader</code> as source for SAX events
     */
    public BeanReader(XMLReader reader) {
        super(reader)
        setRules(new ExtendedBaseRules())
    }

    /**
     * <p>Register a bean class and add mapping rules for this bean class.</p>
     * <p/>
     * <p>A bean class is introspected when it is registered.
     * It will <strong>not</strong> be introspected again even if the introspection
     * settings are changed.
     * If re-introspection is required, then {@link #deregisterBeanClass} must be called
     * and the bean re-registered.</p>
     * <p/>
     * <p>A bean class can only be registered once.
     * If the same class is registered a second time, this registration will be ignored.
     * In order to change a registration, call {@link #deregisterBeanClass}
     * before calling this method.</p>
     * <p/>
     * <p>All the rules required to digest this bean are added when this method is called.
     * Other rules that you want to execute before these should be added before this
     * method is called.
     * Those that should be executed afterwards, should be added afterwards.</p>
     *
     * @param beanClass the <code>Class</code> to be registered
     * @throws IntrospectionException if the bean introspection fails
     */
    public void registerBeanClass(Class beanClass) throws IntrospectionException {
        if (!registeredClasses.contains(beanClass)) {
            register(beanClass, null)
        } else {
            log.warn("Cannot add class " + beanClass.getName() + " since it already exists")
        }
    }

    /**
     * Registers the given class at the given path.
     *
     * @param beanClass <code>Class</code> for binding
     * @param path the path at which the bean class should be registered
     *                  or null if the automatic path is to be used
     * @throws IntrospectionException
     */
    private void register(Class beanClass, String path) throws IntrospectionException {
        log.trace("Registering class " + beanClass)
        XMLBeanInfo xmlInfo = introspector.introspect(beanClass)
        registeredClasses.add(beanClass)

        ElementDescriptor elementDescriptor = xmlInfo.getElementDescriptor()
        if (path == null) {
            path = elementDescriptor.getQualifiedName()
        }

        log.trace("Added path: " + path + ", mapped to: " + beanClass.getName())
        addBeanCreateRule(path, elementDescriptor, beanClass)
    }

    /**
     * <p>Registers a bean class
     * and add mapping rules for this bean class at the given path expression.</p>
     * <p/>
     * <p/>
     * <p>A bean class is introspected when it is registered.
     * It will <strong>not</strong> be introspected again even if the introspection
     * settings are changed.
     * If re-introspection is required, then {@link #deregisterBeanClass} must be called
     * and the bean re-registered.</p>
     * <p/>
     * <p>A bean class can only be registered once.
     * If the same class is registered a second time, this registration will be ignored.
     * In order to change a registration, call {@link #deregisterBeanClass}
     * before calling this method.</p>
     * <p/>
     * <p>All the rules required to digest this bean are added when this method is called.
     * Other rules that you want to execute before these should be added before this
     * method is called.
     * Those that should be executed afterwards, should be added afterwards.</p>
     *
     * @param path the xml path expression where the class is to registered.
     *                  This should be in digester path notation
     * @param beanClass the <code>Class</code> to be registered
     * @throws IntrospectionException if the bean introspection fails
     */
    public void registerBeanClass(String path, Class beanClass) throws IntrospectionException {
        if (!registeredClasses.contains(beanClass)) {
            register(beanClass, path)
        } else {
            log.warn("Cannot add class " + beanClass.getName() + " since it already exists")
        }
    }

    /**
     * <p>Registers a class with a multi-mapping.
     * This mapping is specified by the multi-mapping document
     * contained in the given <code>InputSource</code>.
     * </p><p>
     * <strong>Note:</strong> the custom mappings will be registered with
     * the introspector. This must remain so for the reading to work correctly
     * It is recommended that use of the pre-registeration process provided
     * by {@link XMLIntrospector#register}  be considered as an alternative to this method.
     * </p>
     *
     * @param mapping <code>InputSource</code> giving the multi-mapping document specifying
     *                the mapping
     * @throws IntrospectionException
     * @throws SAXException
     * @throws IOException
     * @see #registerBeanClass(Class) since the general notes given there
     * apply equally to this
     * @see XMLIntrospector#register(InputSource) for more details on the multi-mapping format
     * @since 0.7
     */
    public void registerMultiMapping(InputSource mapping) throws IntrospectionException, IOException, SAXException {
        List<Class> mappedClasses = introspector.register(mapping)
        for (Class beanClass : mappedClasses) {
            if (!registeredClasses.contains(beanClass)) {
                register(beanClass, null)

            }
        }
    }

    /**
     * <p>Registers a class with a custom mapping.
     * This mapping is specified by the standard dot betwixt document
     * contained in the given <code>InputSource</code>.
     * </p><p>
     * <strong>Note:</strong> the custom mapping will be registered with
     * the introspector. This must remain so for the reading to work correctly
     * It is recommended that use of the pre-registeration process provided
     * by {@link XMLIntrospector#register}  be considered as an alternative to this method.
     * </p>
     *
     * @param mapping <code>InputSource</code> giving the dot betwixt document specifying
     *                  the mapping
     * @param beanClass <code>Class</code> that should be register
     * @throws IntrospectionException
     * @throws SAXException
     * @throws IOException
     * @see #registerBeanClass(Class) since the general notes given there
     * apply equally to this
     * @since 0.7
     */
    public void registerBeanClass(InputSource mapping, Class beanClass) throws IntrospectionException, IOException, SAXException {
        if (!registeredClasses.contains(beanClass)) {
            introspector.register(beanClass, mapping)
            register(beanClass, null)

        } else {
            log.warn("Cannot add class " + beanClass.getName() + " since it already exists")
        }
    }

    /**
     * <p>Remove the given class from the register.
     * Calling this method will allow the bean class to be re-registered
     * by a subsequent call to <code>registerBeanClass</code>.
     * This allows (for example) a bean to be reintrospected after a change
     * to the introspection settings.</p>
     * <p/>
     * <p><strong>Note</strong> that deregistering a bean does <strong>not</strong>
     * remove the Digester rules associated with that bean.</p>
     *
     * @param beanClass the <code>Class</code> to remove from the set of registered bean classes
     * @since 0.5
     */
    public void deregisterBeanClass(Class beanClass) {
        registeredClasses.remove(beanClass)
    }

    /**
     * Adds a new bean create rule for the specified path
     *
     * @param path the digester path at which this rule should be added
     * @param elementDescriptor the <code>ElementDescriptor</code> describes the expected element
     * @param beanClass the <code>Class</code> of the bean created by this rule
     */
    protected void addBeanCreateRule(
            String path,
            ElementDescriptor elementDescriptor,
            Class beanClass) {
        log.trace("Adding BeanRuleSet for " + beanClass)
        RuleSet ruleSet = new BeanRuleSet(introspector, path, beanClass, makeContext())
        addRuleSet(ruleSet)
    }

    /**
     * Factory method for new contexts.
     * Ensure that they are correctly configured.
     *
     * @return the ReadContext created, not null
     */
    private ReadContext makeContext() {
        return new ReadContext(bindingConfiguration, readConfiguration)
    }
}
