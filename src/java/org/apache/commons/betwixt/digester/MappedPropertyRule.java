/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/digester/MappedPropertyRule.java,v 1.3 2003/10/05 14:15:57 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/05 14:15:57 $
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
package org.apache.commons.betwixt.digester;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** <p>Factors out common code used by Betwixt rules that access bean properties.
  * Maybe a lot of this should be moved into <code>BeanUtils</code>.</p>
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.3 $
  */
public abstract class MappedPropertyRule extends RuleSupport {

    /** Logger */
    private static final Log log = LogFactory.getLog( MappedPropertyRule.class );   
     /** Classloader used to load classes by name */
    private ClassLoader classLoader;
    /** Base constructor */
    public MappedPropertyRule() {
        this.classLoader = getClass().getClassLoader();
    }
    
    

    // Implementation methods
    //-------------------------------------------------------------------------    

    /** 
     * Returns the property descriptor for the class and property name.
     * Note that some caching could be used to improve performance of 
     * this method. Or this method could be added to PropertyUtils.
     *
     * @param beanClass descriptor for property in this class
     * @param propertyName descriptor for property with this name
     * @return property descriptor for the named property in the given class 
     */
    protected PropertyDescriptor getPropertyDescriptor( Class beanClass, 
                                                        String propertyName ) {
        if ( beanClass != null && propertyName != null ) {
            if (log.isTraceEnabled()) {
                log.trace("Searching for property " + propertyName + " on " + beanClass);
            }
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo( beanClass );
                PropertyDescriptor[] descriptors = 
                    beanInfo.getPropertyDescriptors();
                if ( descriptors != null ) {
                    for ( int i = 0, size = descriptors.length; i < size; i++ ) {
                        PropertyDescriptor descriptor = descriptors[i];
                        if ( propertyName.equals( descriptor.getName() ) ) {
                            log.trace("Found matching method.");
                            return descriptor;
                        }
                    }
                }
                log.trace("No match found.");
                return null;
            } catch (Exception e) {
                log.warn( "Caught introspection exception", e );
            }
        }
        return null;
    }
    
    
    /**
     * Gets the type of a property
     *
     * @param propertyClassName class name for property type (may be null)
     * @param beanClass class that has property 
     * @param propertyName the name of the property whose type is to be determined
     * @return property type 
     */
    protected Class getPropertyType( String propertyClassName, 
                                     Class beanClass, String propertyName ) {
        // XXX: should use a ClassLoader to handle 
        //      complex class loading situations
        if ( propertyClassName != null ) {
            try {
                Class answer = classLoader.loadClass(propertyClassName);
                if (answer != null) {
                    if (log.isTraceEnabled()) {
                        log.trace("Used specified type " + answer);
                    }
                    return answer;
                }
            } catch (Exception e) {
                log.warn("Cannot load specified type", e);
            }
        }
        
        PropertyDescriptor descriptor = 
            getPropertyDescriptor( beanClass, propertyName );        
        if ( descriptor != null ) { 
            return descriptor.getPropertyType();
        }
        
        if (log.isTraceEnabled()) {
            log.trace("Cannot find property type.");
            log.trace("  className=" + propertyClassName 
                        + " base=" + beanClass + " name=" + propertyName);
        }
        return null;            
    }
}
