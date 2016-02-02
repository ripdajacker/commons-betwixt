package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.io.BeanWriter

/**
 * The json sax writer
 */
@TypeChecked
class JsonSaxWriter extends BeanWriter {
    JsonSaxWriter(Writer writer) {
        super(new EmptyWriter())
        beanWriteListener = new JsonBeanWriteEventListener(writer)
    }

    static final class EmptyWriter extends Writer {
        EmptyWriter() {
        }

        @Override
        void write(int c) throws IOException {
        }

        @Override
        void write(char[] cbuf) throws IOException {
        }

        @Override
        void write(String str, int off, int len) throws IOException {
        }

        @Override
        Writer append(CharSequence csq) throws IOException {
            return this
        }

        @Override
        Writer append(CharSequence csq, int start, int end) throws IOException {
            return this
        }

        @Override
        Writer append(char c) throws IOException {
            return this
        }

        @Override
        void write(String str) throws IOException {
        }

        @Override
        void write(char[] cbuf, int off, int len) throws IOException {
        }

        @Override
        void flush() throws IOException {
        }

        @Override
        void close() throws IOException {
        }
    }
}
