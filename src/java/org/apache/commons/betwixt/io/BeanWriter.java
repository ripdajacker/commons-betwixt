/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/BeanWriter.java,v 1.9 2002/08/01 03:58:01 jstrachan Exp $
 * $Revision: 1.9 $
 * $Date: 2002/08/01 03:58:01 $
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
 * $Id: BeanWriter.java,v 1.9 2002/08/01 03:58:01 jstrachan Exp $
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
  * @version $Revision: 1.9 $
  */
public class BeanWriter extends AbstractBeanWriter {

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

    /** Where the output goes */
    private Writer writer;    
    /** text used for end of lines. Defaults to <code>\n</code>*/
    private static final String EOL = "\n";
    /** text used for end of lines. Defaults to <code>\n</code>*/
    private String endOfLine = EOL;
    /** indentation text */
    private String indent;

    /** should we flush after writing bean */
    private boolean autoFlush;
    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( BeanWriter.class );
    
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
     * A helper method that allows you to write the XML Declaration.
     * This should only be called once before you output any beans.
     * 
     * @param xmlDeclaration is the XML declaration string typically of
     *  the form "&lt;xml version='1.0' encoding='UTF-8' ?&gt;
     */
    public void writeXmlDeclaration(String xmlDeclaration) throws IOException {
        writer.write( xmlDeclaration );
        writePrintln();
    }
    
    /**
     * Allows output to be flushed on the underlying output stream
     */
    public void flush() throws IOException {
        writer.flush();
    }
    
    /**
     * Closes the underlying output stream
     */
    public void close() throws IOException {
        writer.close();
    }
    
    public void write(Object bean) throws IOException, SAXException, IntrospectionException  {

        super.write(bean);

        if ( autoFlush ) {
            writer.flush();
        }
    }
    
 
    /**
     * <p> Switch on formatted output.
     * This sets the end of line and the indent.
     * The default is adding 2 spaces and a newline
     */
    public void enablePrettyPrint() {
        endOfLine = EOL;
        indent = "  ";
    }

    /** Returns the string used for end of lines */
    public String getEndOfLine() {
        return endOfLine;
    }
    
    /** 
     * Sets the string used for end of lines 
     * Produces a warning the specified value contains an invalid whitespace character
     */
    public void setEndOfLine(String endOfLine) {
        this.endOfLine = endOfLine;
        for (int i = 0; i < endOfLine.length(); i++) {
            if (!Character.isWhitespace(endOfLine.charAt(i))) {
                log.warn("Invalid EndOfLine character(s)");
                break;
            }
        }
        
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
        writer.write( '<' );
        writer.write( qualifiedName );
    }
    
    protected void expressTagClose() throws IOException {
        writer.write( '>' );
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
        writer.write( '>' );
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
            writer.write( escapeBodyValue(text) );
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
                
        writer.write( ' ' );
        writer.write( qualifiedName );
        writer.write( "=\"" );
        writer.write( escapeAttributeValue(value) );
        writer.write( '\"' );
    }


    // Implementation methods
    //-------------------------------------------------------------------------    
            
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
