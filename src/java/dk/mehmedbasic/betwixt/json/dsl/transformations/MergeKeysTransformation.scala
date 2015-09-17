package dk.mehmedbasic.betwixt.json.dsl.transformations

import java.util

/**
 * A transformation that merges the first key into the second.
 */
class MergeKeysTransformation[V1, V2](sourceKey: String,
                                      destinationKey: String,
                                      mergeFunction: (V1, V2) => V2,
                                      parent: Transformation) extends Transformation(parent) {


   override def apply(source: util.Map[String, Any]): Unit = {
      // The destination is null, this is a simple rename.
      if (source.get(destinationKey) == null) {
         val rename: RenameTransformation = new RenameTransformation(sourceKey, destinationKey, null)
         rename.apply(source)
         return
      }

      val sourceValue: Any = source.remove(sourceKey)
      if (sourceValue == null) {
         // Nothing to do
         return
      }

      val destinationValue: Any = source.remove(destinationKey)
      val newValue = destinationValue match {
         case v: util.Map[String, Any] => mergeObjects(sourceValue, v)
         case v: Array[Any] => mergeIntoArray(sourceValue, v)
         case _ =>
            // A merge function is needed, since the destination is a primitive.
            if (mergeFunction == null) {
               throw new IllegalArgumentException("Trying to merge two values without a specified merge function.")
            }
            mergeFunction.apply(sourceValue.asInstanceOf[V1], destinationValue.asInstanceOf[V2])
      }
      source.put(destinationKey, newValue)
   }


   /**
    * Merges two objects into one.
    *
    * @param source      the source to merge.
    * @param destination the destination to merge into
    *
    * @return the newly created object.
    */
   def mergeObjects(source: Any, destination: util.Map[String, Any]): util.Map[String, Any] = {
      val result: util.LinkedHashMap[String, Any] = new util.LinkedHashMap[String, Any]()
      result.putAll(destination)

      source match {
         case v: util.Map[String, Any] => result.putAll(v)
         case v: Any => result.put(sourceKey, v)
      }

      result
   }

   /**
    * Merges a source into a destination array.
    *
    * @param source      the source to merge.
    * @param destination the destination array.
    *
    * @return a new array containing the merged values.
    */
   private def mergeIntoArray(source: Any, destination: Array[Any]): Array[Any] = {
      val buffer = destination.toBuffer

      source match {
         case v: Array[Any] => buffer ++= v
         case v: Any => buffer += v
      }

      buffer.toArray
   }
}
