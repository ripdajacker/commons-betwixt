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
import groovy.util.logging.Commons
import org.apache.commons.betwixt.*
import org.apache.commons.betwixt.expression.Context
import org.apache.commons.betwixt.expression.Expression
import org.apache.commons.betwixt.io.id.BaseTenIdGenerator
import org.apache.commons.betwixt.strategy.ObjectStringConverter
import org.apache.commons.collections.ArrayStack
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.AttributesImpl

import java.beans.IntrospectionException

@Commons
@CompileStatic
public abstract class AbstractBeanWriter {

    BeanWriteEventListener beanWriteListener = new NoopListener()

    /** Introspector used */
    XMLIntrospector introspector = new XMLIntrospector()

    /** Stack containing beans - used to detect cycles */
    private ArrayStack beanStack = new ArrayStack()

    /** Used to generate ID attribute values*/
    IDGenerator idGenerator = new BaseTenIdGenerator()
    /** Should empty elements be written out? */
    boolean writeEmptyElements = true
    /** Dynamic binding configuration settings */
    BindingConfiguration bindingConfiguration = new BindingConfiguration()
    /** <code>WriteContext</code> implementation reused curing writing */
    MutableWriteContext writeContext = new MutableWriteContext()
    /** Collection of namespaces which have already been declared */
    Collection namespacesDeclared = new ArrayList()

    /**
     * Marks the start of the bean writing.
     * By default doesn't do anything, but can be used
     * to do extra start processing
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     */
    public void start() throws IOException, SAXException {
        beanWriteListener.start()
    }

    /**
     * Marks the start of the bean writing.
     * By default doesn't do anything, but can be used
     * to do extra end processing
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     */

    public void end() throws IOException, SAXException {
        beanWriteListener.end()
    }

    /**
     * <p> Writes the given bean to the current stream using the XML introspector.</p>
     *
     * <p> This writes an xml fragment representing the bean to the current stream.</p>
     *
     * <p>This method will throw a <code>CyclicReferenceException</code> when a cycle
     * is encountered in the graph <strong>only</strong> if the <code>getMapIDs()</code>
     * setting of the </code>BindingConfiguration</code> is false.</p>
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @throws IntrospectionException if a java beans introspection problem occurs
     *
     * @param bean write out representation of this bean
     */
    public void write(Object bean) throws
            IOException,
            SAXException,
            IntrospectionException {
        log.debug("Writing bean graph... $bean")
        start()
        writeBean(null, null, null, bean, makeContext(bean))
        end()
        log.debug("Finished writing bean graph")
    }

    /**
     * <p>Writes the given bean to the current stream
     * using the given <code>qualifiedName</code>.</p>
     *
     * <p>This method will throw a <code>CyclicReferenceException</code> when a cycle
     * is encountered in the graph <strong>only</strong> if the <code>getMapIDs()</code>
     * setting of the <code>BindingConfiguration</code> is false.</p>
     *
     * @param qualifiedName the string naming root element
     * @param bean the <code>Object</code> to write out as xml
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    public void write(String qualifiedName, Object bean)
            throws IOException, SAXException, IntrospectionException {
        start()
        writeBean("", qualifiedName, qualifiedName, bean, makeContext(bean))
        end()
    }

    /**
     * <p>Writes the bean using the mapping specified in the <code>InputSource</code>.
     * </p><p>
     * <strong>Note:</strong> that the custom mapping will <em>not</em>
     * be registered for later use. Please use {@link XMLIntrospector#register}
     * to register the custom mapping for the class and then call
     * {@link #write(Object)}.
     * </p>
     * @see #write(Object) since the standard notes also apply
     * @since 0.7
     * @param bean <code>Object</code> to be written as xml, not null
     * @param source <code>InputSource/code> containing an xml document
     * specifying the mapping to be used (in the usual way), not null
     * @throws IOException
     * @throws SAXException
     * @throws IntrospectionException
     */
    public void write(Object bean, InputSource source)
            throws IOException, SAXException, IntrospectionException {
        def introspect = introspector.introspect(bean.getClass(), source)
        writeBean(null, null, null, bean, makeContext(bean), introspect)
    }

