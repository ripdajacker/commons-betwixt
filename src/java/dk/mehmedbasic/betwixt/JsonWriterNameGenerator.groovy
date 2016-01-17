package dk.mehmedbasic.betwixt

import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import org.apache.commons.betwixt.io.IDGenerator

import java.util.regex.Pattern

/**
 * The name util for JSON writing/reading.
 *
 * It handles writing and reading of inline stuff and the like.
 */
@TypeChecked
@PackageScope
class JsonWriterNameGenerator {
      private IDGenerator idGenerator = new HexIdGenerator()

    /**
     * Gets the next id.
     *
     * @return the next id.
     */
    String nextId() {
        return idGenerator.nextId()
    }

    /**
     * Creates a JSON name from the given descriptor, the name of it in the context and the ID of the bean.
     *
     * @param descriptor the descriptor.
     * @param name the name of the bean in the given context.
     * @param id the ID of the bean.
     *
     * @return the generated name.
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    String nameWithId(BetwixtJsonDescriptor descriptor, String name, String id) {
        if (name.isEmpty()) {
            name = descriptor.name
        }
        return "${name} #${id}"

    }
    /**
     * Creates the name without ID for use in @ref references.
     *
     * @param name the name.
     * @param descriptor the descriptor.
     *
     * @return the resulting name.
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    String nameWithoutId(String name, BetwixtJsonDescriptor descriptor) {
        if (!name || name.trim().isEmpty()) {
            return descriptor.name
        } else {
            return name
        }
    }
    /**
     * Inline a value.
     *
     * @param id the id.
     * @param value the value.
     *
     * @return the combined string.
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    String inlineValue(String id, String value) {
        return "@id:${id} $value"
    }

    /**
     * Inline a reference.
     *
     * @param id the id to reference.
     *
     * @return the combined string.
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    String inlineReference(String id) {
        return "@ref:$id"
    }

}
