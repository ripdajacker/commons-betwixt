package dk.mehmedbasic.betwixt.json.ast

import scala.collection.mutable

/**
 * A json dom.
 */
class JsonDom {
   private val tags = new mutable.HashMap[String, JsonNode]()
   private val ids = new mutable.HashMap[String, JsonNode]()
   private val properties = new mutable.HashMap[String, JsonNode]()

   def registerNode(node: JsonNode): Unit = {
      val identifier: Identifier = node.getIdentifier
      if (identifier.id != null) {
         ids += identifier.id -> node
      }
      if (identifier.tag != null) {
         tags += identifier.tag -> node
      }
      if (identifier.propertyName != null) {
         properties += identifier.propertyName -> node
      }
   }
}
