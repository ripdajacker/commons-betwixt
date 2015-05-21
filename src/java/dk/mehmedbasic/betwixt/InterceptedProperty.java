package dk.mehmedbasic.betwixt;

import java.lang.reflect.InvocationTargetException;

/**
 * TODO - someone remind me to document this class
 *
 * @author Jesenko Mehmedbasic
 *         created 5/19/2015.
 */
public class InterceptedProperty {
    private ValueEventAdapter listener;

    private Object value;
    private Object bean;

    public InterceptedProperty(Object bean, ValueEventAdapter listener) {
        this.bean = bean;
        this.listener = listener;
    }

    public Object getValue() {
        listener.valueRead(this, bean, value);
        return value;
    }

    public <T> void setValue(T value) throws InvocationTargetException, IllegalAccessException {
        listener.valueSet(this, bean, value);
        this.value = value;
    }

}
