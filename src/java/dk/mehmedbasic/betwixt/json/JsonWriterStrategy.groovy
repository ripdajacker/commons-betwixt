package dk.mehmedbasic.betwixt.json
/**
 * TODO - someone remind me to document this class 
 */
interface JsonWriterStrategy {

    void name(String name)

    void beginArray()

    void endArray()

    void beginObject()

    void endObject()

    void valueGeneric(Object value)

    void close()

}