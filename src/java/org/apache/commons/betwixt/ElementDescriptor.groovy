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
package org.apache.commons.betwixt

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.expression.Expression

/** <p><code>ElementDescriptor</code> describes the XML elements
 * to be created for a bean instance.</p>
 *
 * <p> It contains <code>AttributeDescriptor</code>'s for all it's attributes
 * and <code>ElementDescriptor</code>'s for it's child elements.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 */
@TypeChecked
public class ElementDescriptor extends NodeDescriptor {

    /**
     * Descriptors for attributes this element contains.
     * <strong>Note:</strong> Constructed lazily on demand from a List.
     */
    List<AttributeDescriptor> attributeDescriptors = []
    /**
     * Descriptors for child elements.
     * <strong>Note:</strong> Constructed lazily on demand from a List.
     */
    List<ElementDescriptor> elementDescriptors = []

    /**
     * Descriptors for child content.
     * <strong>Note:</strong> Constructed lazily on demand from a List.
     */
    List<Descriptor> contentDescriptors = []

    /** the expression used to evaluate the new context of this node
     * or null if the same context is to be used */
    private Expression contextExpression

    /** Whether this element refers to a primitive type (or property of a parent object) */
    private boolean primitiveType
    /** Is this a collective type? */
    private boolean isCollectiveType

    /**
     * Is this element hollow?
     * In other words, is this descriptor a place holder indicating the name
     * and update for a root ElementDescriptor for this type obtained by introspection
     * TODO: this would probably be better modeled as a separate subclass
     */
    boolean hollow = false

    /**
     * Sets whether <code>Collection</code> bean properties should wrap items in a parent element.
     * In other words, should the mapping for bean properties which are <code>Collection</code>s
     * enclosed the item elements within a parent element.
     * Normally only used when this describes a collection bean property.
     *
     * @param wrapCollectionsInElement true if the elements for the items in the collection
     * should be contained in a parent element
     * @deprecated 0.6 moved to a declarative style of descriptors where the alrogithmic should
     * be done during introspection
     */
    boolean wrapCollectionsInElement = true

    /** specifies a separate implementation class that should be instantiated
     * when reading beans
     * or null if there is no separate implementation */
    private Class implementationClass = null

    /**
     * Should the bind time type determine the mapping?
     * (As opposed to the introspection time type.)
     * Note that this attribute is write once, read many (WORM).
     */
    private Boolean useBindTimeTypeForMapping = null

    /**
     * Constructs an <code>ElementDescriptor</code> that refers to a primitive type.
     */
    public ElementDescriptor() {
    }

    /**
     * Base constructor.
     * @param primitiveType if true, this element refers to a primitive type
     * @deprecated 0.6 PrimitiveType property has been removed
     */
    public ElementDescriptor(boolean primitiveType) {
        this.primitiveType = primitiveType
    }

    /**
     * Creates a ElementDescriptor with no namespace URI or prefix.
     *
     * @param localName the (xml) local name of this node.
     * This will be used to set both qualified and local name for this name.
     */
    public ElementDescriptor(String localName) {
        super(localName)
    }

    /**
     * Creates a <code>ElementDescriptor</code> with namespace URI and qualified name
     * @param localName the (xml) local name of this  node
     * @param qualifiedName the (xml) qualified name of this node
     * @param uri the (xml) namespace prefix of this node
     */
    public ElementDescriptor(String localName, String qualifiedName, String uri) {
        super(localName, qualifiedName, uri)
    }

    /**
     * Returns true if this element has child <code>ElementDescriptors</code>
     * @return true if this element has child elements
     * @see #getElementDescriptors
     */
    public boolean hasChildren() {
        return getElementDescriptors().size() > 0
    }

    /**
     * Returns true if this element has <code>AttributeDescriptors</code>
     * @return true if this element has attributes
     */
    public boolean hasAttributes() {
        return attributeDescriptors.size() > 0
    }

    /**
     * Returns true if this element has child content.
     * @return true if this element has either child mixed content or child elements
     * @see #getContentDescriptors
     * @since 0.5
     */
    public boolean hasContent() {
        return getContentDescriptors().size() > 0
    }

    /**
     * <p>Is this a simple element?</p>
     * <p>
     * A simple element is one without child elements or attributes.
     * This corresponds to the simple type concept used in XML Schema.
     * TODO: need to consider whether it's sufficient to calculate
     * which are simple types (and so don't get IDs assigned etc).
     * </p>
     * @return true if it is a <code>SimpleType</code> element
     */
    public boolean isSimple() {
        return !(hasAttributes()) && !(hasChildren())
    }

    /**
     * Adds an attribute to the element this <code>ElementDescriptor</code> describes
     * @param descriptor the <code>AttributeDescriptor</code> that will be added to the
     * attributes associated with element this <code>ElementDescriptor</code> describes
     */
    public void addAttributeDescriptor(AttributeDescriptor descriptor) {
        attributeDescriptors.add(descriptor)
    }

