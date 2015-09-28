package dk.mehmedbasic.betwixt.json.ast

/**
 * A Node identifier.
 *
 * @param tag          the tag
 * @param id           the id of the nde
 * @param propertyName the name of the property
 */
case class Identifier(tag: String, id: String, propertyName: String) {
   def withPropertyName(newPropertyName: String) = Identifier(tag, id, newPropertyName)
}

object Identifier {
   def fromMetaTag(nameAndId: String): Identifier = {
      if (nameAndId.contains("#")) {
         val split: Array[String] = nameAndId.split("#")

         val tag: String = split(0).trim()
         val id: String = split(1).trim()

         Identifier(tag, id, null)
      } else {
         Identifier(nameAndId, null, null)
      }
   }

   def fromPropertyName(nameAndId: String): Identifier = {
      if (nameAndId.contains("#")) {
         val split: Array[String] = nameAndId.split("#")

         val name: String = split(0).trim()
         val id: String = split(1).trim()

         Identifier(null, id, name)
      } else {
         Identifier(null, null, nameAndId)
      }
   }

   def inArray(): Identifier = Identifier(null, null, null)

   def nameOnly(name: String) = Identifier(null, null, name)

   def stripId(input: String): String = {
      val end = input.indexOf("#")
      if (end == -1) {
         return input
      }
      input.substring(0, end).trim
   }
}
