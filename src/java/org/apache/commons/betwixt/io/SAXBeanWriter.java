/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/SAXBeanWriter.java,v 1.10 2003/02/27 19:20:17 rdonkin Exp $
 * $Revision: 1.10 $
 * $Date: 2003/02/27 19:20:17 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * $Id: SAXBeanWriter.java,v 1.10 2003/02/27 19:20:17 rdonkin Exp $
 */
package org.apache.commons.betwixt.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

// FIX ME
// At the moment, namespaces are NOT supported!

/**
 * The SAXBeanwriter will send events to a ContentHandler
 * 
 * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: SAXBeanWriter.java,v 1.10 2003/02/27 19:20:17 rdonkin Exp $ 
 */
public class SAXBeanWriter extends AbstractBeanWriter {

    /** Where the output goes */
    private ContentHandler contentHandler;    
    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( SAXBeanWriter.class );
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
     * @throws SAXException if an SAX problem occurs during writing 
     * @since 1.0 Alpha 1
     */
    protected void startElement(
                                String uri, 
                                String localName, 
                                String qName, 
                                Attributes attributes)
                                    throws
                                        SAXException {
        contentHandler.startElement(
                                uri, 
                                localName, 
                                qName, 
                                attributes);
    }
    
    /**
     * Writes the end tag for an element
     *
     * @param uri the element's namespace uri
     * @param localName the element's local name 
     * @param qName the element's qualified name
     * @throws SAXException if an SAX problem occurs during writing 
     * @since 1.0 Alpha 1
     */
    protected void endElement(
                                String uri, 
                                String localName, 
                                String qName)
                                    throws
                                        SAXException {
        contentHandler.endElement(
                                uri, 
                                localName, 
                                qName);
    }

    /** 
     * Express body text 
     * @param text the element body text 
     * @throws SAXException if the <code>ContentHandler</code> has a problem
     * @since 1.0 Alpha 1
     */
    protected void bodyText(String text) throws SAXException  {
        // FIX ME
        // CHECK UNICODE->CHAR CONVERSION!
        // THIS WILL QUITE POSSIBLY BREAK FOR NON-ROMAN
        char[] body = text.toCharArray();
        contentHandler.characters(body, 0, body.length);
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
}
