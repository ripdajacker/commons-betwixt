package dk.mehmedbasic.betwixt.json.dsl.transformations

import java.util

/**
 * A single, abstract transformation.
 */
abstract class Transformation(parent: Transformation) {
   def parentOption: Option[Transformation] = Option(parent)

   def apply(source: util.Map[String, Any])
}
