package dk.mehmedbasic.betwixt

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.BindingConfiguration
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.xml.sax.InputSource

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class BetwixtJsonReader {
    private Log log = LogFactory.getLog(BetwixtJsonWriter)

    XMLIntrospector introspector = new XMLIntrospector()
    BindingConfiguration binding = new BindingConfiguration()

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
        readObject(root, rootTree.get(0), descriptor)
        return returnType.cast(root)
    }

    private void readObject(Object parentBean, JsonNode jsonElement, BetwixtJsonDescriptor thisDescriptor) {
        if (thisDescriptor.isPrimitive()) {
            println(thisDescriptor.name)
        }
        for (BetwixtJsonDescriptor childDescriptor : thisDescriptor.children) {
            readObject(null, null, childDescriptor)
        }

    }

    Object createBean(String jsonName, BetwixtJsonDescriptor descriptor) {
        def nameAndId = new BetwixtJsonData(jsonName)
        if (nameAndId.id == null) {
            def instance = descriptor.propertyType.getDeclaredConstructor().newInstance()
            return instance
        }

        def id = nameAndId.id
        return binding.idMappingStrategy.getReferenced(null, id)
    }

    public static void main(String[] args) {
        def reader = new BetwixtJsonReader(new FileReader(new File("c:/test.json")))
        reader.readBeanInfo(new InputSource(new FileReader('C:/src/reportgeneration/src/dk/pls/reportgeneration/xmlserialization/model.betwixt.xml')))

        reader.read(Object)
    }

}
