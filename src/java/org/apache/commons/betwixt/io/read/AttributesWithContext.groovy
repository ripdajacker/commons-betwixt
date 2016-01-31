package org.apache.commons.betwixt.io.read

import org.apache.commons.betwixt.AttributeDescriptor

/**
 * A more advanced attributes class.
 */
interface AttributesWithContext {
    /**
     * Gets the value of the attribute.
     *
     * @param name the name of the attribute.
     * @param descriptor the descriptor.
     *
     * @return the resulting value.
     */
    String getValue(String name, AttributeDescriptor descriptor)
}