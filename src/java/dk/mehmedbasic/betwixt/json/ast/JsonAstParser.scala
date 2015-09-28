package dk.mehmedbasic.betwixt.json.ast

import com.google.gson.Gson

/**
 * A parser for the JsonAst.
 */
object JsonAstParser {


   def readString(jsonSource: String): JsonDom = {
      val resultingMap = new Gson().fromJson(jsonSource, classOf[java.util.Map[String, Any]])

      val dom = new JsonDom
      val rootNod = JsonNode.convertToNode(null, resultingMap, Identifier.inArray(), dom)
      dom.setRoot(rootNod)
      dom
   }
}
