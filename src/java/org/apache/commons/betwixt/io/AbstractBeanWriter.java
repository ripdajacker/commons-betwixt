/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/AbstractBeanWriter.java,v 1.17 2003/07/29 21:32:15 rdonkin Exp $
 * $Revision: 1.17 $
 * $Date: 2003/07/29 21:32:15 $
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
 * $Id: AbstractBeanWriter.java,v 1.17 2003/07/29 21:32:15 rdonkin Exp $
 */
package org.apache.commons.betwixt.io;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.Descriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.expression.Expression;
import org.apache.commons.betwixt.io.id.SequentialIDGenerator;
import org.apache.commons.betwixt.digester.XMLIntrospectorHelper;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

// FIX ME!!!
// Logging logic!

// FIX ME!!
// Error handling strategy!
// i'm going to add SAXExceptions everywhere since it's the easiest way to make things work quick
// but this is a poor strategy

/**
  * <p>Abstract superclass for bean writers.
  * This class encapsulates the processing logic. 
  * Subclasses provide implementations for the actual expression of the xml.</p>
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @version $Revision: 1.17 $
  */
public abstract class AbstractBeanWriter {

    /** Introspector used */
    private XMLIntrospector introspector = new XMLIntrospector();

    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( AbstractBeanWriter.class );
    /** Map containing ID attribute values for beans */
    private HashMap idMap = new HashMap();
    /** Stack containing beans - used to detect cycles */
    private ArrayStack beanStack = new ArrayStack();
    /** Used to generate ID attribute values*/
    private IDGenerator idGenerator = new SequentialIDGenerator();
    /** Should generated <code>ID</code> attribute values be added to the elements? */
    private boolean writeIDs = true;
    /** Should empty elements be written out? */
    private boolean writeEmptyElements = true;
    
    /**
     * Marks the start of the bean writing.
     * By default doesn't do anything, but can be used
     * to do extra start processing 
     * @throws IOException if an IO problem occurs during writing 
     * @throws SAXException if an SAX problem occurs during writing 
     */
    public void start() throws IOException, SAXException {
    }
    
    /**
     * Marks the start of the bean writing.
     * By default doesn't do anything, but can be used
     * to do extra end processing 
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     */
    
    public void end() throws IOException, SAXException {
    }
        
    /** 
     * <p> Writes the given bean to the current stream using the XML introspector.</p>
     * 
     * <p> This writes an xml fragment representing the bean to the current stream.</p>
     *
     * <p>This method will throw a <code>CyclicReferenceException</code> when a cycle
     * is encountered in the graph <strong>only</strong> if the <code>WriteIDs</code>
     * property is false.</p>
     *
     * @throws IOException if an IO problem occurs during writing 
     * @throws SAXException if an SAX problem occurs during writing  
     * @throws IntrospectionException if a java beans introspection problem occurs 
     *
     * @param bean write out representation of this bean
     */
    public void write(Object bean) throws 
                                        IOException, 
                                        SAXException, 
                                        IntrospectionException {
        if (log.isDebugEnabled()) {
            log.debug( "Writing bean graph..." );
            log.debug( bean );
        }
        start();
        write( null, bean );
        end();
        if (log.isDebugEnabled()) {
            log.debug( "Finished writing bean graph." );
        }
    }
    
