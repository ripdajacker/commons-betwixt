/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/AbstractBeanWriter.java,v 1.5 2002/10/12 15:26:22 jstrachan Exp $
 * $Revision: 1.5 $
 * $Date: 2002/10/12 15:26:22 $
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
 * $Id: AbstractBeanWriter.java,v 1.5 2002/10/12 15:26:22 jstrachan Exp $
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

import org.xml.sax.SAXException;

// FIX ME!!!
// Logging logic!

// FIX ME!!
// Error handling strategy!
// i'm going to add SAXExceptions everywhere since it's the easiest way to make things work quick
// but this is a poor strategy

/**
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @version $Revision: 1.5 $
  */
abstract public class AbstractBeanWriter {

    /** Introspector used */
    protected XMLIntrospector introspector = new XMLIntrospector();

    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( AbstractBeanWriter.class );
    /** Map containing ID attribute values for beans */
    protected HashMap idMap = new HashMap();
    /** Used to generate ID attribute values*/
    protected IDGenerator idGenerator = new SequentialIDGenerator();
    /** Should generated <code>ID</code> attribute values be added to the elements? */
    protected boolean writeIDs = true;
    
    /** indentation level */
    protected int indentLevel;

    /** 
     * <p> Writes the given bean to the current stream using the XML introspector.</p>
     * 
     * <p> This writes an xml fragment representing the bean to the current stream.</p>
     *
     * <p>This method will throw a <code>CyclicReferenceException</code> when a cycle
     * is encountered in the graph <strong>only</strong> if the <code>WriteIDs</code>
     * property is false.</p>
     *
     * @throws CyclicReferenceException when a cyclic reference is encountered 
     *
     * @param bean write out representation of this bean
     */
    public void write(Object bean) throws IOException, SAXException, IntrospectionException  {
        log.debug( "Writing bean graph..." );
        log.debug( bean );
        start();
        write( null, bean );
        end();
        log.debug( "Finished writing bean graph." );
    }
    
    /**
     * Marks the start of the bean writing.
     * By default doesn't do anything, but can be used
     * to do extra start processing 
     * @throws IOException
     * @throws SAXException
     */
    public void start() throws IOException, SAXException {
    }
    
    /**
     * Marks the start of the bean writing.
     * By default doesn't do anything, but can be used
     * to do extra end processing 
     * @throws IOExcpetion
     * @throws SAXException
     */
    
    public void end() throws IOException, SAXException {
    }
        
    
    
    /** 
     * <p>Writes the given bean to the current stream using the given <code>qualifiedName</code>.</p>
     *
     * <p>This method will throw a <code>CyclicReferenceException</code> when a cycle
     * is encountered in the graph <strong>only</strong> if the <code>WriteIDs</code>
     * property is false.</p>
     *
     * @throws CyclicReferenceException when a cyclic reference is encountered 
     */
    public void write(
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
                
                String ref = null;
                String id = null;
                
                // only give id's to non-primatives
                if ( elementDescriptor.isPrimitiveType() ) {
                    // write without an id
                    write( 
                        qualifiedName, 
                        elementDescriptor, 
                        context );
                } 
                else {
                
                    ref = (String) idMap.get( context.getBean() );
                    if ( ref == null ) {
                        // this is the first time that this bean has be written
                        AttributeDescriptor idAttribute = beanInfo.getIDAttribute();
                        if (idAttribute == null) {
                            // use a generated id
                            id = idGenerator.nextId();
                            idMap.put( bean, id );
                            
                            if ( writeIDs ) {
                                // write element with id
                                write( 
                                    qualifiedName, 
                                    elementDescriptor, 
                                    context , 
                                    beanInfo.getIDAttributeName(),
                                    id);
                                    
                            } else {    
                                // write element without ID
                                write( 
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
                            write( 
                                qualifiedName, 
                                elementDescriptor, 
                                context );
                        }
                    } 
                    else {
                        // we have a cyclic reference
                        if ( !writeIDs ) {
                            // if we're not writing IDs, then throw exception
                            throw new CyclicReferenceException();
                        }
                        
                        // we've already written this bean so write an IDREF
                        writeIDREFElement( 
                                        qualifiedName,  
                                        beanInfo.getIDREFAttributeName(), 
                                        ref);
                    }
                }
            }
        }
        
        log.trace( "Finished writing bean graph." );
    }
    
