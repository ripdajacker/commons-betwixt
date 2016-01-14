package dk.mehmedbasic.betwixt

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.NodeDescriptor
import org.apache.commons.betwixt.expression.Context
import org.apache.commons.betwixt.expression.Expression
import org.apache.commons.betwixt.expression.IteratorExpression

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
        this.propertyType = baseDescriptor.propertyType
    }

    IteratorExpression iteratorExpression() {
        if (expression) {
            return expression as IteratorExpression
        }
        return children.head().iteratorExpression()
    }

    boolean isCollection() {
        if (expression) {
            return expression instanceof IteratorExpression
        }
        return children.size() == 1 && children.head().isCollection()
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

    Object evaluateAsCollection(Context context) {
        def expression = iteratorExpression()
        if (expression) {
            return expression.evaluate(context)
        }
        return null
    }

    private static <T> T nonNull(T... ts) {
        return ts.find { it != null }
    }
}
