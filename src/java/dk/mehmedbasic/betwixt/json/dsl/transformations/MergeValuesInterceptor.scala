package dk.mehmedbasic.betwixt.json.dsl.transformations

import dk.mehmedbasic.betwixt.json.ast.{JsonArray, JsonObject, JsonValue}

/**
 * A callback for handling conflicts in merge value transformations.
 *
 * The return value overrides the value being merged.
 */
abstract class MergeValuesInterceptor {
   def onMerge(key: String, source: JsonObject, destination: JsonObject): JsonObject = null

   def onMerge(key: String, source: JsonObject, destination: JsonArray): JsonArray = null

   def onMerge(key: String, source: JsonObject, destination: JsonValue[_]): JsonValue[_] = null

   def onMerge(key: String, source: JsonArray, destination: JsonObject): JsonObject = null

   def onMerge(key: String, source: JsonArray, destination: JsonArray): JsonArray = null

   def onMerge(key: String, source: JsonArray, destination: JsonValue[_]): JsonValue[_] = null

   def onMerge(key: String, source: JsonValue[_], destination: JsonObject): JsonObject = null

   def onMerge(key: String, source: JsonValue[_], destination: JsonArray): JsonArray = null

   def onMerge(key: String, source: JsonValue[_], destination: JsonValue[_]): JsonValue[_] = null
}
