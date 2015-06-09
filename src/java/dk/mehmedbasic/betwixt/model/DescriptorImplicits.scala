package dk.mehmedbasic.betwixt.model

import java.lang.reflect.Method

import org.apache.commons.betwixt.expression.{Expression, MethodExpression}
import org.apache.commons.betwixt.{AttributeDescriptor, ElementDescriptor}

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 *         created 6/9/2015.
 */
trait DescriptorImplicits {

  private def attributeToNode(attribute: AttributeDescriptor): Node = {
    new Node(attribute.getLocalName, Option(attribute.getTextExpression), List())
  }

  private def elementToNode(e: ElementDescriptor) = new Node(e.getLocalName, e.expression(), e.children())

  private def notNull[T](refs: T*): Option[T] = refs.find(_ != null)

  implicit class ElementWrapper(descriptor: ElementDescriptor) {

    private def attributes(): List[Node] = descriptor.getAttributeDescriptors.map(attributeToNode).toList

    private def elements(): List[Node] = descriptor.getElementDescriptors.map(elementToNode).toList


    def children() = attributes() ++ elements()

    def expression(): Option[Expression] = notNull(descriptor.getContextExpression, descriptor.getTextExpression)

    def toNode: Node = elementToNode(descriptor)
  }

  implicit class ExpressionWrapper(expression: Expression) {

    private val primitives = List(
      classOf[Int], classOf[Integer],
      classOf[Boolean], classOf[java.lang.Boolean],
      classOf[Long], classOf[java.lang.Long],
      classOf[Double], classOf[java.lang.Double],
      classOf[String])

    private def primitiveType(method: Method): Boolean = primitives.find(_ == method.getReturnType).nonEmpty

    def isPrimitive: Boolean = expression.isInstanceOf[MethodExpression] && primitiveType(expression.asInstanceOf[MethodExpression].getMethod)
  }

}
