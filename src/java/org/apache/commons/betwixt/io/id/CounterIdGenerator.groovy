package org.apache.commons.betwixt.io.id

import groovy.transform.TypeChecked

/**
 * A generator with a counter
 */
@TypeChecked
abstract class CounterIdGenerator extends AbstractIDGenerator {
    private int counter = 0

    @Override
    protected String nextIdImpl() {
        return convert(++counter)
    }

    abstract String convert(int counter)
}
