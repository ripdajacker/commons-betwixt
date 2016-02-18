package dk.mehmedbasic.jsontransformer

import dk.mehmedbasic.betwixt.json.BeanReaderJsonAdapter
import dk.mehmedbasic.betwixt.json.JsonBeanWriteEventListener
import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonType
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import dk.mehmedbasic.jsonast.transform.VersionControl
import dk.mehmedbasic.jsonast.transform.VersionDefinition
import junit.framework.TestCase
import org.apache.commons.betwixt.io.BeanReader
import org.apache.commons.betwixt.io.BeanWriter
import org.codehaus.jackson.map.ObjectMapper

/**
 * A test of the json-transformer project combined with the betwixt json output.
 */
class TestTransformations extends TestCase {
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


    VersionControl versionControl


    TestTransformations() {
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

        versionControl = new VersionControl()
        versionControl.definitions << new VersionDefinition(2, "v1 to v2", {
            document.transform("transformingBean")
                    .renameChild("attribute", "renamedAttribute")
                    .apply()
        })

        versionControl.definitions << new VersionDefinition(3, "v2 to v3", {
            document.transform("transformingBean")
                    .add("nesting", JsonType.Object)
                    .apply()

            document.transform("element")
                    .moveTo("nesting")
                    .apply()
        })

        versionControl.definitions << new VersionDefinition(4, "v3 to v4", {
            document.transform("nesting > element")
                    .renameTo("element2")
                    .moveTo("transformingBean")
                    .apply()

            def value = document.selectSingle("element2 > .string").get()
            document.transform("transformingBean")
                    .deleteChild("nesting")
                    .add("element", value)
                    .deleteChild("element2")
                    .apply()
        })
        versionControl.definitions << new VersionDefinition(5, "v4 to v5", {
            // Do nothing
        })
    }

    void testVersion2() {
        TransformingBean2 bean = readBean(migrate(file1, 2), TransformingBean2)

        assert bean2.element == bean.element
        assert bean2.renamedAttribute == bean.renamedAttribute
    }


    void testVersion3() {
        TransformingBean3 bean = readBean(migrate(file1, 3), TransformingBean3)

        assert bean3.element == bean.element
        assert bean3.renamedAttribute == bean.renamedAttribute
    }

    void testVersion4() {
        def file = migrate(file1, 4)
        TransformingBean4 bean = readBean(file, TransformingBean4)

        assert bean4.element == bean.element
        assert bean4.renamedAttribute == bean.renamedAttribute
    }

    void testVersion5() {
        TransformingBean4 bean = readBean(migrate(file1, 5), TransformingBean4)

        assert bean5.element == bean.element
        assert bean5.renamedAttribute == bean.renamedAttribute
    }

    File migrate(File input, int version) {
        def document = JsonDocument.parse(new FileInputStream(input))
        versionControl.apply(document, version)

        def node = JacksonConverter.asJacksonNode(document)

        def file = createTestFile("output.$version")
        new ObjectMapper().writeValue(file, node)

        file
    }


    static <T> T readBean(File file, Class<T> type) {
        def reader = new BeanReader()
        reader.registerBeanClass(type)

        def adapter = new BeanReaderJsonAdapter(reader)

        adapter.parse(new FileReader(file)) as T
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
