package dk.mehmedbasic.betwixt.json.dsl

import dk.mehmedbasic.betwixt.json.dsl.transformations.{MergeKeysTransformation, RenameTransformation, Transformation}

import scala.collection.mutable.ListBuffer

/**
 * A small builder DSL for chaining transformations.
 */
class TransformerBuilder(selector: String, transformation: Transformation) {
   def this(selector: String) = this(selector, null)

   def renameKey(pair: (String, String)) = {
      val rename: RenameTransformation = new RenameTransformation(pair._1, pair._2, transformation)
      new TransformerBuilder(selector, rename)
   }

   def mergeKeys[V1, V2](pair: (String, String)): TransformerBuilder = mergeKeys(pair, null)

   def mergeKeys[V1, V2](pair: (String, String), function: (V1, V2) => V2): TransformerBuilder = {
      val merge = new MergeKeysTransformation[V1, V2](pair._1, pair._2, function, transformation)
      new TransformerBuilder(selector, merge)
   }

   /**
    * Creates a list of transformations in the order they were created.
    *
    * @return the list of all transformations.
    */
   def transformations(): List[Transformation] = {
      val buffer = new ListBuffer[Transformation]()

      var current = Option(transformation)
      while (current.isDefined) {
         buffer += current.get
         current = current.get.parentOption
      }
      buffer.reverse.toList
   }
}

/**
 * A small collection of implicit conversion to ease the creation of transformations.
 */
sealed trait TransformationImplicits {
   implicit def stringToBuilder(selector: String): TransformerBuilder = new TransformerBuilder(selector)
}

/*
object TestShit extends TransformationImplicits {
   val source =
      """|{
        |    "person": {
        |        "father": {
        |            "firstName": "John",
        |            "lastName": "Hancock",
        |            "birthday": "1956"
        |        },
        |        "cat": {
        |            "name": "Jenny The Cat",
        |            "age": "6"
        |        }
        |    }
        |}""".stripMargin

   def main(args: Array[String]) {
      def map: java.util.Map[String, Any] = new Gson().fromJson(source, classOf[java.util.Map[String, Any]])

      val foo = ".person"
                .renameKey("father" -> "john")
                .renameKey("john" -> "hej")
                .mergeKeys("cat" -> "hej")
                .transformations()

      val personSubtree = map
                          .get("house").asInstanceOf[java.util.Map[String, Any]]
                          .get("person").asInstanceOf[java.util.Map[String, Any]]

      foo.foreach(_.apply(personSubtree))

      println()
   }
}
*/
