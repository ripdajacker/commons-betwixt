package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.expression.Updater
import org.apache.commons.betwixt.io.read.MappingAction
import org.apache.commons.betwixt.io.read.ReadContext
import org.apache.commons.betwixt.io.read.ReadContextEventListener
import org.codehaus.jackson.JsonNode

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class JsonReadContextEventListener implements ReadContextEventListener {
    LinkedList<ElementDescriptor> descriptors = []
    LinkedList<MappingAction> actions = []
    LinkedList<Class> classes = []
    LinkedList<String> elementNames = []
    LinkedList<Object> beans = []
    LinkedList<Updater> updaters = []
    LinkedList<ReadContext> readingContexts = []

    LinkedList<JsonNode> jsonNodes = []

    LinkedList<Closure<Void>> callbacks = []

    @Override
    void descriptorPushed(ReadContext readContext, ElementDescriptor descriptor) {
        descriptors.addFirst(descriptor)
        pushContext()

        if (callbacks.size() > 0) {
            peekCallback().call(jsonNodes.peek(), peekCurrentDescriptor())
        }
    }

    void pushContext() {
    }

    void popContext() {
    }

    @Override
    void descriptorPopped(ReadContext readContext, ElementDescriptor descriptor) {
        popContext()
        descriptors.removeFirst()
    }

    @Override
    void beanPushed(ReadContext readContext, Object bean) {
        pushContext()
        beans.addFirst(bean)
    }

    @Override
    void beanPopped(ReadContext readContext, Object bean) {
        popContext()
        beans.removeFirst()
    }

    @Override
    void elementClassPushed(ReadContext readContext, Class clazz) {
        pushContext()
        classes.addFirst(clazz)
    }

    @Override
    void elementPushed(ReadContext readContext, String elementName) {
        pushContext()
        elementNames.addFirst(elementName)
    }

    @Override
    void elementPopped(ReadContext readContext, String elementName) {
        popContext()
        elementNames.removeFirst()
    }

    @Override
    void updaterPushed(ReadContext readContext, Updater updater) {
        pushContext()
        updaters.addFirst(updater)
    }

    @Override
    void updaterPopped(ReadContext readContext, Updater updater) {
        popContext()
        updaters.removeFirst()
    }

    @Override
    void actionMappingPushed(ReadContext readContext, MappingAction action) {
        pushContext()
        actions.addFirst(action)
    }

    @Override
    void actionMappingPopped(ReadContext readContext, MappingAction action) {
        popContext()
        actions.removeFirst()
    }

    ElementDescriptor peekCurrentDescriptor() {
        if (descriptors.size() > 0) {
            return descriptors.peek()
        }
        return null
    }

    void pushJsonNode(JsonNode node) {
        jsonNodes.addFirst(node)
    }

    void popJsonNode() {
        jsonNodes.removeFirst()
    }

    void pushCallback(Closure closure) {
        callbacks.addFirst(closure)
    }

    Closure popCallback() {
        callbacks.removeFirst()
    }

    Closure peekCallback() {
        callbacks.peek()
    }
}