    /** 
     * <p>Writes the given bean to the current stream 
     * using the given <code>qualifiedName</code>.</p>
     *
     * <p>This method will throw a <code>CyclicReferenceException</code> when a cycle
     * is encountered in the graph <strong>only</strong> if the <code>WriteIDs</code>
     * property is false.</p>
     *
     * @param qualifiedName the string naming root element
     * @param bean the <code>Object</code> to write out as xml
     * 
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    public void write(
                String qualifiedName, 
                Object bean) 
                    throws 
                        IOException, 
                        SAXException,
                        IntrospectionException {
        writeBean( "", qualifiedName, qualifiedName, bean);
    }
    
    /** 
     * <p>Writes the given bean to the current stream 
     * using the given <code>qualifiedName</code>.</p>
     *
     * <p>This method will throw a <code>CyclicReferenceException</code> when a cycle
     * is encountered in the graph <strong>only</strong> if the <code>WriteIDs</code>
     * property is false.</p>
     *
     * @param namespaceUri the namespace uri
     * @param localName the local name
     * @param qualifiedName the string naming root element
     * @param bean the <code>Object</code> to write out as xml
     * 
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    private void writeBean (
                String namespaceUri,
                String localName,
                String qualifiedName, 
                Object bean) 
                    throws 
                        IOException, 
                        SAXException,
                        IntrospectionException {                    
        
        if ( log.isTraceEnabled() ) {
            log.trace( "Writing bean graph (qualified name '" + qualifiedName + "'" );
        }
        
        // introspect to obtain bean info
        XMLBeanInfo beanInfo = introspector.introspect( bean );
        if ( beanInfo != null ) {
            ElementDescriptor elementDescriptor = beanInfo.getElementDescriptor();
            if ( elementDescriptor != null ) {
                Context context = new Context( bean, log );
                if ( qualifiedName == null ) {
                    qualifiedName = elementDescriptor.getQualifiedName();
                }
                if ( namespaceUri == null ) {
                    namespaceUri = elementDescriptor.getURI();
                }
                if ( localName == null ) {
                    localName = elementDescriptor.getLocalName();
                }

                String ref = null;
                String id = null;
                
                // only give id's to non-primatives
                if ( elementDescriptor.isPrimitiveType() ) {
                    // write without an id
                    writeElement( 
                        namespaceUri,
                        localName,
                        qualifiedName, 
                        elementDescriptor, 
                        context );
                        
                } else {
                    pushBean ( context.getBean() );
                    if ( writeIDs ) {
                        ref = (String) idMap.get( context.getBean() );
                    }
                    if ( ref == null ) {
                        // this is the first time that this bean has be written
                        AttributeDescriptor idAttribute = beanInfo.getIDAttribute();
                        if (idAttribute == null) {
                            // use a generated id
                            id = idGenerator.nextId();
                            idMap.put( bean, id );
                            
                            if ( writeIDs ) {
                                // write element with id
                                writeElement(
                                    namespaceUri,
                                    localName,
                                    qualifiedName, 
                                    elementDescriptor, 
                                    context , 
                                    beanInfo.getIDAttributeName(),
                                    id);
                                    
                            } else {    
                                // write element without ID
                                writeElement( 
                                    namespaceUri,
                                    localName,
                                    qualifiedName, 
                                    elementDescriptor, 
                                    context );
                            }
                                                        
                        } else {
                            // use id from bean property
                            // it's up to the user to ensure uniqueness
                            // XXX should we trap nulls?
                            Object exp = idAttribute.getTextExpression().evaluate( context );
                            if (exp == null) {
                                // we'll use a random id
                                log.debug("Using random id");
                                id = idGenerator.nextId();
                                
                            } else {
                                // convert to string
                                id = exp.toString();
                            }
                            idMap.put( bean, id);
                            
                            // the ID attribute should be written automatically
                            writeElement( 
                                namespaceUri,
                                localName,
                                qualifiedName, 
                                elementDescriptor, 
                                context );
                        }
                    } else {
                        
                        if ( !ignoreElement( elementDescriptor, context )) {
                            // we've already written this bean so write an IDREF
                            writeIDREFElement( 
                                            namespaceUri,
                                            localName,
                                            qualifiedName,  
                                            beanInfo.getIDREFAttributeName(), 
                                            ref);
                        }
                    }
                    popBean();
                }
            }
        }
        
        log.trace( "Finished writing bean graph." );
    }
    
    /** 
      * Get <code>IDGenerator</code> implementation used to 
      * generate <code>ID</code> attribute values .
      *
      * @return implementation used for <code>ID</code> attribute generation
      */
    public IDGenerator getIdGenerator() {
        return idGenerator;
    }
    
    /** 
      * Set <code>IDGenerator</code> implementation 
      * used to generate <code>ID</code> attribute values.
      * This property can be used to customize the algorithm used for generation.
      *
      * @param idGenerator use this implementation for <code>ID</code> attribute generation
      */
    public void setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
    
    /** 
     * <p>Should generated <code>ID</code> attribute values be added to the elements?</p>
     * 
     * <p>If IDs are not being written then if a cycle is encountered in the bean graph, 
     * then a {@link CyclicReferenceException} will be thrown by the write method.</p>
     * 
     * @return true if <code>ID</code> and <code>IDREF</code> attributes are to be written
     */
    public boolean getWriteIDs() {
        return writeIDs;
    }

    /** 
     * Set whether generated <code>ID</code> attribute values should be added to the elements 
     * If this property is set to false, then <code>CyclicReferenceException</code> 
     * will be thrown whenever a cyclic occurs in the bean graph.
     *
     * @param writeIDs true if <code>ID</code>'s and <code>IDREF</code>'s should be written
     */
    public void setWriteIDs(boolean writeIDs) {
        this.writeIDs = writeIDs;
    }
    
    /**
     * <p>Gets whether empty elements should be written into the output.</p>
     *
     * <p>An empty element is one that has no attributes, no child elements 
     * and no body text.
     * For example, <code>&lt;element/&gt;</code> is an empty element but
     * <code>&lt;element attr='value'/&gt;</code> is not.</p>
     *
     * @return true if empty elements will be written into the output
     */
    public boolean getWriteEmptyElements() {
        return writeEmptyElements;
    }
    
