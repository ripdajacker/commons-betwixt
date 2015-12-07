package dk.mehmedbasic.betwixt

import com.google.gson.stream.JsonWriter
import groovy.transform.TypeChecked
import org.apache.commons.betwixt.BindingConfiguration
import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.XMLBeanInfo
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.betwixt.io.IDGenerator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class BetwixtJsonWriter {
    private XMLIntrospector introspector = new XMLIntrospector()
    private Log log = LogFactory.getLog(BetwixtJsonWriter)
    private BindingConfiguration binding = new BindingConfiguration()

    private IDGenerator idGenerator = new HexIdGenerator()

    private JsonWriter json

    XMLBeanInfo introspect(Class clazz) {
        introspector.introspect(clazz)
    }

    void write(Object object) {
        json.beginObject()
        writeInner(object, "", false)
        json.endObject()
    }

    void writeInner(Object bean, String name, boolean collection) {
        XMLBeanInfo info = introspector.introspectOrGet(bean.getClass())

        def id = binding.idMappingStrategy.getReferenceFor(null, bean)
        boolean idRef = id != null
        if (!id) {
            id = idGenerator.nextId()
        }

        def descriptor = info.getElementDescriptor()

        // If it's a reference to a already written object, write the @ref file
        if (idRef) {
            if (!collection) {
                json.name(nameWithoutId(name, descriptor))
            }
            json.value("@ref:$id")
            return
        }

        // It's not an idref, set the id for the current bean.
        binding.idMappingStrategy.setReference(null, bean, id)

        def objectStringConverter = binding.objectStringConverter

    }

    private static String nameWithoutId(String name, ElementDescriptor descriptor) {
        if (!name || name.trim().isEmpty()) {
            return descriptor.getLocalName()
        } else {
            return name
        }
    }

    private static String notNull(String... strings) {
        return strings.find { it != null }
    }
}
