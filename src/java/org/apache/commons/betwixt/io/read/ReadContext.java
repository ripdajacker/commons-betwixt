/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/read/ReadContext.java,v 1.3 2003/10/05 14:21:28 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/05 14:21:28 $
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
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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
package org.apache.commons.betwixt.io.read;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.betwixt.expression.Context;

/**  
  * Extends <code>Context</code> to provide read specific functionality. 
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.3 $
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
