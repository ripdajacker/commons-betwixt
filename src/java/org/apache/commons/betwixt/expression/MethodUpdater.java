/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: MethodUpdater.java,v 1.1 2002/06/10 17:53:33 jstrachan Exp $
 */
package org.apache.commons.betwixt.expression;

import java.lang.reflect.Method;

import org.apache.commons.beanutils.ConvertUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** <p><code>MethodUpdater</code> updates the current bean context 
  * by calling a WriteMethod with the String value from the XML attribute 
  * or element.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public class MethodUpdater implements Updater {

    /** Logger */
    private static final Log log = LogFactory.getLog( MethodUpdater.class );
    
    /** The method to call on the bean */
    private Method method;
    /** The type of the first parameter of the method */
    private Class valueType;
    
    /** Base constructor */
    public MethodUpdater() {
    }
    
    /** Convenience constructor sets method property */
    public MethodUpdater(Method method) {
        setMethod( method );
    }

    /** Updates the current bean context with the given String value */
    public void update(Context context, Object newValue) {
        Object bean = context.getBean();
        if ( bean != null ) {
            if ( newValue instanceof String ) {
                // try to convert into primitive types
                newValue = ConvertUtils.convert( (String) newValue, valueType );
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
            }
            catch (Exception e) {
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

    /** Gets the constant value of this expression */
    public Method getMethod() {
        return method;
    }
    
    /** Sets the constant value of this expression */
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
     */
    protected void handleException(Context context, Exception e) {
        log.info( "Caught exception: " + e, e );
    }
}
