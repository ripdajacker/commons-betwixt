package dk.mehmedbasic.betwixt

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.BindingConfiguration
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.betwixt.expression.Context
import org.apache.commons.betwixt.io.read.ReadConfiguration
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.ArrayNode
import org.xml.sax.InputSource

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class BetwixtJsonReader {
    private Log log = LogFactory.getLog(BetwixtJsonWriter)

    XMLIntrospector introspector = new XMLIntrospector()
    BindingConfiguration binding = new BindingConfiguration()
    ReadConfiguration readConfiguration = new ReadConfiguration()

    private JsonWriterNameGenerator nameUtil = new JsonWriterNameGenerator()
    private JsonMappingRegistry registry = new JsonMappingRegistry()
    private Reader reader

    BetwixtJsonReader(Reader reader) {
        // TODO Read in read method
        this.reader = reader
    }

    void readBeanInfo(InputSource mapping) {
        Class[] mappedClasses = introspector.register(mapping);
        for (Class beanClass : mappedClasses) {
            registry.register(beanClass, introspector.introspectOrGet(beanClass));
        }
    }


    public <T> T read(Class<T> returnType) {

        def rootTree = new ObjectMapper().readTree(reader)

        if (rootTree.size() != 1) {
            throw new IllegalStateException("The root object should have exactly one property, but had: ${rootTree.size()}")
        }

        String rootKey = rootTree.fieldNames.next()
        def rootNameData = new BetwixtJsonData(rootKey)
        def descriptor = registry.lookupName(rootNameData.propertyName)

        def root = createBean("", descriptor)
        def next = rootTree.getElements().iterator().next()
        readObject(root, rootKey, next, descriptor, null)
        return returnType.cast(root)
    }

    private Context ctx(root) {
        new Context(root, log, binding)
    }

    private void readObject(Object bean, String mainJsonName,
                            JsonNode jsonElement,
                            BetwixtJsonDescriptor thisDescriptor,
                            Collection parentCollection) {
        def beanData = new BetwixtJsonData(mainJsonName)
        if (beanData.id) {
            bean = binding.idMappingStrategy.getReferenced(null, beanData.id)
        } else if (!thisDescriptor) {
            def tagElement = jsonElement.get("@tag")
            if (tagElement != null) {
                beanData = new BetwixtJsonData(tagElement.textValue)
                thisDescriptor = registry.lookupName(beanData.propertyName)
                bean = thisDescriptor.createInstance(beanData, log, readConfiguration, binding)
            }
        }


        if (!thisDescriptor && !parentCollection) {
            // This should not happen
            log.error("Descriptor not found for child in $mainJsonName")
            return
        }

        if (parentCollection) {
            parentCollection.add(bean)
        }

        for (String childName : jsonElement.fieldNames) {
            if (childName in ["@tag"]) {
                continue
            }
            def holder = new BetwixtJsonData(childName)
            def propertyName = holder.propertyName

            def descriptor = thisDescriptor.children.find { it.name == propertyName }
            if (!descriptor) {
                log.warn("Descriptor not found for property name '$propertyName', json name '$childName'")
                continue
            }

            def childElement = jsonElement.get(childName)
            if (descriptor.primitive) {
                setPrimitive(ctx(bean), childElement, descriptor)
            } else if (descriptor.collection) {
                def collection = descriptor.evaluateAsCollection(ctx(bean))

                ArrayNode arrayNode = childElement as ArrayNode
                def elements = arrayNode.elements
                while (elements.hasNext()) {
                    JsonNode node = elements.next();
                    readObject(null, "", node, null, collection)
                }
            } else {
                if (descriptor.element) {
                    def instance = descriptor.createInstance(holder, log, readConfiguration, binding)
                    readObject(instance, childName, childElement, descriptor, null)
                    descriptor.updater.update(ctx(bean), instance)
                }    else {
                    // I am an attribute
                }
            }

        }
    }

    static void setPrimitive(Context context, JsonNode value, BetwixtJsonDescriptor descriptor) {
        def type = descriptor.propertyType
        if (type in [Double, double]) {
            descriptor.updater.update(context, value.valueAsDouble)
        } else if (type in [Float, float]) {
            descriptor.updater.update(context, value.valueAsDouble as float)
        } else if (type in [Long, long]) {
            descriptor.updater.update(context, value.valueAsLong)
        } else if (type in [Integer, int]) {
            descriptor.updater.update(context, value.valueAsInt)
        } else if (type in [Boolean, boolean]) {
            descriptor.updater.update(context, value.valueAsBoolean)
        } else if (type == String) {
            descriptor.updater.update(context, value.textValue)
        }
    }

    Object createBean(String jsonName, BetwixtJsonDescriptor descriptor) {
        def nameAndId = new BetwixtJsonData(jsonName)
        if (nameAndId.id == null) {
            def instance = descriptor.createInstance(nameAndId, log, readConfiguration, binding)
            return instance
        }

        def id = nameAndId.id
        return binding.idMappingStrategy.getReferenced(null, id)
    }


    public static void main(String[] args) {
    }

}
