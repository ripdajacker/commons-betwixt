/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.betwixt.io

import groovy.util.logging.Commons
import org.xml.sax.Attributes
import org.xml.sax.ContentHandler

/**
 * The SAXBeanwriter will send events to a ContentHandler
 *
 * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 */
@Commons
public class SAXBeanWriter extends AbstractBeanWriter {

    /** Where the output goes */
    private ContentHandler contentHandler;

    /** Should document events (ie. start and end) be called? */
    boolean callDocumentEvents = true;

    /**
     * <p> Constructor sets writer used for output.</p>
     *
     * @param contentHandler feed events to this content handler
     */
    public SAXBeanWriter(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    // Expression methods
    //-------------------------------------------------------------------------

    // Replaced by new API

    // New API
    // -------------------------------------------------------------------------

    /**
     * Writes the start tag for an element.
     *
     * @param uri the element's namespace uri
     * @param localName the element's local name
     * @param qName the element's qualified name
     * @param attributes the element's attributes
     * @since 0.5
     */
    protected void startElement(WriteContext context, String uri, String localName, String qName, Attributes attributes) {
        contentHandler.startElement(uri, localName, qName, attributes);
    }

    /**
     * Writes the end tag for an element
     *
     * @param uri the element's namespace uri
     * @param localName the element's local name
     * @param qName the element's qualified name
     * @since 0.5
     */
    protected void endElement(WriteContext context, String uri, String localName, String qName) {
        contentHandler.endElement(uri, localName, qName);
    }

    /**
     * Express body text
     * @param text the element body text
     * @since 0.5
     */
    protected void bodyText(WriteContext context, String text) {
        char[] body = text.toCharArray();
        contentHandler.characters(body, 0, body.length);
    }

    /**
     * This will announce the start of the document to the content handler.
     *
     * @see org.apache.commons.betwixt.io.AbstractBeanWriter#end()
     */
    public void start() {
        if (callDocumentEvents) {
            contentHandler.startDocument();
        }
    }

    /**
     * This method will announce the end of the document to
     * the contenthandler.
     *
     * @see org.apache.commons.betwixt.io.AbstractBeanWriter#start()
     */
    public void end() {
        if (callDocumentEvents) {
            contentHandler.endDocument();
        }
    }
}
