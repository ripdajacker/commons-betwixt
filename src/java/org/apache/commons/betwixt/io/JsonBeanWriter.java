/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
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
package org.apache.commons.betwixt.io;

/**
 * <p>
 * <code>BeanWriter</code> outputs beans as XML to an io stream.
 * </p>
 * <p/>
 * <p/>
 * The output for each bean is an xml fragment (rather than a well-formed xml-document). This allows
 * bean representations to be appended to a document by writing each in turn to the stream. So to
 * create a well formed xml document, you'll need to write the prolog to the stream first. If you
 * append more than one bean to the stream, then you'll need to add a wrapping root element as well.
 * <p/>
 * <p/>
 * The line ending to be used is set by {@link #setEndOfLine}.
 * <p/>
 * <p/>
 * The output can be formatted (with whitespace) for easy reading by calling
 * {@link #enablePrettyPrint}. The output will be indented. The indent string used is set by
 * {@link #setIndent}.
 * <p/>
 * <p/>
 * Bean graphs can sometimes contain cycles. Care must be taken when serializing cyclic bean graphs
 * since this can lead to infinite recursion. The approach taken by <code>BeanWriter</code> is to
 * automatically assign an <code>ID</code> attribute value to beans. When a cycle is encountered, an
 * element is written that has the <code>IDREF</code> attribute set to the id assigned earlier.
 * <p/>
 * <p/>
 * The names of the <code>ID</code> and <code>IDREF</code> attributes used can be customized by the
 * <code>XMLBeanInfo</code>. The id's used can also be customized by the user via
 * <code>IDGenerator</code> subclasses. The implementation used can be set by the
 * <code>IdGenerator</code> property. BeanWriter defaults to using
 * <code>SequentialIDGenerator</code> which supplies id values in numeric sequence.
 * <p/>
 * <p/>
 * If generated <code>ID</code> attribute values are not acceptable in the output, then this can be
 * disabled by setting the <code>WriteIDs</code> property to false. If a cyclic reference is
 * encountered in this case then a <code>CyclicReferenceException</code> will be thrown. When the
 * <code>WriteIDs</code> property is set to false, it is recommended that this exception is caught
 * by the caller.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 */
public class JsonBeanWriter extends AbstractBeanWriter {

}
