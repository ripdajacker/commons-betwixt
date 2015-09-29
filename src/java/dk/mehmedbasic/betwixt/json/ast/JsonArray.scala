package dk.mehmedbasic.betwixt.json.ast

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
 * A json array
 */
class JsonArray(parent: JsonNode,
                identifier: Identifier,
                rawValues: java.util.Collection[Any],
                dom: JsonDom) extends JsonNode(parent, dom) {
   private val values: ListBuffer[JsonNode] = readValues()

   def getValues = values

   private def readValues(): ListBuffer[JsonNode] = {
      val nodes: ListBuffer[JsonNode] = new ListBuffer[JsonNode]
      nodes ++= rawValues.map(JsonNode.convertToNode(this, _, identifier, dom))
      nodes
   }

   override def getIdentifier: Identifier = identifier

   override def getNodeType: JsonNodeType = JsonNodeType.ARRAY

   override def apply(name: String): Option[JsonNode] = None

   override def apply(index: Int): Option[JsonNode] = {
      if (index < 0 || values.length > index) {
         return None
      }
      Some(values.get(index))
   }

   def addValue(node: JsonNode) = values += node
}
