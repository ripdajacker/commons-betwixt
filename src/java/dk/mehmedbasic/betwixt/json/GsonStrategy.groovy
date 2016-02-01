package dk.mehmedbasic.betwixt.json

import com.google.gson.stream.JsonWriter
import groovy.transform.TypeChecked

/**
 * A JSON writer that uses Googles Gson writer.
 */
@TypeChecked
class GsonStrategy implements JsonWriterStrategy {
    private JsonWriter json
    String deferredName

    GsonStrategy(Writer out) {
        json = new JsonWriter(out)
    }

    @Override
    void name(String name) {
        deferredName = name
    }

    @Override
    void beginArray() {
        writeDeferred()
        json.beginArray()
    }

    private void writeDeferred() {
        if (deferredName != null) {
            json.name(deferredName)
            deferredName = null
        }
    }

    @Override
    void endArray() {
        json.endArray()
    }

    @Override
    void beginObject() {
        writeDeferred()
        json.beginObject()
    }

    @Override
    void endObject() {
        json.endObject()
    }

    @Override
    void valueGeneric(Object value) {
        if (value == null) {
            writeDeferred()
            json.nullValue()
            return
        }
        Class propertyType = value.class
        if (propertyType in [Boolean, boolean]) {
            writeDeferred()
            json.value(value as boolean)
        } else if (propertyType in [Integer, int]) {
            writeDeferred()
            json.value(value as int)
        } else if (propertyType in [Double, double]) {
            def doubleValue = value as double
            if (doubleValue.infinite) {
                doubleValue = Double.NaN
            }
            if (doubleValue.naN) {
                clearDeferred()
            } else {
                writeDeferred()
                json.value(doubleValue)
            }
        } else if (propertyType in [Float, float]) {
            def floatValue = value as float
            if (floatValue.infinite) {
                floatValue = Float.NaN
            }
            if (floatValue.naN) {
                clearDeferred()
            } else {
                writeDeferred()
                json.value(floatValue)
            }
        } else if (propertyType in [Long, long]) {
            writeDeferred()
            json.value(value as long)
        } else {
            writeDeferred()
            json.value(value as String)
        }
    }

    private void clearDeferred() {
        deferredName = null
    }


    @Override
    void close() {
        json.close()
    }
}