    /**
     * <p>Sets whether empty elements should be written into the output.</p>
     *
     * <p>An empty element is one that has no attributes, no child elements 
     * and no body text.
     * For example, <code>&lt;element/&gt;</code> is an empty element but
     * <code>&lt;element attr='value'/&gt;</code> is not.
     *
     * @param writeEmptyElements true if empty elements should be written into the output 
     */
    public void setWriteEmptyElements(boolean writeEmptyElements) {
        this.writeEmptyElements = writeEmptyElements;
    }

    /**
     * <p>Gets the introspector used.</p>
     *
     * <p>The {@link XMLBeanInfo} used to map each bean is 
     * created by the <code>XMLIntrospector</code>.
     * One way in which the mapping can be customized is 
     * by altering the <code>XMLIntrospector</code>. </p>
     *
     * @return the <code>XMLIntrospector</code> used for introspection
     */
    public XMLIntrospector getXMLIntrospector() {
        return introspector;
    }
    

    /**
     * <p>Sets the introspector to be used.</p>
     *
     * <p>The {@link XMLBeanInfo} used to map each bean is 
     * created by the <code>XMLIntrospector</code>.
     * One way in which the mapping can be customized is by 
     * altering the <code>XMLIntrospector</code>. </p>
     *
     * @param introspector use this introspector
     */
    public void  setXMLIntrospector(XMLIntrospector introspector) {
        this.introspector = introspector;
    }

    /**
     * <p>Gets the current logging implementation.</p>
     *
     * @return the <code>Log</code> implementation which this class logs to
     */ 
    public final Log getAbstractBeanWriterLog() {
        return log;
    }

    /**
     * <p> Set the current logging implementation. </p>
     *
     * @param log <code>Log</code> implementation to use
     */ 
    public final void setAbstractBeanWriterLog(Log log) {
        this.log = log;
    }
        
    // SAX-style methods
    //-------------------------------------------------------------------------    
        
    /**
     * Writes the start tag for an element.
     *
     * @param uri the element's namespace uri
     * @param localName the element's local name 
     * @param qName the element's qualified name
     * @param attr the element's attributes
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @since 1.0 Alpha-1
     */
    protected void startElement(
                                String uri, 
                                String localName, 
                                String qName, 
                                Attributes attr)
                                    throws
                                        IOException,
                                        SAXException {}
    
    /**
     * Writes the end tag for an element
     *
     * @param uri the element's namespace uri
     * @param localName the element's local name 
     * @param qName the element's qualified name
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @since 1.0 Alpha-1
     */
    protected void endElement(
                                String uri, 
                                String localName, 
                                String qName)
                                    throws
                                        IOException,
                                        SAXException {}
    
    /** 
     * Writes body text
     *
     * @param text the body text to be written
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @since 1.0 Alpha 1
     */
    protected void bodyText(String text) throws IOException, SAXException {}
    
    // Implementation methods
    //-------------------------------------------------------------------------    

