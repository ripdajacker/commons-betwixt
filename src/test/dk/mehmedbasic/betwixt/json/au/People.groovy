package dk.mehmedbasic.betwixt.json.au

import groovy.transform.TypeChecked

/**
 * The people holder
 */
@TypeChecked
class People {
    LinkedList<Person> people = []

    void addPerson(Person person) {
        people << person
    }
}
