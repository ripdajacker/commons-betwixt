package dk.mehmedbasic.betwixt.equivavlency

import dk.mehmedbasic.betwixt.json.BeanReaderJsonAdapter
import dk.mehmedbasic.betwixt.json.JsonBeanWriteEventListener
import dk.mehmedbasic.betwixt.json.JsonSaxWriter
import groovy.transform.TypeChecked

/**
 * A json writing strategy
 */
@TypeChecked
final class JsonStrategy implements ReadWriteStrategy {
    private ReaderWriterFactory factory

    JsonStrategy(ReaderWriterFactory factory) {
        this.factory = factory
    }

    @Override
    String serialize(Object object) {
        def writer = factory.createWriter()

        def buffer = new StringWriter()
        writer.writer = new JsonSaxWriter.EmptyWriter()
        writer.beanWriteListener = new JsonBeanWriteEventListener(buffer)

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
        def adapter = new BeanReaderJsonAdapter(reader)
        return adapter.parse(streamReader)
    }
}
