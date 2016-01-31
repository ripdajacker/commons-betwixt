package dk.mehmedbasic.betwixt.json

import dk.mehmedbasic.betwixt.BetwixtJsonData
import dk.mehmedbasic.betwixt.NameType
import groovy.transform.TypeChecked
import org.apache.commons.betwixt.AttributeDescriptor
import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.expression.IteratorExpression
import org.apache.commons.betwixt.io.BeanReader
import org.apache.commons.betwixt.io.read.AttributesWithContext
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.JsonNodeFactory
import org.xml.sax.Attributes

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class JsonSaxEventAdapter {
    JsonReadContextEventListener listener

    private BeanReader handler
    private final JsonNode rootNode
    private JsonNodeFactory nodeFactory

    JsonSaxEventAdapter(BeanReader handler, InputStream inputStream) {
        this.handler = handler

        ObjectMapper mapper = new ObjectMapper()
        nodeFactory = mapper.nodeFactory
        rootNode = mapper.readTree(inputStream)


        listener = handler.readContextEventListener as JsonReadContextEventListener
    }

    void execute() {
        handler.startDocument()
        for (String name : rootNode.fieldNames) {
            def data = new BetwixtJsonData(name)
            parseInner(data.propertyName, false, rootNode.get(name))
        }
        handler.endDocument()
    }

    private void parseInner(String thisName, boolean hollow, JsonNode baseNode) {

        String tagName = null
        if (baseNode.get("@tag") != null) {
            def text1 = baseNode.get("@tag").valueAsText
            tagName = text1
        }
        def attributes = new JsonAttributes()

        if (tagName != null) {
            if (hollow || thisName == null) {
                thisName = tagName
            }
        }
        for (String child : baseNode.fieldNames) {
            def childNode = baseNode.get(child)
            if (childNode.valueNode) {
                def childData = new BetwixtJsonData(child)
                if (!systemName(childData.propertyName)) {
                    attributes.setValue(childData.propertyName, childData.propertyName, childNode.valueAsText)
                }
            }
        }
        def id = null
        if (baseNode.get("@id") != null) {
            id = baseNode.get("@id").valueAsText
        }

        def data = new BetwixtJsonData(thisName)

        listener.pushJsonNode(baseNode)

        if (data.idRef != null) {
            attributes.idref = data.idRef
        }
        if (baseNode.get("idref") != null) {
            attributes.idref = baseNode.get("idref").valueAsText
        }
        if (baseNode.get("inlinedValue") != null) {
            attributes.setValue("inlinedValue", "inlinedValue", baseNode.get("inlinedValue").valueAsText)
        }
        if (baseNode.get("analysisID") != null) {
            attributes.setValue("analysisID", "analysisID", baseNode.get("analysisID").valueAsText)
        }
        if (id != null) {
            attributes.id = id
        }
        if (id == null && data.id != null) {
            attributes.id = data.id
        }


        listener.pushCallback { JsonNode first, ElementDescriptor newDescriptor ->
            attributes.initializeAttributeDescriptors(newDescriptor)
            attributes.initNode(first)
            if (newDescriptor) {
                hollow = newDescriptor.hollow
            }
        }
        handler.startElement(null, data.propertyName, data.propertyName, attributes)
        listener.popCallback()



        ElementDescriptor descriptor = listener.peekCurrentDescriptor()

        if (baseNode.isValueNode()) {
            if (descriptor != null && onlyBody(descriptor)) {
                def text = baseNode.valueAsText
                def chars = text.chars
                handler.characters(chars, 0, chars.length)
            } else {
                throw new IllegalStateException("This should not happen")
            }
        } else if (baseNode.isArray()) {
            def size = baseNode.size()
            for (int i = 0; i < size; i++) {
                def nextNode = baseNode.get(i)

                if (nextNode.valueNode) {
                    def valueAsText = nextNode.valueAsText
                    if (valueAsText != null) {
                        def inlineData = new BetwixtJsonData(valueAsText)
                        def type = BetwixtJsonData.determineType(valueAsText)
                        if (type == NameType.INLINE_REFERENCE) {
                            def objectNode = nodeFactory.objectNode()
                            objectNode.put("idref", inlineData.idRef as String)
                            if (inlineData.propertyName != null) {
                                parseInner(inlineData.propertyName, hollow, objectNode)
                            } else {
                                parseInner(descriptor.qualifiedName, hollow, objectNode)
                            }
                        } else if (type == NameType.INLINE_VALUE) {
                            def objectNode = nodeFactory.objectNode()
                            objectNode.put("@id", inlineData.id)
                            objectNode.put("inlinedValue", inlineData.inlineValue)
                            parseInner(inlineData.propertyName, descriptor.hollow, objectNode)
                        } else {
                            throw new IllegalStateException("This should not happen")
                        }
                    }
                } else {
                    if (childCollective(descriptor)) {
                        readWithDescriptor(nextNode, descriptor, descriptor.elementDescriptors[0])
                    } else {
                        readWithDescriptor(nextNode, descriptor, null)
                    }
                }
            }
        } else {
            for (String fieldName : baseNode.getFieldNames()) {
                def systemName = systemName(fieldName)
                def isAttribute = attributes.realNames.contains(fieldName)
                if (!systemName) {
                    if (isAttribute) {

                    } else if (!isAttribute) {
                        String nextName = stripArrayIdentifier(fieldName)
                        def contains = fieldName.contains("[")
                        def currentData = new BetwixtJsonData(nextName)
                        def descriptorName = currentData.propertyName

                        boolean nextIsHollow = false
                        if (contains) {

                            ElementDescriptor find = null
                            if (descriptor != null) {
                                find = descriptor.elementDescriptors.find {
                                    it.qualifiedName == null && it.propertyName == descriptorName
                                }
                            }

                            if (find != null) {
                                nextIsHollow = find.hollow
                            }
                        }

                        def nextNode = baseNode.get(fieldName)
                        if (nextNode.valueNode) {
                            def valueAsText = nextNode.valueAsText
                            if (valueAsText != null) {
                                def inlineData = new BetwixtJsonData(valueAsText)
                                def type = BetwixtJsonData.determineType(valueAsText)


                                if (type == NameType.INLINE_REFERENCE) {
                                    def objectNode = nodeFactory.objectNode()
                                    objectNode.put("idref", inlineData.idRef as String)
                                    parseInner(nextName, nextIsHollow, objectNode)
                                } else if (type == NameType.INLINE_VALUE || currentData.id != null) {
                                    if (currentData.id != null) {
                                        def objectNode = nodeFactory.objectNode()
                                        objectNode.put("@id", currentData.id as String)
                                        objectNode.put("inlinedValue", valueAsText)
                                        parseInner(nextName, nextIsHollow, objectNode)
                                    } else {
                                        def objectNode = nodeFactory.objectNode()
                                        objectNode.put("@id", inlineData.id as String)
                                        objectNode.put("inlinedValue", inlineData.inlineValue)
                                        parseInner(nextName, nextIsHollow, objectNode)
                                    }
                                } else {
                                    def found = descriptor.getElementDescriptors().find {
                                        [it.qualifiedName, it.localName, it.propertyName].contains(nextName)
                                    }
                                    if (found != null && found.textExpression != null) {
                                        parseInner(nextName, nextIsHollow, nextNode)
                                    } else {
                                        def objectNode = nodeFactory.objectNode()
                                        objectNode.put("inlinedValue", valueAsText)
                                        parseInner(nextName, nextIsHollow, objectNode)
                                    }
                                }

                            }
                        } else {
                            parseInner(nextName, nextIsHollow, nextNode)
                        }
                    }
                }


            }
        }
        handler.endElement(null, data.propertyName, data.propertyName)
        listener.popJsonNode()

    }

    boolean childCollective(ElementDescriptor elementDescriptor) {
        if (elementDescriptor.contentDescriptors.length == 1) {

            def descriptor = elementDescriptor.contentDescriptors[0]
            if (descriptor instanceof ElementDescriptor) {
                def cast = descriptor as ElementDescriptor
                return cast.collective
            }
        }
        return false
    }

    private void readWithDescriptor(JsonNode node, ElementDescriptor descriptor, ElementDescriptor childDescriptor) {
        if (node.isValueNode()) {
            def inlineData = new BetwixtJsonData(node.valueAsText)
            if (inlineData.idRef != null) {
                def objectNode = nodeFactory.objectNode()
                objectNode.put("idref", inlineData.idRef)
                parseInner(descriptor.qualifiedName, false, objectNode)
            } else if (inlineData.inlineValue != null) {
                def objectNode = nodeFactory.objectNode()
                objectNode.put("@id", inlineData.id)
                objectNode.put("inlinedValue", inlineData.inlineValue)
                parseInner(descriptor.qualifiedName, false, objectNode)
            } else {
                throw new RuntimeException("I am an unknown node")
            }
        } else {
            String name = null
            if (childDescriptor != null) {
                name = childDescriptor.qualifiedName
            }
            parseInner(name, false, node)
        }
    }

    private static String stripArrayIdentifier(String input) {
        return input.replaceAll(/\[\d+\]/, "")
    }

    static boolean systemName(String name) {
        return ["@tag", "@id", "idref", "inlinedValue"].contains(name)
    }

    private static boolean onlyBody(ElementDescriptor elementDescriptor) {
        elementDescriptor.textExpression && (elementDescriptor.contentDescriptors == null || elementDescriptor.contentDescriptors.length == 0)
    }

    static class JsonAttributes implements Attributes, AttributesWithContext {
        JsonNode node
        List<String> realNames = []
        List<String> qualifiedNames = []
        List<String> values = []

        List<AttributeDescriptor> attributeDescriptors = []
        List<ElementDescriptor> elementDescriptors = []

        String idref
        String id

        void initializeAttributeDescriptors(ElementDescriptor descriptor) {
            if (descriptor != null) {
                attributeDescriptors.clear()
                attributeDescriptors.addAll(descriptor.getAttributeDescriptors())

                elementDescriptors.clear()
                elementDescriptors.addAll(descriptor.getElementDescriptors())

                if (descriptor.contentDescriptors.length == 1 && descriptor.elementDescriptors[0].collective) {
                } else if (descriptor.contentDescriptors.length == 0 && descriptor.contextExpression != null && descriptor.contextExpression instanceof IteratorExpression) {
                } else {
                    realNames.findAll { realName ->
                        attributeDescriptors.find { realName.equals(it.qualifiedName) } == null
                    }.each {
                        if (!JsonSaxEventAdapter.systemName(it) && "analysisID" != it) {
                            deleteValue(it)
                        }
                    }
                }
            }
        }

        void initNode(JsonNode node) {
            List<String> filtered = filter(node.fieldNames)
            for (String name : filtered) {
                def potentialNode = node.get(name)
                if (potentialNode.isValueNode()) {
                    def data = new BetwixtJsonData(name)

                    def type = BetwixtJsonData.determineType(name)
                    if (type == NameType.NAME_WITHOUT_ID) {
                        setValue(name, data.propertyName, potentialNode.valueAsText)
                    }
                }
            }
            this.node = node
        }

        List<String> filter(Iterator<String> iterator) {
            def list = []
            while (iterator.hasNext()) {
                String name = iterator.next();


                def data = new BetwixtJsonData(name)
                def betwixtName = data.propertyName
                def found = attributeDescriptors.find {
                    [it.qualifiedName, it.localName, it.propertyName].contains(betwixtName)
                }
                def foundInElements = elementDescriptors.find {
                    [it.qualifiedName, it.localName, it.propertyName].contains(betwixtName)
                }


                if (found != null && foundInElements == null) {
                    list << name
                }
            }

            return list
        }

        void setValue(String realName, String qualifiedName, String value) {
            def index = realNames.indexOf(realName)
            if (index >= 0) {
                qualifiedNames.set(index, qualifiedName)
                values.set(index, value)
            } else {
                realNames << realName
                qualifiedNames << qualifiedName
                values << value

            }
        }

        void deleteValue(String realName) {

            def index = realNames.indexOf(realName)
            if (index >= 0) {
                realNames.remove(index)
                qualifiedNames.remove(index)
                values.remove(index)
            }
        }

        @Override
        int getLength() {
            return realNames.size()
        }

        @Override
        String getURI(int index) {
            return ""
        }

        @Override
        String getLocalName(int index) {
            return qualifiedNames.get(index)
        }

        @Override
        String getQName(int index) {
            return qualifiedNames.get(index)
        }

        @Override
        String getType(int index) {
            return ""
        }

        @Override
        String getValue(int index) {
            return values.get(index)
        }

        @Override
        int getIndex(String uri, String localName) {
            return qualifiedNames.indexOf(localName)
        }

        @Override
        int getIndex(String qName) {
            return qualifiedNames.indexOf(qName)
        }

        @Override
        String getType(String uri, String localName) {
            return ""
        }

        @Override
        String getType(String qName) {
            return ""
        }

        @Override
        String getValue(String uri, String localName) {
            def index = getIndex(localName)
            if (index < 0) {
                return null
            }
            return values.get(index)
        }

        @Override
        String getValue(String qName) {
            if (qName == "idref") {
                return idref
            }
            if (qName == "id") {
                return id
            }
            def index = getIndex(qName)
            if (index < 0) {
                return null
            }
            return values.get(index)

        }

        @Override
        String getValue(String name, AttributeDescriptor descriptor) {
            if (getIndex(name) == -1) {
                name = descriptor.qualifiedName
                if (getIndex(name) == -1) {
                    name = descriptor.propertyName
                    if (getIndex(name) == -1) {
                        return null
                    }
                }
            }
            return getValue(name)
        }
    }

}
