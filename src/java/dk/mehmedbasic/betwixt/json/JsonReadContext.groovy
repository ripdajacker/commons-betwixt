package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked
import groovy.util.logging.Commons
import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.Options
import org.apache.commons.betwixt.XMLBeanInfo
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.betwixt.expression.Updater
import org.apache.commons.betwixt.io.read.AbstractReadContext
import org.apache.commons.betwixt.io.read.ElementMapping
import org.apache.commons.betwixt.registry.PolymorphicReferenceResolver

import java.beans.IntrospectionException

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
@Commons
class JsonReadContext implements AbstractReadContext {

    XMLIntrospector introspector = new XMLIntrospector()

    private LinkedList<Object> elementMappingStack = []
    private LinkedList<ElementDescriptor> descriptorStack = []
    private LinkedList<Updater> updaterStack = []
    private LinkedList<Object> objectStack = []

    Class rootClass

    /**
     * Pushes the given element onto the element mapping stack.
     *
     * @param elementName the local name if the parser is namespace aware,
     * otherwise the full element name. Not null
     */
    public void pushElement(String elementName) throws Exception {
        elementMappingStack.push(elementName);

        ElementDescriptor nextDescriptor = null;
        if (elementMappingStack.size() == 1 && rootClass != null) {
            XMLBeanInfo rootClassInfo = introspector.introspect(rootClass);
            nextDescriptor = rootClassInfo.getElementDescriptor();
        } else {
            ElementDescriptor currentDescriptor = peekDescriptor();
            if (currentDescriptor != null) {
                nextDescriptor = currentDescriptor.getElementDescriptor(elementName);
            }
        }
        Updater updater = null;
        Options options = null;
        if (nextDescriptor != null) {
            updater = nextDescriptor.getUpdater();
            options = nextDescriptor.getOptions();
        }
        updaterStack.push(updater);
        descriptorStack.push(nextDescriptor);
    }

    private ElementDescriptor peekDescriptor() throws Exception {
        if (descriptorStack.empty) {
            return null
        }
        return descriptorStack.peek()
    }

    private Updater peekUpdater() {
        if (updaterStack.empty) {
            return null
        }
        return updaterStack.peek()
    }

    public Object peekBean() {
        if (objectStack.empty) {
            return null
        }
        return objectStack.peek()
    }

    public Object popBean() {
        return objectStack.pop()
    }

    @Override
    Class resolvePolymorphicType(ElementMapping mapping) {
        Class result = null;
        try {
            ElementDescriptor currentDescriptor = peekDescriptor();
            if (currentDescriptor != null) {
                if (currentDescriptor.isPolymorphic()) {
                    PolymorphicReferenceResolver resolver = introspector.getPolymorphicReferenceResolver();
                    result = resolver.resolveType(mapping, this);
                    if (result == null) {
                        // try the other polymorphic descriptors
                        ElementDescriptor parent = peekParentDescriptor();
                        if (parent != null) {
                            ElementDescriptor[] descriptors = parent.getElementDescriptors();
                            ElementDescriptor originalDescriptor = mapping.getDescriptor();
                            boolean resolved = false;
                            for (int i = 0; i < descriptors.length; i++) {
                                ElementDescriptor descriptor = descriptors[i];
                                if (descriptor.isPolymorphic()) {
                                    mapping.setDescriptor(descriptor);
                                    result = resolver.resolveType(mapping, this);
                                    if (result != null) {
                                        resolved = true;
                                        descriptorStack.pop();
                                        descriptorStack.push(descriptor);

                                        Updater originalUpdater = originalDescriptor.getUpdater();
                                        Updater newUpdater = descriptor.getUpdater();

                                        substituteUpdater(originalUpdater, newUpdater);

                                        break;
                                    }
                                }
                            }
                            if (resolved) {
                                log.debug("Resolved polymorphic type");
                            } else {
                                log.debug("Failed to resolve polymorphic type");
                                mapping.setDescriptor(originalDescriptor);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("Failed to resolved polymorphic type");
            log.debug(mapping, e);
        }
        return result;
    }


    private ElementDescriptor peekParentDescriptor() throws IntrospectionException {
        ElementDescriptor result = null;
        if (descriptorStack.size() > 1) {
            result = (ElementDescriptor) descriptorStack.get(descriptorStack.size() - 2);
        }
        return result;
    }

    /**
     * Substitutes one updater in the stack for another.
     *
     * @param originalUpdater <code>Updater</code> possibly null
     * @param newUpdater <code>Updater</code> possibly null
     */
    private void substituteUpdater(Updater originalUpdater, Updater newUpdater) {
        if (!updaterStack.isEmpty()) {
            Updater updater = (Updater) updaterStack.pop();
            if (originalUpdater == null && updater == null) {
                updaterStack.push(newUpdater);
            } else if (originalUpdater.equals(updater)) {
                updaterStack.push(newUpdater);
            } else {
                substituteUpdater(originalUpdater, newUpdater);
                updaterStack.push(updater);
            }
        }
    }
}
