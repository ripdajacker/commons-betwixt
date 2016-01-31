package dk.mehmedbasic.betwixt.json

import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.apache.commons.betwixt.io.BeanWriteEventListener
import org.apache.commons.betwixt.io.MutableWriteContext
import org.apache.commons.betwixt.io.WriteContext
import org.xml.sax.Attributes

/**
 * TODO - someone remind me to document this class 
 */
@PackageScope
@TypeChecked
class JsonBeanWriteEventListener implements BeanWriteEventListener {
    StreamingJsonWriter json
    LinkedList<Boolean> state = []
    LinkedList<List<String>> propertyNames = []

    JsonBeanWriteEventListener(Writer out) {
        json = new StreamingJsonWriter(new GsonStrategy(out))
    }

    @Override
    void start() {
        objectStart()
    }

    private void objectStart() {
        json.beginObject()
        propertyNames.addFirst([])
        state.addFirst(false)
    }

    private void arrayStart() {
        json.beginArray()
        propertyNames.addFirst([])
        state.addFirst(true)
    }

    @Override
    void startElement(MutableWriteContext writeContext, String qualifiedName, Attributes attributes) {
        if (!state.peek()) {
            json.name(asJsonName(attributes, qualifiedName))
        }
        if (writeContext.currentDescriptor.contentIterable) {
            arrayStart()
        } else {
            objectStart()
            def idref = attributes.getValue("idref")
            if (idref != null) {
                json.name("@ref")
                json.valueGeneric(idref)
            }

            if (state.peek()) {
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
                for (Tuple2<String, String> tuple2 : new IterableAttributes(attributes)) {
                    if (tuple2.getSecond() != null) {
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
        if (state.peek()) {
            json.endArray()
        } else {
            json.endObject()
        }
        elementEnd()
    }

    private boolean elementEnd() {
        state.removeFirst()
        propertyNames.removeFirst()
    }

    @Override
    void end() {
        json.endObject()
        json.close()
    }

    public String asJsonName(Attributes attributes, String name) {
        if (name == null) {
            return "@null"
        }
        String id = attributes.getValue("id")

        if (id != null) {
            return "$name #$id"
        }

        List<String> peek = propertyNames.peek()
        if (peek.contains(name)) {
            name = "$name[${peek.size()}]"
        }
        peek.add(name)
        return name
    }

    static final class IterableAttributes implements Iterable<Tuple2<String, String>> {
        List<Tuple2<String, String>> values = []

        IterableAttributes(Attributes attributes) {
            for (int i = 0; i < attributes.length; i++) {
                String name = attributes.getQName(i)
                if (!["id", "idref"].contains(name)) {
                    values << new Tuple2<String, String>(name, attributes.getValue(i))
                }
            }
        }

        @Override
        Iterator<Tuple2<String, String>> iterator() {
            return values.iterator()
        }
    }
}
