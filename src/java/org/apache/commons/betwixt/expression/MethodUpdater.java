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

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** <p><code>MethodUpdater</code> updates the current bean context 
  * by calling a WriteMethod with the String value from the XML attribute 
  * or element.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.12 $
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
