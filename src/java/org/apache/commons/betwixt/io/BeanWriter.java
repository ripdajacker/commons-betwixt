/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/BeanWriter.java,v 1.5 2002/06/14 23:05:26 mvdb Exp $
 * $Revision: 1.5 $
 * $Date: 2002/06/14 23:05:26 $
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
 * $Id: BeanWriter.java,v 1.5 2002/06/14 23:05:26 mvdb Exp $
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


/** <p><code>BeanWriter</code> outputs beans as XML to an io stream.</p>
  *
  * <p>The output for each bean is an xml fragment
  * (rather than a well-formed xml-document).
  * This allows bean representations to be appended to a document 
  * by writing each in turn to the stream.
  * So to create a well formed xml document, 
  * you'll need to write the prolog to the stream first.
  * If you append more than one bean to the stream, 
  * then you'll need to add a wrapping root element as well.
  *
  * <p> The line ending to be used is set by {@link #setEndOfLine}. 
  * 
  * <p> The output can be formatted (with whitespace) for easy reading 
  * by calling {@link #enablePrettyPrint}. 
  * The output will be indented. 
  * The indent string used is set by {@link #setIndent}.
  *
  * <p> Bean graphs can sometimes contain cycles. 
  * Care must be taken when serializing cyclic bean graphs
  * since this can lead to infinite recursion. 
  * The approach taken by <code>BeanWriter</code> is to automatically
  * assign an <code>ID</code> attribute value to beans.
  * When a cycle is encountered, 
  * an element is written that has the <code>IDREF</code> attribute set to the 
  * id assigned earlier.
  *
  * <p> The names of the <code>ID</code> and <code>IDREF</code> attributes used 
  * can be customized by the <code>XMLBeanInfo</code>.
  * The id's used can also be customized by the user 
  * via <code>IDGenerator</code> subclasses.
  * The implementation used can be set by the <code>IdGenerator</code> property.
  * BeanWriter defaults to using <code>SequentialIDGenerator</code> 
  * which supplies id values in numeric sequence.
  * 
  * <p>If generated <code>ID</code> attribute values are not acceptable in the output,
  * then this can be disabled by setting the <code>WriteIDs</code> property to false.
  * If a cyclic reference is encountered in this case then a
  * <code>CyclicReferenceException</code> will be thrown. 
  * When the <code>WriteIDs</code> property is set to false,
  * it is recommended that this exception is caught by the caller.
  * 
  * 
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
  * @version $Revision: 1.5 $
  */
public class BeanWriter {

    /** Escaped <code>&lt;</code> entity */
    private final static String LESS_THAN_ENTITY = "&lt;";
    /** Escaped <code>&gt;</code> entity */
    private final static String GREATER_THAN_ENTITY = "&gt;";
    /** Escaped <code>&amp;</code> entity */
    private final static String AMPERSAND_ENTITY = "&amp;";
    /** Escaped <code>'</code> entity */
    private final static String APOSTROPHE_ENTITY = "&apos;";
    /** Escaped <code>"</code> entity */
    private final static String QUOTE_ENTITY = "&quot;";

    /** Introspector used */
    private XMLIntrospector introspector = new XMLIntrospector();
    /** Where the output goes */
    private Writer writer;    
    /** text used for end of lines. Defaults to <code>\n</code>*/
    private String endOfLine = "\n";
    /** indentation text */
    private String indent;
    /** indentation level */
    private int indentLevel;
    /** should we flush after writing bean */
    private boolean autoFlush;
    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( BeanWriter.class );
    /** Map containing ID attribute values for beans */
    private HashMap idMap = new HashMap();
    /** Used to generate ID attribute values*/
    private IDGenerator idGenerator = new SequentialIDGenerator();
    /** Should generated <code>ID</code> attribute values be added to the elements? */
    private boolean writeIDs = true;
    
    /**
     * <p> Constructor uses <code>System.out</code> for output.</p>
     */
    public BeanWriter() {
        this( System.out );
    }
    
