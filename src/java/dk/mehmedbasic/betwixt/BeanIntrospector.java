package dk.mehmedbasic.betwixt;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * TODO - someone remind me to document this class
 *
 * @author Jesenko Mehmedbasic created 5/19/2015.
 */
public class BeanIntrospector {
    private static ThreadLocal<InfoMap<Class, BeanInfo>> ignoreMap = new ThreadLocal<InfoMap<Class, BeanInfo>>() {
        @Override
        protected InfoMap<Class, BeanInfo> initialValue() {
            return new InfoMap<Class, BeanInfo>();
        }
    };
    private static ThreadLocal<InfoMap<Class, BeanInfo>> beanMap = new ThreadLocal<InfoMap<Class, BeanInfo>>() {
        @Override
        protected InfoMap<Class, BeanInfo> initialValue() {
            return new InfoMap<Class, BeanInfo>();
        }
    };


    public static <T> BeanInfo getBeanInfo(Class<T> beanClass, int flags) throws IntrospectionException {
        boolean ignoreAllInfo = Introspector.IGNORE_ALL_BEANINFO == flags;
        InfoMap<Class, BeanInfo> map;
        if (ignoreAllInfo) {
            map = ignoreMap.get();
        } else {
            map = beanMap.get();
        }
        BeanInfo beanInfo = map.get(beanClass);
        if (beanInfo != null) {
            return beanInfo;
        }
        EnhancedBeanInfo<T> info = new EnhancedBeanInfo<T>(beanClass, ignoreAllInfo);
        map.put(beanClass, info);
        return info;
    }

    public static <T> BeanInfo getBeanInfo(Class<T> beanClass) throws IntrospectionException {
        return getBeanInfo(beanClass, -1);
    }

    private static final class InfoMap<Key, Value> {
        private Map<Key, Value> wrapped = new WeakHashMap<Key, Value>();

        public Value get(Object key) {
            return wrapped.get(key);
        }

        public void put(Key key, Value value) {
            wrapped.put(key, value);
        }

    }
}
