package dk.mehmedbasic.betwixt.equivavlency

import org.apache.commons.betwixt.io.BeanReader
import org.apache.commons.betwixt.io.BeanWriter

/**
 * Creates bean readers and writers
 */
interface ReaderWriterFactory {
    BeanReader createReader()

    BeanWriter createWriter()

}