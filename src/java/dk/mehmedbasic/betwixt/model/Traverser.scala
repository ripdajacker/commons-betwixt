package dk.mehmedbasic.betwixt.model

import java.io.PrintWriter

import com.google.gson.stream.JsonWriter
import dk.mehmedbasic.betwixt.HexIdGenerator
import org.apache.commons.betwixt.expression.{Context, Expression, IteratorExpression}
import org.apache.commons.betwixt.{BindingConfiguration, ElementDescriptor, XMLBeanInfo, XMLIntrospector}
import org.apache.commons.logging.LogFactory

import scala.collection.JavaConverters._

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 *         created 6/9/2015.
 */
class Traverser extends DescriptorImplicits {
  private val introspector: XMLIntrospector = new XMLIntrospector()
  private val log = LogFactory.getLog(getClass)
  private val binding = new BindingConfiguration()

  private val idGenerator = new HexIdGenerator

  private val json: JsonWriter = new JsonWriter(new PrintWriter(System.out))

  def introspect(clazz: Class[_]) = introspector.introspect(clazz)

  def traverse(bean: AnyRef): Unit = {
        json.setIndent(" ")
    json.beginObject()

    traverseInner(bean)

    json.endObject()
    println()
  }

  def findReference(bean: Any) = Option(binding.getIdMappingStrategy.getReferenceFor(null, bean))

  def traverseInner(bean: Any, name: String = "", inCollection: Boolean = false): Unit = {

    val info: XMLBeanInfo = introspector.getRegistry.get(bean.getClass)
    val descriptor: ElementDescriptor = info.getElementDescriptor

    val reference: Option[String] = findReference(bean)
    val id = reference match {
      case None => idGenerator.nextId()
      case Some(x) => x
    }

    if (reference.isDefined) {
      if (!inCollection) {
        json.name(formatNameNoId(name, descriptor))
        json.value("@ref:%s".format(id))
      } else {
        json.beginObject()
        json.name("@ref")
        json.value(id)
        json.endObject()
      }

      return
    }

    binding.getIdMappingStrategy.setReference(null, bean, id)


    if (!inCollection) {
      json.name(formatName(name, descriptor, id))
      json.beginObject()
    } else {
      json.beginObject()

      json.name("@tag")
      json.value("%s #%s".format(descriptor.getLocalName, id))
    }

    val parentNode: Node = descriptor.toNode
    for (node <- parentNode.children) {
      println(node)
      if (node.isCollection) {
        val eval: AnyRef = evaluateExpression(node.iteratorExpression, bean)
        val iterator = eval.asInstanceOf[java.util.Iterator[_]].asScala

        if (iterator.hasNext) {
          json.name(node.name)
          json.beginArray()

          for (childBean <- iterator) {
            if (childBean != null) {
              traverseInner(childBean, "", inCollection = true)
            }
          }
          json.endArray()
        }

      } else if (node.isPrimitive) {
        node.expression match {
          case Some(x) =>
            val evaluate: AnyRef = evaluateExpression(x, bean)

            if (evaluate != null) {
              json.name(node.name)
              json.value(evaluate.toString)
            }
          case None =>
        }
      } else {
        node.expression match {
          case Some(x) =>
            val evaluate = evaluateExpression(x, bean)
            if (evaluate != null) {
              traverseInner(evaluate, node.name)
            }
          case None =>
        }
      }
    }

    json.endObject()
  }

  def formatName(name: String, descriptor: ElementDescriptor, id: String): String = {
    if (name.isEmpty) {
      "@%s #%s".format(descriptor.getLocalName, id)
    } else {
      "@%s:%s #%s".format(descriptor.getLocalName, name, id)
    }
  }

  def formatNameNoId(name: String, descriptor: ElementDescriptor): String = {
    if (name.isEmpty) {
      "@%s".format(descriptor.getLocalName)
    } else {
      "@%s:%s".format(descriptor.getLocalName, name)
    }
  }

  def evaluateExpression(e: Expression, bean: Any) = e.evaluate(new Context(bean, log, binding))


  def close(): Unit = {
    json.close()
  }


}

class Node(val name: String, val expression: Option[Expression], val children: List[Node]) extends DescriptorImplicits {
  def iteratorExpression: IteratorExpression = expression match {
    case None => children.head.iteratorExpression
    case Some(e) => e.asInstanceOf[IteratorExpression]
  }

  /**
   * Whether or not the node is a collection.
   *
   * @return the value.
   */
  def isCollection: Boolean = {
    expression match {
      case Some(e) => e.isInstanceOf[IteratorExpression]
      case None => children.size == 1 && children.head.isCollection
    }
  }

  def isPrimitive: Boolean = children.isEmpty && isSimpleExpression

  private def isSimpleExpression: Boolean = {
    expression match {
      case None => false
      case Some(e) => e.isPrimitive
    }
  }

  override def toString = "%s: simple=%s, collection=%s".format(name, isPrimitive, isCollection)
}

