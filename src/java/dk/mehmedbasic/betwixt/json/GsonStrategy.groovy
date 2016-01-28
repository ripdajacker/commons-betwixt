package dk.mehmedbasic.betwixt.json

import com.google.gson.stream.JsonWriter
import groovy.transform.TypeChecked

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class GsonStrategy implements JsonWriterStrategy {
    private JsonWriter json

    GsonStrategy(Writer out) {
        json = new JsonWriter(out)
    }

    @Override
    void name(String name) {
        json.name(name)
    }

    @Override
    void beginArray() {
        json.beginArray()
    }

    @Override
    void endArray() {
        json.endArray()
    }

    @Override
    void beginObject() {
        json.beginObject()
    }

    @Override
    void endObject() {
        json.endObject()
    }

    @Override
    void valueGeneric(Object value) {
        if (value == null) {
            json.nullValue()
            return
        }
        Class propertyType = value.class
        if (propertyType in [Boolean, boolean]) {
            json.value(value as boolean)
        } else if (propertyType in [Integer, int]) {
            json.value(value as int)
        } else if (propertyType in [Double, double]) {
            def doubleValue = value as double
            if (doubleValue.infinite) {
                doubleValue = Double.NaN
            }
            json.value(doubleValue)
        } else if (propertyType in [Float, float]) {
            def floatValue = value as float
            if (floatValue.infinite) {
                floatValue = Float.NaN
            }

            json.value(floatValue)
        } else if (propertyType in [Long, long]) {
            json.value(value as long)
        } else {
            json.value(value as String)
        }
    }


    @Override
    void close() {
        json.close()
    }
}
