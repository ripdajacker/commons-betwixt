/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/SAXBeanWriter.java,v 1.6 2003/01/08 22:07:21 rdonkin Exp $
 * $Revision: 1.6 $
 * $Date: 2003/01/08 22:07:21 $
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
 * $Id: SAXBeanWriter.java,v 1.6 2003/01/08 22:07:21 rdonkin Exp $
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
 * @version $Id: SAXBeanWriter.java,v 1.6 2003/01/08 22:07:21 rdonkin Exp $ 
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
    
    /**
     * <p> Constructor sets writer used for output.</p>
     *
     * @param contentHandler feed events to this content handler
     */
    public SAXBeanWriter(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
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
     */
    protected void expressElementStart(String qualifiedName) throws SAXException  {
        if (elementStack == null) {
            elementStack = new Stack();
        }
        if (elementWaiting) {
            sendElementStart();
        }
        attributes = new AttributesImpl();
        elementStack.push(qualifiedName);
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
     */
    protected void expressElementEnd(String qualifiedName) throws SAXException  {
        if (elementWaiting) {
            elementWaiting = false;
            sendElementStart();
        }
        // can't handle namespaces yet
        contentHandler.endElement("","",qualifiedName);
    }    
    
    /** 
     * Express an empty element end 
     * @throws SAXException if the <code>ContentHandler</code> has a problem
     */
    protected void expressElementEnd() throws SAXException  {
        // last element name must be correct since there haven't been any tag in between
        String lastElement = (String) elementStack.peek();
        contentHandler.endElement("","",lastElement);
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
     * @throws SAXException if the <code>ContentHandler</code> has a problem
     */
    protected void expressAttribute(
                                String qualifiedName, 
                                String value) 
                                    throws
                                        SAXException  {
        // FIX ME
        // SHOULD PROBABLY SUPPORT ID IDREF HERE
        attributes.addAttribute("", "", qualifiedName, "CDATA", value);
    }


    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /**
     * Send the start element event to the <code>ContentHandler</code> 
     * @throws SAXException if the <code>ContentHandler</code> has a problem
     */
    private void sendElementStart() throws SAXException {
        String lastElement = (String)elementStack.peek();
        contentHandler.startElement("","",lastElement,attributes);
    }
    /**
     * This will announce the start of the document
     * to the contenthandler.
     * 
     * @see org.apache.commons.betwixt.io.AbstractBeanWriter#end()
     */
    public void start() throws SAXException {
        contentHandler.startDocument();
    }

    /**
     * This method will announce the end of the document to
     * the contenthandler.
     * 
     * @see org.apache.commons.betwixt.io.AbstractBeanWriter#start()
     */
    public void end() throws SAXException {
        contentHandler.endDocument();
    }

}