    /** 
     * Writes the given element 
     *
     * @param namespaceUri the namespace uri
     * @param localName the local name
     * @param qualifiedName qualified name to use for the element
     * @param elementDescriptor the <code>ElementDescriptor</code> describing the element
     * @param context the <code>Context</code> to use to evaluate the bean expressions
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    private void writeElement(
                            String namespaceUri,
                            String localName,
                            String qualifiedName, 
                            ElementDescriptor elementDescriptor, 
                            Context context ) 
                                throws 
                                    IOException, 
                                    SAXException,
                                    IntrospectionException {
        if( log.isTraceEnabled() ) {
            log.trace( "Writing: " + qualifiedName + " element: " + elementDescriptor );
        }
                
        if ( !ignoreElement( elementDescriptor, context )) {
            if ( log.isTraceEnabled() ) {
                log.trace( "Element " + elementDescriptor + " is empty." );
            }
        
            if (elementDescriptor.isWrapCollectionsInElement()) {
                startElement( 
                            namespaceUri, 
                            localName, 
                            qualifiedName,
                            new ElementAttributes( elementDescriptor, context ));
            }
    
            writeElementContent( elementDescriptor, context ) ;
            if ( elementDescriptor.isWrapCollectionsInElement() ) {
                endElement( namespaceUri, localName, qualifiedName );
            }
        }
    }
    
    /** 
     * Writes the given element adding an ID attribute 
     *
     * @param namespaceUri the namespace uri
     * @param localName the local name
     * @param qualifiedName the qualified name
     * @param elementDescriptor the ElementDescriptor describing this element
     * @param context the context being evaliated against
     * @param idAttribute the qualified name of the <code>ID</code> attribute 
     * @param idValue the value for the <code>ID</code> attribute 
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    private void writeElement( 
                            String namespaceUri,
                            String localName,
                            String qualifiedName, 
                            ElementDescriptor elementDescriptor, 
                            Context context,
                            String idAttribute,
                            String idValue ) 
                                throws 
                                    IOException, 
                                    SAXException,
                                    IntrospectionException {
                   
        if ( !ignoreElement( elementDescriptor, context ) ) {
        
            startElement( 
                        namespaceUri, 
                        localName, 
                        qualifiedName,
                        new ElementAttributes( 
                                                elementDescriptor, 
                                                context, 
                                                idAttribute, 
                                                idValue ));
    
            writeElementContent( elementDescriptor, context ) ;
            endElement( namespaceUri, localName, qualifiedName );

        } else if ( log.isTraceEnabled() ) {
            log.trace( "Element " + qualifiedName + " is empty." );
        }
    }
    

    /**
     * Write attributes, child elements and element end 
     * 
     * @param uri the element namespace uri 
     * @param localName the local name of the element
     * @param qualifiedName the qualified name of the element
     * @param elementDescriptor the descriptor for this element
     * @param context evaluate against this context
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    private void writeRestOfElement( 
                            String uri,
                            String localName,
                            String qualifiedName, 
                            ElementDescriptor elementDescriptor, 
                            Context context ) 
                                throws 
                                    IOException, 
                                    SAXException,
                                    IntrospectionException {

        if ( elementDescriptor.isWrapCollectionsInElement() ) {
            writeAttributes( elementDescriptor, context );
        }

        writeElementContent( elementDescriptor, context );
        if ( elementDescriptor.isWrapCollectionsInElement() ) {
            endElement( uri, localName, qualifiedName );
        }
    }

    /**
     * Writes an element with a <code>IDREF</code> attribute 
     *
     * @param uri the namespace uri
     * @param localName the local name
     * @param qualifiedName of the element with <code>IDREF</code> attribute 
     * @param idrefAttributeName the qualified name of the <code>IDREF</code> attribute 
     * @param idrefAttributeValue the value for the <code>IDREF</code> attribute 
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    private void writeIDREFElement( 
                                    String uri,
                                    String localName,
                                    String qualifiedName, 
                                    String idrefAttributeName,
                                    String idrefAttributeValue ) 
                                        throws 
                                            IOException, 
                                            SAXException,
                                            IntrospectionException {

        
        
        // write IDREF element
        AttributesImpl attributes = new AttributesImpl();
        // XXX for the moment, assign IDREF to default namespace
        attributes.addAttribute( 
                                "",
                                idrefAttributeName, 
                                idrefAttributeName,
                                "IDREF",
                                idrefAttributeValue);
        startElement( uri, localName, qualifiedName, attributes);        
        endElement( uri, localName, qualifiedName );
    }
    
    /** 
     * Writes the element content.
     *
     * @param elementDescriptor the <code>ElementDescriptor</code> to write as xml 
     * @param context the <code>Context</code> to use to evaluate the bean expressions
     * 
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     */
    private void writeElementContent( 
                        ElementDescriptor elementDescriptor, 
                        Context context ) 
                            throws 
                                IOException, 
                                SAXException,
                                IntrospectionException {     
                                
        Descriptor[] childDescriptors = elementDescriptor.getContentDescriptors();
        if ( childDescriptors != null && childDescriptors.length > 0 ) {
            // process child elements
            for ( int i = 0, size = childDescriptors.length; i < size; i++ ) {
                if (childDescriptors[i] instanceof ElementDescriptor) {
                    // Element content
                    ElementDescriptor childDescriptor = (ElementDescriptor) childDescriptors[i];
                    Context childContext = context;
                    Expression childExpression = childDescriptor.getContextExpression();
                    if ( childExpression != null ) {
                        Object childBean = childExpression.evaluate( context );
                        if ( childBean != null ) {
                            String qualifiedName = childDescriptor.getQualifiedName();
                            String namespaceUri = childDescriptor.getURI();
                            String localName = childDescriptor.getLocalName();
                            // XXXX: should we handle nulls better
                            if ( childBean instanceof Iterator ) {
                                for ( Iterator iter = (Iterator) childBean; iter.hasNext(); ) {
                                    Object object = iter.next();
                                    if (object == null) {
                                        continue;
                                    }
                                    writeBean( namespaceUri, localName, qualifiedName, object );
                                }
                            } else {
                                writeBean( namespaceUri, localName, qualifiedName, childBean );
                            }
                        }                    
                    } else {
                        writeElement(
                                    childDescriptor.getURI(), 
                                    childDescriptor.getLocalName(), 
                                    childDescriptor.getQualifiedName(), 
                                    childDescriptor, 
                                    childContext );
                    }
                } else {
                    // Mixed text content
                    // evaluate the body text 
                    Expression expression = childDescriptors[i].getTextExpression();
                    if ( expression != null ) {
                        Object value = expression.evaluate( context );
                        String text = convertToString( value );
                        if ( text != null && text.length() > 0 ) {
                            bodyText(text);
                        }               
                    }
                }
            }
        } else {
            // evaluate the body text 
            Expression expression = elementDescriptor.getTextExpression();
            if ( expression != null ) {
                Object value = expression.evaluate( context );
                String text = convertToString(value);
                if ( text != null && text.length() > 0 ) {
                    bodyText(text);
                }
            }
        }
    }

