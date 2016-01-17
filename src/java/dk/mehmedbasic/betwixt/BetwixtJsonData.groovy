package dk.mehmedbasic.betwixt

import groovy.transform.TypeChecked

import java.util.regex.Pattern

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class BetwixtJsonData {
    private static Pattern PATTERN_NAME_AND_ID = Pattern.compile("([A-Za-z0-9]+) #([0-9a-f]+)")
    private static Pattern PATTERN_INLINE_VALUE = Pattern.compile("@id:([0-9a-f]+) (.+)")
    private static Pattern PATTERN_INLINE_REFERENCE = Pattern.compile("@ref:([0-9a-f]+)")

    String propertyName
    String id
    String inlineValue

    BetwixtJsonData(String jsonName) {
        def type = determineType(jsonName)
        switch (type) {
            case NameType.INLINE_REFERENCE:
                id = parseReference(jsonName)
                break
            case NameType.INLINE_VALUE:
                def value = parseInlinedValue(jsonName)
                id = value.first
                inlineValue = value.second
                break
            case NameType.NAME_AND_ID:
                def value = parseNameAndId(jsonName)
                id = value.first
                propertyName = value.second
                break
            case NameType.NAME_WITHOUT_ID:
                propertyName = jsonName
        }
    }

    /**
     * Parse name and id.
     *
     * @param jsonName the source.
     *
     * @return a tuple of name and id.
     */
    private static Tuple2<String, String> parseNameAndId(String jsonName) {
        def matcher = PATTERN_NAME_AND_ID.matcher(jsonName)
        if (matcher.find()) {
            def group1 = matcher.group(1)
            def group2 = matcher.group(2)

            return new Tuple2<String, String>(group2, group1)
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
    private static Tuple2<String, String> parseInlinedValue(String jsonValue) {
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
    private static String parseReference(String jsonValue) {
        def matcher = PATTERN_INLINE_REFERENCE.matcher(jsonValue)
        if (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }

    private static NameType determineType(String jsonName) {
        if (parseReference(jsonName) != null) {
            return NameType.INLINE_REFERENCE
        }
        if (parseNameAndId(jsonName) != null) {
            return NameType.NAME_AND_ID
        }
        if (parseInlinedValue(jsonName)) {
            return NameType.INLINE_VALUE
        }
        return NameType.NAME_WITHOUT_ID
    }
}
