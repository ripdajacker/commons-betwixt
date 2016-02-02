package dk.mehmedbasic.betwixt.equivavlency

import groovy.transform.TypeChecked

/**
 * A json writing strategy
 */
@TypeChecked
final class XmlStrategy implements ReadWriteStrategy {
    private ReaderWriterFactory factory

    XmlStrategy(ReaderWriterFactory factory) {
        this.factory = factory
    }

    @Override
    String serialize(Object object) {
        def buffer = new StringWriter()

        def writer = factory.createWriter()
        writer.writer = buffer
        writer.write(object)

        return buffer.toString()
    }

    @Override
    Object deserializeStream(InputStream stream) {
        return deserializeReader(new InputStreamReader(stream))
    }

    @Override
    Object deserializeReader(Reader streamReader) {
        def reader = factory.createReader()
        return reader.parse(streamReader)
    }
}
