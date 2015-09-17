package dk.mehmedbasic.betwixt

import java.awt.*
import java.beans.*
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.List

/**
 * A bean info that enhances the default BeanInfo used in Betwixt.
 */
public class EnhancedBeanInfo<T> implements BeanInfo {
    static final String ADD_PREFIX = "add";
    static final String REMOVE_PREFIX = "remove";
    static final String GET_PREFIX = "get";
    static final String SET_PREFIX = "set";
    static final String IS_PREFIX = "is";


    private Map<String, Collection<PropertyDescriptor>> temporaryMap = new HashMap<String, Collection<PropertyDescriptor>>();
    private Map<String, PropertyDescriptor> properties = new TreeMap<String, PropertyDescriptor>();


    private BeanDescriptor beanDescriptor;
    private BeanInfo legacy
    private PropertyDescriptor[] propertyDescriptors;
    private boolean ignoreInfo

    public EnhancedBeanInfo(Class<T> clazz) {
        this(clazz, false)
    }

    public EnhancedBeanInfo(Class<T> clazz, boolean ignoreAllInfo) {
        if (ignoreAllInfo) {
            legacy = Introspector.getBeanInfo(clazz, Introspector.IGNORE_ALL_BEANINFO)
            this.beanDescriptor = new BeanDescriptor(Object.class);
            this.propertyDescriptors = legacy.getPropertyDescriptors()
        } else {
            legacy = Introspector.getBeanInfo(clazz)
            this.beanDescriptor = new BeanDescriptor(clazz);
        }
        getPropertyDescriptors()
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return beanDescriptor;
    }

    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDefaultEventIndex() {
        return -1;
    }

