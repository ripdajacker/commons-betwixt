package org.apache.commons.betwixt.io.read

/**
 * A pull-up of relevant methods to support reading from multiple formats
 */
interface AbstractReadContext {

    /**
     * Resolves any polymorphism in the element mapping.
     *
     * @param mapping <code>ElementMapping</code> describing the mapped element
     *
     * @return <code>null</code> if the type cannot be resolved
     * or if the current descriptor is not polymorphic
     */
    Class resolvePolymorphicType(ElementMapping mapping)
}