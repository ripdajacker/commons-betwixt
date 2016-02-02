package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.io.BeanWriteEventListener
import org.apache.commons.betwixt.io.MutableWriteContext
import org.apache.commons.betwixt.io.WriteContext
import org.xml.sax.Attributes

/**
 * A JSON writer implemented as a betwixt write event listener.
 *
 * The gist of the class is that it follows the SAX-like nature of {@link org.apache.commons.betwixt.io.AbstractBeanWriter}.
 * Instead of writing XML elements it writes the JSON equivalent.
 */
@TypeChecked
class JsonBeanWriteEventListener implements BeanWriteEventListener {
    private JsonWriterStrategy json
    LinkedList<State> states = []

    /**
     * Constructs a listener that outputs JSON to the given writer.
     *
     * @param out the destination writer.
     */
    JsonBeanWriteEventListener(Writer out) {
        json = new GsonStrategy(out)
    }

    @Override
    void start() {
        objectStart()
    }

    private void objectStart() {
        json.beginObject()
        pushState(false)
    }

    private void arrayStart() {
        json.beginArray()
        pushState(true)
    }

    @Override
    void startElement(MutableWriteContext writeContext, String qualifiedName, Attributes attributes) {
        if (!peekState().collection) {
            // Only write field names within JSON object, not within arrays.
            json.name(asJsonName(attributes, qualifiedName))
        }
        if (writeContext.currentDescriptor.contentIterable) {
            arrayStart()
        } else {
            objectStart()
            def idref = attributes.getValue("idref")
            if (idref != null) {
                // Idrefs are written with @ref
                json.name("@ref")
                json.valueGeneric(idref)
            }

            if (peekState().collection) {
                // Inside collections @tag and @id are needed, since the value has no field name.
                json.name("@tag")
                json.valueGeneric(qualifiedName)

                if (idref != null) {
                    def id = attributes.getValue("id")
                    if (id != null) {
                        json.name("@id")
                        json.valueGeneric(id)
                    }
                }
            }

            if (idref == null) {
                for (Tuple2<String, String> tuple2 : convert(attributes)) {
                    if (tuple2.getSecond() != null) {
                        // Write all attributes, ignore null values.
                        json.name(tuple2.getFirst())
                        json.valueGeneric(tuple2.getSecond())
                    }
                }
            }
        }
    }


    @Override
    void bodyText(WriteContext context, String text) {
        json.name("@body")
        json.valueGeneric(text)
    }

    @Override
    void endElement(MutableWriteContext writeContext, String qualifiedName) {
        if (peekState().collection) {
            json.endArray()
        } else {
            json.endObject()
        }
        popState()
    }

    @Override
    void end() {
        json.endObject()
        json.close()
    }

    /**
     * Converts a name and an attributes object into a JSON shorthand name on the form: name #id.
     *
     * If the element already exists, the name gets a suffix of [size]
     *
     * @param attributes the attributes.
     * @param name the name
     *
     * @return the new and improved, JSON-approved field name.
     */
    private String asJsonName(Attributes attributes, String name) {
        if (name == null) {
            return "@null"
        }
        String id = attributes.getValue("id")

        if (id != null) {
            return "$name #$id"
        }

        List<String> names = peekState().propertyNames
        if (names.contains(name)) {
            name = "$name[${names.size()}]"
        }
        names.add(name)
        return name
    }

    private void pushState(boolean collection) {
        states.addFirst(new State(collection))
    }

    private void popState() {
        states.removeFirst()
    }

    private State peekState() {
        states.peek()
    }

    /**
     * Converts an attributes object into a list of tuple2 of string.
     *
     * @param attributes the source to convert.
     *
     * @return the resulting list, never null.
     */
    private static List<Tuple2<String, String>> convert(Attributes attributes) {
        List<Tuple2<String, String>> values = []
        for (int i = 0; i < attributes.length; i++) {
            String name = attributes.getQName(i)
            if (!["id", "idref"].contains(name)) {
                values << new Tuple2<String, String>(name, attributes.getValue(i))
            }
        }
        return values
    }

    static final class State {
        List<String> propertyNames = []
        boolean collection

        State(boolean collection) {
            this.collection = collection
        }
    }

}