    @Override
    public int getDefaultPropertyIndex() {
        return -1;
    }

    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return null;
    }

    @Override
    public Image getIcon(int iconKind) {
        throw new UnsupportedOperationException();
    }

    Class getBeanClass() {
        return beanDescriptor.getBeanClass()
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        if (propertyDescriptors != null) {
            return propertyDescriptors;
        }

        Class<T> clazz = (Class<T>) beanDescriptor.getBeanClass();


        List<Method> methodList = new LinkedList<Method>();
        Class current = clazz;
        while (current != null && current != Object.class && current != Enum.class) {
            def methods = current.getMethods()
            Collections.addAll(methodList, methods);
            current = current.getSuperclass();
        }
        Collection<PropertyDescriptor> descriptors = new LinkedList<PropertyDescriptor>();
        injectInterceptors(methodList);
        processPropertyDescriptors()

        propertyDescriptors = (PropertyDescriptor[]) properties.values().toArray(new PropertyDescriptor[descriptors.size()]);
        return propertyDescriptors;
    }

    private void injectInterceptors(Collection<Method> methodList) {
        // Now analyze each method.
        for (Method method : methodList) {
            if (method == null) {
                continue;
            }
            // skip static methods.
            int mods = method.getModifiers();
            if (Modifier.isStatic(mods)) {
                continue;
            }
            String name = method.getName();
            Class[] argTypes = method.getParameterTypes();
            Class resultType = method.getReturnType();
            int argCount = argTypes.length;
            PropertyDescriptor descriptor = null;

            if (name.length() <= 3 && !name.startsWith(IS_PREFIX)) {
                // Optimization. Don't bother with invalid propertyNames.
                continue;
            }

            try {

                if (argCount == 0) {
                    if (name.startsWith(GET_PREFIX)) {
                        // Simple getter
                        descriptor = new PropertyDescriptor(this.beanClass, name.substring(3), method, null);
                    } else if (resultType == boolean.class && name.startsWith(IS_PREFIX)) {
                        // Boolean getter
                        descriptor = new PropertyDescriptor(this.beanClass, name.substring(2), method, null);
                    }
                } else if (argCount == 1) {
                    if (int.class.equals(argTypes[0]) && name.startsWith(GET_PREFIX)) {
                        descriptor = new IndexedPropertyDescriptor(this.beanClass, name.substring(3), null, null, method, null);
                    } else if (void.class.equals(resultType) && name.startsWith(SET_PREFIX)) {
                        // Simple setter
                        descriptor = new PropertyDescriptor(this.beanClass, name.substring(3), null, method);
                    }
                } else if (argCount == 2) {
                    if (void.class.equals(resultType) && int.class.equals(argTypes[0]) && name.startsWith(SET_PREFIX)) {
                        descriptor = new IndexedPropertyDescriptor(this.beanClass, name.substring(3), null, null, null, method);
                    }
                }
            } catch (IntrospectionException ex) {
                // This happens if a PropertyDescriptor or IndexedPropertyDescriptor
                // constructor fins that the method violates details of the deisgn
                // pattern, e.g. by having an empty name, or a getter returning
                // void , or whatever.
                descriptor = null;
            }

            if (descriptor != null) {
                // If this class or one of its base classes is a PropertyChange
                // source, then we assume that any properties we discover are "bound".
//                if (propertyChangeSource) {
//                    pd.setBound(true);
//                }
                addPropertyDescriptor(descriptor)
            }
        }
    }

    /**
     * Populates the property descriptor table by merging the
     * lists of Property descriptors.
     */
    private void processPropertyDescriptors() {
        List list;

        PropertyDescriptor pd, gpd, spd;
        IndexedPropertyDescriptor ipd, igpd, ispd;

        Iterator it = temporaryMap.values().iterator();
        while (it.hasNext()) {
            pd = null; gpd = null; spd = null;
            ipd = null; igpd = null; ispd = null;

            list = (List) it.next();

            // First pass. Find the latest getter method. Merge properties
            // of previous getter methods.
            for (int i = 0; i < list.size(); i++) {
                pd = (PropertyDescriptor) list.get(i);
                if (pd instanceof IndexedPropertyDescriptor) {
                    ipd = (IndexedPropertyDescriptor) pd;
                    if (ipd.getIndexedReadMethod() != null) {
                        if (igpd != null) {
                            igpd = new IndexedPropertyDescriptor(igpd, ipd);
                        } else {
                            igpd = ipd;
                        }
                    }
                } else {
                    if (pd.getReadMethod() != null) {
                        if (gpd != null) {
                            // Don't replace the existing read
                            // method if it starts with "is"
                            Method method = gpd.getReadMethod();
                            if (!method.getName().startsWith(IS_PREFIX)) {
                                gpd = new PropertyDescriptor(gpd, pd);
                            }
                        } else {
                            gpd = pd;
                        }
                    }
                }
            }

            // Second pass. Find the latest setter method which
            // has the same type as the getter method.
            for (int i = 0; i < list.size(); i++) {
                pd = (PropertyDescriptor) list.get(i);
                if (pd instanceof IndexedPropertyDescriptor) {
                    ipd = (IndexedPropertyDescriptor) pd;
                    if (ipd.getIndexedWriteMethod() != null) {
                        if (igpd != null) {
                            if (igpd.getIndexedPropertyType()
                                    == ipd.getIndexedPropertyType()) {
                                if (ispd != null) {
                                    ispd = new IndexedPropertyDescriptor(ispd, ipd);
                                } else {
                                    ispd = ipd;
                                }
                            }
                        } else {
                            if (ispd != null) {
                                ispd = new IndexedPropertyDescriptor(ispd, ipd);
                            } else {
                                ispd = ipd;
                            }
                        }
                    }
                } else {
                    if (pd.getWriteMethod() != null) {
                        if (gpd != null) {
                            if (gpd.getPropertyType() == pd.getPropertyType()) {
                                if (spd != null) {
                                    spd = new PropertyDescriptor(spd, pd);
                                } else {
                                    spd = pd;
                                }
                            }
                        } else {
                            if (spd != null) {
                                spd = new PropertyDescriptor(spd, pd);
                            } else {
                                spd = pd;
                            }
                        }
                    }
                }
            }

            // At this stage we should have either PDs or IPDs for the
            // representative getters and setters. The order at which the
            // property descriptors are determined represent the
            // precedence of the property ordering.
            pd = null; ipd = null;

            if (igpd != null && ispd != null) {
                // Complete indexed properties set
                // Merge any classic property descriptors
                if (gpd != null) {
                    PropertyDescriptor tpd = mergePropertyDescriptor(igpd, gpd);
                    if (tpd instanceof IndexedPropertyDescriptor) {
                        igpd = (IndexedPropertyDescriptor) tpd;
                    }
                }
                if (spd != null) {
                    PropertyDescriptor tpd = mergePropertyDescriptor(ispd, spd);
                    if (tpd instanceof IndexedPropertyDescriptor) {
                        ispd = (IndexedPropertyDescriptor) tpd;
                    }
                }
                if (igpd == ispd) {
                    pd = igpd;
                } else {
                    pd = mergePropertyDescriptor(igpd, ispd);
                }
            } else if (gpd != null && spd != null) {
                // Complete simple properties set
                if (gpd == spd) {
                    pd = gpd;
                } else {
                    pd = mergePropertyDescriptor(gpd, spd);
                }
            } else if (ispd != null) {
                // indexed setter
                pd = ispd;
                // Merge any classic property descriptors
                if (spd != null) {
                    pd = mergePropertyDescriptor(ispd, spd);
                }
                if (gpd != null) {
                    pd = mergePropertyDescriptor(ispd, gpd);
                }
            } else if (igpd != null) {
                // indexed getter
                pd = igpd;
                // Merge any classic property descriptors
                if (gpd != null) {
                    pd = mergePropertyDescriptor(igpd, gpd);
                }
                if (spd != null) {
                    pd = mergePropertyDescriptor(igpd, spd);
                }
            } else if (spd != null) {
                // simple setter
                pd = spd;
            } else if (gpd != null) {
                // simple getter
                pd = gpd;
            }

            // Very special case to ensure that an IndexedPropertyDescriptor
            // doesn't contain less information than the enclosed
            // PropertyDescriptor. If it does, then recreate as a
            // PropertyDescriptor. See 4168833
            if (pd instanceof IndexedPropertyDescriptor) {
                ipd = (IndexedPropertyDescriptor) pd;
                if (ipd.getIndexedReadMethod() == null && ipd.getIndexedWriteMethod() == null) {
                    pd = new PropertyDescriptor(ipd);
                }
            }

            // Find the first property descriptor
            // which does not have getter and setter methods.
            // See regression bug 4984912.
            if ((pd == null) && (list.size() > 0)) {
                pd = (PropertyDescriptor) list.get(0);
            }

            if (pd != null) {
                properties.put(pd.getName(), pd);
            }
        }
    }

    // Handle regular pd merge
    private PropertyDescriptor mergePropertyDescriptor(PropertyDescriptor pd1,
                                                       PropertyDescriptor pd2) {
        if (pd1.getClass0().isAssignableFrom(pd2.getClass0())) {
            return new PropertyDescriptor(pd1, pd2);
        } else {
            return new PropertyDescriptor(pd2, pd1);
        }
    }

    // Handle regular ipd merge
    private PropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor ipd1,
                                                       IndexedPropertyDescriptor ipd2) {
        if (ipd1.getClass0().isAssignableFrom(ipd2.getClass0())) {
            return new IndexedPropertyDescriptor(ipd1, ipd2);
        } else {
            return new IndexedPropertyDescriptor(ipd2, ipd1);
        }
    }
    /**
     * Adds the property descriptor to the list store.
     */
    private void addPropertyDescriptor(PropertyDescriptor pd) {
        String propName = pd.getName();
        Collection<PropertyDescriptor> list = temporaryMap.get(propName);
        if (list == null) {
            list = new LinkedList<PropertyDescriptor>();
            temporaryMap.put(propName, list);
        }
        // replace existing property descriptor
        // only if we have types to resolve
        // in the context of this.beanClass
        Method read = pd.getReadMethod();
        Method write = pd.getWriteMethod();
        boolean cls = true;
        if (read != null) cls = cls && read.getGenericReturnType() instanceof Class;
        if (write != null) cls = cls && write.getGenericParameterTypes()[0] instanceof Class;
        if (pd instanceof IndexedPropertyDescriptor) {
            IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor) pd;
            Method readI = ipd.getIndexedReadMethod();
            Method writeI = ipd.getIndexedWriteMethod();
            if (readI != null) cls = cls && readI.getGenericReturnType() instanceof Class;
            if (writeI != null) cls = cls && writeI.getGenericParameterTypes()[1] instanceof Class;
            if (!cls) {
                pd = new IndexedPropertyDescriptor(ipd);
                pd.updateGenericsFor(beanDescriptor.getBeanClass());
            }
        } else if (!cls) {
            pd = new PropertyDescriptor(pd);
            pd.updateGenericsFor(beanDescriptor.getBeanClass());
        }
        list.add(pd);
    }

}
