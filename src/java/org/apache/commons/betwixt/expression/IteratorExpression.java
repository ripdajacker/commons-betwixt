 /*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/expression/IteratorExpression.java,v 1.1 2002/06/10 17:53:33 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2002/06/10 17:53:33 $
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
 * $Id: IteratorExpression.java,v 1.1 2002/06/10 17:53:33 jstrachan Exp $
 */

package org.apache.commons.betwixt.expression;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.ArrayIterator;
import org.apache.commons.collections.EnumerationIterator;
import org.apache.commons.collections.SingletonIterator;

/** <p><code>IteratorExpression</code> returns an iterator over the current context.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public class IteratorExpression implements Expression {
    
    /** Use this <code>Expression</code> to perform initial evaluation*/
    private Expression expression;
    
    /** Construct <code>IteratorExpression</code> using given expression for initial evaluation.
     */
    public IteratorExpression(Expression expression) {
        this.expression = expression;
    }
    
    /** Returns an interator over the current context */
    public Object evaluate(Context context) {        
        // evaluate wrapped expression against context
        Object value = expression.evaluate( context );
        
        // based on the class of the result,
        // return an appropriate iterator
        if ( value instanceof Iterator ) {
            // if the value is an iterator, we're done
            return (Iterator) value;
        }
        else if ( value instanceof Collection ) {
            // if it's a collection, return an iterator for that collection
            Collection collection = (Collection) value;
            return collection.iterator();
        }
        else if ( value instanceof Map ) {
            // if it's a map, return an iterator for the map entries
            Map map = (Map) value;
            return map.entrySet().iterator();
        }
        else if ( value instanceof Enumeration ) {
            // if it's an enumeration, wrap it in an EnumerationIterator
            return new EnumerationIterator( (Enumeration) value );
        }
        else if ( value != null ) {
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
    
    public void update(Context context, String newValue) {
        // do nothing
    }
}
