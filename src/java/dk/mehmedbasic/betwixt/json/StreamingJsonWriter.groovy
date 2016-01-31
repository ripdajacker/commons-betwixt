package dk.mehmedbasic.betwixt.json
/**
 * A streaming json writer.
 */
class StreamingJsonWriter implements JsonWriterStrategy {
    @Delegate
    private JsonWriterStrategy json

    StreamingJsonWriter(JsonWriterStrategy json) {
        this.json = json
    }

    void startElement(String name, JsonWriteOptions options) {
        if (name && !options.insideCollection) {
            json.name(name)
        }
        if (options.collectionDescriptor) {
            json.beginArray()
        } else {
            json.beginObject()
        }
    }

    void endElement(JsonWriteOptions options) {
        if (options.collectionDescriptor) {
            json.endArray()
        } else {
            json.endObject()
        }
    }

    void close() {
        json.close()
    }
}