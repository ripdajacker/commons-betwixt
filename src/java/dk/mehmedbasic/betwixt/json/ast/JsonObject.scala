package dk.mehmedbasic.betwixt.json.ast

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * A mutable json object.
 */
class JsonObject(parent: JsonNode,
                 private var identifier: Identifier,
                 reference: java.util.Map[String, Any],
                 dom: JsonDom)
   extends JsonNode(parent, dom) {

   private val innerMap: mutable.LinkedHashMap[String, Any] = readMap()

   private def readMap(): mutable.LinkedHashMap[String, Any] = {
      val result: mutable.LinkedHashMap[String, Any] = new mutable.LinkedHashMap[String, Any]

      for (key <- reference.keySet().asScala) {
         val childIdentifier: Identifier = Identifier.fromPropertyName(key)
         val value = JsonNode.convertToNode(this, reference.get(key), childIdentifier, dom)
         result.put(key, value)
      }

      result
   }

   private def asString(option: Option[Any]): Option[String] = {
      option match {
         case Some(s) => Option(s.asInstanceOf[String])
         case _ => None
      }
   }

   override def getIdentifier: Identifier = identifier
}
