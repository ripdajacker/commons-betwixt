package dk.mehmedbasic.jsontransformer

import dk.mehmedbasic.betwixt.json.BeanReaderJsonAdapter
import dk.mehmedbasic.betwixt.json.JsonBeanWriteEventListener
import groovy.transform.TypeChecked
import junit.framework.TestCase
import org.apache.commons.betwixt.io.BeanReader
import org.apache.commons.betwixt.io.BeanWriter

/**
 * A test of the versioning capabilities of the test classes.
 */
@TypeChecked
class TestLegacyVersion extends TestCase {
    File file1
    File file2
    File file3
    File file4
    File file5

    TransformingBean1 bean1
    TransformingBean2 bean2
    TransformingBean3 bean3
    TransformingBean4 bean4
    TransformingBean5 bean5

    TestLegacyVersion() {
        super("TransformingTests")
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()


        bean1 = new TransformingBean1()
        bean1.attribute = "attribute value"
        bean1.element = "element value"

        bean2 = new TransformingBean2()
        bean2.renamedAttribute = "attribute value"
        bean2.element = "element value"

        bean3 = new TransformingBean3()
        bean3.renamedAttribute = "attribute value"
        bean3.element = "element value"

        bean4 = new TransformingBean4()
        bean4.renamedAttribute = "attribute value"
        bean4.element = "element value"

        bean5 = new TransformingBean5()
        bean5.renamedAttribute = "attribute value"
        bean5.element = "element value"

        file1 = write("version1", TransformingBean1, bean1)
        file2 = write("version2", TransformingBean2, bean2)
        file3 = write("version3", TransformingBean3, bean3)
        file4 = write("version4", TransformingBean4, bean4)
        file5 = write("version5", TransformingBean5, bean5)
    }

    /**
     * The pre-written bean v1 should be equal to the LegacyHandlingBean
     */
    void testLegacyBeanVersioning1() {
        def that = readLegacy(file1)
        assert that.renamedAttribute == bean1.attribute
        assert that.element == bean1.element
    }

    /**
     * The pre-written bean v2 should be equal to the LegacyHandlingBean
     */
    void testLegacyBeanVersioning2() {
        def that = readLegacy(file2)
        assert that.renamedAttribute == bean2.renamedAttribute
        assert that.element == bean2.element
    }

    /**
     * The pre-written bean v3 should be equal to the LegacyHandlingBean
     */
    void testLegacyBeanVersioning3() {
        def that = readLegacy(file3)
        assert that.renamedAttribute == bean3.renamedAttribute
        assert that.element == bean3.element
    }

    /**
     * The pre-written bean v4 should be equal to the LegacyHandlingBean
     */
    void testLegacyBeanVersioning4() {
        def that = readLegacy(file4)
        assert that.renamedAttribute == bean4.renamedAttribute
        assert that.element == bean4.element
    }

    /**
     * The pre-written bean v5 should be equal to the LegacyHandlingBean
     */
    void testLegacyBeanVersioning5() {
        def that = readLegacy(file5)
        assert that.renamedAttribute == bean5.renamedAttribute
        assert that.element == bean5.element
    }


    static LegacyHandlingBean readLegacy(File file) {
        def reader = new BeanReader()
        reader.registerBeanClass(LegacyHandlingBean)

        def adapter = new BeanReaderJsonAdapter(reader)

        adapter.parse(new FileReader(file)) as LegacyHandlingBean
    }

    static File write(String name, Class type, Object bean) {
        def file = createTestFile(name)

        def writer = new BeanWriter(new ByteArrayOutputStream())
        writer.beanWriteListener = new JsonBeanWriteEventListener(new FileWriter(file))
        writer.introspector.introspect(type)

        writer.write(bean)
        writer.close()
        file
    }

    static File createTestFile(String name) {
        File.createTempFile("transforming-test-$name", ".json")
    }
}
