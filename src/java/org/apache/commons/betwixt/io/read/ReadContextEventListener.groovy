package org.apache.commons.betwixt.io.read

import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.expression.Updater

/**
 * Events on read
 */
interface ReadContextEventListener {
    void descriptorPushed(ReadContext readContext, ElementDescriptor descriptor)

    void descriptorPopped(ReadContext readContext, ElementDescriptor descriptor)

    void beanPushed(ReadContext readContext, Object bean)

    void beanPopped(ReadContext readContext, Object bean)


    void elementClassPushed(ReadContext readContext, Class clazz)

    void elementPushed(ReadContext readContext, String elementName)

    void elementPopped(ReadContext readContext, String elementName)

    void updaterPushed(ReadContext readContext, Updater updater)

    void updaterPopped(ReadContext readContext, Updater updater)

    void actionMappingPushed(ReadContext readContext, MappingAction action)

    void actionMappingPopped(ReadContext readContext, MappingAction action)


}