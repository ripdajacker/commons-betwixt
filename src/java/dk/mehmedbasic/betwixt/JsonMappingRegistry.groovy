package dk.mehmedbasic.betwixt

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.XMLBeanInfo

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class JsonMappingRegistry {

    private Map<Class, Tuple2<XMLBeanInfo, BetwixtJsonDescriptor>> mapping = [:]
    private Map<String, Class> nameToClass = [:]

    boolean isRegistered(Class clazz) {
        return mapping.get(clazz) != null
    }

    void register(Class beanClass, XMLBeanInfo xmlBeanInfo) {
        if (isRegistered(beanClass)) {
            return
        }

        def descriptor = new BetwixtJsonDescriptor(xmlBeanInfo.elementDescriptor)
        mapping.put(beanClass, new Tuple2<XMLBeanInfo, BetwixtJsonDescriptor>(xmlBeanInfo, descriptor))

        nameToClass.put(descriptor.name, beanClass)
    }

    BetwixtJsonDescriptor lookupClass(Class beanClass) {
        if (mapping.get(beanClass)) {
            return mapping.get(beanClass).second
        }
        return null
    }

    BetwixtJsonDescriptor lookupName(String descriptorName) {
        def beanClass = nameToClass.get(descriptorName)
        if (beanClass) {
            return lookupClass(beanClass)
        }
        return null
    }
}
