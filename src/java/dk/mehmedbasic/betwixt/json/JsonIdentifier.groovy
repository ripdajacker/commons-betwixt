package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked
import org.codehaus.jackson.node.ObjectNode

import java.util.regex.Pattern

/**
 * A JSON identifier
 */
@TypeChecked
class JsonIdentifier {
    private static Pattern PATTERN_NAME_AND_ID = Pattern.compile(/([A-Za-z0-9]+) #(\-?[0-9a-f]+)\s*$/)

    String tagName
    String idName
    String idRef

    JsonIdentifier(ObjectNode inlineObject) {
        this.tagName = safeValue(inlineObject, "@tag")
        this.idName = safeValue(inlineObject, "@id")
        this.idRef = safeValue(inlineObject, "@ref")
    }

    JsonIdentifier(String fieldName) {
        fieldName = fieldName.replaceAll(/\[[0-9]+\]/, "").trim()
        def matcher = PATTERN_NAME_AND_ID.matcher(fieldName)
        if (matcher.find()) {
            tagName = matcher.group(1)
            idName = matcher.group(2)
        } else {
            tagName = fieldName
        }
    }

    private static String safeValue(ObjectNode inlineObject, String name) {
        inlineObject.get(name) == null ? null : inlineObject.get(name).textValue
    }
}
