package dk.mehmedbasic.betwixt.json.au

import dk.mehmedbasic.betwixt.json.EquivalencyTestCase
import groovy.transform.TypeChecked

/**
 * Test the people database from the Aarhus University PURE database.
 */
@TypeChecked
class TestPeopleFromAu extends EquivalencyTestCase {
    public void testFirst() throws Exception {
        def file = new File("people_from_au.xml")

        def test = createTest(file, [Name, People, Person, Query], new File("people_from"))
        test.execute()
    }
}