    /**
     * <p> Constuctor uses given <code>OutputStream</code> for output.</p>
     *
     * @param out write out representations to this stream
     */
    public BeanWriter(OutputStream out) {
        this.writer = new BufferedWriter( new OutputStreamWriter( out ) );
        this.autoFlush = true;
    }

    /**
     * <p> Constructor sets writer used for output.</p>
     *
     * @param writer write out representations to this writer
     */
    public BeanWriter(Writer writer) {
        this.writer = writer;
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
     * @throws CyclicReferenceException when a cyclic reference is encountered 
     *
     * @param bean write out representation of this bean
     */
    public void write(Object bean) throws IOException, IntrospectionException  {
        log.debug( "Writing bean graph..." );
        log.debug( bean );
        
        write( null, bean );

        if ( autoFlush ) {
            writer.flush();
        }
        
        log.debug( "Finished writing bean graph." );
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
                
                Object ref = null;
                Object id = null;
                
                // only give id's to non-primatives
                if ( elementDescriptor.isPrimitiveType() ) {
                    // write without an id
                    write( 
                        qualifiedName, 
                        elementDescriptor, 
                        context );
                } 
                else {
                
                    ref = idMap.get( context.getBean() );
                    if ( ref == null ) {
                        // this is the first time that this bean has be written
                        AttributeDescriptor idAttribute = beanInfo.getIDAttribute();
                        if (idAttribute == null) {
                            // use a generated id
                            id = new Integer( idGenerator.nextId() );
                            idMap.put( bean, id);
                            
                            if ( writeIDs ) {
                                // write element with id
                                write( 
                                    qualifiedName, 
                                    elementDescriptor, 
                                    context , 
                                    beanInfo.getIDAttributeName(),
                                    id.toString());
                                    
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
                            id = idAttribute.getTextExpression().evaluate( context );
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
                                        ref.toString());
                    }
                }
            }
        }
        
        log.trace( "Finished writing bean graph." );
    }

    /**
     * <p> Switch on formatted output.
     * This sets the end of line and the indent.
     * The default is adding 2 spaces and a newline
     */
    public void enablePrettyPrint() {
        endOfLine = "\n";
        indent = "  ";
    }

    /** Returns the string used for end of lines */
    public String getEndOfLine() {
        return endOfLine;
    }
    
    /** Sets the string used for end of lines */
    public void setEndOfLine(String endOfLine) {
        this.endOfLine = endOfLine;
    }

    /** Returns the string used for indentation */
    public String getIndent() {
        return indent;
    }
    
