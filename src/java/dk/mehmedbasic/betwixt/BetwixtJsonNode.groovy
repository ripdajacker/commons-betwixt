package dk.mehmedbasic.betwixt

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.expression.Expression
import org.apache.commons.betwixt.expression.IteratorExpression

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class BetwixtJsonNode {
    List<BetwixtJsonNode> children = []
    String name
    Class<?> propertyType
    Expression expression

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
        return expression && expression.isSimpleExpression()
    }
}
