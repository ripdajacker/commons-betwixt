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
}
