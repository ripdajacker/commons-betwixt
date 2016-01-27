package dk.mehmedbasic.betwixt

import com.google.gson.stream.JsonWriter
import groovy.transform.TypeChecked
import org.apache.commons.betwixt.BindingConfiguration
import org.apache.commons.betwixt.XMLBeanInfo
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.betwixt.expression.Context
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
    Stack<JsonWriteOptions> optionStack = []

    private JsonWriterNameGenerator nameUtil = new JsonWriterNameGenerator()

    private JsonWriter json

    BetwixtJsonWriter(Writer output) {
        json = new JsonWriter(new BufferedWriter(output))
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
        optionStack.push(new JsonWriteOptions())
        json.beginObject()
        writeObject(root, createContext(root), "")
        json.endObject()
    }

    private JsonWriteOptions writeOptions() {
        return optionStack.peek()
    }

    /**
     * Writes an object in the tree. This method is recursive.
     *
     * @param bean the bean.
     * @param name the name of the bean in the context.
     * @param collection whether or not the bean is in a collection.
     */
    private void writeObject(Object bean, Context context, String name) {
        XMLBeanInfo info = introspector.introspectOrGet(bean.getClass())
        def descriptor = new BetwixtJsonDescriptor(info.getElementDescriptor())
        if (!name) {
            name = descriptor.name
        }
        writeBean(bean, context, name, descriptor)
    }

    private void writeBean(Object bean, Context context, String name, BetwixtJsonDescriptor descriptor) {
        def id = binding.idMappingStrategy.getReferenceFor(null, bean)
        boolean idRef = id != null
        if (!id) {
            id = nameUtil.nextId()
        }

        // If it's a reference to a already written object, write the @ref file
        if (idRef) {
            if (!writeOptions().collection) {
                json.name(nameUtil.nameWithoutId(name, descriptor))
            }
            json.value(nameUtil.inlineReference(id))
            return
        }

        // It's not an idref, set the id for the current bean.
        binding.idMappingStrategy.setReference(null, bean, id)

        def objectStringConverter = binding.objectStringConverter
        def canBeInlined = objectStringConverter.canHandle(descriptor.propertyType)

        if (context == null) {
            context = createContext(bean)
        }

        String inlinedString = null
        if (canBeInlined) {
            inlinedString = objectStringConverter.objectToString(bean, descriptor.propertyType, context)
        }

        boolean inlined = false

        if (!writeOptions().collection) {
            json.name(nameUtil.nameWithId(descriptor, name, id))

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
                json.value(nameUtil.inlineValue(descriptor, id, inlinedString))
                inlined = true
            } else {
                json.beginObject()
                json.name("@tag")
                json.value(nameUtil.nameWithId(descriptor, "", id))
            }
        }
        if (!canBeInlined) {
            for (BetwixtJsonDescriptor child : descriptor.children) {
                if (child.isCollection()) {
                    def potential = child.evaluateAsIterator(context)
                    Iterator iterator = potential as Iterator
                    if (iterator.hasNext()) {
                        json.name(child.name)

                        def options = new JsonWriteOptions()
                        options.collection = true
                        optionStack.push(options)

                        json.beginArray()
                        while (iterator.hasNext()) {
                            Object childBean = iterator.next();
                            if (childBean) {
                                writeObject(childBean, createContext(childBean), "")
                            }
                        }
                        json.endArray()

                        optionStack.pop()
                    }
                } else {
                    if (child.hollow) {
                        def children = child.children
                        def newName = child.name
                        json.name(newName)

                        def writeOptions = new JsonWriteOptions()
                        if (child.onlyChildIsCollection()) {
                            json.beginArray()
                            writeOptions.collection = true
                        } else {
                            json.beginObject()
                        }
                        optionStack.push(writeOptions)
                        if (child.expression) {
                            def newBean = child.expression.evaluate(context)
                            println()
                        } else {
                            writeBean(bean, context, child.children[0].name, child.children[0])
                        }

                        optionStack.pop()
                        if (writeOptions.collection) {
                            json.endArray()
                        } else {
                            json.endObject()
                        }
                    } else if (child.expression) {
                        def evaluated = child.evaluateThis(context)
                        if (evaluated) {
                            if (child.isPrimitive()) {
                                writePrimitive(child.name, child.propertyType, evaluated)
                            } else {
                                optionStack.push(new JsonWriteOptions())
                                writeObject(evaluated, context, child.name)
                                optionStack.pop()
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
            if (!doubleValue.naN && !doubleValue.infinite) {
                json.name(name)
                json.value(doubleValue)
            }
        } else if (propertyType in [Float, float]) {
            def floatValue = value as float
            if (!floatValue.naN && !floatValue.infinite) {
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
        json.flush()
        json.close()
    }

    private Context createContext(bean) {
        new Context(bean, log, binding)
    }


}
