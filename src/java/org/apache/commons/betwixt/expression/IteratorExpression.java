/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.EnumerationIterator;

/** <p><code>IteratorExpression</code> returns an iterator over the current context.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.8 $
  */
public class IteratorExpression implements Expression {
    
    /** Use this <code>Expression</code> to perform initial evaluation*/
    private Expression expression;
    
    /** 
     * Construct <code>IteratorExpression</code> using given expression for initial evaluation.
     * @param expression this expression will be evaluated and the result converted to an 
     *        iterator.
     */
    public IteratorExpression(Expression expression) {
        this.expression = expression;
    }
    
    /** 
     * Returns an interator over the current context 
     * @see org.apache.commons.betwixt.expression.Expression
     */
    public Object evaluate(Context context) {        
        // evaluate wrapped expression against context
        Object value = expression.evaluate( context );
        
        // based on the class of the result,
        // return an appropriate iterator
        if ( value instanceof Iterator ) {
            // if the value is an iterator, we're done
            return (Iterator) value;
            
        } else if ( value instanceof Collection ) {
            // if it's a collection, return an iterator for that collection
            Collection collection = (Collection) value;
            return collection.iterator();
            
        } else if ( value instanceof Map ) {
            // if it's a map, return an iterator for the map entries
            Map map = (Map) value;
            return map.entrySet().iterator();
            
        } else if ( value instanceof Enumeration ) {
            // if it's an enumeration, wrap it in an EnumerationIterator
            return new EnumerationIterator( (Enumeration) value );
            
        } else if ( value != null ) {
            // if we have an array return an ArrayIterator
            Class type = value.getClass();
            if ( type.isArray() ) {
                return new ArrayIterator( value );
            }
        }
        
        // we've got something we can't deal with
        // so return an empty iterator
        return Collections.EMPTY_LIST.iterator();
    }

    /** 
     * Do nothing
     * @see org.apache.commons.betwixt.expression.Expression
     */
    public void update(Context context, String newValue) {
        // do nothing
    }
    
    /**
     * Returns something useful for logging
     * @return string useful for logging
     */
    public String toString() {
        return "IteratorExpression [expression=" + expression + "]";
    }
}
