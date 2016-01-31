package org.apache.commons.betwixt.io

import groovy.transform.TypeChecked
import org.xml.sax.Attributes

/**
 * A default implementation of attributes.
 */
@TypeChecked
class EmptyAttributes implements Attributes {
    List<String> names = []
    List<String> values = []

    @Override
    int getLength() {
        return names.size()
    }

    @Override
    String getURI(int index) {
        return null
    }

    @Override
    String getLocalName(int index) {
        return names[index]
    }

    @Override
    String getQName(int index) {
        return names[index]
    }

    @Override
    String getType(int index) {
        return "CDATA"
    }

    @Override
    String getValue(int index) {
        return values.get(index)
    }

    @Override
    int getIndex(String uri, String localName) {
        return names.indexOf(localName)
    }

    @Override
    int getIndex(String qName) {
        return names.indexOf(qName)
    }

    @Override
    String getType(String uri, String localName) {
        def index = getIndex(uri, localName)
        if (index >= 0) {
            return getType(index)
        }
        return null
    }

    @Override
    String getType(String qName) {
        def index = getIndex(qName)
        if (index >= 0) {
            return getType(index)
        }
        return null
    }

    @Override
    String getValue(String uri, String localName) {
        def index = getIndex(uri, localName)
        if (index >= 0) {
            return getValue(names[index])
        }
        return null
    }

    @Override
    String getValue(String qName) {
        def index = getIndex(qName)
        if (index >= 0) {
            return values[index]
        }
        return null
    }

    void addValue(String qualifiedName, String value) {
        names << qualifiedName
        values << value
    }
}

