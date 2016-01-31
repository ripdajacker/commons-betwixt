package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked

/**
 * Write options for JSON
 */
@TypeChecked
final class JsonWriteOptions {
    boolean collectionDescriptor
    boolean insideCollection

    JsonWriteOptions(boolean collectionDescriptor, boolean insideCollection) {
        this.collectionDescriptor = collectionDescriptor
        this.insideCollection = insideCollection
    }
}