    /** 
      * Get <code>IDGenerator</code> implementation used to generate <code>ID</code> attribute values .
      *
      * @return implementation used for <code>ID</code> attribute generation
      */
    public IDGenerator getIdGenerator() {
        return idGenerator;
    }
    
    /** 
      * Set <code>IDGenerator</code> implementation used to generate <code>ID</code> attribute values.
      * This property can be used to customize the algorithm used for generation.
      *
      * @param idGenerator use this implementation for <code>ID</code> attribute generation
      */
    public void setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
    
    /** Get whether generated <code>ID</code> attribute values should be added to the elements */
    public boolean getWriteIDs() {
        return writeIDs;
    }

    /** 
     * Set whether generated <code>ID</code> attribute values should be added to the elements 
     * If this property is set to false, then <code>CyclicReferenceException</code> 
     * will be thrown whenever a cyclic occurs in the bean graph.
     */
    public void setWriteIDs(boolean writeIDs) {
        this.writeIDs = writeIDs;
    }

    /**
     * <p> Get the introspector used. </p>
     *
     * <p> The {@link XMLBeanInfo} used to map each bean is created by the <code>XMLIntrospector</code>.
     * One way in which the mapping can be customized is by altering the <code>XMLIntrospector</code>. </p>
     */
    public XMLIntrospector getXMLIntrospector() {
        return introspector;
    }
    

    /**
     * <p> Set the introspector to be used. </p>
     *
     * <p> The {@link XMLBeanInfo} used to map each bean is created by the <code>XMLIntrospector</code>.
     * One way in which the mapping can be customized is by altering the <code>XMLIntrospector</code>. </p>
     *
     * @param introspector use this introspector
     */
    public void  setXMLIntrospector(XMLIntrospector introspector) {
        this.introspector = introspector;
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
    abstract protected void expressElementStart(String qualifiedName) throws IOException, SAXException;
        
    abstract protected void expressTagClose() throws IOException, SAXException;
    
    /** Express an element end tag using given qualifiedName */
    abstract protected void expressElementEnd(String qualifiedName) throws IOException, SAXException;
    
    /** Express an empty element end */
    abstract protected void expressElementEnd() throws IOException, SAXException;

    /** Express body text */
    abstract protected void expressBodyText(String text) throws IOException, SAXException;
    
    /** Express an attribute */
    abstract protected void expressAttribute(
                                String qualifiedName, 
                                String value) 
                                    throws
                                        IOException, 
                                        SAXException;


    // Implementation methods
    //-------------------------------------------------------------------------    
    

    /** Writes the given element */
    protected void write( 
                            String qualifiedName, 
                            ElementDescriptor elementDescriptor, 
                            Context context ) 
                                throws 
                                    IOException, 
                                    SAXException,
                                    IntrospectionException {
                                        
        if (elementDescriptor.isWrapCollectionsInElement()) {
            expressElementStart( qualifiedName );
        }
        
        writeRestOfElement( qualifiedName, elementDescriptor, context);
    }
    
    

    /** Writes the given element adding an ID attribute */
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
                                  
        expressElementStart( qualifiedName );
             
        expressAttribute( idAttribute, idValue );        
        
        writeRestOfElement( qualifiedName, elementDescriptor, context );
    }
    
    /** Write attributes, child elements and element end */
    protected void writeRestOfElement( 
                            String qualifiedName, 
                            ElementDescriptor elementDescriptor, 
                            Context context ) 
                                throws 
                                    IOException, 
                                    SAXException,
                                    IntrospectionException {

        if (elementDescriptor.isWrapCollectionsInElement()) {
            writeAttributes( elementDescriptor, context );
        }

        if ( writeContent( elementDescriptor, context ) ) {
            if (elementDescriptor.isWrapCollectionsInElement()) {
                expressElementEnd( qualifiedName );
            }
        }  
        else {
            if (elementDescriptor.isWrapCollectionsInElement()) {
                expressElementEnd();
            }
        }
    }
    

    
    
