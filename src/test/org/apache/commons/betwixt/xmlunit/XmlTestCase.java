/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/xmlunit/XmlTestCase.java,v 1.11 2003/10/19 14:44:53 mvdb Exp $
 * $Revision: 1.11 $
 * $Date: 2003/10/19 14:44:53 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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
 */ 
 
package org.apache.commons.betwixt.xmlunit;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** 
  * Provides xml test utilities. 
  * Hopefully, these might be moved into [xmlunit] sometime.
  *
  * @author Robert Burrell Donkin
  */
public class XmlTestCase extends TestCase {

    protected static boolean debug = false;

    DocumentBuilderFactory domFactory;

         
    public XmlTestCase(String testName) {
        super(testName);
    }

    
    public void xmlAssertIsomorphicContent(
                                org.w3c.dom.Document documentOne, 
                                org.w3c.dom.Document documentTwo)
                                    throws 
                                        AssertionFailedError {
        log("Testing documents:" + documentOne.getDocumentElement().getNodeName() 
            + " and " + documentTwo.getDocumentElement().getNodeName());
        xmlAssertIsomorphicContent(documentOne, documentTwo, false);
    }

    public void xmlAssertIsomorphicContent(
                                org.w3c.dom.Document documentOne, 
                                org.w3c.dom.Document documentTwo,
                                boolean orderIndependent)
                                    throws 
                                        AssertionFailedError {
        xmlAssertIsomorphicContent(null, documentOne, documentTwo, orderIndependent);
    }
    
    public void xmlAssertIsomorphicContent(
                                String message,
                                org.w3c.dom.Document documentOne, 
                                org.w3c.dom.Document documentTwo)
                                    throws 
                                        AssertionFailedError {
        
        xmlAssertIsomorphicContent(message, documentOne, documentTwo, false);
    }
    
    public void xmlAssertIsomorphicContent(
                                String message,
                                org.w3c.dom.Document documentOne, 
                                org.w3c.dom.Document documentTwo,
                                boolean orderIndependent)
                                    throws 
                                        AssertionFailedError
    {
        // two documents have isomorphic content iff their root elements 
        // are isomophic
        xmlAssertIsomorphic(
                            message, 
                            documentOne.getDocumentElement(), 
                            documentTwo.getDocumentElement(),
                            orderIndependent);
    }
 
    
    public void xmlAssertIsomorphic(
                                org.w3c.dom.Node rootOne, 
                                org.w3c.dom.Node rootTwo) 
                                    throws 
                                        AssertionFailedError {
        xmlAssertIsomorphic(rootOne, rootTwo, false);
    }
    
    public void xmlAssertIsomorphic(
                                org.w3c.dom.Node rootOne, 
                                org.w3c.dom.Node rootTwo,
                                boolean orderIndependent)
                                    throws 
                                        AssertionFailedError
    {
        xmlAssertIsomorphic(null, rootOne, rootTwo, orderIndependent);
    }
    
    public void xmlAssertIsomorphic(
                                String message,
                                org.w3c.dom.Node rootOne, 
                                org.w3c.dom.Node rootTwo) {
                                
        xmlAssertIsomorphic(message, rootOne, rootTwo, false);
    
    }
    
