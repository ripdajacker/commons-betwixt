package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.*
import org.apache.commons.betwixt.expression.Context
import org.apache.commons.betwixt.io.CyclicReferenceException
import org.apache.commons.betwixt.io.IDGenerator
import org.apache.commons.betwixt.io.id.SequentialIDGenerator
import org.apache.commons.betwixt.strategy.ObjectStringConverter
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.beans.IntrospectionException
import java.lang.reflect.Modifier
import java.util.regex.Pattern

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class JsonBeanWriter {
    private static final Pattern PATTERN_INSIDE_COLLECTION = Pattern.compile(/\w+\s*\[\w+\](\s*#\w+)?/)

    Log log = LogFactory.getLog(getClass())
    StreamingJsonWriter json

    Stack<Object> beanStack = []
    Stack<JsonWriteOptions> optionStack = []
    IDGenerator idGenerator = new SequentialIDGenerator();
    BindingConfiguration bindingConfiguration = new BindingConfiguration();
    MutableWriteContext writeContext = new MutableWriteContext();
    XMLIntrospector introspector = new XMLIntrospector()

    JsonBeanWriter(StreamingJsonWriter json) {
        this.json = json
    }

    void write(Object bean) {
        writeBean(null, bean, ctx(bean))
    }

    private Context ctx(Object bean) {
        return new Context(bean, log, bindingConfiguration);
    }

    void writeBean(String name, Object bean, Context context) {
        XMLBeanInfo beanInfo = introspector.introspect(bean);
        writeBean(name, bean, context, beanInfo);
    }

    void writeBean(String name, Object bean, Context context, ElementDescriptor parentDescriptor) {
        XMLBeanInfo beanInfo = findXMLBeanInfo(bean, parentDescriptor);
        if (name == null) {
            name = beanInfo.elementDescriptor.qualifiedName
        }
        writeBean(name, bean, context, beanInfo)
    }

    void writeBean(String name, Object bean, Context context, XMLBeanInfo beanInfo) {
        if (beanInfo == null) {
            return
        }


        def elementDescriptor = beanInfo.elementDescriptor
        context = context.newContext(bean);

        String reference = bindingConfiguration.idMappingStrategy.getReferenceFor(context, bean)

        if (elementDescriptor.simple) {
            writeBean(name, elementDescriptor, context, reference)
        } else {
            pushBean(context.getBean());

            if (reference) {
                if (!options.insideCollection) {
                    json.name(elementDescriptor.qualifiedName)
                    json.valueGeneric("@ref:$reference")
                } else {
                    json.valueGeneric("@ref:$reference")
                }
            } else {
                reference = idGenerator.nextId()
                bindingConfiguration.idMappingStrategy.setReference(context, bean, reference)

                writeBean(name, elementDescriptor, context, reference)
            }
        }
    }

    void writeBean(String name, ElementDescriptor elementDescriptor, Context context, String id) {
        def converter = bindingConfiguration.objectStringConverter

        def inlineByPropertyType = converter.canHandle(elementDescriptor.propertyType)

        if (onlyBody(elementDescriptor)) {
            def value = elementDescriptor.textExpression.evaluate(context)
            def childId = bindingConfiguration.idMappingStrategy.getReferenceFor(context, value)
            if (childId) {
                json.name(name)
                json.valueGeneric("@ref:$childId")
            } else {
                def text = convertToString(value, elementDescriptor, context)
                if (text && !text.empty) {
                    def strategy = bindingConfiguration.valueSuppressionStrategy
                    def shouldSuppress = strategy.suppressElement(elementDescriptor, null, name, name, value)
                    if (!shouldSuppress) {
                        writeNameValueWithId(name, text, context, value)
                    }
                }
            }
            return
        } else if (inlineByPropertyType) {
            // I can be inlined
            if (!options.insideCollection) {
                json.name("$name #$id")
            }
            bindingConfiguration.idMappingStrategy.setReference(context, context.bean, id)

            def string = converter.objectToString(context.bean, elementDescriptor.propertyType, context)
            if (options.insideCollection) {
                if (Modifier.isFinal(elementDescriptor.propertyType.modifiers)) {
                    json.valueGeneric("#$id:$string")
                } else {
                    json.valueGeneric("@${elementDescriptor.qualifiedName} #$id:$string")
                }
            } else {
                json.valueGeneric(string)
            }
            return
        }
        def suppressionStrategy = bindingConfiguration.valueSuppressionStrategy
        if (suppressionStrategy.suppressElement(elementDescriptor, null, name, name, context.bean)) {
            return
        }

        def childIsCollection = childCollective(elementDescriptor)
        def parentIsCollection = options.insideCollection
        pushOptions(elementDescriptor.collective || (!parentIsCollection && childIsCollection))

        if (name && id) {
            json.startElement("$name #$id", options)
            bindingConfiguration.idMappingStrategy.setReference(context, context.bean, id)
        } else {
            json.startElement(name, options)
        }
        options.insideCollection = options.collectionDescriptor


        def arrayLessCollection = name != null && PATTERN_INSIDE_COLLECTION.matcher(name).find()
        if (parentIsCollection || arrayLessCollection) {
            json.name("@tag")
            json.valueGeneric(elementDescriptor.qualifiedName)
            if (!arrayLessCollection) {
                json.name("@id")
                json.valueGeneric(id)
            }
        }

        for (AttributeDescriptor attributeDescriptor : elementDescriptor.attributeDescriptors) {
            def expression = attributeDescriptor.textExpression
            if (expression) {
                def value = expression.evaluate(context)
                if (value) {
                    def qualifiedName = attributeDescriptor.qualifiedName
                    def childId = bindingConfiguration.idMappingStrategy.getReferenceFor(context, value)
                    if (childId) {
                        json.name(qualifiedName)
                        json.valueGeneric("@ref:$childId")
                    } else {
                        def text = convertToString(value, attributeDescriptor, context)
                        if (text && !text.empty) {
                            def strategy = bindingConfiguration.valueSuppressionStrategy
                            def shouldSuppress = strategy.suppressAttribute(attributeDescriptor, text)
                            if (!shouldSuppress) {
                                writeNameValueWithId(qualifiedName, text, context, value)
                            }
                        }
                    }
                }
            }
        }

        if (elementDescriptor.contentDescriptors.length > 0 && !options.insideCollection) {
//            TODO determine when tag should be used
            /**/
//            json.name("@tag")
//            json.value(elementDescriptor.propertyName)
        }

        writeContext.setCurrentDescriptor(elementDescriptor);
        for (Descriptor childDescriptor : elementDescriptor.contentDescriptors) {
            if (childDescriptor instanceof ElementDescriptor) {
                def childElementDescriptor = childDescriptor as ElementDescriptor
                def childExpression = childElementDescriptor.getContextExpression()

                Context childContext = context;
                childContext.pushOptions(childDescriptor.getOptions())

                if (childExpression) {
                    def childBean = childExpression.evaluate(context)
                    if (childBean) {
                        if (childBean instanceof Iterator) {
                            def iterator = childBean as Iterator
                            if (iterator.hasNext()) {
                                int counter = 0
                                while (iterator.hasNext()) {
                                    Object object = iterator.next();
                                    if (object) {
                                        String collectionChildName = childElementDescriptor.propertyName
                                        if (!options.insideCollection) {
                                            collectionChildName = "$collectionChildName[$counter]"
                                        } else {
                                            collectionChildName = childElementDescriptor.qualifiedName
                                        }
//                                        pushOptions(options.collectionDescriptor)
//                                        options.insideCollection = true
                                        writeBean(collectionChildName, object, context, childElementDescriptor)
//                                        popOptions()
                                        counter++
                                    }
                                }
                            }
                        } else {
                            writeBean(childElementDescriptor.qualifiedName, childBean, context, childElementDescriptor)
                        }
                    }
                } else {
                    writeBean(childElementDescriptor.qualifiedName, childElementDescriptor, childContext, (String) null)
                }

                childContext.popOptions()
            } else {
                def expression = childDescriptor.textExpression
                if (expression) {
                    def value = expression.evaluate(context)
                    def childId = bindingConfiguration.idMappingStrategy.getReferenceFor(context, value)
                    if (childId) {
                        json.name("${childDescriptor.propertyName}")
                        json.valueGeneric("@ref:$childId")
                    } else {
                        def text = convertToString(value, childDescriptor, context)
                        if (text && !text.empty) {
                            writeNameValueWithId(childDescriptor.propertyName, text, context, value)
                        }
                    }

                }
            }
        }
        writeContext.setCurrentDescriptor(elementDescriptor);

        json.endElement(options)
        popOptions()
    }

    private void writeNameValueWithId(String name, String text, Context context, Object value) {
        if (isPrimitive(value)) {
            json.name(name)
            json.valueGeneric(text)
        } else {
            def childId = idGenerator.nextId()

            json.name("$name #$childId")
            json.valueGeneric(text)

            bindingConfiguration.idMappingStrategy.setReference(context, value, childId)
        }
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

    private boolean onlyBody(ElementDescriptor elementDescriptor) {
        elementDescriptor.textExpression && (elementDescriptor.contentDescriptors == null || elementDescriptor.contentDescriptors.length == 0)
    }


    void pushOptions(boolean collectionDescriptor) {
        boolean currentlyInsideCollection = options.insideCollection
        optionStack.push(new JsonWriteOptions(collectionDescriptor, currentlyInsideCollection))
    }

    JsonWriteOptions getOptions() {
        return optionStack.empty() ? new JsonWriteOptions(false, false) : optionStack.peek()
    }

    void popOptions() {
        optionStack.pop()
    }

    protected Object popBean() {
        return beanStack.pop();
    }

    void close() {
        json.close()
    }

    private XMLBeanInfo findXMLBeanInfo(Object bean, ElementDescriptor parentDescriptor) throws IntrospectionException {
        XMLBeanInfo beanInfo = null;
        Class introspectedBindType = parentDescriptor.getSingularPropertyType();
        if (introspectedBindType == null) {
            introspectedBindType = parentDescriptor.getPropertyType();
        }
        if (parentDescriptor.isUseBindTimeTypeForMapping() || introspectedBindType == null) {
            beanInfo = introspector.introspect(bean);
        } else {
            beanInfo = introspector.introspect(introspectedBindType);
        }
        return beanInfo;
    }

    protected void pushBean(Object bean) {
        // check that we don't have a cyclic reference when we're not writing IDs
        if (!bindingConfiguration.getMapIDs()) {
            Iterator it = beanStack.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                // use absolute equality rather than equals
                // we're only really bothered if objects are actually the same
                createCyclicReferenceError(bean, next)
            }
        }
        beanStack.push(bean);
    }

    private void createCyclicReferenceError(bean, next) {
        if (bean == next) {
            final String message = "Cyclic reference at bean: " + bean;
            StringBuffer buffer = new StringBuffer(message);
            buffer.append(" Stack: ");
            Iterator errorStack = beanStack.iterator();
            while (errorStack.hasNext()) {
                Object errorObj = errorStack.next();
                if (errorObj != null) {
                    buffer.append(errorObj.getClass().getName());
                    buffer.append(": ");
                }
                buffer.append(errorObj);
                buffer.append(";");
            }
            final String debugMessage = buffer.toString();
            log.info(debugMessage);
            throw new CyclicReferenceException(debugMessage);
        }
    }

    private String convertToString(Object value, Descriptor descriptor, Context context) {
        return bindingConfiguration.objectStringConverter.objectToString(value, descriptor.getPropertyType(), context);
    }

    private static boolean isPrimitive(Object value) {
        if (value) {
            return ObjectStringConverter.implicit.contains(value.class)
        }
        return false
    }
}