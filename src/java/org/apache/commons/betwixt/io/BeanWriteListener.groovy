package org.apache.commons.betwixt.io

import org.xml.sax.Attributes

/**
 * A write listener
 */
interface BeanWriteListener {
    void startElement(MutableWriteContext writeContext, String qualifiedName, Attributes attributes)

    void bodyText(WriteContext context, String text)

    void endElement(MutableWriteContext writeContext, String qualifiedName)
}