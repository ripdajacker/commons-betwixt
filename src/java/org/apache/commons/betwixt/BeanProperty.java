package org.apache.commons.betwixt;

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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.betwixt.expression.DynaBeanExpression;
import org.apache.commons.betwixt.expression.Expression;
import org.apache.commons.betwixt.expression.MethodExpression;
import org.apache.commons.betwixt.expression.MethodUpdater;
import org.apache.commons.betwixt.expression.Updater;

/** 
  * Betwixt-centric view of a bean (or pseudo-bean) property.
  * This object decouples the way that the (possibly pseudo) property introspection
  * is performed from the results of that introspection.
  *
  * @author Robert Burrell Donkin
  * @version $Id: BeanProperty.java,v 1.5 2004/02/28 13:38:32 yoavs Exp $
  */
public class BeanProperty {

    /** The bean name for the property (not null) */
    private String propertyName;
    /** The type of this property (not null) */
    private Class propertyType;
    /** The Expression used to read values of this property (possibly null) */
    private Expression propertyExpression;
    /** The Updater used to write values of this property (possibly null) */
    private Updater propertyUpdater;

    /**
     * Construct a BeanProperty.
     * @param propertyName not null
     * @param propertyType not null
     * @param propertyExpression the Expression used to read the property, 
     * null if the property is not readable
     * @param propertyUpdater the Updater used to write the property, 
     * null if the property is not writable
     */
    public BeanProperty (
                        String propertyName, 
                        Class propertyType, 
                        Expression propertyExpression, 
                        Updater propertyUpdater) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.propertyExpression = propertyExpression;
        this.propertyUpdater = propertyUpdater;        
    }
    
    /**
     * Constructs a BeanProperty from a <code>PropertyDescriptor</code>.
     * @param descriptor not null
     */
    public BeanProperty(PropertyDescriptor descriptor) {
        this.propertyName = descriptor.getName();
        this.propertyType = descriptor.getPropertyType();
        
        Method readMethod = descriptor.getReadMethod();
        if ( readMethod != null ) {
            this.propertyExpression = new MethodExpression( readMethod );
        }
        
        Method writeMethod = descriptor.getWriteMethod();
        if ( writeMethod != null ) {
            this.propertyUpdater = new MethodUpdater( writeMethod ); 
        }
    }
    
    /**
     * Constructs a BeanProperty from a <code>DynaProperty</code>
     * @param dynaProperty not null
     */
    public BeanProperty(DynaProperty dynaProperty) {
        this.propertyName = dynaProperty.getName();
        this.propertyType = dynaProperty.getType();
        this.propertyExpression = new DynaBeanExpression( propertyName );
        // todo: add updater
    }

    /**
      * Gets the bean name for this property.
      * Betwixt will map this to an xml name.
      * @return the bean name for this property, not null
      */
    public String getPropertyName() {
        return propertyName;
    }

    /** 
      * Gets the type of this property.
      * @return the property type, not null
      */
    public Class getPropertyType() {
        return propertyType;
    }
    
    /**
      * Gets the expression used to read this property.
      * @return the expression to be used to read this property 
      * or null if this property is not readable.
      */
    public Expression getPropertyExpression() {
        return propertyExpression;
    }
    
    /**
      * Gets the updater used to write to this properyty.
      * @return the Updater to the used to write to this property
      * or null if this property is not writable.
      */ 
    public Updater getPropertyUpdater() {
        return propertyUpdater;
    }
}
