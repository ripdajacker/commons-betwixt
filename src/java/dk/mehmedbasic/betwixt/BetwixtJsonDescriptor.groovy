package dk.mehmedbasic.betwixt

import dk.mehmedbasic.betwixt.json.JsonReadContextEventListener
import groovy.transform.TypeChecked
import org.apache.commons.betwixt.BindingConfiguration
import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.NodeDescriptor
import org.apache.commons.betwixt.expression.Context
import org.apache.commons.betwixt.expression.Expression
import org.apache.commons.betwixt.expression.IteratorExpression
import org.apache.commons.betwixt.expression.Updater
import org.apache.commons.betwixt.io.AbstractBeanWriter
import org.apache.commons.betwixt.io.read.ElementMapping
import org.apache.commons.betwixt.io.read.ReadConfiguration
import org.apache.commons.betwixt.io.read.ReadContext
import org.apache.commons.logging.Log

/**
 * The JSON descriptor.
 *
 * This works as a compacted, composite descriptor that does not distinguish between AttributeDescriptor and
 * ElementDescriptor children.
 */
@TypeChecked
class BetwixtJsonDescriptor {
    List<BetwixtJsonDescriptor> children = []
    String name
    Class<?> propertyType
    Expression expression
    Updater updater

    private NodeDescriptor originalDescriptor

    BetwixtJsonDescriptor(NodeDescriptor baseDescriptor) {
        if (baseDescriptor instanceof ElementDescriptor) {
            def elementDescriptor = baseDescriptor as ElementDescriptor
            this.children += elementDescriptor.getAttributeDescriptors().collect {
                new BetwixtJsonDescriptor(it as NodeDescriptor)
            }
            this.children += elementDescriptor.getElementDescriptors().collect {
                new BetwixtJsonDescriptor(it as NodeDescriptor)
            }
            this.expression = nonNull(elementDescriptor.contextExpression, elementDescriptor.textExpression)
        } else {
            this.expression = baseDescriptor.textExpression
        }
        this.name = nonNull(baseDescriptor.localName, baseDescriptor.qualifiedName, baseDescriptor.propertyName)
        this.updater = baseDescriptor.getUpdater()
        this.propertyType = baseDescriptor.singularPropertyType
        this.originalDescriptor = baseDescriptor
    }

    IteratorExpression iteratorExpression() {
        if (expression) {
            return expression as IteratorExpression
        }
        return children.head().iteratorExpression()
    }

    boolean isHollow() {
        if (originalDescriptor instanceof ElementDescriptor) {
            def descriptor = originalDescriptor as ElementDescriptor
            return descriptor.hollow
        }
        return false
    }

    boolean isCollection() {
        if (expression) {
            return expression instanceof IteratorExpression
        }
        return false
    }

    boolean onlyChildIsCollection() {
        if (children.size() != 1) {
            return false
        }
        return children.get(0).collection
    }

    boolean isPrimitive() {
        return children.empty && primitiveType
    }

    boolean isPrimitiveType() {
        return expression && expression.isPrimitiveResult()
    }

    Object evaluateThis(Context context) {
        if (expression) {
            return expression.evaluate(context)
        }
        return null
    }

    Iterator evaluateAsIterator(Context context) {
        def expression = iteratorExpression()
        if (expression) {
            return expression.evaluate(context) as Iterator
        }
        return null
    }

    Collection evaluateAsCollection(Context context) {
        def expression = iteratorExpression()
        if (expression) {
            def iteratorExpression = expression as IteratorExpression
            return iteratorExpression.expression.evaluate(context) as Collection
        }
        return null
    }

    Object createInstance(BetwixtJsonData data,
                          Log log,
                          ReadConfiguration readConfiguration,
                          BindingConfiguration bindingConfiguration) {
        if (isCollection()) {
            return null
        }
        if (element) {

            def mapping = fakeElementMapping(bindingConfiguration, data, log)
            def readContext = new ReadContext(new JsonReadContextEventListener(), log, bindingConfiguration, readConfiguration)

            return readConfiguration.beanCreationChain.create(mapping, readContext)
        } else {
            throw new IllegalArgumentException()
        }
    }

    private static <T> T nonNull(T... ts) {
        return ts.find { it != null }
    }

    boolean isElement() {
        return originalDescriptor instanceof ElementDescriptor
    }

    private ElementMapping fakeElementMapping(BindingConfiguration configuration, BetwixtJsonData data, Log log) {

        def mapping = new ElementMapping()

        def elementDescriptor = this.originalDescriptor as ElementDescriptor

        def context = new Context(null, log)
        mapping.attributes = new AbstractBeanWriter.ElementAttributes(configuration, elementDescriptor, context)
        mapping.descriptor = elementDescriptor
        mapping.name = data.propertyName ?: ""
        mapping.namespace = ""
        mapping.type = propertyType
        return mapping
    }
}
