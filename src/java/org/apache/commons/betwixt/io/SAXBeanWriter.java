/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/SAXBeanWriter.java,v 1.4 2002/11/08 22:09:01 mvdb Exp $
 * $Revision: 1.4 $
 * $Date: 2002/11/08 22:09:01 $
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
 * $Id: SAXBeanWriter.java,v 1.4 2002/11/08 22:09:01 mvdb Exp $
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
 * @version $Id: SAXBeanWriter.java,v 1.4 2002/11/08 22:09:01 mvdb Exp $ 
 */
public class SAXBeanWriter extends AbstractBeanWriter {

    /** Where the output goes */
    private ContentHandler contentHandler;    
    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( SAXBeanWriter.class );
    
    private String lastElementName;
    
    /**
     * Place holder for elements that are started.
     */
    private Stack elementStack;
    
    private AttributesImpl attributes;
    
    private boolean elementWaiting = false;
    
    /**
     * <p> Constructor sets writer used for output.</p>
     *
     * @param writer write out representations to this writer
     */
    public SAXBeanWriter(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    /**
     * <p> Get the current level for logging. </p>
     *
     * @return a <code>org.apache.commons.logging.Log</code> level constant
     */ 
    public Log getLog() {
        return log;
    }

    /**
     * <p> Set the current logging level. </p>
     *
     * @param level a <code>org.apache.commons.logging.Log</code> level constant
     */ 
    public void setLog(Log log) {
        this.log = log;
    }
    
        
    // Expression methods
    //-------------------------------------------------------------------------    
    
    /** Express an element tag start using given qualified name */
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
    
    protected void expressTagClose() {
        // using this could probably make life easier
        // but i only know that i needed it after i'd written the rest
    }
    
    /** Express an element end tag using given qualifiedName */
    protected void expressElementEnd(String qualifiedName) throws SAXException  {
        if (elementWaiting) {
            elementWaiting = false;
            sendElementStart();
        }
        // can't handle namespaces yet
        contentHandler.endElement("","",qualifiedName);
    }    
    
    /** Express an empty element end */
    protected void expressElementEnd() throws SAXException  {
        // last element name must be correct since there haven't been any tag in between
        String lastElement = (String) elementStack.peek();
        contentHandler.endElement("","",lastElement);
    }

    /** Express body text */
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
    
    /** Express an attribute */
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