    /**
     * <p>Writes the given bean to the current stream
     * using the given <code>qualifiedName</code>.</p>
     *
     * <p>This method will throw a <code>CyclicReferenceException</code> when a cycle
     * is encountered in the graph <strong>only</strong> if the <code>getMapIDs()</code>
     * setting of the <code>BindingConfiguration</code> is false.</p>
     *
     * @param namespaceUri the namespace uri
     * @param localName the local name
     * @param qualifiedName the string naming root element
     * @param introspectedBindType the <code>Class</code> of the bean
     * as resolved at introspection time, or null if the type has not been resolved
     * @param bean the <code>Object</code> to write out as xml
     * @param context not null
     */
    private void writeBean(String namespaceUri, String localName, String qualifiedName, Object bean, Context context) {
        log.trace("Writing bean graph (qualified name '$qualifiedName'")
        XMLBeanInfo beanInfo = introspector.introspect(bean)
        writeBean(namespaceUri, localName, qualifiedName, bean, context, beanInfo)
        log.trace("Finished writing bean graph.")
    }


    private void writeBean(
            String namespaceUri,
            String localName,
            String qualifiedName,
            Object bean,
            ElementDescriptor parentDescriptor,
            Context context) {

        log.trace("Writing bean graph (qualified name '" + qualifiedName + "'")

        // introspect to obtain bean info
        XMLBeanInfo beanInfo = findXMLBeanInfo(bean, parentDescriptor)
        writeBean(namespaceUri, localName, qualifiedName, bean, context, beanInfo)

        log.trace("Finished writing bean graph.")
    }

    /**
     * Finds the appropriate bean info for the given (hollow) element.
     * @param bean
     * @param parentDescriptor <code>ElementDescriptor</code>, not null
     * @return <code>XMLBeanInfo</code>, not null
     */
    private XMLBeanInfo findXMLBeanInfo(Object bean, ElementDescriptor parentDescriptor) {
        Class introspectedBindType = parentDescriptor.getSingularPropertyType()
        if (introspectedBindType == null) {
            introspectedBindType = parentDescriptor.getPropertyType()
        }
        if (parentDescriptor.isUseBindTimeTypeForMapping() || introspectedBindType == null) {
            return introspector.introspect(bean)
        } else {
            return introspector.introspect(introspectedBindType)
        }
    }

    /**
     * <p>Writes the given bean to the current stream
     * using the given mapping.</p>
     *
     * <p>This method will throw a <code>CyclicReferenceException</code> when a cycle
     * is encountered in the graph <strong>only</strong> if the <code>getMapIDs()</code>
     * setting of the <code>BindingConfiguration</code> is false.</p>
     *
     * @param namespaceUri the namespace uri, or null to use the automatic binding
     * @param localName the local name  or null to use the automatic binding
     * @param qualifiedName the <code>String</code> naming the root element
     *  or null to use the automatic binding
     * @param bean <code>Object</code> to be written, not null
     * @param context <code>Context</code>, not null
     * @param beanInfo <code>XMLBeanInfo</code>, not null
     */
    private void writeBean(
            String namespaceUri,
            String localName,
            String qualifiedName,
            Object bean,
            Context context,
            XMLBeanInfo beanInfo) {
        if (beanInfo != null) {
            ElementDescriptor elementDescriptor = beanInfo.getElementDescriptor()
            if (elementDescriptor != null) {
                // Construct the options
                Options combinedOptions = new Options()

                // Add options defined by the current bean's element descriptor
                combinedOptions.addOptions(elementDescriptor.getOptions())

                // The parent descriptor may have defined options
                // for the current bean.  These options take precedence
                // over the options of the current class descriptor
                if (context.getOptions() != null) {
                    combinedOptions.addOptions(context.getOptions())
                }
                context = context.newContext(bean)
                context.pushOptions(combinedOptions)

                if (qualifiedName == null) {
                    qualifiedName = elementDescriptor.getQualifiedName()
                }
                if (namespaceUri == null) {
                    namespaceUri = elementDescriptor.getURI()
                }
                if (localName == null) {
                    localName = elementDescriptor.getLocalName()
                }

                String ref = null

                // simple type should not have IDs
                if (elementDescriptor.isSimple()) {
                    // write without an id
                    writeElement(namespaceUri, localName, qualifiedName, elementDescriptor, context)
                } else {
                    pushBean(context.getBean())
                    if (getBindingConfiguration().getMapIDs()) {
                        ref = getBindingConfiguration().getIdMappingStrategy().getReferenceFor(context, context.getBean())
                    }
                    if (ref == null) {
                        // this is the first time that this bean has be written
                        AttributeDescriptor idAttribute = beanInfo.getIDAttribute()
                        if (idAttribute == null) {
                            // use a generated id
                            String id = idGenerator.nextId()
                            getBindingConfiguration().getIdMappingStrategy().setReference(context, bean, id)

                            if (getBindingConfiguration().getMapIDs()) {
                                // write element with id
                                writeElement(
                                        namespaceUri,
                                        localName,
                                        qualifiedName,
                                        elementDescriptor,
                                        context,
                                        beanInfo.idAttributeName,
                                        id)


                            } else {
                                // write element without ID
                                writeElement(
                                        namespaceUri,
                                        localName,
                                        qualifiedName,
                                        elementDescriptor,
                                        context)
                            }

                        } else {
                            // use id from bean property
                            // it's up to the user to ensure uniqueness
                            Expression idExpression = idAttribute.getTextExpression()
                            if (idExpression == null) {
                                throw new IntrospectionException("The specified id property wasn't found in the bean (${idAttribute}).")
                            }
                            String id

                            Object exp = idExpression.evaluate(context)
                            if (exp == null) {
                                // we'll use a random id
                                log.debug("Using random id")
                                id = idGenerator.nextId()

                            } else {
                                // convert to string
                                id = exp.toString()
                            }
                            getBindingConfiguration().getIdMappingStrategy().setReference(context, bean, id)

                            // the ID attribute should be written automatically
                            writeElement(
                                    namespaceUri,
                                    localName,
                                    qualifiedName,
                                    elementDescriptor,
                                    context)
                        }
                    } else {

                        if (!ignoreElement(elementDescriptor, namespaceUri, localName, qualifiedName, context)) {
                            // we've already written this bean so write an IDREF
                            writeIDREFElement(
                                    elementDescriptor,
                                    namespaceUri,
                                    localName,
                                    qualifiedName,
                                    beanInfo.idRefAttributeName,
                                    ref)
                        }
                    }
                    popBean()
                }

                context.popOptions()
            }
        }
    }

