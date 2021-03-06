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

package org.apache.commons.betwixt;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
 * @version $Revision$
 */
public class TestArrays extends AbstractTestCase {

   public TestArrays(String testName) {
      super(testName);
   }

   public void testWriteArray() throws Exception {
      StringWriter out = new StringWriter();
      out.write("<?xml version='1.0'?>");
      BeanWriter writer = new BeanWriter(out);
      writer.getIntrospector().getConfiguration().setAttributesForPrimitives(true);
      writer.getBindingConfiguration().setMapIDs(false);

      LibraryBean libraryBean = new LibraryBean();
      libraryBean.addBook(new BookBean("Martin Fowler", "Refactoring", "Addision Wesley"));
      libraryBean.addBook(new BookBean("Ben Laurie", "Apache", "O'Reilly"));
      libraryBean.addBook(new BookBean("Kent Beck", "Test Driven Development", "Addision Wesley"));

      writer.write(libraryBean);
      String xml = out.toString();
      String expected = "<?xml version='1.0'?><LibraryBean>" +
            "<books>" +
            "<book author='Martin Fowler' title='Refactoring' publisher='Addision Wesley'/>" +
            "<book author='Ben Laurie' title='Apache' publisher='O&apos;Reilly'/>" +
            "<book author='Kent Beck' title='Test Driven Development' publisher='Addision Wesley'/>" +
            "</books>" +
            "</LibraryBean>";

      xmlAssertIsomorphicContent(
            parseString(xml),
            parseString(expected),
            true);
   }

   public void testReadArray() throws Exception {
      String xml = "<?xml version='1.0'?><LibraryBean>" +
            "<books>" +
            "<book author='Martin Fowler' title='Refactoring' publisher='Addision Wesley'/>" +
            "<book author='Ben Laurie' title='Apache' publisher='O&apos;Reilly'/>" +
            "<book author='Kent Beck' title='Test Driven Development' publisher='Addision Wesley'/>" +
            "</books>" +
            "</LibraryBean>";

      BeanReader reader = new BeanReader();
      reader.getIntrospector().getConfiguration().setAttributesForPrimitives(true);
      reader.getBindingConfiguration().setMapIDs(false);
      reader.registerBeanClass(LibraryBean.class);
      LibraryBean bean = (LibraryBean) reader.parse(new StringReader(xml));

      BookBean[] books = bean.getBooks();
      assertEquals("Three books read", 3, books.length);
      assertEquals("Book one", new BookBean("Martin Fowler", "Refactoring", "Addision Wesley"), books[0]);
      assertEquals("Book two", new BookBean("Ben Laurie", "Apache", "O'Reilly"), books[1]);
      assertEquals("Book three", new BookBean("Kent Beck", "Test Driven Development", "Addision Wesley"), books[2]);

   }

   public void testWriteArrayWithSetter() throws Exception {
      StringWriter out = new StringWriter();
      out.write("<?xml version='1.0'?>");
      BeanWriter writer = new BeanWriter(out);
      writer.getIntrospector().getConfiguration().setAttributesForPrimitives(true);
      writer.getBindingConfiguration().setMapIDs(false);


      LibraryBeanWithArraySetter libraryBean = new LibraryBeanWithArraySetter();
      BookBean[] books = {
            new BookBean("Martin Fowler", "Refactoring", "Addision Wesley"),
            new BookBean("Ben Laurie", "Apache", "O'Reilly"),
            new BookBean("Kent Beck", "Test Driven Development", "Addision Wesley")};
      libraryBean.setBooks(books);

      writer.write(libraryBean);
      String xml = out.toString();
      String expected = "<?xml version='1.0'?><LibraryBeanWithArraySetter>" +
            "<books>" +
            "<BookBean author='Martin Fowler' title='Refactoring' publisher='Addision Wesley'/>" +
            "<BookBean author='Ben Laurie' title='Apache' publisher='O&apos;Reilly'/>" +
            "<BookBean author='Kent Beck' title='Test Driven Development' publisher='Addision Wesley'/>" +
            "</books>" +
            "</LibraryBeanWithArraySetter>";

      xmlAssertIsomorphicContent(
            parseString(xml),
            parseString(expected),
            true);
   }

   public void testReadArrayWithSetter() throws Exception {
      String xml = "<?xml version='1.0'?><LibraryBeanWithArraySetter>" +
            "<books>" +
            "<BookBean author='Martin Fowler' title='Refactoring' publisher='Addision Wesley'/>" +
            "<BookBean author='Ben Laurie' title='Apache' publisher='O&apos;Reilly'/>" +
            "<BookBean author='Kent Beck' title='Test Driven Development' publisher='Addision Wesley'/>" +
            "</books>" +
            "</LibraryBeanWithArraySetter>";

      BeanReader reader = new BeanReader();
      reader.getIntrospector().getConfiguration().setAttributesForPrimitives(true);
      reader.getBindingConfiguration().setMapIDs(false);
      reader.registerBeanClass(LibraryBeanWithArraySetter.class);
      LibraryBeanWithArraySetter bean = (LibraryBeanWithArraySetter) reader.parse(new StringReader(xml));

      BookBean[] books = bean.getBooks();
      assertEquals("Three books read", 3, books.length);
      assertEquals("Book one", new BookBean("Martin Fowler", "Refactoring", "Addision Wesley"), books[0]);
      assertEquals("Book two", new BookBean("Ben Laurie", "Apache", "O'Reilly"), books[1]);
      assertEquals("Book three", new BookBean("Kent Beck", "Test Driven Development", "Addision Wesley"), books[2]);

   }

   public void testIntrospectArrayWithSetter() throws Exception {
      XMLIntrospector introspector = new XMLIntrospector();

      XMLBeanInfo xmlBeanInfo = introspector.introspect(LibraryBeanWithArraySetter.class);

      ElementDescriptor beanDescriptor = xmlBeanInfo.getElementDescriptor();
      java.util.List<ElementDescriptor> childDescriptors = beanDescriptor.getElementDescriptors();
      assertEquals("Only one child element", 1, childDescriptors.size());

      ElementDescriptor booksWrapperDescriptor = childDescriptors.get(0);
      java.util.List<ElementDescriptor> wrapperChildren = booksWrapperDescriptor.getElementDescriptors();
      assertEquals("Only one child element", 1, childDescriptors.size());
      ElementDescriptor booksDescriptor = wrapperChildren.get(0);
      assertNotNull("Updater for property", booksDescriptor.getUpdater());
   }

}
