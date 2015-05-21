package org.apache.commons.betwixt.io

import com.google.gson.stream.JsonWriter
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 * created 5/13/2015.
 */
class JsonTest extends DefaultHandler {

    def stream = new ByteArrayOutputStream()
    private JsonWriter json = new JsonWriter(new OutputStreamWriter(stream));

    @Override
    void startDocument() throws SAXException {

        json.beginObject()

    }

    @Override
    void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        json.name(qName)
        json.beginObject()

    }

    @Override
    void endElement(String uri, String localName, String qName) throws SAXException {
        json.endObject()

    }

    @Override
    void endDocument() throws SAXException {
        json.endObject()
        json.close()
    }


    public String getJson() {
        return stream.toString()
    }
}
