/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/read/ChainedBeanCreatorFactory.java,v 1.1 2003/08/21 22:39:00 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/08/21 22:39:00 $
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
 * $Id: ChainedBeanCreatorFactory.java,v 1.1 2003/08/21 22:39:00 rdonkin Exp $
 */
package org.apache.commons.betwixt.io.read;

/**  
  * Group of factory methods for <code>ChainedBeanCreator</code>'s.
  * The standard implementations used by Betwixt are present here.
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.1 $
  */
public class ChainedBeanCreatorFactory {
    
    /** Singleton instance for creating derived beans */
    private static final ChainedBeanCreator derivedBeanCreator 
        = new ChainedBeanCreator() {
            public Object create(
                                ElementMapping elementMapping, 
                                ReadContext context, 
                                BeanCreationChain chain) {
                                
                String className = elementMapping.getAttributes().getValue( context.getClassNameAttribute() );
                if ( className != null ) {
                    try {
                        // load the class we should instantiate
                        ClassLoader classLoader = context.getClassLoader();
                        if ( classLoader == null ) {
                            context.getLog().warn( 
                            "Could not create derived instance: read context classloader not set." );
                        }
                        Class clazz = classLoader.loadClass( className );
                        return clazz.newInstance();
                                        
                    } catch (Exception e) {
                        // it would be nice to have a pluggable strategy for exception management
                        context.getLog().warn( "Could not create instance of type: " + className );
                        context.getLog().debug( "Create new instance failed: ", e );
                        return null;
                    }
                    
                } else {
                    // pass responsibility down the chain
                    return chain.create( elementMapping, context );
                }
            }
        };
    
    /**
      * Creates a <code>ChainedBeanCreator</code> that constructs derived beans.
      * These have their classname set by an xml attribute.
      * @return <code>ChainedBeanCreator</code> that implements Derived beans logic, not null
      */
    public static final ChainedBeanCreator createDerivedBeanCreator() {
        return derivedBeanCreator;
    }
    
    /** Singleton instance that creates beans based on type */
    private static final ChainedBeanCreator elementTypeBeanCreator 
        = new ChainedBeanCreator() {
            public Object create(
                                ElementMapping element, 
                                ReadContext context, 
                                BeanCreationChain chain) {
                                    // create based on type
                
                Class theClass = element.getType();
                try {

                    return theClass.newInstance();
                    
                } catch (Exception e) {
                    // it would be nice to have a pluggable strategy for exception management
                    context.getLog().warn( "Could not create instance of type: " + theClass.getName() );
                    context.getLog().debug( "Create new instance failed: ", e );
                    return null;
                }
            }
        }; 
    
    /**
      * Creates a <code>ChainedBeanCreator</code> that constructs beans based on element type.
      * @return <code>ChainedBeanCreator</code> that implements load by type beans logic, not null
      */
    public static final ChainedBeanCreator createElementTypeBeanCreator() {
        return elementTypeBeanCreator;
    }
    
    /** Singleton instance that creates beans based on IDREF */
    private static final ChainedBeanCreator idRefBeanCreator 
        = new ChainedBeanCreator() {
            public Object create(
                                ElementMapping elementMapping, 
                                ReadContext context, 
                                BeanCreationChain chain) {
                if ( context.getMapIDs() ) {
                    String idref = elementMapping.getAttributes().getValue( "idref" );
                    if ( idref != null ) {
                        // XXX need to check up about ordering
                        // XXX this is a very simple system that assumes that 
                        // XXX id occurs before idrefs
                        // XXX would need some thought about how to implement a fuller system
                        context.getLog().trace( "Found IDREF" );
                        Object bean = context.getBean( idref );
                        if ( bean != null ) {
                            if ( context.getLog().isTraceEnabled() ) {
                                context.getLog().trace( "Matched bean " + bean );
                            }
                            return bean;
                        }
                        context.getLog().trace( "No match found" );
                    }
                }
                return chain.create( elementMapping, context );
            }
        }; 
    
    /**
      * Creates a <code>ChainedBeanCreator</code> that finds existing beans based on their IDREF.
      * @return <code>ChainedBeanCreator</code> that implements IDREF beans logic, not null
      */
    public static final ChainedBeanCreator createIDREFBeanCreator() {
        return idRefBeanCreator;
    }
}