    public void xmlAssertIsomorphic(
                                String message,
                                org.w3c.dom.Node rootOne, 
                                org.w3c.dom.Node rootTwo,
                                boolean orderIndependent)
                                    throws 
                                        AssertionFailedError
    {
        // first normalize the xml
        rootOne.normalize();
        rootTwo.normalize();
        // going to use recursion so avoid normalizing each time
        testIsomorphic(message, rootOne, rootTwo, orderIndependent);
    }
 
    
    private void testIsomorphic(
                                String message,
                                org.w3c.dom.Node nodeOne, 
                                org.w3c.dom.Node nodeTwo)
                                    throws 
                                        AssertionFailedError {
                                        
        testIsomorphic(message, nodeOne, nodeTwo, false);
    }
                                    
    
    private void testIsomorphic(
                                String message,
                                org.w3c.dom.Node nodeOne, 
                                org.w3c.dom.Node nodeTwo,
                                boolean orderIndependent)
                                    throws 
                                        AssertionFailedError
    {
        try {
            if (debug) {
                log(
                    "node 1 name=" + nodeOne.getNodeName() 
                    + " qname=" + nodeOne.getLocalName());
                log(
                    "node 2 name=" + nodeTwo.getNodeName() 
                    + " qname=" + nodeTwo.getLocalName());
            }
            
            // compare node properties
            log("Comparing node properties");
            assertEquals(
                        (null == message ? "(Unequal node types)" : message + "(Unequal node types)"), 
                        nodeOne.getNodeType(), 
                        nodeTwo.getNodeType());
            assertEquals(
                        (null == message ? "(Unequal node names)" : message + "(Unequal node names)"), 
                        nodeOne.getNodeName(), 
                        nodeTwo.getNodeName());
            assertEquals(
                        (null == message ? "(Unequal node values)" : message + "(Unequal node values)"), 
                        trim(nodeOne.getNodeValue()), 
                        trim(nodeTwo.getNodeValue()));
            assertEquals(
                        (null == message ? "(Unequal local names)" : message + "(Unequal local names)"), 
                        nodeOne.getLocalName(), 
                        nodeTwo.getLocalName());
            assertEquals(
                        (null == message ? "(Unequal namespace)" : message + "(Unequal namespace)"), 
                        nodeOne.getNamespaceURI(), 
                        nodeTwo.getNamespaceURI());
            
                                                  
            // compare attributes
            log("Comparing attributes");
            // make sure both have them first
            assertEquals(
                        (null == message ? "(Unequal attributes)" : message + "(Unequal attributes)"), 
                        nodeOne.hasAttributes(), 
                        nodeTwo.hasAttributes());            
            if (nodeOne.hasAttributes()) {
                // do the actual comparison
                // first we check the number of attributes are equal 
                // we then check that for every attribute of node one, 
                // a corresponding attribute exists in node two
                // (this should be sufficient to prove equality)
                NamedNodeMap attributesOne = nodeOne.getAttributes();
                NamedNodeMap attributesTwo = nodeTwo.getAttributes();
                
                assertEquals(
                        (null == message ? "(Unequal attributes)" : message + "(Unequal attributes)"), 
                        attributesOne.getLength(), 
                        attributesTwo.getLength());
                
                for (int i=0, size=attributesOne.getLength(); i<size; i++) {
                    Attr attributeOne = (Attr) attributesOne.item(i);
                    Attr attributeTwo = (Attr) attributesTwo.getNamedItemNS(
                                                    attributeOne.getNamespaceURI(),
                                                    attributeOne.getLocalName());
                    if (attributeTwo == null) {
                        attributeTwo = (Attr) attributesTwo.getNamedItem(attributeOne.getName());
                    }
                    
                    // check attribute two exists
                    if (attributeTwo == null) {
                        String diagnosis = "[Missing attribute (" + attributeOne.getName() +  ")]";
                        fail((null == message ?  diagnosis : message + diagnosis));
                    }
                    
                    // now check attribute values
                    assertEquals(
                        (null == message ? "(Unequal attribute values)" : message + "(Unequal attribute values)"), 
                        attributeOne.getValue(), 
                        attributeTwo.getValue());                    
                }
            }
            
            
            // compare children
            log("Comparing children");
            // this time order is important
            // so we can just go down the list and compare node-wise using recursion
            List listOne = sanitize(nodeOne.getChildNodes());
            List listTwo = sanitize(nodeTwo.getChildNodes());

            if (orderIndependent) {
                log("[Order Independent]");
                Comparator nodeByName = new NodeByNameComparator();
                Collections.sort(listOne, nodeByName);
                Collections.sort(listTwo, nodeByName);
            }
            
            Iterator it = listOne.iterator();
            Iterator iter2 = listTwo.iterator();
            while (it.hasNext() & iter2.hasNext()) {
                Node nextOne = ((Node)it.next());
                Node nextTwo = ((Node)iter2.next());
                log(nextOne.getNodeName() + ":" + nextOne.getNodeValue());
                log(nextTwo.getNodeName() + ":" + nextTwo.getNodeValue());
            }

            assertEquals(
                        (null == message ? "(Unequal child nodes@" + nodeOne.getNodeName() +")": 
                                message + "(Unequal child nodes @" + nodeOne.getNodeName() +")"), 
                        listOne.size(), 
                        listTwo.size());           
                        
            it = listOne.iterator();
            iter2 = listTwo.iterator();
            while (it.hasNext() & iter2.hasNext()) {	
                Node nextOne = ((Node)it.next());
                Node nextTwo = ((Node)iter2.next());
                log(nextOne.getNodeName() + " vs " + nextTwo.getNodeName());
                testIsomorphic(message, nextOne, nextTwo, orderIndependent);
            
            }
        
        } catch (DOMException ex) {
            fail((null == message ? "" : message + " ") + "DOM exception" + ex.toString());
        }
    }
    
    
    protected DocumentBuilder createDocumentBuilder() {
        try {

            return getDomFactory().newDocumentBuilder();
        
        } catch (ParserConfigurationException e) {
            fail("Cannot create DOM builder: " + e.toString());
        
        }
        // just to keep the compiler happy
        return null;
    }
    