    /**
     * Removes an attribute descriptor from this element descriptor.
     * @param descriptor the <code>AttributeDescriptor</code> to be removed, not null
     * @since 0.8
     */
    public void removeAttributeDescriptor(AttributeDescriptor descriptor) {
        attributeDescriptors.remove(descriptor)
    }

    /**
     * Returns an attribute descriptor with a given name or null.
     *
     * @param name to search for will be checked against the attributes' qualified name.
     * @return <code>AttributeDescriptor</code> with the given name,
     * or null if no descriptor has that name
     * @since 0.8
     */
    public AttributeDescriptor getAttributeDescriptor(final String name) {
        for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
            if (attributeDescriptor.getQualifiedName().equals(name)) {
                return attributeDescriptor
            }
        }

        return null
    }

    /**
     * Sets the <code>AttributesDescriptors</code> for this element.
     * This sets descriptors for the attributes of the element describe by the
     * <code>ElementDescriptor</code>.
     *
     * @param attributeDescriptors the <code>AttributeDescriptor</code> describe the attributes
     * of the element described by this <code>ElementDescriptor</code>
     */
    public void setAttributeDescriptors(List<AttributeDescriptor> attributeDescriptors) {
        this.attributeDescriptors = attributeDescriptors
    }

    /**
     * Adds a descriptor for a child element.
     *
     * @param descriptor the <code>ElementDescriptor</code> describing the child element to add
     */
    public void addElementDescriptor(ElementDescriptor descriptor) {
        elementDescriptors.add(descriptor)
        addContentDescriptor(descriptor)
    }

    /**
     * Removes an element descriptor from this element descriptor.
     * @param descriptor the <code>ElementDescriptor</code> that will be removed.
     * @since 0.8
     */
    public void removeElementDescriptor(ElementDescriptor descriptor) {
        elementDescriptors.remove(descriptor)
        contentDescriptors.remove(descriptor)
    }

    /**
     * Returns descriptors for the child elements of the element this describes.
     * @return the <code>ElementDescriptor</code> describing the child elements
     * of the element that this <code>ElementDescriptor</code> describes
     */
    public List<ElementDescriptor> getElementDescriptors() {
        return elementDescriptors
    }

    /**
     * Gets a child ElementDescriptor matching the given name if one exists.
     * Note that (so long as there are no better matches), a null name
     * acts as a wildcard. In other words, an
     * <code>ElementDescriptor</code> the first descriptor
     * with a null name will match any name
     * passed in, unless some other matches the name exactly.
     *
     * @param name the localname to be matched, not null
     * @return the child ElementDescriptor with the given name if one exists,
     * otherwise null
     */
    public ElementDescriptor getElementDescriptor(String name) {

        ElementDescriptor elementDescriptor = null
        ElementDescriptor descriptorWithNullName = null
        ElementDescriptor firstPolymorphic = null
        for (ElementDescriptor current : elementDescriptors) {
            if (firstPolymorphic == null && current.isPolymorphic()) {
                firstPolymorphic = current
            }
            String elementName = current.getQualifiedName()
            if (name.equals(elementName)) {
                elementDescriptor = current
                break
            }
            if (descriptorWithNullName == null && elementName == null) {
                descriptorWithNullName = current
            }
        }
        if (elementDescriptor == null) {
            elementDescriptor = firstPolymorphic
        }
        if (elementDescriptor == null) {
            elementDescriptor = descriptorWithNullName
        }
        return elementDescriptor
    }

    /**
     * Sets the descriptors for the child element of the element this describes.
     * Also sets the child content descriptors for this element
     *
     * @param elementDescriptors the <code>ElementDescriptor</code>s of the element
     * that this describes
     */
    public void setElementDescriptors(List<ElementDescriptor> elementDescriptors) {
        this.elementDescriptors = elementDescriptors
        setContentDescriptors(elementDescriptors)
    }

    /**
     * Adds a descriptor for child content.
     *
     * @param descriptor the <code>Descriptor</code> describing the child content to add
     * @since 0.5
     */
    public void addContentDescriptor(Descriptor descriptor) {
        contentDescriptors.add(descriptor)
    }

    /**
     * Returns descriptors for the child content of the element this describes.
     * @return the <code>Descriptor</code> describing the child elements
     * of the element that this <code>ElementDescriptor</code> describes
     * @since 0.5
     */
    public List<Descriptor> getContentDescriptors() {
        return contentDescriptors
    }

    /**
     * <p>Gets the primary descriptor for body text of this element.
     * Betwixt collects all body text for any element together.
     * This makes it rounds tripping difficult for beans that write more than one
     * mixed content property.
     * </p><p>
     * The algorithm used in the default implementation is that the first TextDescriptor
     * found amongst the descriptors is returned.
     *
     * @return the primary descriptor or null if this element has no mixed body content
     * @since 0.5
     */
    public TextDescriptor getPrimaryBodyTextDescriptor() {
        for (Descriptor descriptor : contentDescriptors) {
            if (descriptor instanceof TextDescriptor) {
                return (TextDescriptor) descriptor
            }
        }

        return null
    }

    /**
     * Sets the descriptors for the child content of the element this describes.
     * @param contentDescriptors the <code>Descriptor</code>s of the element
     * that this describes
     * @since 0.5
     */
    public void setContentDescriptors(List<? extends Descriptor> contentDescriptors) {
        this.contentDescriptors = contentDescriptors
    }

    /**
     * Returns the expression used to evaluate the new context of this element.
     * @return the expression used to evaluate the new context of this element
     */
    public Expression getContextExpression() {
        return contextExpression
    }

    /**
     * Sets the expression used to evaluate the new context of this element
     * @param contextExpression the expression used to evaluate the new context of this element
     */
    public void setContextExpression(Expression contextExpression) {
        this.contextExpression = contextExpression
    }

    /**
     * Returns true if this element refers to a primitive type property
     * @return whether this element refers to a primitive type (or property of a parent object)
     * @deprecated 0.6 moved to a declarative style of descriptors where the alrogithmic should
     * be done during introspection
     */
    public boolean isPrimitiveType() {
        return primitiveType
    }

    /**
     * Sets whether this element refers to a primitive type (or property of a parent object)
     * @param primitiveType true if this element refers to a primitive type
     * @deprecated 0.6 moved to a declarative style of descriptors where the alrogithmic should
     * be done during introspection
     */
    public void setPrimitiveType(boolean primitiveType) {
        this.primitiveType = primitiveType
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Gets the class which should be used for instantiation.
     * @return the class which should be used for instantiation of beans
     * mapped from this element, null if the standard class should be used
     */
    public Class getImplementationClass() {
        return implementationClass
    }

    /**
     * Sets the class which should be used for instantiation.
     * @param implementationClass the class which should be used for instantiation
     * or null to use the mapped type
     * @since 0.5
     */
    public void setImplementationClass(Class implementationClass) {
        this.implementationClass = implementationClass
    }

    /**
     * Does this describe a collective?
     */
    public boolean isCollective() {
        // TODO is this implementation correct?
        // maybe this method is unnecessary
        return isCollectiveType
    }

    /**
     * Sets whether the element described is a collective.
     * @since 0.7
     * @param isCollectiveType
     */
    public void setCollective(boolean isCollectiveType) {
        this.isCollectiveType = isCollectiveType
    }

    /**
     * Finds the parent of the given descriptor.
     * @param elementDescriptor <code>ElementDescriptor</code>
     * @return <code>ElementDescriptor</code>, not null
     */
    public ElementDescriptor findParent(ElementDescriptor elementDescriptor) {
        //TODO: is this really a good design?
        for (ElementDescriptor child : elementDescriptors) {
            if (child.equals(elementDescriptor)) {
                return this
            } else {
                def result = child.findParent(elementDescriptor)
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }

    /**
     * Returns something useful for logging.
     *
     * @return a string useful for logging
     */
    public String toString() {
        return "ElementDescriptor[qname=$qualifiedName,pname=$propertyName,class=$propertyType,singular=$singularPropertyType,updater=$updater,wrap=$wrapCollectionsInElement]"
    }

    /**
     * <p>Is the bind time type to be used to determine the mapping?</p>
     * <p>
     * The mapping for an object property value can either be the
     * introspection time type (based on the logical type of the property)
     * or the bind time type (based on the type of the actual instance).
     * </p>
     * @since 0.7
     * @return true if the bind time type is to be used to determine the mapping,
     * false if the introspection time type is to be used
     */
    public boolean isUseBindTimeTypeForMapping() {
        boolean result = true
        if (this.useBindTimeTypeForMapping != null) {
            result = this.useBindTimeTypeForMapping.booleanValue()
        }
        return result
    }

    /**
     * <p>Sets whether the bind time type to be used to determine the mapping.
     * The mapping for an object property value can either be the
     * introspection time type (based on the logical type of the property)
     * or the bind time type (based on the type of the actual instance).
     * </p><p>
     * <strong>Note:</strong> this property is write once, read many.
     * So, the first time that this method is called the value will be set
     * but subsequent calls will be ignored.
     * </p>
     * @since 0.7
     * @param useBindTimeTypeForMapping true if the bind time type is to be used to
     * determine the mapping, false if the introspection time type is to be used
     */
    public void setUseBindTimeTypeForMapping(boolean useBindTimeTypeForMapping) {
        if (this.useBindTimeTypeForMapping == null) {
            this.useBindTimeTypeForMapping = new Boolean(useBindTimeTypeForMapping)
        }
    }

    /**
     * <p>Is this a polymorphic element?</p>
     * <p>
     * A polymorphic element's name is not fixed at
     * introspection time and it's resolution is postponed to bind time.
     * </p>
     * @since 0.7
     * @return true if {@link #getQualifiedName} is null,
     * false otherwise
     */
    public boolean isPolymorphic() {
        return (getQualifiedName() == null)
    }
}
