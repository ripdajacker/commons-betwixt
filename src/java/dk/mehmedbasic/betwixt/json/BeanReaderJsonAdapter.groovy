package dk.mehmedbasic.betwixt.json

import com.fasterxml.jackson.core.JsonFactory
import groovy.util.logging.Log
import org.apache.commons.betwixt.io.BeanReader
import org.apache.commons.betwixt.io.EmptyAttributes
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.ObjectNode

/**
 * Adapts a JSON stream so it can be read by a {@link BeanReader}
 */
@Log(value = "logger")
class BeanReaderJsonAdapter {
    BeanReader wrapped
    JsonFactory jsonFactory

    /**
     * Constructs an adapter.
     *
     * @param wrapped The bean reader to user
     */
    BeanReaderJsonAdapter(BeanReader wrapped) {
        this.wrapped = wrapped
        this.jsonFactory = new JsonFactory()
    }

    /**
     * Parses the JSON read from the given reader.
     *
     * @param reader the reader to read from.
     *
     * @return the resulting object.
     */
    Object parse(Reader reader) {
        wrapped.startDocument()
        def root = new ObjectMapper().readTree(reader)

        String name = root.fieldNames.next()
        parseJson(name, root.get(name))

        wrapped.endDocument()
        return wrapped.getRoot()
    }

    /**
     * This method parses the JSON AST.
     *
     * @param name
     * @param thisNode
     */
    private void parseJson(String name, JsonNode thisNode) {
        if (thisNode.object) {
            // This is a complex node and not a collection.
            JsonIdentifier thisIdentifier = createIdentifier(name, thisNode)

            def attributes = new JsonAttributes(thisIdentifier, thisNode as ObjectNode)
            attributes.clean()

            def filter = {
                !JsonAttributes.SYSTEM_NAMES.contains(it) && !attributes.names.contains(it)
            }

            // Null names are written as '@null' in the writer.
            if (name != "@null") {
                wrapped.startElement(null, null, thisIdentifier.tagName, attributes)
            }

            // Body text is encoded with a property called '@body'
            if (thisNode.get("@body") != null) {
                def text = thisNode.get("@body").valueAsText
                def chars = text.chars
                wrapped.characters(chars, 0, chars.length)
            }

            // Recursively parse all fields that are not reserved names and not XML attributes
            for (String childName : thisNode.fieldNames.findAll(filter)) {
                parseJson(childName, thisNode.get(childName))
            }

            if (name != "@null") {
                wrapped.endElement(null, null, thisIdentifier.tagName)
            }
        } else {
            if (name != null) {
                JsonIdentifier thisIdentifier = new JsonIdentifier(name)
                wrapped.startElement(null, null, thisIdentifier.tagName, new EmptyAttributes())

                int size = thisNode.size()
                // We are inside a JSON array, iterate through the elements and parse them recursively
                for (int i = 0; i < size; i++) {
                    parseJson(null, thisNode.get(i))
                }
                wrapped.endElement(null, null, thisIdentifier.tagName)
            }

        }
    }

    private static JsonIdentifier createIdentifier(String name, JsonNode thisNode) {
        JsonIdentifier thisIdentifier
        if (name != null) {
            // No name means we are currently inside a collection.
            thisIdentifier = new JsonIdentifier(name)
        } else {
            // This is an ordinary property
            thisIdentifier = new JsonIdentifier(thisNode as ObjectNode)
        }
        thisIdentifier
    }


}
