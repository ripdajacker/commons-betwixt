package dk.mehmedbasic.betwixt.model

import java.lang.reflect.Method

import org.apache.commons.betwixt.expression.{Expression, IteratorExpression, MethodExpression}
import org.apache.commons.betwixt.{AttributeDescriptor, ElementDescriptor}

/**
 * A trait containing implicits for handling Betwixt descriptors.
 */
trait DescriptorImplicits {

   private def attributeToNode(attribute: AttributeDescriptor): Node = {
      val name = notNull(attribute.getLocalName, attribute.getQualifiedName, attribute.getPropertyName)
      if (name.isEmpty) {
         println(attribute)
      }
      new Node(name.get, attribute.getPropertyType, Option(attribute.getTextExpression), List())
   }

   private def elementToNode(element: ElementDescriptor) = {
      val name = notNull(element.getLocalName, element.getQualifiedName, element.getPropertyName)
      new Node(name.get, element.getPropertyType, element.expression(), element.children())
   }

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
         classOf[Float], classOf[java.lang.Float],
         classOf[Double], classOf[java.lang.Double],
         classOf[String])

      private def primitiveType(method: Method): Boolean = primitives.find(_ == method.getReturnType).nonEmpty

      def isPrimitive: Boolean = expression.isInstanceOf[MethodExpression] && primitiveType(expression.asInstanceOf[MethodExpression].getMethod)

      def findMethod(): Option[Method] = findMethod(expression)

      private def findMethod(expression: Expression): Option[Method] = {
         expression match {
            case e: MethodExpression => Option(e.getMethod)
            case iter: IteratorExpression => findMethod(iter.getExpression)
            case _ => None
         }
      }

   }

}
