/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/SAXBeanWriter.java,v 1.1 2002/07/18 23:19:07 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2002/07/18 23:19:07 $
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
 * $Id: SAXBeanWriter.java,v 1.1 2002/07/18 23:19:07 rdonkin Exp $
 */
package org.apache.commons.betwixt.io;

import java.beans.IntrospectionException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.expression.Expression;
import org.apache.commons.betwixt.io.id.SequentialIDGenerator;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

// FIX ME
// At the moment, namespaces are NOT supported!

/**
  * 
  */
public class SAXBeanWriter {

    /** Where the output goes */
    private ContentHandler contentHandler;    
    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( BeanWriter.class );
    
    private String lastElementName;
    
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
        // make sure any previous elements have been sent
        sendElementStart();
        // ok prepare for new one
	elementWaiting = true;
        attributes = new AttributesImpl();
        lastElementName = qualifiedName;
    }
    
    protected void expressTagClose() {
        // using this could probably make life easier
        // but i only know that i needed it after i'd written the rest
    }
    
    /** Express an element end tag using given qualifiedName */
    protected void expressElementEnd(String qualifiedName) throws SAXException  {
        // make sure that we sent the last element to be handled
        sendElementStart();
        // can't handle namespaces yet
        contentHandler.endElement("","",qualifiedName);
    }    
    
    /** Express an empty element end */
    protected void expressElementEnd() throws SAXException  {
        // last element name must be correct since there haven't been any tag in between
        contentHandler.endElement("","",lastElementName);
    }

    /** Express body text */
    protected void expressBodyText(String text) throws SAXException  {
        // FIX ME
        // CHECK UNICODE->CHAR CONVERSION!
        // THIS WILL QUITE POSSIBLY BREAK FOR NON-ROMAN
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
        if (elementWaiting) {
            contentHandler.startElement("","",lastElementName,attributes);
            elementWaiting = false;
        }
    }
}
