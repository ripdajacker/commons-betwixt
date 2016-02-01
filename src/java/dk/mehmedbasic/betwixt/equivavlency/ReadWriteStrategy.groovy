package dk.mehmedbasic.betwixt.equivavlency

/**
 * A strategy for hooking into some stuff.
 */
interface ReadWriteStrategy {
    /**
     * Serializes the object.
     *
     * @param object the object.
     *
     * @return the string representation of the object.
     */
    String serialize(Object object)

    /**
     * Deserialize the stream as an object.
     *
     * @param stream the stream to read.
     *
     * @return the resulting object.
     */
    Object deserializeStream(InputStream stream)

    /**
     * Deserializes a reader.
     *
     * @param reader the reader.
     *
     * @return the resulting object.
     */
    Object deserializeReader(Reader reader)
}