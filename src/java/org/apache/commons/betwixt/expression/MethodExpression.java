/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/expression/MethodExpression.java,v 1.3 2003/01/06 22:50:44 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/01/06 22:50:44 $
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
 * $Id: MethodExpression.java,v 1.3 2003/01/06 22:50:44 rdonkin Exp $
 */
package org.apache.commons.betwixt.expression;

import java.lang.reflect.Method;

/** <p><code>MethodExpression</code> evaluates a method on the current bean context.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.3 $
  */
public class MethodExpression implements Expression {

    /** null arguments */
    protected static Object[] NULL_ARGUMENTS;
    /** null classes */
    protected static Class[] NULL_CLASSES;
    
    /** The method to call on the bean */
    private Method method;
    
    /** Base constructor */
    public MethodExpression() {
    }
    
    /** Convenience constructor sets method property */
    public MethodExpression(Method method) {
        this.method = method;
    }

    /** Evaluate by calling the read method on the current bean */
    public Object evaluate(Context context) {
        Object bean = context.getBean();
        if ( bean != null ) {
            Object[] arguments = getArguments();
            try {
                return method.invoke( bean, arguments );
                
            } catch (IllegalAccessException e) {
                // lets try use another method with the same name
                try {
                    Class type = bean.getClass();
                    Method alternate = findAlternateMethod( type, method );
                    if ( alternate != null ) {
                        return alternate.invoke( bean, arguments );
                    }
                } catch (Exception e2) {
                    handleException(context, e2);
                }
            } catch (Exception e) {
                handleException(context, e);
            }
        }
        return null;
    }

    public void update(Context context, String newValue) {
        // do nothing
    }

    /** Gets the constant value of this expression */
    public Method getMethod() {
        return method;
    }
    
    /** Sets the constant value of this expression */
    public void setMethod(Method method) {
        this.method = method;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /** Allows derived objects to create arguments for the method call */
    protected Object[] getArguments() {
        return NULL_ARGUMENTS;
    }
    
    /** Tries to find an alternate method for the given type using interfaces
      * which gets around the problem of inner classes, 
      * such as on Map.Entry implementations.
      */
    protected Method findAlternateMethod( 
                                            Class type, 
                                            Method method ) 
                                                throws 
                                                    NoSuchMethodException {
        Class[] interfaces = type.getInterfaces();
        if ( interfaces != null ) {
            String name = method.getName();
            for ( int i = 0, size = interfaces.length; i < size; i++ ) {
                Class otherType = interfaces[i];
                Method alternate = otherType.getMethod( name, NULL_CLASSES );
                if ( alternate != null && alternate != method ) {
                    return alternate;
                }
            }
        }
        return null;
    }
    
    /** 
      * <p> Log error to context's logger. </p> 
      *
      * <p> Allows derived objects to handle exceptions differently. </p>
      */
    protected void handleException(Context context, Exception e) {
        // use the context's logger to log the problem
        context.getLog().error("[MethodExpression] Cannot evaluate expression", e);
    }
    
    public String toString() {
        return "MethodExpression [method=" + method + "]";
    }
}