    protected void writeIDREFElement( 
                                    String qualifiedName, 
                                    String idrefAttributeName,
                                    String idrefAttributeValue ) 
                                        throws 
                                            IOException, 
                                            SAXException,
                                            IntrospectionException {

        // write IDREF element
        expressElementStart( qualifiedName );
        
        expressAttribute( idrefAttributeName, idrefAttributeValue );
                             
        expressElementEnd();
    }
        
    /** Writes the element content.
     *
     * @return true if some content was written
     */
    protected boolean writeContent( 
                        ElementDescriptor elementDescriptor, 
                        Context context ) 
                            throws 
                                IOException, 
                                SAXException,
                                IntrospectionException {        
        ElementDescriptor[] childDescriptors = elementDescriptor.getElementDescriptors();
        boolean writtenContent = false;
        if ( childDescriptors != null && childDescriptors.length > 0 ) {
            // process child elements
            for ( int i = 0, size = childDescriptors.length; i < size; i++ ) {
                ElementDescriptor childDescriptor = childDescriptors[i];
                Context childContext = context;
                Expression childExpression = childDescriptor.getContextExpression();
                if ( childExpression != null ) {
                    Object childBean = childExpression.evaluate( context );
                    if ( childBean != null ) {
                        String qualifiedName = childDescriptor.getQualifiedName();
                        // XXXX: should we handle nulls better
                        if ( childBean instanceof Iterator ) {
                            for ( Iterator iter = (Iterator) childBean; iter.hasNext(); ) {
                                Object object = iter.next();
                                if (object == null) {
                                    continue;
                                }
                                if ( ! writtenContent ) {
                                    writtenContent = true;
                                    if (elementDescriptor.isWrapCollectionsInElement()) {
                                        expressTagClose();
                                    }
                                }
                                ++indentLevel;
                                write( qualifiedName, object );
                                --indentLevel;
                            }
                        }
                        else {
                            if ( ! writtenContent ) {
                                writtenContent = true;
                                expressTagClose();
                            }
                            ++indentLevel;
                            write( qualifiedName, childBean );
                            --indentLevel;
                        }
                    }                    
                }
                else {
                    if ( ! writtenContent ) {
                        writtenContent = true;
                        expressTagClose();
                    }
                    if (childDescriptor.isWrapCollectionsInElement()) {
                        ++indentLevel;
                    }

                     write( childDescriptor.getQualifiedName(), childDescriptor, childContext );

                    if (childDescriptor.isWrapCollectionsInElement()) {
                        --indentLevel;
                    }
                }
            }
            if ( writtenContent ) {
                writePrintln();
                writeIndent();
            }
        }
        else {
            // evaluate the body text 
            Expression expression = elementDescriptor.getTextExpression();
            if ( expression != null ) {
                Object value = expression.evaluate( context );
                if ( value != null ) {
                    String text = value.toString();
                    if ( text != null && text.length() > 0 ) {
                        if ( ! writtenContent ) {
                            writtenContent = true;
                            expressTagClose();
                        }
                        expressBodyText(text);
                    }
                }                
            }
        }
        return writtenContent;
    }
    
    /** Writes the attribute declarations */
    protected void writeAttributes( 
                    ElementDescriptor elementDescriptor, 
                    Context context ) 
                        throws 
                            IOException, SAXException {
        if (!elementDescriptor.isWrapCollectionsInElement()) 
            return;
            
        AttributeDescriptor[] attributeDescriptors = elementDescriptor.getAttributeDescriptors();
        if ( attributeDescriptors != null ) {
            for ( int i = 0, size = attributeDescriptors.length; i < size; i++ ) {
                AttributeDescriptor attributeDescriptor = attributeDescriptors[i];
                writeAttribute( attributeDescriptor, context );
            }
        }
    }

    
    /** Writes an attribute declaration */
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
                    expressAttribute(attributeDescriptor.getQualifiedName(), text);
                }
            }                
        }
    }

    protected void writePrintln() throws IOException {}
    protected void writeIndent() throws IOException {}
}
