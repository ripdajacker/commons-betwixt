/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/expression/VariableExpression.java,v 1.5 2003/10/09 20:52:04 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/10/09 20:52:04 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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
 */ 
package org.apache.commons.betwixt.expression;

/** <p><code>VariableExpression</code> represents a variable expression such as 
  * <code>$foo</code> which returns the value of the given variable.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.5 $
  */
public class VariableExpression implements Expression {

    /** The variable name */
    private String variableName;
    
    /** Base constructor */
    public VariableExpression() {
    }
    
    /** 
     * Convenience constructor sets <code>VariableName</code> property 
     * @param variableName the name of the context variable 
     * whose value will be returned by an evaluation 
     */
    public VariableExpression(String variableName) {
        this.variableName = variableName;
    }
    
    /** Return the value of a context variable.
      *
      * @param context evaluate against this context
      * @return the value of the context variable named by the <code>VariableName</code> property
      */
    public Object evaluate(Context context) {
        return context.getVariable( variableName );
    }
    
    /** 
     * Gets the variable name 
     * @return the name of the context variable whose value will be returned by an evaluation
     */
    public String getVariableName() {
        return variableName;
    }
    
    /** 
     * Sets the variable name 
     * @param variableName the name of the context variable 
     * whose value will be returned by an evaluation
     */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
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
     * @return something useful for logging
     */
    public String toString() {
        return "VariableExpression [variable name=" + variableName + "]";
    }
}