    /**
     * Writes the start tag for an element.
     *
     * @param uri the element's namespace uri
     * @param localName the element's local name
     * @param qName the element's qualified name
     * @param attr the element's attributes
     */
    protected void startElement(WriteContext context, String uri, String localName, String qName, Attributes attr) {
        // for backwards compatibility call older methods
        //noinspection GrDeprecatedAPIUsage
        startElement(uri, localName, qName, attr)
    }

    /**
     * Writes the end tag for an element
     *
     * @param uri the element's namespace uri
     * @param localName the element's local name
     * @param qName the element's qualified name
     */
    protected void endElement(WriteContext context, String uri, String localName, String qName) {
        // for backwards compatibility call older interface
        //noinspection GrDeprecatedAPIUsage
        endElement(uri, localName, qName)
    }

    /**
     * Writes body text
     *
     * @param text the body text to be written
     */
    protected void bodyText(WriteContext context, String text) {
        // for backwards compatibility call older interface
        //noinspection GrDeprecatedAPIUsage
        bodyText(text)
    }

    // Older SAX-style methods
    //-------------------------------------------------------------------------

    /**
     * Writes the start tag for an element.
     *
     * @param uri the element's namespace uri
     * @param localName the element's local name
     * @param qName the element's qualified name
     * @param attr the element's attributes
     *
     * @deprecated 0.5 use {@link #startElement(WriteContext, String, String, String, Attributes)}
     */
    protected void startElement(String uri, String localName, String qName, Attributes attr) {
    }

    /**
     * Writes the end tag for an element
     *
     * @param uri the element's namespace uri
     * @param localName the element's local name
     * @param qName the element's qualified name
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @deprecated 0.5 use {@link #endElement(WriteContext, String, String, String)}
     */
    protected void endElement(String uri, String localName, String qName) {
    }

