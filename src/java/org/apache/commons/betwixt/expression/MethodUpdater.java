/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/expression/MethodUpdater.java,v 1.9 2003/07/31 21:40:58 rdonkin Exp $
 * $Revision: 1.9 $
 * $Date: 2003/07/31 21:40:58 $
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
 * $Id: MethodUpdater.java,v 1.9 2003/07/31 21:40:58 rdonkin Exp $
 */
package org.apache.commons.betwixt.expression;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** <p><code>MethodUpdater</code> updates the current bean context 
  * by calling a WriteMethod with the String value from the XML attribute 
  * or element.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.9 $
  */
public class MethodUpdater implements Updater {

    /** Logger */
    private static Log log = LogFactory.getLog( MethodUpdater.class );

    /** 
     * Programmatically set log 
     * @param aLog the implementation to which this class should log
     */
    public static void setLog( Log aLog ) {
        log = aLog;
    }
    
    /** The method to call on the bean */
    private Method method;
    /** The type of the first parameter of the method */
    private Class valueType;
    
    /** Base constructor */
    public MethodUpdater() {
    }
    
    /** 
     * Convenience constructor sets method property 
     * @param method the Method to be invoked on the context's bean in the update
     */
    public MethodUpdater(Method method) {
        setMethod( method );
    }

    /** 
     * Updates the current bean context with the given String value 
     * @param context the Context to be updated
     * @param newValue the update to this new value 
     */
    public void update(Context context, Object newValue) {
        Object bean = context.getBean();
        if ( bean != null ) {
            if ( newValue instanceof String ) {
                // try to convert into primitive types
                if ( log.isTraceEnabled() ) {
                    log.trace("Converting primitive to " + valueType);
                }
                newValue = context.getObjectStringConverter()
                    .stringToObject( (String) newValue, valueType, null, context );
            }
            if ( newValue != null ) {
                // check that it is of the correct type
/*                
                if ( ! valueType.isAssignableFrom( newValue.getClass() ) ) {
                    log.warn( 
                        "Cannot call setter method: " + method.getName() + " on bean: " + bean
                        + " with type: " + bean.getClass().getName() 
                        + " as parameter should be of type: " + valueType.getName() 
                        + " but is: " + newValue.getClass().getName() 
                    );
                    return;
                }
*/                
            }                    
            Object[] arguments = { newValue };
            try {
                if ( log.isDebugEnabled() ) {
                    log.debug( 
                        "Calling setter method: " + method.getName() + " on bean: " + bean 
                        + " with new value: " + newValue 
                    );
                }
                method.invoke( bean, arguments );
                
            } catch (Exception e) {
                String valueTypeName = (newValue != null) ? newValue.getClass().getName() : "null";
                log.warn( 
                    "Cannot evaluate method: " + method.getName() + " on bean: " + bean 
                    + " of type: " + bean.getClass().getName() + " with value: " + newValue 
                    + " of type: " + valueTypeName 
                );
                handleException(context, e);
            }
        }
    }

    /** 
     * Gets the method which will be invoked by the update
     *
     * @return the Method to be invoked by the update
     */
    public Method getMethod() {
        return method;
    }
    
    /** 
     * Sets the constant value of this expression 
     * @param method the Method to be invoked by the update
     */
    public void setMethod(Method method) {
        this.method = method;
        Class[] types = method.getParameterTypes();
        if ( types == null || types.length <= 0 ) {
            throw new IllegalArgumentException( "The Method must have at least one parameter" );
        }
        this.valueType = types[0];
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /** 
     * Strategy method to allow derivations to handle exceptions differently.
     * @param context the Context being updated when this exception occured
     * @param e the Exception that occured during the update
     */
    protected void handleException(Context context, Exception e) {
        log.info( "Caught exception: " + e, e );
    }
    
    /**
     * Returns something useful for logging.
     * @return something useful for logging
     */
    public String toString() {
        return "MethodUpdater [method=" + method + "]";
    }
}