    /** Sets the string used for end of lines */
    public void setIndent(String indent) {
        this.indent = indent;
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
    protected void expressElementStart(String qualifiedName) throws IOException {
        if ( qualifiedName == null ) {
            // XXX this indicates a programming error
            log.fatal( "[expressElementStart]Qualified name is null." );
            throw new RuntimeException( "Qualified name is null." );
        }
        
        writePrintln();
        writeIndent();
        writer.write( "<" );
        writer.write( qualifiedName );
    }
    
    /** Express an element end tag using given qualifiedName */
    protected void expressElementEnd(String qualifiedName) throws IOException {
        if (qualifiedName == null) {
            // XXX this indicates a programming error
            log.fatal( "[expressElementEnd]Qualified name is null." );
            throw new RuntimeException( "Qualified name is null." );
        }
        
        writer.write( "</" );
        writer.write( qualifiedName );
        writer.write( ">" );
    }    
    
    /** Express an empty element end */
    protected void expressElementEnd() throws IOException {
        writer.write( "/>" );
    }

    /** Express body text */
    protected void expressBodyText(String text) throws IOException {
        if ( text == null ) {
            // XXX This is probably a programming error
            log.error( "[expressBodyText]Body text is null" );
            
        } else {
            writer.write( text );
        }
    }
    
    /** Express an attribute */
    protected void expressAttribute(
                                String qualifiedName, 
                                String value) 
                                    throws
                                        IOException{
        if ( value == null ) {
            // XXX probably a programming error
            log.error( "Null attribute value." );
            return;
        }
        
        if ( qualifiedName == null ) {
            // XXX probably a programming error
            log.error( "Null attribute value." );
            return;
        }
                
        writer.write( " " );
        writer.write( qualifiedName );
        writer.write( "=\"" );
        writer.write( value );
        writer.write( "\"" );
    }


    // Implementation methods
    //-------------------------------------------------------------------------    
    

    /** Writes the given element */
    protected void write( 
                            String qualifiedName, 
                            ElementDescriptor elementDescriptor, 
                            Context context ) 
                                throws 
                                    IOException, 
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
                                if ( ! writtenContent ) {
                                    writtenContent = true;
                                    writer.write( ">" );
                                }
                                ++indentLevel;
                                write( qualifiedName, iter.next() );
                                --indentLevel;
                            }
                        }
                        else {
                            if ( ! writtenContent ) {
                                writtenContent = true;
                                writer.write( ">" );
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
                        writer.write( ">" );
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
                    String text = escapeBodyValue(value);
                    if ( text != null && text.length() > 0 ) {
                        if ( ! writtenContent ) {
                            writtenContent = true;
                            writer.write( ">" );
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
                            IOException {
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
                                IOException {
        Expression expression = attributeDescriptor.getTextExpression();
        if ( expression != null ) {
            Object value = expression.evaluate( context );
            if ( value != null ) {
                String text = escapeAttributeValue(value);
                if ( text != null && text.length() > 0 ) {
                    expressAttribute(attributeDescriptor.getQualifiedName(), text);
                }
            }                
        }
    }
    
    /** Writes out an empty line.
     * Uses current <code>endOfLine</code>.
     */
    protected void writePrintln() throws IOException {
        if ( endOfLine != null ) {
            writer.write( endOfLine );
        }
    }
    
    /** Writes out <code>indent</code>'s to the current <code>indentLevel</code>
     */
    protected void writeIndent() throws IOException {
        if ( indent != null ) {
            for ( int i = 0; i < indentLevel; i++ ) {
                writer.write( getIndent() );
            }
        }
    }
    
    /** 
     * <p>Escape the <code>toString</code> of the given object.
     * For use as body text.</p>
     */
    protected String escapeBodyValue(Object value) {
        StringBuffer buffer = new StringBuffer(value.toString());
        for (int i=0, size = buffer.length(); i <size; i++) {
            switch (buffer.charAt(i)) {
                case '<':
                    buffer.replace(i, i+1, LESS_THAN_ENTITY);
                    size += 3;
                    i+=3;
                    break;
                 case '>':
                    buffer.replace(i, i+1, GREATER_THAN_ENTITY);
                    size += 3;
                    i += 3;
                    break;
                 case '&':
                    buffer.replace(i, i+1, AMPERSAND_ENTITY);
                    size += 4;
                    i += 4;
                    break;        
            }
        }
        return buffer.toString();
    }

    /** 
     * <p>Escape the <code>toString</code> of the given object.
     * For use in an attribute value.</p>
     */
    protected String escapeAttributeValue(Object value) {
        StringBuffer buffer = new StringBuffer(value.toString());
        for (int i=0, size = buffer.length(); i <size; i++) {
            switch (buffer.charAt(i)) {
                case '<':
                    buffer.replace(i, i+1, LESS_THAN_ENTITY);
                    size += 3;
                    i+=3;
                    break;
                 case '>':
                    buffer.replace(i, i+1, GREATER_THAN_ENTITY);
                    size += 3;
                    i += 3;
                    break;
                 case '&':
                    buffer.replace(i, i+1, AMPERSAND_ENTITY);
                    size += 4;
                    i += 4;
                    break;
                 case '\'':
                    buffer.replace(i, i+1, APOSTROPHE_ENTITY);
                    size += 4;
                    i += 4;
                    break;
                 case '\"':
                    buffer.replace(i, i+1, QUOTE_ENTITY);
                    size += 5;
                    i += 5;
                    break;           
            }
        }
        return buffer.toString();
    }

}
