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
class JsonNameUtil {
    private static Pattern PATTERN_NAME_AND_ID = Pattern.compile("([A-Za-z0-9]+) #([0-9a-f]+)")
    private static Pattern PATTERN_INLINE_VALUE = Pattern.compile("@id:([0-9a-f]+) (.+)")
    private static Pattern PATTERN_INLINE_REFERENCE = Pattern.compile("@ref:([0-9a-f]+)")

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

    /**
     * Parse name and id.
     *
     * @param jsonName the source.
     *
     * @return a tuple of name and id.
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    Tuple2<String, String> parseNameAndId(String jsonName) {
        def matcher = PATTERN_NAME_AND_ID.matcher(jsonName)
        if (matcher.find()) {
            def group1 = matcher.group(1)
            def group2 = matcher.group(2)

            return new Tuple2<String, String>(group1, group2)
        }
        return null
    }
    /**
     * Parse name and id.
     *
     * @param jsonValue the source.
     *
     * @return a tuple of name and id.
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    Tuple2<String, String> parseInlinedValue(String jsonValue) {
        def matcher = PATTERN_INLINE_VALUE.matcher(jsonValue)
        if (matcher.find()) {
            def group1 = matcher.group(1)
            def group2 = matcher.group(2)

            return new Tuple2<String, String>(group1, group2)
        }
        return null
    }
    /**
     * Parses the reference in the json string.
     *
     * @param jsonValue the value.
     *
     * @return the reference.
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    String parseReference(String jsonValue) {
        def matcher = PATTERN_INLINE_REFERENCE.matcher(jsonValue)
        if (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }
}
