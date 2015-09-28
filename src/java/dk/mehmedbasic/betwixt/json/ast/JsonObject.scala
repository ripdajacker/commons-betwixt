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

   private val innerMap: mutable.LinkedHashMap[String, JsonNode] = readMap()

   private def readMap(): mutable.LinkedHashMap[String, JsonNode] = {
      val result = new mutable.LinkedHashMap[String, JsonNode]

      for (key <- reference.keySet().asScala) {
         val childIdentifier: Identifier = Identifier.fromPropertyName(key)
         val value = JsonNode.convertToNode(this, reference.get(key), childIdentifier, dom)
         result.put(Identifier.stripId(key), value)
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

   override def getNodeType: JsonNodeType = JsonNodeType.OBJECT

   override def apply(name: String): Option[JsonNode] = innerMap.get(name)

   override def apply(index: Int): Option[JsonNode] = None
}
