package org.apache.commons.betwixt.io.id

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.io.IDGenerator

/**
 * A generator with a counter
 */
@TypeChecked
abstract class CounterIdGenerator implements IDGenerator {
    private int counter = 0

    @Override
    String nextId() {
        return convert(++counter)
    }

    abstract String convert(int counter)

    @Override
    String getLastId() {
        return null
    }
}
