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

package org.apache.commons.betwixt.expression;

import java.lang.reflect.Array;
import java.util.*;


/**
 * <p><code>IteratorExpression</code> returns an iterator over the current context.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision$
 */
public class IteratorExpression implements Expression {

    /**
     * Use this <code>Expression</code> to perform initial evaluation
     */
    private final Expression expression;

    /**
     * Construct <code>IteratorExpression</code> using given expression for initial evaluation.
     *
     * @param expression this expression will be evaluated and the result converted to an
     *                   iterator.
     */
    public IteratorExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * Returns an interator over the current context
     *
     * @see org.apache.commons.betwixt.expression.Expression
     */
    public Object evaluate(Context context) {
        // evaluate wrapped expression against context
        Object value = expression.evaluate(context);

        // based on the class of the result,
        // return an appropriate iterator
        if (value instanceof Iterator) {
            // if the value is an iterator, we're done
            return value;

        } else if (value instanceof Collection) {
            // if it's a collection, return an iterator for that collection
            Collection collection = (Collection) value;
            return collection.iterator();

        } else if (value instanceof Map) {
            // if it's a map, return an iterator for the map entries
            Map map = (Map) value;
            return map.entrySet().iterator();

        } else if (value instanceof Enumeration) {
            // if it's an enumeration, wrap it in an EnumerationIterator
            return new EnumerationIterator((Enumeration) value);

        } else if (value != null) {
            // if we have an array return an ArrayIterator
            Class type = value.getClass();
            if (type.isArray()) {
                return new ArrayIterator(value);
            }
        }

        // we've got something we can't deal with
        // so return an empty iterator
        return Collections.EMPTY_LIST.iterator();
    }

    /**
     * Returns something useful for logging
     *
     * @return string useful for logging
     */
    public String toString() {
        return "IteratorExpression [expression=" + expression + "]";
    }


    /**
     * <code>ArrayIterator</code> originated in commons-collections. Added
     * as a private inner class to break dependency.
     *
     * @author James Strachan
     * @author Mauricio S. Moura
     * @author Michael A. Smith
     * @author Neil O'Toole
     * @author Stephen Colebourne
     */
    private static final class ArrayIterator implements Iterator {

        /**
         * The array to iterate over
         */
        Object array;

        /**
         * The end index to loop to
         */
        int endIndex = 0;

        /**
         * The current iterator index
         */
        int index = 0;

        // Constructors
        // ----------------------------------------------------------------------

        /**
         * Constructs an ArrayIterator that will iterate over the values in the
         * specified array.
         *
         * @param array the array to iterate over.
         * @throws IllegalArgumentException if <code>array</code> is not an array.
         * @throws NullPointerException     if <code>array</code> is <code>null</code>
         */
        public ArrayIterator(final Object array) {
            super();
            setArray(array);
        }


        /**
         * Returns true if there are more elements to return from the array.
         *
         * @return true if there is a next element to return
         */
        public boolean hasNext() {
            return (index < endIndex);
        }

        /**
         * Returns the next element in the array.
         *
         * @return the next element in the array
         * @throws NoSuchElementException if all the elements in the array have already been
         *                                returned
         */
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return Array.get(array, index++);
        }

        /**
         * Throws {@link UnsupportedOperationException}.
         *
         * @throws UnsupportedOperationException always
         */
        public void remove() {
            throw new UnsupportedOperationException(
                    "remove() method is not supported");
        }

        // Properties
        //-----------------------------------------------------------------------

        /**
         * Sets the array that the ArrayIterator should iterate over.
         * <p/>
         *
         * @param array the array that the iterator should iterate over.
         * @throws IllegalArgumentException if <code>array</code> is not an array.
         * @throws NullPointerException     if <code>array</code> is <code>null</code>
         */
        public void setArray(final Object array) {
            // Array.getLength throws IllegalArgumentException if the object is
            // not
            // an array or NullPointerException if the object is null. This call
            // is made before saving the array and resetting the index so that
            // the
            // array iterator remains in a consistent state if the argument is
            // not
            // an array or is null.
            this.endIndex = Array.getLength(array);
            this.array = array;
            this.index = 0;
        }

    }


    /**
     * Adapter to make {@link Enumeration Enumeration}instances appear to be
     * {@link Iterator Iterator}instances. Originated in commons-collections.
     * Added as a private inner class to break dependency.
     *
     * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
     * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall </a>
     */
    private static final class EnumerationIterator implements Iterator {

        /**
         * The enumeration being converted
         */
        private final Enumeration enumeration;

        /**
         * The last object retrieved
         */
        private Object last;

        /**
         * Constructs a new <code>EnumerationIterator</code> that provides
         * an iterator view of the given enumeration.
         *
         * @param enumeration the enumeration to use
         */
        public EnumerationIterator(final Enumeration enumeration) {
            this.enumeration = enumeration;
        }


        // Iterator interface
        //-----------------------------------------------------------------------

        /**
         * Returns true if the underlying enumeration has more elements.
         *
         * @return true if the underlying enumeration has more elements
         * @throws NullPointerException if the underlying enumeration is null
         */
        public boolean hasNext() {
            return enumeration.hasMoreElements();
        }

        /**
         * Returns the next object from the enumeration.
         *
         * @return the next object from the enumeration
         * @throws NullPointerException if the enumeration is null
         */
        public Object next() {
            last = enumeration.nextElement();
            return last;
        }

        /**
         * Removes the last retrieved element if a collection is attached.
         * <p/>
         * Functions if an associated <code>Collection</code> is known.
         * If so, the first occurrence of the last returned object from this
         * iterator will be removed from the collection.
         *
         * @throws IllegalStateException         <code>next()</code> not called.
         * @throws UnsupportedOperationException if no associated collection
         */
        public void remove() {

            throw new UnsupportedOperationException("No Collection associated with this Iterator");
        }


    }

}
