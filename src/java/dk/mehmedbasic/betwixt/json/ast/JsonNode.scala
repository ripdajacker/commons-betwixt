package dk.mehmedbasic.betwixt.json.ast

/**
 * The JSON node abstract class.
 *
 * @param parent the parent in which this node is defined.
 */
abstract class JsonNode(parent: JsonNode, dom: JsonDom) {

   def getIdentifier: Identifier

}

object JsonNode {
   def convertToNode(parent: JsonNode,
                     source: Any,
                     identifier: Identifier,
                     dom: JsonDom): JsonNode = {
      val result = source match {
         case obj: java.util.Map[String, Any] =>
            val tag = obj.remove("@tag")
            val newIdentifier = if (tag == null) {
               identifier
            } else {
               val condensedName = tag.asInstanceOf[String]
               if (condensedName.contains("#")) {
                  Identifier.fromMetaTag(condensedName)
               } else {
                  Identifier.nameOnly(condensedName)
               }
            }

            new JsonObject(parent, newIdentifier, obj, dom)

         case array: java.util.List[Any] => new JsonArray(parent, identifier, array, dom)
         case value: Any => new JsonValue(parent, identifier, value, dom)
      }
      dom.registerNode(result)
      result
   }


}