    /**
     * Pushes the bean onto the ancestry stack.
     * If IDs are not being written, then check for cyclic references.
     *
     * @param bean push this bean onto the ancester stack
     */
    protected void pushBean( Object bean ) {
        // check that we don't have a cyclic reference when we're not writing IDs
        if ( !writeIDs ) {
            Iterator it = beanStack.iterator();
            while ( it.hasNext() ) {
                Object next = it.next();
                // use absolute equality rather than equals
                // we're only really bothered if objects are actually the same
                if ( bean == next ) {
                    if ( log.isDebugEnabled() ) {
                        log.debug("Element stack: ");
                        Iterator debugStack = beanStack.iterator();
                        while ( debugStack.hasNext() ) {
                            log.debug(debugStack.next());
                        }
                    }
                    log.error("Cyclic reference at bean: " + bean);
                    throw new CyclicReferenceException();
                }
            }
        }
        if (log.isTraceEnabled()) {
            log.trace( "Pushing onto object stack: " + bean );
        }
        beanStack.push( bean );
    }
    
    /** 
     * Pops the top bean off from the ancestry stack 
     *
     * @return the last object pushed onto the ancester stack
     */
    protected Object popBean() {
        Object bean = beanStack.pop();
        if (log.isTraceEnabled()) {
            log.trace( "Popped from object stack: " + bean );
        }
        return bean;
    }
    
    /** 
     * Should this element (and children) be written out?
     *
     * @param descriptor the <code>ElementDescriptor</code> to evaluate
     * @param context the <code>Context</code> against which the element will be evaluated
     * @return true if this element should be written out
     */
    private boolean ignoreElement( ElementDescriptor descriptor, Context context ) {
        if ( ! getWriteEmptyElements() ) {
            return isEmptyElement( descriptor, context );
        }
        return false;
    }
    
    /** 
     * <p>Will evaluating this element against this context result in an empty element?</p>
     *
     * <p>An empty element is one that has no attributes, no child elements 
     * and no body text.
     * For example, <code>&lt;element/&gt;</code> is an empty element but
     * <code>&lt;element attr='value'/&gt;</code> is not.</p>
     *
     * @param descriptor the <code>ElementDescriptor</code> to evaluate
     * @param context the <code>Context</code> against which the element will be evaluated
     * @return true if this element is empty on evaluation
     */
    private boolean isEmptyElement( ElementDescriptor descriptor, Context context ) {
        if ( log.isTraceEnabled() ) {
            log.trace( "Is " + descriptor + " empty?" );
        }
        
        // an element which has attributes is not empty
        if ( descriptor.hasAttributes() ) {
            log.trace( "Element has attributes." );
            return false;
        }
        
        // an element is not empty if it has a non-empty body
        Expression expression = descriptor.getTextExpression();
        if ( expression != null ) {
            Object value = expression.evaluate( context );
            String text = convertToString(value);
            if ( text != null && text.length() > 0 ) {
                log.trace( "Element has body text which isn't empty." );
                return false;
            }
        }
        
        // always write out loops - even when they have no elements
        if ( XMLIntrospectorHelper.isLoopType( descriptor.getPropertyType() ) ) {
            log.trace("Loop type so not empty.");
            return false;
        }
        
        // now test child elements
        // an element is empty if it has no non-empty child elements
        if ( descriptor.hasChildren() ) {
            for ( int i=0, size=descriptor.getElementDescriptors().length; i<size; i++ ) {
                if ( ! isEmptyElement( descriptor.getElementDescriptors()[i], context ) ) {
                    log.trace( "Element has child which isn't empty." );
                    return false;
                }
            }
        }
        
        log.trace( "Element is empty." );
        return true;
    }
    
    
    
    
    /**
     * Attributes backed by attribute descriptors
     */
    private class ElementAttributes implements Attributes {
        /** Attribute descriptors backing the <code>Attributes</code> */
        private AttributeDescriptor[] attributes;
        /** Context to be evaluated when finding values */
        private Context context;
        /** ID attribute value */
        private String idValue;
        /** ID attribute name */
        private String idAttributeName;
        
        
        /** 
         * Construct attributes for element and context.
         *
         * @param descriptor the <code>ElementDescriptor</code> describing the element
         * @param context evaluate against this context
         */
        ElementAttributes( ElementDescriptor descriptor, Context context ) {
            attributes = descriptor.getAttributeDescriptors();
            this.context = context;
        }
        
