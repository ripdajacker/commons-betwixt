package dk.mehmedbasic.betwixt

import com.google.gson.stream.JsonWriter
import groovy.transform.TypeChecked
import org.apache.commons.betwixt.BindingConfiguration
import org.apache.commons.betwixt.XMLBeanInfo
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.betwixt.expression.Context
import org.apache.commons.betwixt.io.IDGenerator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * A class that writes betwixt-described object trees as JSON instead of XML.
 */
@TypeChecked
class BetwixtJsonWriter {
    private Log log = LogFactory.getLog(BetwixtJsonWriter)

    XMLIntrospector introspector = new XMLIntrospector()
    BindingConfiguration binding = new BindingConfiguration()

    private IDGenerator idGenerator = new HexIdGenerator()

    private JsonWriter json

    BetwixtJsonWriter(Writer output) {
        json = new JsonWriter(output)
    }

    XMLBeanInfo introspect(Class clazz) {
        introspector.introspect(clazz)
    }

    /**
     * Writes the given object tree.
     *
     * @param root the root of the tree.
     */
    void write(Object root) {
        json.beginObject()
        writeObject(root, "", false)
        json.endObject()
    }

    /**
     * Writes an object in the tree. This method is recursive.
     *
     * @param bean the bean.
     * @param name the name of the bean in the context.
     * @param collection whether or not the bean is in a collection.
     */
    private void writeObject(Object bean, String name, boolean collection) {
        XMLBeanInfo info = introspector.introspectOrGet(bean.getClass())

        def id = binding.idMappingStrategy.getReferenceFor(null, bean)
        boolean idRef = id != null
        if (!id) {
            id = idGenerator.nextId()
        }

        def descriptor = new BetwixtJsonDescriptor(info.getElementDescriptor())

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
        def canBeInlined = objectStringConverter.canHandle(descriptor.propertyType)
        def context = createContext(bean)

        String inlinedString = null
        if (canBeInlined) {
            inlinedString = objectStringConverter.objectToString(bean, descriptor.propertyType, context)
        }

        boolean inlined = false

        if (!collection) {
            json.name(createName(descriptor, name, id))

            if (canBeInlined) {
                if (!inlinedString) {
                    inlinedString = ""
                }
                inlined = true
                json.value(inlinedString)
            } else {
                json.beginObject()
            }
        } else {
            if (canBeInlined && inlinedString) {
                // This is only true if the current descriptor is a leaf.
                json.value("@id:#${id} $inlinedString")
                inlined = true
            } else {
                json.beginObject()
                json.name("@tag")
                json.value("${descriptor.name} #$id")
            }
        }
        if (!canBeInlined) {
            for (BetwixtJsonDescriptor child : descriptor.children) {
                if (child.isCollection()) {
                    Iterator iterator = child.evaluateAsCollection(context) as Iterator
                    if (iterator.hasNext()) {
                        json.name(child.name)
                        json.beginArray()

                        while (iterator.hasNext()) {
                            Object childBean = iterator.next();
                            if (childBean) {
                                writeObject(childBean, "", true)
                            }
                        }

                        json.endArray()
                    }
                } else {
                    if (child.expression) {
                        def evaluated = child.evaluateThis(context)
                        if (evaluated) {
                            if (child.isPrimitive()) {
                                writePrimitive(child.name, child.propertyType, evaluated)
                            } else {
                                writeObject(evaluated, child.name, false)
                            }

                        }
                    }
                }
            }
        }

        if (!inlined) {
            json.endObject()
        }

    }

    /**
     * Writes a primitive (or its Object equivalent) value to the json stream.
     *
     * @param name the name of the value.
     * @param propertyType the type of the property.
     * @param value the value.
     */
    private void writePrimitive(String name, Class<?> propertyType, Object value) {
        if (propertyType in [Boolean, boolean]) {
            json.name(name)
            json.value(value as boolean)
        } else if (propertyType in [Integer, int]) {
            json.name(name)
            json.value(value as int)
        } else if (propertyType in [Double, double]) {
            def doubleValue = value as double
            if (!doubleValue.naN) {
                json.name(name)
                json.value(doubleValue)
            }
        } else if (propertyType in [Float, float]) {
            def floatValue = value as float
            if (!floatValue.naN) {
                json.name(name)
                json.value(floatValue)
            }
        } else if (propertyType in [Long, long]) {
            json.name(name)
            json.value(value as long)
        } else if (propertyType == String) {
            json.name(name)
            json.value(value as String)
        }
    }

    void close() {
        json.close()
    }

    private Context createContext(bean) {
        new Context(bean, log, binding)
    }

    /**
     * Creates a JSON name from the given descriptor, the name of it in the context and the ID of the bean.
     *
     * @param descriptor the descriptor.
     * @param name the name of the bean in the given context.
     * @param id the ID of the bean.
     *
     * @return the generated name.
     */
    private static String createName(BetwixtJsonDescriptor descriptor, String name, String id) {
        if (name.isEmpty()) {
            name = descriptor.name
        }
        return "${name} #${id}"

    }

    private static String nameWithoutId(String name, BetwixtJsonDescriptor descriptor) {
        if (!name || name.trim().isEmpty()) {
            return descriptor.name
        } else {
            return name
        }

    }

}
