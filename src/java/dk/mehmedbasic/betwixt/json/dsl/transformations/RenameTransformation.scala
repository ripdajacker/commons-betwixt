package dk.mehmedbasic.betwixt.json.dsl.transformations

import java.util

/**
 * A simple rename transformation.
 */
class RenameTransformation(key: String, newKeyName: String, parent: Transformation) extends Transformation(parent) {
   override def apply(source: util.Map[String, Any]): Unit = {
      // Remove the old key and put it in the map.
      val oldValue = source.remove(key)
      source.put(newKeyName, oldValue)
   }


   override def toString = "Rename(%s -> %s)".format(key, newKeyName)
}