        /** 
         * Construct attributes for element and context.
         *
         * @param descriptor the <code>ElementDescriptor</code> describing the element
         * @param context evaluate against this context
         * @param idAttributeName the name of the id attribute 
         * @param idValue the ID attribute value
         */
        ElementAttributes( 
                            ElementDescriptor descriptor, 
                            Context context, 
                            String idAttributeName,
                            String idValue) {
            attributes = descriptor.getAttributeDescriptors();
            this.context = context;
            this.idValue = idValue;
            this.idAttributeName = idAttributeName;
        }
        
        /**
         * Gets the index of an attribute by qualified name.
         * 
         * @param qName the qualified name of the attribute
         * @return the index of the attribute - or -1 if there is no matching attribute
         */
        public int getIndex( String qName ) {
            for ( int i=0; i<attributes.length; i++ ) {
                if (attributes[i].getQualifiedName() != null 
                       && attributes[i].getQualifiedName().equals( qName )) {
                    return i;
                }
            }
            return -1;
        }
        
        /**
         * Gets the index of an attribute by namespace name.
         *
         * @param uri the namespace uri of the attribute
         * @param localName the local name of the attribute
         * @return the index of the attribute - or -1 if there is no matching attribute
         */
        public int getIndex( String uri, String localName ) {
            for ( int i=0; i<attributes.length; i++ ) {
                if (
                        attributes[i].getURI() != null 
                        && attributes[i].getURI().equals(uri)
                        && attributes[i].getLocalName() != null 
                        && attributes[i].getURI().equals(localName)) {
                    return i;
                }
            } 
            
            return -1;
        }
        
        /**
         * Gets the number of attributes in the list.
         *
         * @return the number of attributes in this list
         */
        public int getLength() {
            return attributes.length;
        }
        
        /** 
         * Gets the local name by index.
         * 
         * @param index the attribute index (zero based)
         * @return the attribute local name - or null if the index is out of range
         */
        public String getLocalName( int index ) {
            if ( indexInRange( index ) ) {
                return attributes[index].getLocalName();
            }
            
            return null;
        }
        
        /**
         * Gets the qualified name by index.
         *
         * @param index the attribute index (zero based)
         * @return the qualified name of the element - or null if the index is our of range
         */
        public String getQName( int index ) {
            if ( indexInRange( index ) ) {
                return attributes[index].getQualifiedName();
            }
            
            return null;
        }
        
        /**
         * Gets the attribute SAX type by namespace name.
         *
         * @param index the attribute index (zero based)
         * @return the attribute type (as a string) or null if the index is out of range
         */
        public String getType( int index ) {
            if ( indexInRange( index ) ) {
                return "CDATA";
            }
            return null;
        }
        
        /**
         * Gets the attribute SAX type by qualified name.
         *
         * @param qName the qualified name of the attribute
         * @return the attribute type (as a string) or null if the attribute is not in the list
         */
        public String getType( String qName ) {
            return getType( getIndex( qName ) );
        }
        
        /**
         * Gets the attribute SAX type by namespace name.
         *
         * @param uri the namespace uri of the attribute
         * @param localName the local name of the attribute
         * @return the attribute type (as a string) or null if the attribute is not in the list
         */
        public String getType( String uri, String localName ) {
            return getType( getIndex( uri, localName ));
        }
        
        /**
         * Gets the namespace URI for attribute at the given index.
         *
         * @param index the attribute index (zero-based)
         * @return the namespace URI (empty string if none is available) 
         * or null if the index is out of range
         */
        public String getURI( int index ) {
            if ( indexInRange( index ) ) {
                return attributes[index].getURI();
            }
            return null;
        }
        
        /**
         * Gets the value for the attribute at given index.
         * 
         * @param index the attribute index (zero based)
         * @return the attribute value or null if the index is out of range
         * @todo add value caching
         */
        public String getValue( int index ) {
            if ( indexInRange( index )) {
                if (
                    idAttributeName != null 
                    && idAttributeName.equals(attributes[index].getLocalName())) {
                        
                    return idValue;
                    
                } else {
                    Expression expression = attributes[index].getTextExpression();
                    if ( expression != null ) {
                        Object value = expression.evaluate( context );
                        return convertToString( value );
                    }
                }
                return "";
            }
            return null;
        }
        
