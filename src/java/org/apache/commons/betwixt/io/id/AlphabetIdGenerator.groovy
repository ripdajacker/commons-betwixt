package org.apache.commons.betwixt.io.id

import groovy.transform.TypeChecked

/**
 * An excel columns-like id generator.
 */
@TypeChecked
class AlphabetIdGenerator extends CounterIdGenerator {
    @Override
    String convert(int counter) {
        return toName(counter)
    }

    private static String toName(int number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            number--

            int character = 'A' as char
            int newCharacter = character + (number % 26)
            builder.append(newCharacter as char);

            number /= 26;
        }
        return builder.reverse().toString().toLowerCase();
    }
}
