package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.io.WriteContext

/**
 * A mutalbe write context
 */
@TypeChecked
class MutableWriteContext extends WriteContext {
    ElementDescriptor currentDescriptor
}
