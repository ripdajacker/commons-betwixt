package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked

/**
 * TODO - someone remind me to document this class 
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