    protected DocumentBuilderFactory getDomFactory() {
        // lazy creation
        if (domFactory == null) {
            domFactory = DocumentBuilderFactory.newInstance();
        }
        
        return domFactory;
    }
    
    protected Document parseString(StringWriter writer) {
        try { 
        
            return createDocumentBuilder().parse(new InputSource(new StringReader(writer.getBuffer().toString())));
        
        } catch (SAXException e) {
            fail("Cannot create parse string: " + e.toString());
        
        } catch (IOException e) {
            fail("Cannot create parse string: " + e.toString());
        
        } 
        // just to keep the compiler happy
        return null;
    }
    
    protected Document parseString(String string) {
        try { 
        
            return createDocumentBuilder().parse(new InputSource(new StringReader(string)));
        
        } catch (SAXException e) {
            fail("Cannot create parse string: " + e.toString());
        
        } catch (IOException e) {
            fail("Cannot create parse string: " + e.toString());
        
        } 
        // just to keep the compiler happy
        return null;
    }

    
    protected Document parseFile(String path) {
        try { 
        
            return createDocumentBuilder().parse(new File(path));
        
        } catch (SAXException e) {
            fail("Cannot create parse file: " + e.toString());
        
        } catch (IOException e) {
            fail("Cannot create parse file: " + e.toString());
        
        } 
        // just to keep the compiler happy
        return null;
    }
    
    private void log(String message)
    {
        if (debug) {
            System.out.println("[XmlTestCase]" + message);
        }
    }
    
    private String trim(String trimThis)
    {
        if (trimThis == null) {
            return trimThis;
        }
        
        return trimThis.trim();
    }
    
    private List sanitize(NodeList nodes) {
        ArrayList list = new ArrayList();
        
        for (int i=0, size=nodes.getLength(); i<size; i++) {
            if ( nodes.item(i).getNodeType() == Node.TEXT_NODE ) {
                if ( !( nodes.item(i).getNodeValue() == null ||  
                        nodes.item(i).getNodeValue().trim().length() == 0 )) {
                    list.add(nodes.item(i));
                } else {
                    log("Ignoring text node:" + nodes.item(i).getNodeValue());
                }
            } else {
                list.add(nodes.item(i));
            }
        }
        return list;
    }
    
    private class NodeByNameComparator implements Comparator {
        public int compare(Object objOne, Object objTwo) {
            String nameOne = ((Node) objOne).getNodeName();
            String nameTwo = ((Node) objTwo).getNodeName();
            
            if (nameOne == null) {
                if (nameTwo == null) {
                    return 0;
                }
                return -1;
            }
            
            if (nameTwo == null) {
                return 1;
            }
            
            return nameOne.compareTo(nameTwo);
        }
    }
}

