package dk.mehmedbasic.betwixtjson

import dk.mehmedbasic.betwixt.json.ast.JsonAstParser
import dk.mehmedbasic.betwixt.json.selectors.SelectorEngine
import org.w3c.css.sac.ElementSelector

/**
 * Tests jackson library parsing.
 */
class TestJsonAst extends GroovyTestCase {

    private static String source = """{
    "person #54": {
        "father #10": {
            "firstName": "John",
            "lastName": "Hancock",
            "birthday": 1956,
            "phones": [ { "@tag": "shit #42", "brand": "Huawei", "model": "P8 Lite"}]
        },
        "cat #1": {
            "name": "Jenny The Cat",
            "age": 6,
            "phones": [ "555-1234"]

        }
    }
}"""

    void testPropertySelection() {
        def dom = JsonAstParser.readString(source)
        def father = dom.selectByProperty("father")
        def firstName = dom.selectByProperty("firstName")
        def phones = dom.selectByProperty("phones")


        def parser = new SelectorEngine()
        def a = parser.parse("shit.name")
        def b = parser.parse(".phones.john")
        def c = parser.parse('.firstName:val("John")')
        def d = parser.parse('.john .hej')
        def e = parser.parse('.test')

        println()

    }
}
