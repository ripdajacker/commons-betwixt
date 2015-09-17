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
   private val values = readValues()

   def getValues = values

   private def readValues(): ListBuffer[JsonNode] = {
      val nodes: ListBuffer[JsonNode] = new ListBuffer[JsonNode]
      nodes ++= rawValues.map(JsonNode.convertToNode(this, _, identifier, dom))
      nodes
   }

   override def getIdentifier: Identifier = identifier
}
