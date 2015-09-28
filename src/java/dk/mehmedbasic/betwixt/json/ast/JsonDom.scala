package dk.mehmedbasic.betwixt.json.ast

import scala.collection.mutable

/**
 * A json dom.
 */
class JsonDom {
   private val tags = new mutable.HashMap[String, mutable.Set[JsonNode]]()
   private val ids = new mutable.HashMap[String, mutable.Set[JsonNode]]()
   private val properties = new mutable.HashMap[String, mutable.Set[JsonNode]]()

   private var root: JsonNode = null

   private[ast] def setRoot(root: JsonNode) = this.root = root

   def registerNode(node: JsonNode): Unit = {
      val identifier: Identifier = node.getIdentifier
      if (identifier.id != null) {
         ids.getOrElseUpdate(identifier.id, mutable.Set.empty) += node
      }
      if (identifier.tag != null) {
         tags.getOrElseUpdate(identifier.tag, mutable.Set.empty) += node
      }
      if (identifier.propertyName != null) {
         if (!node.isInCollection) {
            properties.getOrElseUpdate(identifier.propertyName, mutable.Set.empty) += node
         }
      }
   }

   private def ensureSet(key: String, map: mutable.HashMap[String, mutable.Set[JsonNode]]): Unit = {
      map.get(key) match {
         case None => map += key -> new mutable.LinkedHashSet[JsonNode]
         case _ =>
      }
   }

   def selectByProperty(name: String) = properties.getOrElseUpdate(name, mutable.Set.empty).toList

   def selectByTag(name: String) = tags.getOrElseUpdate(name, mutable.Set.empty).toList
}
