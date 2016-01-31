package org.apache.commons.betwixt.io

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.ElementDescriptor

/**
 * A mutable write context
 */
@TypeChecked
class MutableWriteContext extends WriteContext {
    ElementDescriptor currentDescriptor
}
