package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.examples.rss.Channel

/**
 * Test the equivalency of the existing RSS reader functionality.
 */
@TypeChecked
class RssEquivalency extends EquivalencyTestCase {
    public void testRssAsJson() throws Exception {
        File file = new File(getTestFile("src/test/org/apache/commons/betwixt/examples/rss/rss-example.xml"));
        def comparison = createTest(file, [Channel])
        def execute = comparison.execute()
        assertEquals(execute.first, execute.second)
    }
}
