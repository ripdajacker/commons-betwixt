package dk.mehmedbasic.betwixt.json.au

import groovy.transform.ToString
import groovy.transform.TypeChecked

/**
 * A person
 */
@TypeChecked
@ToString()
class Person {
    String uuid
    Name name
    String external

    List<Query> queries = []

    void addQuery(Query string) {
        queries << string
    }
}
