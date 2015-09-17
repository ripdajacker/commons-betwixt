package dk.mehmedbasic.betwixt.json.ast

import com.google.gson.Gson

/**
 * A parser for the JsonAst.
 */
object JsonAstParser {


   def readString(jsonSource: String): JsonNode = {
      val resultingMap = new Gson().fromJson(jsonSource, classOf[java.util.Map[String, Any]])
      val dom = new JsonDom

      val node = JsonNode.convertToNode(null, resultingMap, Identifier.inArray(), dom)
      node
   }


   val source =
      """|{
        |    "person #54": {
        |        "father #10": {
        |            "firstName": "John",
        |            "lastName": "Hancock",
        |            "birthday": 1956,
        |            "phones": [ { "@tag": "shit #42", "brand": "Huawei", "model": "P8 Lite"}]
        |        },
        |        "cat #1": {
        |            "name": "Jenny The Cat",
        |            "age": 6
        |        }
        |    }
        |}""".stripMargin


   def main(args: Array[String]) {
      val node = readString(source)
      println("")
   }
}
