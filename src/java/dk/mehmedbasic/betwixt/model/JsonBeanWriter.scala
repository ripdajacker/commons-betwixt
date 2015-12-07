package dk.mehmedbasic.betwixt.model

import java.io.Writer
import java.lang.reflect.{Method, ParameterizedType}

import com.google.gson.stream.JsonWriter
import dk.mehmedbasic.betwixt.HexIdGenerator
import org.apache.commons.betwixt._
import org.apache.commons.betwixt.expression.{Context, Expression, IteratorExpression}
import org.apache.commons.betwixt.strategy.ObjectStringConverter
import org.apache.commons.logging.LogFactory

import scala.collection.JavaConverters._

/**
  * The JSON-based class-hierarchy traversal tool.
  *
  * The class uses a JsonWriter from Gson to create the json tree.
  */
class JsonBeanWriter(output: Writer) extends DescriptorImplicits {
   private val introspector: XMLIntrospector = new XMLIntrospector()
   private val log = LogFactory.getLog(getClass)
   private val binding = new BindingConfiguration()

   private val idGenerator = new HexIdGenerator
   private val json: JsonWriter = new JsonWriter(output)

   def introspect(clazz: Class[_]) = introspector.introspect(clazz)

   def write(bean: AnyRef): Unit = {
      json.setIndent("")
      json.beginObject()

      traverseInner(bean)

      json.endObject()
   }

   def findReference(bean: Any) = Option(binding.getIdMappingStrategy.getReferenceFor(null, bean))

   def traverseInner(bean: Any, name: String = "", inCollection: Boolean = false): Unit = {
      var info: XMLBeanInfo = introspector.getRegistry.get(bean.getClass)
      if (info == null) {
         info = introspector.introspect(bean.getClass)
      }
      val toStringConverter: ObjectStringConverter = getBindingConfiguration.getObjectStringConverter

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
            json.value("@ref:%s".format(id))
         }
         return
      }

      binding.getIdMappingStrategy.setReference(null, bean, id)

      val parentNode: Node = descriptor.toNode

      val inlined = inlineDescriptor(descriptor, toStringConverter, bean)
      if (!inCollection) {
         json.name(formatName(name, descriptor, id))
         json.beginObject()

         if (inlined.isDefined) {
            json.name("@value #%s".format(id))
            json.value(inlined.get)
         }
      } else {
         if (inlined.isDefined && parentNode.children.isEmpty) {
            json.value("#%s %s".format(id, inlined.get))
         } else {
            json.beginObject()
            json.name("@tag")
            json.value("%s #%s".format(descriptor.getLocalName, id))
         }
      }


      for (node <- parentNode.children) {
         val propertyType: Class[_] = node.propertyType
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
                     writePrimitive(node.name, node.propertyType, evaluate)
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

   private def inlineDescriptor(descriptor: NodeDescriptor, toStringConverter: ObjectStringConverter, bean: Any): Option[String] = {
      if (toStringConverter.canHandle(descriptor.getPropertyType)) {
         val context: Context = createContext(bean)
         return Some(toStringConverter.objectToString(bean, descriptor.getPropertyType, context))
      }
      None
   }

   def formatName(name: String, descriptor: ElementDescriptor, id: String): String = {
      if (name.isEmpty) {
         "@%s #%s".format(descriptor.getLocalName, id)
      } else {
         "@%s #%s".format(name, id)
      }
   }

   def formatNameNoId(name: String, descriptor: ElementDescriptor): String = {
      if (name.isEmpty) {
         "@%s".format(descriptor.getLocalName)
      } else {
         "%s".format(name)
      }
   }

   def evaluateExpression(e: Expression, bean: Any) = e.evaluate(createContext(bean))

   def createContext(bean: Any) = new Context(bean, log, binding)

   def close(): Unit = {
      json.close()
   }

   def getBindingConfiguration = binding

   def getXMLIntrospector = introspector

   /**
     * Writes a primitive as a JSON value.
     *
     * If the value given is a string, a JSON string is written.
     *
     * @param name         the name of the property.
     * @param propertyType the property class.
     * @param value        the property value.
     */
   private def writePrimitive(name: String, propertyType: Class[_], value: AnyRef): Unit = {
      if (propertyType.equals(classOf[Boolean])) {
         val boolean = value.asInstanceOf[Boolean]
         json.name(name)

         // Write boolean values as 0 or 1.
         json.value(boolean.compare(false))
         return
      }

      if (classOf[Number].isAssignableFrom(propertyType)) {
         val number: Number = value.asInstanceOf[Number]
         // Don't write NaN values
         if (!number.doubleValue().isNaN) {
            json.name(name)
            json.value(number)
         }
      } else if (classOf[Double].isAssignableFrom(propertyType)) {
         val double: Double = value.asInstanceOf[Double]
         // Don't write NaN values
         if (!double.isNaN) {
            json.name(name)
            json.value(double)
         }
      } else if (classOf[Float].isAssignableFrom(propertyType)) {
         val float: Float = value.asInstanceOf[Float]
         // Don't write NaN values
         if (!float.isNaN) {
            json.name(name)
            json.value(float)
         }
      } else if (classOf[Int].isAssignableFrom(propertyType)) {
         json.name(name)
         json.value(value.asInstanceOf[Int])
      } else if (classOf[Long].isAssignableFrom(propertyType)) {
         json.name(name)
         json.value(value.asInstanceOf[Long])
      } else {
         json.name(name)
         json.value(value.toString)
      }
   }
}

/**
  * A node in the tree.
  *
  * @param name       the name of the node.
  * @param expression the betwixt node expression.
  * @param children   the list of children.
  */
class Node(val name: String, val propertyType: Class[_], val expression: Option[Expression], val children: List[Node]) extends DescriptorImplicits {
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

   def typeCheck(): Option[Class[_]] = {
      findType() match {
         case None => None
         case Some(m) =>
            m.getGenericReturnType match {
               case parametrized: ParameterizedType =>
                  parametrized.getActualTypeArguments.foreach(println(_))
                  Some(parametrized.getRawType.asInstanceOf[Class[_]])
               case _ => None
            }
      }
   }

   private def findType(): Option[Method] = expression match {
      case None => None
      case Some(e) => e.findMethod()
   }

   private def isSimpleExpression: Boolean = {
      expression match {
         case None => false
         case Some(e) => e.isPrimitive
      }
   }

   override def toString = "%s: simple=%s, collection=%s".format(name, isPrimitive, isCollection)
}

