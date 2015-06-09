package dk.mehmedbasic.betwixt

import com.google.gson.stream.JsonWriter
import org.apache.commons.betwixt.*
import org.apache.commons.betwixt.expression.Context
import org.apache.commons.betwixt.expression.Expression
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.beans.BeanInfo

/**
 * A simple event listener for betwixt beans.
 *
 * @author Jesenko Mehmedbasic
 * created 6/8/2015.
 */
public class JsonBeanWriter {
    private JsonWriter jsonWriter

    private Stack<Object> beanStack = new Stack<>();

    private Log log = LogFactory.getLog(getClass());
    private XMLIntrospector introspector = new XMLIntrospector();
    private BindingConfiguration bindingConfiguration = new BindingConfiguration();

    JsonBeanWriter(Writer out) {
        jsonWriter = new JsonWriter(new PrintWriter(System.out))
    }

    public void start() {
        jsonWriter.beginObject()
    }

    public void write(Object bean) {

        XMLBeanInfo beanInfo = introspector.introspect(bean);
        if (beanInfo != null) {
            def context = new Context(bean, log, bindingConfiguration)
            ElementDescriptor parentDescriptor = beanInfo.getElementDescriptor();
            if (parentDescriptor != null) {
                for (Descriptor childDescriptor : parentDescriptor.getContentDescriptors()) {
                    if (childDescriptor instanceof ElementDescriptor) {
                        // Element content
                        context.pushOptions(childDescriptor.getOptions());

                        Expression contextExpression = childDescriptor.getContextExpression();
                        if (contextExpression != null) {
                            Object childBean = contextExpression.evaluate(context);
                            if (childBean != null) {
                                if (childBean instanceof Iterator) {
                                    writeIterator(childDescriptor, childBean)
                                } else {
                                    write(childDescriptor, childBean)
                                }
                            }
                        } else {
                            write(childDescriptor, bean)
                        }
                        context.popOptions();
                    } else {
                        // Mixed text content
                        // evaluate the body text
                        Expression expression = childDescriptor.getTextExpression();
                        if (expression != null) {
                            Object value = expression.evaluate(context);
                            String text = convertToString(value, childDescriptor, context);

                            if (text != null && text.length() > 0) {
                                jsonWriter.name(childDescriptor.getLocalName())
                                jsonWriter.value(text)

                            }
                        }
                    }

                }
            }
        }
    }

    public void end() {
        jsonWriter.endObject()
        jsonWriter.close()
    }


    void write(ElementDescriptor descriptor, Object bean) {
        def context = new Context(bean, log, bindingConfiguration)

        if (descriptor.isSimple()) {

            if (descriptor.getTextExpression() != null) {
                Expression expression = descriptor.getTextExpression();

                Object value = expression.evaluate(context);
                String text = convertToString(value, descriptor, context);

                jsonWriter.name(descriptor.getLocalName())
                jsonWriter.value(text)
            }

            println()
        } else {
            write(bean)
            println()
        }
    }

    private void writeIterator(Descriptor descriptor, Iterator iterator) {
        if (iterator.hasNext()) {
            jsonWriter.beginArray()

            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (object != null) {
                    write(descriptor, object)
                }
            }

            jsonWriter.endArray()
        }
    }

    /**
     * Converts an object to a string.
     *
     * @param value the Object to represent as a String, possibly null
     * @param descriptor writing out this descriptor not null
     * @param context not null
     * @return String representation, not null
     */
    private String convertToString(Object value, Descriptor descriptor, Context context) {
        return bindingConfiguration
                .getObjectStringConverter()
                .objectToString(value, descriptor.getPropertyType(), context);
    }


    static class BeanElementContext {
        ElementDescriptor descriptor
        BeanInfo beanInfo
        Context betwixtContext
    }
}
