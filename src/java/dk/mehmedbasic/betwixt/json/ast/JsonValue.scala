package dk.mehmedbasic.betwixt.json.ast

/**
 * A JsonValue that is not an object
 */
class JsonValue[Value](parent: JsonNode,
                       identifier: Identifier,
                       value: Value,
                       dom: JsonDom) extends JsonNode(parent, dom) {
   def getValue: Value = value

   override def getIdentifier: Identifier = identifier

   override def getNodeType: JsonNodeType = value match {
      case d: Double => JsonNodeType.NUMBER
      case s: String => JsonNodeType.STRING
      case b: Boolean => JsonNodeType.BOOLEAN
      case _ => JsonNodeType.NULL
   }

   override def apply(name: String): Option[JsonNode] = None

   override def apply(index: Int): Option[JsonNode] = None
}
