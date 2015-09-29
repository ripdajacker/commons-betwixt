package dk.mehmedbasic.betwixt.json.dsl.transformations

import java.util

/**
 * A manipulation of values.
 */
class ValueManipulation[Source, Destination](key: String, parent: Transformation, transformer: (Source) => Destination)
   extends Transformation(parent) {

   override def apply(source: util.Map[String, Any]): Unit = {
      val previous = source.get(key)
      if (previous == null) {
         return
      }
      val value = previous match {
         case s: Source => transformer.apply(s)
         case t: Any => t
      }
      source.put(key, value)
   }
}
