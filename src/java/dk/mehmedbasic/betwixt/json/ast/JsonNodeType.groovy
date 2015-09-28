package dk.mehmedbasic.betwixt.json.ast

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * A Json node type
 */
@PackageScope
@CompileStatic
enum JsonNodeType {
    ARRAY,
    BOOLEAN,
    NULL,
    NUMBER,
    OBJECT,
    STRING

    boolean isContainer() {
        this in [ARRAY, OBJECT]
    }

    boolean isCollection() {
        this == ARRAY
    }
}