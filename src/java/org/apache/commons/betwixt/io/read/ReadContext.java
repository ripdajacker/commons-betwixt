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
package org.apache.commons.betwixt.io.read;

import java.util.HashMap;

import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**  
  * Extends <code>Context</code> to provide read specific functionality. 
  *
  * @author Robert Burrell Donkin
  * @since 0.5
  */
public class ReadContext extends Context {

    /** Beans indexed by ID strings */
    private HashMap beansById = new HashMap();
    /** Classloader to be used to load beans during reading */
    private ClassLoader classLoader;
    /** The read specific configuration */
    private ReadConfiguration readConfiguration;
    
    /** 
      * Constructs a <code>ReadContext</code> with the same settings 
      * as an existing <code>Context</code>.
      * @param context not null
      * @param readConfiguration not null
      */
    public ReadContext( Context context, ReadConfiguration readConfiguration ) {
        super( context );
        this.readConfiguration = readConfiguration;
    }
    
    /**
      * Constructs a <code>ReadContext</code> with standard log.
      * @param bindingConfiguration the dynamic configuration, not null
      * @param readConfiguration the extra read configuration not null
      */
    public ReadContext( 
                    BindingConfiguration bindingConfiguration, 
                    ReadConfiguration readConfiguration ) {
        this( 
                LogFactory.getLog( ReadContext.class ), 
                bindingConfiguration,  
                readConfiguration);
    }
    
    /** 
      * Base constructor
      * @param log log to this Log
      * @param bindingConfiguration the dynamic configuration, not null
      * @param readConfiguration the extra read configuration not null
      */
    public ReadContext(
                        Log log, 
                        BindingConfiguration bindingConfiguration, 
                        ReadConfiguration readConfiguration  ) {
        super( null, log , bindingConfiguration );
        this.readConfiguration = readConfiguration;
    }
    
    /** 
      * Constructs a <code>ReadContext</code> 
      * with the same settings as an existing <code>Context</code>.
      * @param readContext not null
      */
    public ReadContext( ReadContext readContext ) {
        super( readContext );
        beansById = readContext.beansById;
        classLoader = readContext.classLoader;
        readConfiguration = readContext.readConfiguration;
    }
    
    /**
     * Puts a bean into storage indexed by an (xml) ID.
     *
     * @param id the ID string of the xml element associated with the bean
     * @param bean the Object to store, not null
     */
    public void putBean( String id, Object bean ) {
        beansById.put( id, bean );
    }
    
    /**
     * Gets a bean from storage by an (xml) ID.
     *
     * @param id the ID string of the xml element associated with the bean
     * @return the Object that the ID references, otherwise null
     */
    public Object getBean( String id ) {
        return beansById.get( id );
    }
    
    /** 
     * Clears the beans indexed by id.
     */
    public void clearBeans() {
        beansById.clear();
    }
    
    /**
      * Gets the classloader to be used.
      * @return the classloader that should be used to load all classes, possibly null
      */
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    /**
      * Sets the classloader to be used.
      * @param classLoader the ClassLoader to be used, possibly null
      */
    public void setClassLoader( ClassLoader classLoader ) {
        this.classLoader = classLoader;
    }
    
    /** 
      * Gets the <code>BeanCreationChange</code> to be used to create beans 
      * when an element is mapped.
      * @return the BeanCreationChain not null
      */
    public BeanCreationChain getBeanCreationChain() {
        return readConfiguration.getBeanCreationChain();
    }
}
