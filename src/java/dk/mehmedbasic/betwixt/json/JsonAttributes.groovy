package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.io.EmptyAttributes
import org.codehaus.jackson.node.ObjectNode

/**
 * Attributes wrapper for JSON reading.
 */
@TypeChecked
class JsonAttributes extends EmptyAttributes {
    final static List<String> SYSTEM_NAMES = ["@tag", "@id", "@ref", "@body"]

    JsonAttributes(JsonIdentifier identifier, ObjectNode node) {
        if (identifier.tagName != null) {
            node.put("@tag", identifier.tagName)
        }
        if (identifier.idRef != null) {
            node.put("@ref", identifier.idRef)
        }
        if (identifier.idName != null) {
            node.put("@id", identifier.idName)
        }
        for (String childName : node.fieldNames) {
            if (SYSTEM_NAMES.contains(childName)) {
                if (!childName.equals("@body")) {
                    def attributeName = childName.replace("@id", "id").replace("@ref", "idref")

                    def text = node.get(childName).valueAsText
                    if (text != null) {
                        addValue(attributeName, text)
                    }
                }
            } else {
                if (node.get(childName).valueNode) {
                    def text = node.get(childName).valueAsText
                    if (text != null) {
                        addValue(childName, text)
                    }
                }
            }
        }
    }


}
