/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/SAXBeanWriter.java,v 1.8 2003/02/17 19:41:56 rdonkin Exp $
 * $Revision: 1.8 $
 * $Date: 2003/02/17 19:41:56 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * $Id: SAXBeanWriter.java,v 1.8 2003/02/17 19:41:56 rdonkin Exp $
 */
package org.apache.commons.betwixt.io;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

// FIX ME
// At the moment, namespaces are NOT supported!

/**
 * The SAXBeanwriter will send events to a ContentHandler
 * 
 * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: SAXBeanWriter.java,v 1.8 2003/02/17 19:41:56 rdonkin Exp $ 
 */
public class SAXBeanWriter extends AbstractBeanWriter {

    /** Where the output goes */
    private ContentHandler contentHandler;    
    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( SAXBeanWriter.class );
    
    /**
     * Place holder for elements that are started.
     */
    private Stack elementStack;
    /** Current element's attributes. */
    private AttributesImpl attributes;
    /** Is there a element currently waiting to be written out? */
    private boolean elementWaiting = false;
    /** Should document events (ie. start and end) be called? */
    private boolean callDocumentEvents = true;
    
    /**
     * <p> Constructor sets writer used for output.</p>
     *
     * @param contentHandler feed events to this content handler
     */
    public SAXBeanWriter(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    /** 
     * Should document events (ie start and end) be called?
     *
     * @return true if this SAXWriter should call start and end of the content handler
     */
    public boolean getCallDocumentEvents() {
        return callDocumentEvents;
    }
    
    /**
     * Sets whether the document events (ie start and end) should be called.
     *
     * @param callDocumentEvents should document events be called
     */
    public void setCallDocumentEvents(boolean callDocumentEvents) {
        this.callDocumentEvents = callDocumentEvents;
    }

    /**
     * <p> Set the log implementation used. </p>
     *
     * @return <code>Log</code> implementation that this class logs to
     */ 
    public Log getLog() {
        return log;
    }

    /**
     * <p> Set the log implementation used. </p>
     *
     * @param log <code>Log</code> implementation to use
     */ 
    public void setLog(Log log) {
        this.log = log;
    }
    
        
    // Expression methods
    //-------------------------------------------------------------------------    
    
    /** 
     * Express an element tag start using given qualified name 
     *
     * @param qualifiedName the fully qualified element name
     * @throws SAXException if the <code>ContentHandler</code> has a problem
     * @deprecated use {@link #expressElementStart(String, String, String)}
     */
    protected void expressElementStart(String qualifiedName) throws SAXException  {
        expressElementStart("", qualifiedName, qualifiedName);
    }
    
    /** 
     * Express an element tag start using given qualified name.
     *
     * @param uri the namespace uri 
     * @param localName the local name for this element
     * @param qualifiedName the qualified name of the element to be expressed
     * @throws SAXException if an SAX problem occurs during writing 
     */
    protected void expressElementStart(String uri, String localName, String qualifiedName) 
                                        throws SAXException {
        if (elementStack == null) {
            elementStack = new Stack();
        }
        if (elementWaiting) {
            sendElementStart();
        }
        attributes = new AttributesImpl();
        elementStack.push(new ElementName(uri, localName, qualifiedName));
        elementWaiting = true;
    }
    
    /** Element end */
    protected void expressTagClose() {
        // using this could probably make life easier
        // but i only know that i needed it after i'd written the rest
    }
    
    /** 
     * Express an element end tag
     *
     * @param qualifiedName the fully qualified name of the element
     * @throws SAXException if the <code>ContentHandler</code> has a problem
     * @deprecated use {@link #expressElementEnd(String, String, String)}
     */
    protected void expressElementEnd(String qualifiedName) throws SAXException  {
        expressElementEnd("", qualifiedName, qualifiedName);
    }    
    
    /** 
     * Express an element tag start using given qualified name.
     *
     * @param uri the namespace uri 
     * @param localName the local name for this element
     * @param qualifiedName the qualified name of the element to be expressed
     * @throws SAXException if an SAX problem occurs during writing 
     */
    protected void expressElementEnd(String uri, String localName, String qualifiedName) 
                                        throws SAXException {
        if (elementWaiting) {
            elementWaiting = false;
            sendElementStart();
        }
        
        contentHandler.endElement(uri, localName, qualifiedName);
    }
    
    /** 
     * Express an empty element end 
     * @throws SAXException if the <code>ContentHandler</code> has a problem
     */
    protected void expressElementEnd() throws SAXException  {
        // last element name must be correct since there haven't been any tag in between
        ElementName lastElement = (ElementName) elementStack.peek();
        contentHandler.endElement(
                                lastElement.getUri(), 
                                lastElement.getLocalName() ,
                                lastElement.getQName());
    }

    /** 
     * Express body text 
     * @param text the element body text 
     * @throws SAXException if the <code>ContentHandler</code> has a problem
     */
    protected void expressBodyText(String text) throws SAXException  {
        // FIX ME
        // CHECK UNICODE->CHAR CONVERSION!
        // THIS WILL QUITE POSSIBLY BREAK FOR NON-ROMAN
        if (elementWaiting) {
            elementWaiting = false;
            sendElementStart();
        }
        char[] body = text.toCharArray();
        contentHandler.characters(body, 0, body.length);
    }
    
    /** 
     * Express an attribute 
     * @param qualifiedName the fully qualified attribute name
     * @param value the attribute value
     * @deprecated use {@link #expressAttribute(String, String, String, String)}
     */
    protected void expressAttribute(
                                String qualifiedName, 
                                String value) {
        expressAttribute("", qualifiedName, qualifiedName, value);
    }
    
    /** 
     * Express an attribute 
     *
     * @param namespaceUri the namespace for the attribute
     * @param localName the local name for the attribute
     * @param qualifiedName the qualified name of the attribute
     * @param value the attribute value
     */
    protected void expressAttribute(
                                String namespaceUri,
                                String localName,
                                String qualifiedName, 
                                String value) {
        // FIX ME
        // SHOULD PROBABLY SUPPORT ID IDREF HERE
        attributes.addAttribute(namespaceUri, localName, qualifiedName, "CDATA", value);
    }


    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /**
     * Send the start element event to the <code>ContentHandler</code> 
     * @throws SAXException if the <code>ContentHandler</code> has a problem
     */
    private void sendElementStart() throws SAXException {
        ElementName lastElement = (ElementName) elementStack.peek();
        if (log.isTraceEnabled()) {
            log.trace(lastElement);
        }
        contentHandler.startElement(
                                lastElement.getUri(), 
                                lastElement.getLocalName(), 
                                lastElement.getQName(), 
                                attributes);
    }
    /**
     * This will announce the start of the document
     * to the contenthandler.
     * 
     * @see org.apache.commons.betwixt.io.AbstractBeanWriter#end()
     */
    public void start() throws SAXException {
        if ( callDocumentEvents ) {
            contentHandler.startDocument();
        }
    }

    /**
     * This method will announce the end of the document to
     * the contenthandler.
     * 
     * @see org.apache.commons.betwixt.io.AbstractBeanWriter#start()
     */
    public void end() throws SAXException {
        if ( callDocumentEvents ) {
            contentHandler.endDocument();
        }
    }

    /** Used to store element names stored on the stack */
    private class ElementName {
        /** Namespace uri */
        private String uri;
        /** Local name */
        private String localName;
        /** Qualified name */
        private String qName;
        
        /** 
         * Gets namespace uri 
         * @return the namespace uri
         */
        String getUri() {
            return uri;
        }
 
        /** 
         * Gets local name 
         * @return the local name
         */
        String getLocalName() {
            return localName;
        }
        
        /** 
         * Gets qualified name 
         * @return the qualified name
         */
        String getQName() {
            return qName;
        }
                      
        /** 
         * Base constructor 
         * @param uri the namespace uri
         * @param localName the local name of this element
         * @param qName the qualified name of this element
         */
        ElementName(String uri, String localName, String qName) {
            this.uri = uri;
            this.localName = localName;
            this.qName = qName;
        }
        
        /**
         * Return something useful for logging
         *
         * @return something useful for logging
         */
        public String toString() {
            return "[ElementName uri=" + uri + " ,lName=" + localName + " ,qName=" + qName + "]";
        }	
    }

}
