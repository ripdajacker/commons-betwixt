package dk.mehmedbasic.betwixt.json

import com.fasterxml.jackson.core.JsonFactory
import groovy.util.logging.Log
import org.apache.commons.betwixt.io.BeanReader
import org.apache.commons.betwixt.io.EmptyAttributes
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.ObjectNode

/**
 * TODO - someone remind me to document this class 
 */
@Log(value = "logger")
class JsonBeanReadListener {

    BeanReader wrapped
    JsonFactory jsonFactory


    JsonBeanReadListener(BeanReader wrapped) {
        this.wrapped = wrapped
        this.jsonFactory = new JsonFactory()
    }

    Object parse(Reader reader) {

        wrapped.startDocument()
        def root = new ObjectMapper().readTree(reader)


        String name = root.fieldNames.next()
        parseJson(name, root.get(name))

        wrapped.endDocument()
        return wrapped.getRoot()
    }

    void parseJson(String name, JsonNode thisNode) {


        if (thisNode.object) {

            JsonIdentifier thisIdentifier
            if (name != null) {
                thisIdentifier = new JsonIdentifier(name)
            } else {
                thisIdentifier = new JsonIdentifier(thisNode as ObjectNode)
            }

            def attributes = new JsonAttributes(thisIdentifier, thisNode as ObjectNode)
            attributes.clean()

            def filter = {
                !JsonAttributes.SYSTEM_NAMES.contains(it) && !attributes.names.contains(it)
            }
            logger.finest("Starting element ${thisIdentifier.tagName}")
            if (name != "@null") {

                wrapped.startElement(null, null, thisIdentifier.tagName, attributes)
            }

            if (thisNode.get("@body") != null) {
                def text = thisNode.get("@body").valueAsText
                def chars = text.chars
                wrapped.characters(chars, 0, chars.length)
            }

            for (String childName : thisNode.fieldNames.findAll(filter)) {
                parseJson(childName, thisNode.get(childName))
            }

            logger.finest("Ending element ${thisIdentifier.tagName}")
            if (name != "@null") {
                wrapped.endElement(null, null, thisIdentifier.tagName)
            }
        } else {

            if (name != null) {
                JsonIdentifier thisIdentifier = new JsonIdentifier(name)
                wrapped.startElement(null, null, thisIdentifier.tagName, new EmptyAttributes())

                int size = thisNode.size()
                for (int i = 0; i < size; i++) {
                    parseJson(null, thisNode.get(i))
                }

                wrapped.endElement(null, null, thisIdentifier.tagName)

            }

        }
    }


}
