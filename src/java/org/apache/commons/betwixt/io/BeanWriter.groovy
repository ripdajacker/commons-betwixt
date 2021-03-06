/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License") you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.betwixt.io

import groovy.transform.CompileStatic
import groovy.util.logging.Commons
import org.apache.commons.betwixt.XMLUtils
import org.apache.commons.betwixt.strategy.MixedContentEncodingStrategy
import org.xml.sax.Attributes
import org.xml.sax.SAXException

import java.beans.IntrospectionException

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
 * <p> The line ending to be used is set by {@link BeanWriter#setEndOfLine(java.lang.String)}.
 *
 * <p> The output can be formatted (with whitespace) for easy reading
 * by calling {@link BeanWriter#enablePrettyPrint}.
 * The output will be indented.
 * The indent string used is set by {@link BeanWriter#setIndent}.
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
 * BeanWriter defaults to using <code>BaseTenIdGenerator</code>
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
 */
@Commons
@CompileStatic
public class BeanWriter extends AbstractBeanWriter {

    /** Where the output goes */
    Writer writer
    /** text used for end of lines. Defaults to <code>\n</code>*/
    private static final String EOL = System.lineSeparator()

    /** text used for end of lines. Defaults to <code>\n</code>*/
    String endOfLine = EOL
    /** Initial level of indentation (starts at 1 with the first element by default) */
    int initialIndentLevel = 1
    /** indentation text */
    String indent

    /** should we flush after writing bean */
    private boolean autoFlush
    /** Has any content (excluding attributes) been written to the current element */
    private boolean currentElementIsEmpty = false
    /** Has the current element written any body text */
    private boolean currentElementHasBodyText = false
    /** Has the last start tag been closed */
    private boolean closedStartTag = true
    /** Should an end tag be added for empty elements? */
    boolean endTagForEmptyElement = false
    /** Current level of indentation */
    private int indentLevel
    /** Used to determine how body content should be encoded before being output*/
    MixedContentEncodingStrategy mixedContentEncodingStrategy = MixedContentEncodingStrategy.DEFAULT

    /**
     * <p> Constructor uses <code>System.out</code> for output.</p>
     */
    public BeanWriter() {
        this(System.out)
    }

    /**
     * <p> Constuctor uses given <code>OutputStream</code> for output.</p>
     *
     * @param out write out representations to this stream
     */
    public BeanWriter(OutputStream out) {
        this.writer = new BufferedWriter(new OutputStreamWriter(out))
        this.autoFlush = true
    }

    /**
     * <p>Constuctor uses given <code>OutputStream</code> for output
     * and allows encoding to be set.</p>
     *
     * @param out write out representations to this stream
     * @param enc the name of the encoding to be used. This should be compatible
     * with the encoding types described in <code>java.io</code>
     * @throws UnsupportedEncodingException if the given encoding is not supported
     */
    public BeanWriter(OutputStream out, String enc) throws UnsupportedEncodingException {
        this.writer = new BufferedWriter(new OutputStreamWriter(out, enc))
        this.autoFlush = true
    }

    /**
     * <p> Constructor sets writer used for output.</p>
     *
     * @param writer write out representations to this writer
     */
    public BeanWriter(Writer writer) {
        this.writer = writer
    }

    /**
     * A helper method that allows you to write the XML Declaration.
     * This should only be called once before you output any beans.
     *
     * @param xmlDeclaration is the XML declaration string typically of
     *  the form "&ltxml version='1.0' encoding='UTF-8' ?&gt
     *
     * @throws IOException when declaration cannot be written
     */
    public void writeXmlDeclaration(String xmlDeclaration) throws IOException {
        writer.write(xmlDeclaration)
        printLine()
    }

    /**
     * Allows output to be flushed on the underlying output stream
     *
     * @throws IOException when the flush cannot be completed
     */
    public void flush() throws IOException {
        writer.flush()
    }

    /**
     * Closes the underlying output stream
     *
     * @throws IOException when writer cannot be closed
     */
    public void close() throws IOException {
        writer.close()
    }

    /**
     * Write the given object to the stream (and then flush).
     *
     * @param bean write this <code>Object</code> to the stream
     * @throws IOException if an IO problem causes failure
     * @throws SAXException if a SAX problem causes failure
     * @throws IntrospectionException if bean cannot be introspected
     */
    public void write(Object bean) throws IOException, SAXException, IntrospectionException {
        super.write(bean)

        if (autoFlush) {
            writer.flush()
        }
    }

    /**
     * <p> Switch on formatted output.
     * This sets the end of line and the indent.
     * The default is adding 2 spaces and a newline
     */
    public void enablePrettyPrint() {
        endOfLine = EOL
        indent = "  "
    }

    /**
     * Sets the string used for end of lines
     * Produces a warning the specified value contains an invalid whitespace character
     *
     * @param endOfLine the <code>String</code to use
     */
    public void setEndOfLine(String endOfLine) {
        this.endOfLine = endOfLine
    }

    /**
     * Writes the start tag for an element.
     *
     * @param uri the element's namespace uri
     * @param local the element's local name
     * @param qualifiedName the element's qualified name
     * @param attr the element's attributes
     * @since 0.5
     */
    protected void startElement(WriteContext context, String uri, String local, String qualifiedName, Attributes attr) {
        if (!closedStartTag) {
            writer.write('>')
            printLine()
        }

        indentLevel++
        indent()

        writer.write("<$qualifiedName")

        for (int i = 0; i < attr.getLength(); i++) {
            def escapedValue = XMLUtils.escapeAttributeValue(attr.getValue(i))
            writer.write(" ${attr.getQName(i)}=\"$escapedValue\"")
        }
        closedStartTag = false
        currentElementIsEmpty = true
        currentElementHasBodyText = false
    }

    /**
     * Writes the end tag for an element
     *
     * @param uri the element's namespace uri
     * @param localName the element's local name
     * @param qualifiedName the element's qualified name
     *
     * @since 0.5
     */
    protected void endElement(WriteContext context, String uri, String localName, String qualifiedName) {
        if (!endTagForEmptyElement && !closedStartTag && currentElementIsEmpty) {
            writer.write("/>")
            closedStartTag = true
        } else {
            if (endTagForEmptyElement && !closedStartTag) {
                writer.write(">")
                closedStartTag = true
            } else if (!currentElementHasBodyText) {
                indent()
            }
            writer.write("</$qualifiedName>")

        }
        indentLevel--
        printLine()

        currentElementHasBodyText = false
    }

    /**
     * Write element body text
     *
     * @param text write out this body text
     * @throws IOException when the stream write fails
     * @since 0.5
     */
    protected void bodyText(WriteContext context, String text) {
        if (text == null) {
            // XXX This is probably a programming error
            log.error("[expressBodyText] body text is null")

        } else {
            if (!closedStartTag) {
                writer.write('>')
                closedStartTag = true
            }

            def encoded = mixedContentEncodingStrategy.encode(text, context.getCurrentDescriptor())
            writer.write(encoded)
            currentElementIsEmpty = false
            currentElementHasBodyText = true
        }
    }

    /**
     * Writes out an empty line.
     * Uses current <code>endOfLine</code>.
     */
    private void printLine() {
        if (endOfLine != null) {
            writer.write(endOfLine)
        }
    }

    /**
     * Writes out <code>indent</code>'s to the current <code>indentLevel</code>
     */
    private void indent() {
        if (indent != null) {
            for (int i = 1 - initialIndentLevel; i < indentLevel; i++) {
                writer.write(getIndent())
            }
        }
    }

    /**
     * Writes out an empty line.
     * Uses current <code>endOfLine</code>.
     *
     * @deprecated 0.5 replaced by new SAX inspired API
     */

    /**
     * Writes out <code>indent</code>'s to the current <code>indentLevel</code>
     *
     * @deprecated 0.5 replaced by new SAX inspired API
     */
}