        /**
         * Gets the value for the attribute by qualified name.
         * 
         * @param qName the qualified name 
         * @return the attribute value or null if there are no attributes 
         * with the given qualified name
         * @todo add value caching
         */
        public String getValue( String qName ) {
            return getValue( getIndex( qName ) );
        }
        
        /**
         * Gets the value for the attribute by namespace name.
         * 
         * @param uri the namespace URI of the attribute
         * @param localName the local name of the attribute
         * @return the attribute value or null if there are not attributes 
         * with the given namespace and local name
         * @todo add value caching
         */
        public String getValue( String uri, String localName ) {
            return getValue( getIndex( uri, localName ) );
        }
        
        /**
         * Is the given index within the range of the attribute list
         *
         * @param index the index whose range will be checked
         * @return true if the index with within the range of the attribute list
         */
        private boolean indexInRange( int index ) {
            return ( index >= 0 && index < attributes.length );
        }
    }
    
    
    // OLD API (DEPRECATED)
    // --------------------------------------------------------------------------------------
    
    
    /** 
     * Get the indentation for the current element. 
     * Used for pretty priting.
     *
     * @return the amount that the current element is indented
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected int getIndentLevel() {
        return 0;
    }
    
    // Expression methods
    //-------------------------------------------------------------------------    

    /** 
     * Express an element tag start using given qualified name.
     *
     * @param qualifiedName the qualified name of the element to be expressed
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void expressElementStart(String qualifiedName) 
                                        throws IOException, SAXException {
        // do nothing
    }
                                        
    /** 
     * Express an element tag start using given qualified name.
     *
     * @param uri the namespace uri 
     * @param localName the local name for this element
     * @param qualifiedName the qualified name of the element to be expressed
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void expressElementStart(String uri, String localName, String qualifiedName) 
                                        throws IOException, SAXException {
        expressElementStart( qualifiedName );
    }
    
     /**
     * Express a closing tag.
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void expressTagClose() throws IOException, SAXException {}
    
    /** 
     * Express an element end tag (with given name) 
     *
     * @param qualifiedName the qualified name for the element to be closed
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void expressElementEnd(String qualifiedName) 
                                              throws IOException, SAXException {
        // do nothing
    }
    
    /** 
     * Express an element end tag (with given name) 
     *
     * @param uri the namespace uri of the element close tag
     * @param localName the local name of the element close tag
     * @param qualifiedName the qualified name for the element to be closed
     *
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void expressElementEnd(
                                                String uri,
                                                String localName,
                                                String qualifiedName) 
                                                    throws 
                                                        IOException, 
                                                        SAXException {
        expressElementEnd(qualifiedName);
    }
                                              
    
    /** 
     * Express an empty element end.
     * 
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void expressElementEnd() throws IOException, SAXException {}

    /** 
     * Express body text 
     *
     * @param text the string to write out as the body of the current element
     * 
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void expressBodyText(String text) throws IOException, SAXException {}
    
    /** 
     * Express an attribute 
     *
     * @param qualifiedName the qualified name of the attribute
     * @param value the attribute value
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void expressAttribute(
                                String qualifiedName, 
                                String value) 
                                    throws
                                        IOException, 
                                        SAXException {
        // Do nothing
    }

    /** 
     * Express an attribute 
     *
     * @param namespaceUri the namespace uri
     * @param localName the local name
     * @param qualifiedName the qualified name of the attribute
     * @param value the attribute value
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void expressAttribute(
                                String namespaceUri,
                                String localName,
                                String qualifiedName, 
                                String value) 
                                    throws
                                        IOException, 
                                        SAXException {
        expressAttribute(qualifiedName, value);
    }
    
    
    /** 
     * Writes the given element 
     *
     * @param qualifiedName qualified name to use for the element
     * @param elementDescriptor the <code>ElementDescriptor</code> describing the element
     * @param context the <code>Context</code> to use to evaluate the bean expressions
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void write( 
                            String qualifiedName, 
                            ElementDescriptor elementDescriptor, 
                            Context context ) 
                                throws 
                                    IOException, 
                                    SAXException,
                                    IntrospectionException {
        writeElement( "", qualifiedName, qualifiedName, elementDescriptor, context );
    }
    
    /** 
     * Writes the given element adding an ID attribute 
     *
     * @param qualifiedName qualified name to use for the element
     * @param elementDescriptor the <code>ElementDescriptor</code> describing the element
     * @param context the <code>Context</code> to use to evaluate the bean expressions
     * @param idAttribute the qualified name of the <code>ID</code> attribute 
     * @param idValue the value for the <code>ID</code> attribute 
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void write( 
                            String qualifiedName, 
                            ElementDescriptor elementDescriptor, 
                            Context context,
                            String idAttribute,
                            String idValue ) 
                                throws 
                                    IOException, 
                                    SAXException,
                                    IntrospectionException {
        writeElement( 
                    "", 
                    qualifiedName, 
                    qualifiedName, 
                    elementDescriptor, 
                    context, 
                    idAttribute, 
                    idValue );
    }
    
    /** 
     * Write attributes, child elements and element end 
     *
     * @param qualifiedName qualified name to use for the element
     * @param elementDescriptor the <code>ElementDescriptor</code> describing the element
     * @param context the <code>Context</code> to use to evaluate the bean expressions
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void writeRestOfElement( 
                            String qualifiedName, 
                            ElementDescriptor elementDescriptor, 
                            Context context ) 
                                throws 
                                    IOException, 
                                    SAXException,
                                    IntrospectionException {
        writeRestOfElement( "", qualifiedName, qualifiedName, elementDescriptor, context );
    }

    /**
     * Writes an element with a <code>IDREF</code> attribute 
     *
     * @param qualifiedName of the element with <code>IDREF</code> attribute 
     * @param idrefAttributeName the qualified name of the <code>IDREF</code> attribute 
     * @param idrefAttributeValue the value for the <code>IDREF</code> attribute 
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void writeIDREFElement( 
                                    String qualifiedName, 
                                    String idrefAttributeName,
                                    String idrefAttributeValue ) 
                                        throws 
                                            IOException, 
                                            SAXException,
                                            IntrospectionException {
        writeIDREFElement( 
                            "", 
                            qualifiedName, 
                            qualifiedName, 
                            idrefAttributeName, 
                            idrefAttributeValue );
    }

        
    /** 
     * Writes the element content.
     *
     * @param elementDescriptor the <code>ElementDescriptor</code> to write as xml 
     * @param context the <code>Context</code> to use to evaluate the bean expressions
     * @return true if some content was written
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @throws IntrospectionException if a java beans introspection problem occurs
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected boolean writeContent( 
                        ElementDescriptor elementDescriptor, 
                        Context context ) 
                            throws 
                                IOException, 
                                SAXException,
                                IntrospectionException {     
        return false;
    }

    
    /**  
     * Writes the attribute declarations 
     *
     * @param elementDescriptor the <code>ElementDescriptor</code> to be written out as xml
     * @param context the <code>Context</code> to use to evaluation bean expressions
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void writeAttributes( 
                    ElementDescriptor elementDescriptor, 
                    Context context ) 
                        throws 
                            IOException, SAXException {
        if (!elementDescriptor.isWrapCollectionsInElement()) {
            return;
        }
            
        AttributeDescriptor[] attributeDescriptors = elementDescriptor.getAttributeDescriptors();
        if ( attributeDescriptors != null ) {
            for ( int i = 0, size = attributeDescriptors.length; i < size; i++ ) {
                AttributeDescriptor attributeDescriptor = attributeDescriptors[i];
                writeAttribute( attributeDescriptor, context );
            }
        }
    }

    
    /** 
     * Writes an attribute declaration 
     *
     * @param attributeDescriptor the <code>AttributeDescriptor</code> to be written as xml
     * @param context the <code>Context</code> to use to evaluation bean expressions
     * @throws IOException if an IO problem occurs during writing
     * @throws SAXException if an SAX problem occurs during writing 
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void writeAttribute( 
                        AttributeDescriptor attributeDescriptor, 
                        Context context ) 
                            throws 
                                IOException, SAXException {
        Expression expression = attributeDescriptor.getTextExpression();
        if ( expression != null ) {
            Object value = expression.evaluate( context );
            if ( value != null ) {
                String text = value.toString();
                if ( text != null && text.length() > 0 ) {
                    expressAttribute(
                                    attributeDescriptor.getURI(),
                                    attributeDescriptor.getLocalName(),
                                    attributeDescriptor.getQualifiedName(), 
                                    text);
                }
            }                
        }
    }
    /** 
     * Writes a empty line.  
     * This implementation does nothing but can be overridden by subclasses.
     *
     * @throws IOException if the line cannot be written
     * @deprecated after 1.0-Alpha-1 replaced by new SAX inspired API
     */
    protected void writePrintln() throws IOException {}
    
    /** 
     * Writes an indentation.
     * This implementation does nothing but can be overridden by subclasses.
     * 
     * @throws IOException if the indent cannot be written
     * @deprecated after 1.0-Alpha-1 replaced by new BeanWriter API
     */
    protected void writeIndent() throws IOException {}
    
    /**
      * Converts an object to a string.
      *
      * @param value the Object to represent as a String, possibly null
      * @return String representation, not null
      */
    private String convertToString(Object value) {
        if ( value != null ) {
            String text = ConvertUtils.convert( value );
            if ( text != null ) {
                return text;
            }
        }
        return "";
    }
}
