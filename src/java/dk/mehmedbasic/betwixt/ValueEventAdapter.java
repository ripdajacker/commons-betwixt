package dk.mehmedbasic.betwixt;

import java.lang.reflect.InvocationTargetException;

/**
 * TODO - someone remind me to document this class
 *
 * @author Jesenko Mehmedbasic
 *         created 5/21/2015.
 */
public abstract class ValueEventAdapter {
    public void valueSet(InterceptedProperty property, Object bean, Object value) throws InvocationTargetException, IllegalAccessException {

    }

    public void valueRead(InterceptedProperty property, Object bean, Object value) {

    }
}
