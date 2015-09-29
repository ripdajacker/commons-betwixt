package dk.mehmedbasic.betwixt.json.dsl

import dk.mehmedbasic.betwixt.json.dsl.transformations.{MergeKeysTransformation, RenameTransformation, Transformation, ValueManipulation}

import scala.collection.mutable.ListBuffer

/**
 * A small builder DSL for chaining transformations.
 */
class TransformerBuilder(selector: String, transformation: Transformation, parent: TransformerBuilder = null)
   extends TransformationImplicits {

   def this(selector: String) = this(selector, null)

   def renameKey(pair: (String, String)) = {
      val rename: RenameTransformation = new RenameTransformation(pair._1, pair._2, transformation)
      new TransformerBuilder(selector, rename)
   }

   def doSomething(parameters: TransformationParameters) = this

   def mergeKeys(parameters: TransformationParameters): TransformerBuilder = {
      val nextTransformation = new MergeKeysTransformation[_, _](parameters.keys._1, parameters.keys._2, null, transformation)
      if (parameters.switchesContext()) {
         return new TransformerBuilder(parameters.selector, nextTransformation, this)
      }

      new TransformerBuilder(selector, nextTransformation)
   }

   def mergeKeys[V1, V2](pair: (String, String, String), function: (V1, V2) => V2): TransformerBuilder = {
      val merge = new MergeKeysTransformation[V1, V2](pair._1, pair._2, function, transformation)
      new TransformerBuilder(selector, merge)
   }

   def manipulateValue[S, D](key: String, function: (S) => D): TransformerBuilder = {
      val manipulation: ValueManipulation[S, D] = new ValueManipulation[S, D](key, transformation, function)
      new TransformerBuilder(selector, manipulation)
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

   private implicit def tupleToParameters(keys: (String, String)): TransformationParameters = TransformationParameters(null, keys)
}

case class TransformationParameters(selector: String, keys: (String, String), direction: Direction = Up) {
   def upward = TransformationParameters(selector, keys, Up)

   def downward = TransformationParameters(selector, keys, Down)

   def switchesContext() = selector != null
}


/**
 * A small collection of implicit conversion to ease the creation of transformations.
 */
trait TransformationImplicits {

   implicit class TupleExtensions(input: (String, String)) {
      def ++(appended: String) = (input._1, input._2, appended)

      def in(selector: String) = new TransformationParameters(selector, input, null)
   }

}

object TransformerBuilder {
   def apply(selector: String): TransformerBuilder = new TransformerBuilder(selector)
}
