package dk.mehmedbasic.betwixt.json

import dk.mehmedbasic.betwixt.equivavlency.EquivalencyComparison
import dk.mehmedbasic.betwixt.equivavlency.JsonStrategy
import dk.mehmedbasic.betwixt.equivavlency.ReaderWriterFactory
import dk.mehmedbasic.betwixt.equivavlency.XmlStrategy
import groovy.transform.TypeChecked
import org.apache.commons.betwixt.AbstractTestCase
import org.apache.commons.betwixt.io.BeanReader
import org.apache.commons.betwixt.io.BeanWriter

/**
 * A common testcase for equivalency.
 */
@TypeChecked
abstract class EquivalencyTestCase extends AbstractTestCase {

    EquivalencyTestCase() {
        super(getClass().simpleName)
    }

    protected static EquivalencyComparison createTest(File file, List<Class> mappings) {
        new EquivalencyComparison(new FileInputStream(file), json(mappings), xml(mappings))
    }

    private static XmlStrategy xml(List<Class> mappings) {
        new XmlStrategy(new Factory(mappings))
    }

    private static JsonStrategy json(List<Class> mappings) {
        new JsonStrategy(new Factory(mappings))
    }


    static final class Factory implements ReaderWriterFactory {
        List<Class> mappings

        Factory(List<Class> mappings) {
            this.mappings = mappings
        }

        @Override
        BeanReader createReader() {
            def reader = new BeanReader()
            for (Class classToRegister : mappings) {
                reader.registerBeanClass(classToRegister)
            }
            return reader
        }

        @Override
        BeanWriter createWriter() {
            def writer = new BeanWriter()
            for (Class classToRegister : mappings) {
                writer.introspector.introspect(classToRegister)
            }
            return writer
        }
    }
}
