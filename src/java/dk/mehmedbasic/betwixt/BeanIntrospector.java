package dk.mehmedbasic.betwixt;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.WeakHashMap;

/**
 * A very nice Introspector replacement that handles properties that are not public.
 */
public class BeanIntrospector {
    private static ThreadLocal<WeakHashMap<Class, BeanInfo>> ignoreMap = new ThreadLocal<WeakHashMap<Class, BeanInfo>>() {
        @Override
        protected WeakHashMap<Class, BeanInfo> initialValue() {
            return new WeakHashMap<>();
        }
    };
    private static ThreadLocal<WeakHashMap<Class, BeanInfo>> beanMap = new ThreadLocal<WeakHashMap<Class, BeanInfo>>() {
        @Override
        protected WeakHashMap<Class, BeanInfo> initialValue() {
            return new WeakHashMap<>();
        }
    };


    public static <T> BeanInfo getBeanInfo(Class<T> beanClass, int flags) throws IntrospectionException {
        boolean ignoreAllInfo = Introspector.IGNORE_ALL_BEANINFO == flags;
        WeakHashMap<Class, BeanInfo> map;
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
}
