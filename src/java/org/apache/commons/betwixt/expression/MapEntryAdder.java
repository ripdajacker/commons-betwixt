/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/expression/MapEntryAdder.java,v 1.4.2.2 2004/05/01 09:42:22 rdonkin Exp $
 * $Revision: 1.4.2.2 $
 * $Date: 2004/05/01 09:42:22 $
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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** <p><code>MapEntryAdder</code> is used to add entries to a map.</p>
  *
  * <p>
  * <code>MapEntryAdder</code> supplies two updaters:
  * <ul>
  *   <li>{@link #getKeyUpdater()} which allows the entry key to be updated</li>
  *   <li>{@link #getValueUpdater()} which allows the entry value to be updated</li>
  * </ul>
  * When both of these updaters have been called, the entry adder method is called.
  * Once this has happened then the values can be updated again.
  * Note that only the <code>Context</code> passed by the last update will be used.
  * </p>
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @version $Revision: 1.4.2.2 $
  */
public class MapEntryAdder {

    
    // Class Attributes
    //-------------------------------------------------------------------------   
    
    /** Log used by this class */
    private static Log log = LogFactory.getLog( MapEntryAdder.class );
    
    
    // Class Methods
    //-------------------------------------------------------------------------      
    
    /** 
     * Sets the logger used by this class.
     *
     * @param newLog log to this
     */
    public static void setLog(Log newLog) {
        log = newLog;
    }
    
    // Attributes
    //-------------------------------------------------------------------------    

    /** The method to be called to add a new map entry */
    private Method adderMethod;
    
    /** Has the entry key been updated? */
    private boolean keyUpdated = false;
    /** The entry key */
    private Object key;
    
    /** Has the entry value been updated? */
    private boolean valueUpdated = false;
    /** The entry value */
    private Object value;
    
        
    // Constructors
    //-------------------------------------------------------------------------    
    
    /**
     * Construct a <code>MapEntryAdder</code> which adds entries to given method.
     *
     * @param method the <code>Method</code> called to add a key-value entry
     * @throws IllegalArgumentException if the given method does not take two parameters 
     */
    public MapEntryAdder(Method method) {
        
        Class[] types = method.getParameterTypes();
        if ( types == null || types.length != 2) {
            throw new IllegalArgumentException(
                "Method used to add entries to maps must have two parameter.");
        }
        this.adderMethod = method;
    }
    
    // Properties
    //-------------------------------------------------------------------------    
    
    /**
     * Gets the entry key <code>Updater</code>.
     * This is used to update the entry key value to the read value.
     * If {@link #getValueUpdater} has been called previously, 
     * then this trigger the updating of the adder method.
     *
     * @return the <code>Updater</code> which should be used to populate the entry key
     */
    public Updater getKeyUpdater() {
        
        return new Updater() {
            public void update( Context context, Object keyValue ) {
                // might as well make sure that his can only be set once
                if ( !keyUpdated ) {
                    keyUpdated = true;
                    key = keyValue;
                    if ( log.isTraceEnabled() ) {
                        log.trace( "Setting entry key to " + key );
                        log.trace( "Current entry value is " + value );
                    }
                    if ( valueUpdated ) {
                        callAdderMethod( context );
                    }
                }
            }
        };
    }
    
    /**
     * Gets the entry value <code>Updater</code>.
     * This is used to update the entry key value to the read value.
     * If {@link #getKeyUpdater} has been called previously, 
     * then this trigger the updating of the adder method.
     *
     * @return the <code>Updater</code> which should be used to populate the entry value
     */
    public Updater getValueUpdater() {
        
        return new Updater() {
            public void update( Context context, Object valueValue ) {
                // might as well make sure that his can only be set once
                if ( !valueUpdated ) {
                    valueUpdated = true;
                    value = valueValue;
                    if ( log.isTraceEnabled() ) {
                        log.trace( "Setting entry value to " + value);
                        log.trace( "Current entry key is " + key );
                    }
                    if ( keyUpdated ) {
                        callAdderMethod( context );
                    }
                }
            }
        };
    }
    
    
    
    // Implementation methods
    //-------------------------------------------------------------------------    

    /**
     * Call the adder method on the bean associated with the <code>Context</code>
     * with the key, value entry values stored previously.
     *
     * @param context the Context against whose bean the adder method will be invoked
     */
    private void callAdderMethod(Context context) {
        log.trace("Calling adder method");
        
        // this allows the same instance to be used multiple times.
        keyUpdated = false;
        valueUpdated = false;
        
        //
        // XXX This is (basically) cut and pasted from the MethodUpdater code
        // I haven't abstracted this code just yet since I think that adding
        // handling for non-beans will mean adding quite a lot more structure
        // and only once this is added will the proper position for this method 
        // become clear.
        //
        
        Class[] types = adderMethod.getParameterTypes();
        // key is first parameter
        Class keyType = types[0];
        // value is the second
        Class valueType = types[1];
        
        Object bean = context.getBean();
        if ( bean != null ) {
            if ( key instanceof String ) {
                // try to convert into primitive types
                key = context.getObjectStringConverter()
                        .stringToObject( (String) key, keyType, null, context );
            }
            
            if ( value instanceof String ) {
                // try to convert into primitive types
                value = context.getObjectStringConverter()
                        .stringToObject( (String) value, valueType, null, context );
            }
            
            // special case for collection objects into arrays                    
            if (value instanceof Collection && valueType.isArray()) {
                Collection valuesAsCollection = (Collection) value;
                Class componentType = valueType.getComponentType();
                if (componentType != null) {
                    Object[] valuesAsArray = 
                        (Object[]) Array.newInstance(componentType, valuesAsCollection.size());
                    value = valuesAsCollection.toArray(valuesAsArray);
                }
            }
            
                 
            Object[] arguments = { key, value };
            try {
                if ( log.isTraceEnabled() ) {
                    log.trace( 
                        "Calling adder method: " + adderMethod.getName() + " on bean: " + bean 
                        + " with key: " + key + " and value: " + value
                    );
                }
                adderMethod.invoke( bean, arguments );
                
            } catch (Exception e) {
                log.warn( 
                    "Cannot evaluate adder method: " + adderMethod.getName() + " on bean: " + bean 
                    + " of type: " + bean.getClass().getName() + " with value: " + value 
                    + " of type: " + valueType + " and key: " + key
                    + " of type: " + keyType 
                );
                log.debug(e);
            }
        }
    }
}