    /**
     * Writes body text
     *
     * @param text the body text to be written
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @deprecated 0.5 use {@link #bodyText(WriteContext, String)}
     */
    protected void bodyText(String text) throws IOException, SAXException {
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Writes the given element
     *
     * @param namespaceUri the namespace uri
     * @param localName the local name
     * @param qualifiedName qualified name to use for the element
     * @param elementDescriptor the <code>ElementDescriptor</code> describing the element
     * @param context the <code>Context</code> to use to evaluate the bean expressions
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    private void writeElement(String namespaceUri, String localName, String qualifiedName, ElementDescriptor elementDescriptor, Context context) {
        log.trace("Writing: $qualifiedName  element: $elementDescriptor")

        if (!ignoreElement(elementDescriptor, namespaceUri, localName, qualifiedName, context)) {
            log.trace("Element $elementDescriptor is empty.")


            def type = elementDescriptor.propertyType


            def converter = bindingConfiguration.objectStringConverter
            def canHandle = converter.canHandle(type)

            def stringified = canHandle && !ObjectStringConverter.isPrimitive(type)
            Attributes attributes
            if (stringified) {
                String value = converter.objectToString(context.bean, elementDescriptor.propertyType, context)
                attributes = addNamespaceDeclarations(new InlineValueAttributes(null, null, value), namespaceUri)
            } else {
                def newAttributes = new ElementAttributes(bindingConfiguration, elementDescriptor, context)
                attributes = addNamespaceDeclarations(newAttributes, namespaceUri)
            }
            writeContext.setCurrentDescriptor(elementDescriptor)
            startElement(
                    writeContext,
                    namespaceUri,
                    localName,
                    qualifiedName,
                    attributes)
            beanWriteListener.startElement(writeContext, qualifiedName, attributes)
            if (!stringified) {
                writeElementContent(elementDescriptor, context)
            }
            writeContext.setCurrentDescriptor(elementDescriptor)
            endElement(writeContext, namespaceUri, localName, qualifiedName)
            beanWriteListener.endElement(writeContext, qualifiedName)
        }
    }

    /**
     * Adds namespace declarations (if any are needed) to the given attributes.
     * @param attributes Attributes, not null
     * @param elementNamespaceUri the URI for the enclosing element, possibly null
     * @return Attributes , not null
     */
    private Attributes addNamespaceDeclarations(Attributes attributes, String elementNamespaceUri) {
        Attributes result = attributes
        AttributesImpl withDeclarations = null
        int size = attributes.getLength()
        for (int i = -1; i < size; i++) {
            String uri
            if (i == -1) {
                uri = elementNamespaceUri
            } else {
                uri = attributes.getURI(i)
            }
            if (uri != null && !"".equals(uri) && !namespacesDeclared.contains(uri)) {
                if (withDeclarations == null) {
                    withDeclarations = new AttributesImpl(attributes)
                }
                withDeclarations.addAttribute(
                        "", "", "xmlns:"
                        + getIntrospector().getConfiguration().getPrefixMapper().getPrefix(uri), "NOTATION", uri)
                namespacesDeclared.add(uri)
            }
        }

        if (withDeclarations != null) {
            result = withDeclarations
        }
        return result
    }

    /**
     * Writes the given element adding an ID attribute
     *
     * @param namespaceUri the namespace uri
     * @param localName the local name
     * @param qualifiedName the qualified name
     * @param elementDescriptor the ElementDescriptor describing this element
     * @param context the context being evaliated against
     * @param idAttribute the qualified name of the <code>ID</code> attribute
     * @param idValue the value for the <code>ID</code> attribute
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    private void writeElement(
            String namespaceUri,
            String localName,
            String qualifiedName,
            ElementDescriptor elementDescriptor,
            Context context,
            String idAttribute,
            String idValue) {

        if (!ignoreElement(elementDescriptor, namespaceUri, localName, qualifiedName, context)) {
            writeContext.setCurrentDescriptor(elementDescriptor)
            Attributes attributes = new IDElementAttributes(
                    bindingConfiguration,
                    elementDescriptor,
                    context,
                    idAttribute,
                    idValue)
            attributes = addNamespaceDeclarations(attributes, namespaceUri)
            startElement(
                    writeContext,
                    namespaceUri,
                    localName,
                    qualifiedName,
                    attributes)
            beanWriteListener.startElement(writeContext, qualifiedName, attributes)

            writeElementContent(elementDescriptor, context)

            writeContext.setCurrentDescriptor(elementDescriptor)
            endElement(writeContext, namespaceUri, localName, qualifiedName)
            beanWriteListener.endElement(writeContext, qualifiedName)
        } else if (log.isTraceEnabled()) {
            log.trace("Element " + qualifiedName + " is empty.")
        }
    }

    /**
     * Writes an element with a <code>IDREF</code> attribute
     *
     * @param uri the namespace uri
     * @param localName the local name
     * @param qualifiedName of the element with <code>IDREF</code> attribute
     * @param idrefAttributeName the qualified name of the <code>IDREF</code> attribute
     * @param idrefAttributeValue the value for the <code>IDREF</code> attribute
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    private void writeIDREFElement(
            ElementDescriptor elementDescriptor,
            String uri,
            String localName,
            String qualifiedName,
            String idrefAttributeName,
            String idrefAttributeValue) {

        // write IDREF element
        Attributes attributes = new AttributesImpl()
        // XXX for the moment, assign IDREF to default namespace
        attributes.addAttribute(
                "",
                idrefAttributeName,
                idrefAttributeName,
                "IDREF",
                idrefAttributeValue)
        writeContext.setCurrentDescriptor(elementDescriptor)
        attributes = addNamespaceDeclarations(attributes, uri)
        startElement(writeContext, uri, localName, qualifiedName, attributes)
        beanWriteListener.startElement(writeContext, qualifiedName, attributes)

        endElement(writeContext, uri, localName, qualifiedName)
        beanWriteListener.endElement(writeContext, qualifiedName)
    }

    /**
     * Writes the element content.
     *
     * @param elementDescriptor the <code>ElementDescriptor</code> to write as xml
     * @param context the <code>Context</code> to use to evaluate the bean expressions
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    private void writeElementContent(
            ElementDescriptor elementDescriptor,
            Context context) {
        writeContext.setCurrentDescriptor(elementDescriptor)
        List<Descriptor> childDescriptors = elementDescriptor.getContentDescriptors()
        if (childDescriptors != null && childDescriptors.size() > 0) {
            for (Descriptor currentDescriptor : childDescriptors) {
                if (currentDescriptor instanceof ElementDescriptor) {
                    // Element content
                    ElementDescriptor childDescriptor = (ElementDescriptor) currentDescriptor
                    Context childContext = context
                    childContext.pushOptions(childDescriptor.getOptions())
                    Expression childExpression = childDescriptor.getContextExpression()
                    if (childExpression != null) {
                        Object childBean = childExpression.evaluate(context)
                        if (childBean != null) {
                            String qualifiedName = childDescriptor.getQualifiedName()
                            String namespaceUri = childDescriptor.getURI()
                            String localName = childDescriptor.getLocalName()

                            if (childBean instanceof Iterator) {
                                def iterator = childBean as Iterator
                                while (iterator.hasNext()) {
                                    Object object = iterator.next();
                                    if (object != null) {
                                        writeBean(namespaceUri, localName, qualifiedName, object, childDescriptor, context)
                                    }
                                }
                            } else {
                                writeBean(
                                        namespaceUri,
                                        localName,
                                        qualifiedName,
                                        childBean,
                                        childDescriptor,
                                        context)
                            }
                        }
                    } else {
                        writeElement(
                                childDescriptor.getURI(),
                                childDescriptor.getLocalName(),
                                childDescriptor.getQualifiedName(),
                                childDescriptor,
                                childContext)
                    }
                    childContext.popOptions()
                } else {
                    // Mixed text content
                    // evaluate the body text
                    Expression expression = currentDescriptor.getTextExpression()
                    if (expression != null) {
                        Object value = expression.evaluate(context)
                        String text = convertToString(value, currentDescriptor, context)
                        if (text != null && text.length() > 0) {
                            beanWriteListener.bodyText(writeContext, text)
                            bodyText(writeContext, text)
                        }
                    }
                }
            }
        } else {
            // evaluate the body text
            Expression expression = elementDescriptor.getTextExpression()
            if (expression != null) {
                Object value = expression.evaluate(context)
                String text = convertToString(value, elementDescriptor, context)
                if (text != null && text.length() > 0) {
                    beanWriteListener.bodyText(writeContext, text)
                    bodyText(writeContext, text)
                }
            }
        }
    }

    /**
     * Pushes the bean onto the ancestry stack.
     * If IDs are not being written, then check for cyclic references.
     *
     * @param bean push this bean onto the ancester stack
     */
    protected void pushBean(Object bean) {
        // check that we don't have a cyclic reference when we're not writing IDs
        if (!getBindingConfiguration().getMapIDs()) {
            Iterator it = beanStack.iterator()
            while (it.hasNext()) {
                Object next = it.next()
                // use absolute equality rather than equals
                // we're only really bothered if objects are actually the same
                if (bean == next) {
                    final String message = "Cyclic reference at bean: " + bean
                    log.error(message)
                    StringBuffer buffer = new StringBuffer(message)
                    buffer.append(" Stack: ")
                    Iterator errorStack = beanStack.iterator()
                    while (errorStack.hasNext()) {
                        Object errorObj = errorStack.next()
                        if (errorObj != null) {
                            buffer.append(errorObj.getClass().getName())
                            buffer.append(": ")
                        }
                        buffer.append(errorObj)
                        buffer.append("")
                    }
                    final String debugMessage = buffer.toString()
                    log.info(debugMessage)
                    throw new CyclicReferenceException(debugMessage)
                }
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("Pushing onto object stack: " + bean)
        }
        beanStack.push(bean)
    }

    /**
     * Pops the top bean off from the ancestry stack
     *
     * @return the last object pushed onto the ancester stack
     */
    protected Object popBean() {
        Object bean = beanStack.pop()
        if (log.isTraceEnabled()) {
            log.trace("Popped from object stack: " + bean)
        }
        return bean
    }

    /**
     * Should this element (and children) be written out?
     *
     * @param descriptor the <code>ElementDescriptor</code> to evaluate
     * @param context the <code>Context</code> against which the element will be evaluated
     * @return true if this element should be written out
     * @throws IntrospectionException
     */
    private boolean ignoreElement(ElementDescriptor descriptor,
                                  String uri,
                                  String localName,
                                  String qualifiedName,
                                  Context context) {
        def strategy = bindingConfiguration.valueSuppressionStrategy
        if (strategy.suppressElement(descriptor, uri, localName, qualifiedName, context.getBean())) {
            return true
        }

        if (!writeEmptyElements) {
            return isEmptyElement(descriptor, context)
        }
        return false
    }

    /**
     * <p>Will evaluating this element against this context result in an empty element?</p>
     *
     * <p>An empty element is one that has no attributes, no child elements
     * and no body text.
     * For example, <code>&ltelement/&gt</code> is an empty element but
     * <code>&ltelement attr='value'/&gt</code> is not.</p>
     *
     * @param descriptor the <code>ElementDescriptor</code> to evaluate
     * @param context the <code>Context</code> against which the element will be evaluated
     * @return true if this element is empty on evaluation
     */
    private boolean isEmptyElement(ElementDescriptor descriptor, Context context) {
        if (descriptor.hasAttributes()) {
            return false
        }

        // an element is not empty if it has a non-empty body
        Expression expression = descriptor.getTextExpression()
        if (expression != null) {
            Object value = expression.evaluate(context)
            String text = convertToString(value, descriptor, context)
            if (text != null && text.length() > 0) {
                return false
            }
        }

        // always write out loops - even when they have no elements
        if (descriptor.collective) {
            return false
        }

        // now test child elements
        // an element is empty if it has no non-empty child elements
        if (descriptor.hasChildren()) {
            for (ElementDescriptor child : descriptor.getElementDescriptors()) {
                if (!isEmptyElement(child, context)) {
                    return false
                }
            }
        }

        if (descriptor.hollow) {
            Expression contentExpression = descriptor.getContextExpression()
            if (contentExpression != null) {
                Object childBean = contentExpression.evaluate(context)
                if (childBean != null) {
                    XMLBeanInfo xmlBeanInfo = findXMLBeanInfo(childBean, descriptor)
                    Object currentBean = context.getBean()
                    context.setBean(childBean)
                    boolean result = isEmptyElement(xmlBeanInfo.getElementDescriptor(), context)
                    context.setBean(currentBean)
                    return result
                }
            }
        }

        return true
    }

    private static final class InlineValueAttributes extends EmptyAttributes {
        InlineValueAttributes(String id, String idref, String value) {
            if (id) {
                addValue("id", id)
            }
            if (idref) {
                addValue("idref", idref)
            }
            addValue("inlinedValue", value)
        }
    }

    static class ElementAttributes extends EmptyAttributes {
        /** Attribute descriptors backing the <code>Attributes</code> */
        private List<AttributeDescriptor> attributes = []

        /** Context to be evaluated when finding values */
        private Context context
        /** The number of unsuppressed attributes */
        private BindingConfiguration bindingConfiguration

        /**
         * Construct attributes for element and context.
         *
         * @param descriptor the <code>ElementDescriptor</code> describing the element
         * @param context evaluate against this context
         */
        ElementAttributes(BindingConfiguration bindingConfiguration, ElementDescriptor descriptor, Context context) {
            this.bindingConfiguration = bindingConfiguration
            this.context = context
            init(descriptor.getAttributeDescriptors())
        }

        private void init(Iterable<AttributeDescriptor> source) {
            if (context != null) {
                for (AttributeDescriptor baseAttribute : source) {
                    String attributeValue = valueAttribute(baseAttribute)

                    def suppressionStrategy = bindingConfiguration.valueSuppressionStrategy
                    if (attributeValue != null && !suppressionStrategy.suppressAttribute(baseAttribute, attributeValue)) {
                        addValue(baseAttribute.qualifiedName, attributeValue)
                        attributes.add(baseAttribute)
                    }
                }
            }
        }

        private String valueAttribute(AttributeDescriptor attribute) {
            Expression expression = attribute.getTextExpression()
            if (expression != null) {
                Object value = expression.evaluate(context)
                return convertToString(bindingConfiguration, value, attribute, context)
            }

            return ""
        }

    }

    static class IDElementAttributes extends ElementAttributes {
        /**
         * Construct attributes for element and context.
         *
         * @param descriptor the <code>ElementDescriptor</code> describing the element
         * @param context evaluate against this context
         * @param idAttributeName the name of the id attribute
         * @param idValue the ID attribute value
         */
        IDElementAttributes(
                BindingConfiguration bindingConfiguration,
                ElementDescriptor descriptor,
                Context context,
                String idAttributeName,
                String idValue) {
            super(bindingConfiguration, descriptor, context)
            addValue(idAttributeName, idValue)
        }
    }

    /**
     * Writes the given element
     *
     * @param qualifiedName qualified name to use for the element
     * @param elementDescriptor the <code>ElementDescriptor</code> describing the element
     * @param context the <code>Context</code> to use to evaluate the bean expressions
     *
     * @deprecated 0.5 replaced by new SAX inspired API
     */
    protected void write(String qualifiedName, ElementDescriptor elementDescriptor, Context context) {
        writeElement("", qualifiedName, qualifiedName, elementDescriptor, context)
    }

    /**
     * Writes the given element adding an ID attribute
     *
     * @param qualifiedName qualified name to use for the element
     * @param elementDescriptor the <code>ElementDescriptor</code> describing the element
     * @param context the <code>Context</code> to use to evaluate the bean expressions
     * @param idAttribute the qualified name of the <code>ID</code> attribute
     * @param idValue the value for the <code>ID</code> attribute
     *
     * @deprecated 0.5 replaced by new SAX inspired API
     */
    protected void write(
            String qualifiedName,
            ElementDescriptor elementDescriptor,
            Context context,
            String idAttribute,
            String idValue) {
        writeElement("", qualifiedName, qualifiedName, elementDescriptor, context, idAttribute, idValue)
    }

    /**
     * Converts an object to a string.
     *
     * @param value the Object to represent as a String, possibly null
     * @param descriptor writing out this descriptor not null
     * @param context not null
     * @return String representation, not null
     */
    private String convertToString(Object value, Descriptor descriptor, Context context) {
        return convertToString(bindingConfiguration, value, descriptor, context)
    }
    /**
     * Converts an object to a string.
     *
     * @param value the Object to represent as a String, possibly null
     * @param descriptor writing out this descriptor not null
     * @param context not null
     * @return String representation, not null
     */
    private
    static String convertToString(BindingConfiguration config, Object value, Descriptor descriptor, Context context) {
        return config.objectStringConverter.objectToString(value, descriptor.getPropertyType(), context)
    }

    /**
     * Factory method for new contexts.
     * Ensure that they are correctly configured.
     *
     * @param bean make a new Context for this bean
     * @return a new context
     */
    private Context makeContext(Object bean) {
        return new Context(bean, log, bindingConfiguration)
    }

    /**
     * A listener that does nothing.
     */
    private static final class NoopListener implements BeanWriteEventListener {
        @Override
        void start() {
        }

        @Override
        void startElement(MutableWriteContext writeContext, String qualifiedName, Attributes attributes) {
        }

        @Override
        void bodyText(WriteContext context, String text) {
        }

        @Override
        void endElement(MutableWriteContext writeContext, String qualifiedName) {
        }

        @Override
        void end() {

        }
    }
}
