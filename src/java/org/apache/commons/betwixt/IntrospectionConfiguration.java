/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/IntrospectionConfiguration.java,v 1.1.2.1 2004/01/18 22:24:34 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/01/18 22:24:34 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
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
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior 
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

package org.apache.commons.betwixt;

import org.apache.commons.betwixt.strategy.ClassNormalizer;
import org.apache.commons.betwixt.strategy.DefaultNameMapper;
import org.apache.commons.betwixt.strategy.DefaultPluralStemmer;
import org.apache.commons.betwixt.strategy.NameMapper;
import org.apache.commons.betwixt.strategy.PluralStemmer;

/**
 * <p>Stores introspection phase binding configuration.</p>
 * <p>
 * There are two phase in Betwixt's processing. 
 * The first phase is the introspection of the bean. 
 * Strutural configuration settings effect this phase. 
 * The second phase comes when Betwixt dynamically uses reflection 
 * to execute the mapping. 
 * This object stores configuration settings pertaining to the first phase.
 * </p>
 * <p>
 * These common settings have been collected into one class so that they can  
 * be more easily shared not only between the objects that execute the introspection
 * but also (by a user) between different <code>XMLIntrospector</code>s.
 * </p>
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class IntrospectionConfiguration {

    /** should attributes or elements be used for primitive types */
    private boolean attributesForPrimitives = false;
    
    /** should we wrap collections in an extra element? */
    private boolean wrapCollectionsInElement = true;

    /** Should the existing bean info search path for java.reflect.Introspector be used? */
    private boolean useBeanInfoSearchPath = false;

    // pluggable strategies        
    /** The strategy used to detect matching singular and plural properties */
    private PluralStemmer pluralStemmer;
    
    /** The strategy used to convert bean type names into element names */
    private NameMapper elementNameMapper;

    /** Strategy normalizes the Class of the Object before introspection */
    private ClassNormalizer classNormalizer = new ClassNormalizer(); 

    /**
     * The strategy used to convert bean type names into attribute names
     * It will default to the normal nameMapper.
     */
    private NameMapper attributeNameMapper;


    /**
      * Gets the <code>ClassNormalizer</code> strategy.
      * This is used to determine the Class to be introspected
      * (the normalized Class). 
      *
      * @return the <code>ClassNormalizer</code> used to determine the Class to be introspected
      * for a given Object.
      */
    public ClassNormalizer getClassNormalizer() {
        return classNormalizer;
    }
    
    /**
      * Sets the <code>ClassNormalizer</code> strategy.
      * This is used to determine the Class to be introspected
      * (the normalized Class). 
      *
      * @param classNormalizer the <code>ClassNormalizer</code> to be used to determine 
      * the Class to be introspected for a given Object.
      */    
    public void setClassNormalizer(ClassNormalizer classNormalizer) {
        this.classNormalizer = classNormalizer;
    }

    /** 
      * Should attributes (or elements) be used for primitive types.
      * @return true if primitive types will be mapped to attributes in the introspection
      */
    public boolean isAttributesForPrimitives() {
        return attributesForPrimitives;
    }

    /** 
      * Set whether attributes (or elements) should be used for primitive types. 
      * @param attributesForPrimitives pass trus to map primitives to attributes,
      *        pass false to map primitives to elements
      */
    public void setAttributesForPrimitives(boolean attributesForPrimitives) {
        this.attributesForPrimitives = attributesForPrimitives;
    }
    
    /**
     * Should collections be wrapped in an extra element?
     * 
     * @return whether we should we wrap collections in an extra element? 
     */
    public boolean isWrapCollectionsInElement() {
        return wrapCollectionsInElement;
    }

    /** 
     * Sets whether we should we wrap collections in an extra element.
     *
     * @param wrapCollectionsInElement pass true if collections should be wrapped in a
     *        parent element
     */
    public void setWrapCollectionsInElement(boolean wrapCollectionsInElement) {
        this.wrapCollectionsInElement = wrapCollectionsInElement;
    }    

    /** 
     * Get singular and plural matching strategy.
     *
     * @return the strategy used to detect matching singular and plural properties 
     */
    public PluralStemmer getPluralStemmer() {
        if ( pluralStemmer == null ) {
            pluralStemmer = createPluralStemmer();
        }
        return pluralStemmer;
    }
    
    /** 
     * Sets the strategy used to detect matching singular and plural properties 
     *
     * @param pluralStemmer the PluralStemmer used to match singular and plural
     */
    public void setPluralStemmer(PluralStemmer pluralStemmer) {
        this.pluralStemmer = pluralStemmer;
    }
    
    /**
     * Gets the name mapping strategy used to convert bean names into elements.
     *
     * @return the strategy used to convert bean type names into element 
     * names. If no element mapper is currently defined then a default one is created.
     */
    public NameMapper getElementNameMapper() {
        if ( elementNameMapper == null ) {
            elementNameMapper = createNameMapper();
         }
        return elementNameMapper;
    }
     
    /**
     * Sets the strategy used to convert bean type names into element names
     * @param nameMapper the NameMapper to use for the conversion
     */
    public void setElementNameMapper(NameMapper nameMapper) {
        this.elementNameMapper = nameMapper;
    }
    
    /**
     * Gets the name mapping strategy used to convert bean names into attributes.
     *
     * @return the strategy used to convert bean type names into attribute
     * names. If no attributeNamemapper is known, it will default to the ElementNameMapper
     */
    public NameMapper getAttributeNameMapper() {
        if (attributeNameMapper == null) {
            attributeNameMapper = createNameMapper();
        }
        return attributeNameMapper;
     }


    /**
     * Sets the strategy used to convert bean type names into attribute names
     * @param nameMapper the NameMapper to use for the convertion
     */
    public void setAttributeNameMapper(NameMapper nameMapper) {
        this.attributeNameMapper = nameMapper;
    }
    
    /**
     * Should the original <code>java.reflect.Introspector</code> bean info search path be used?
     * By default it will be false.
     * 
     * @return boolean if the beanInfoSearchPath should be used.
     */
    public boolean useBeanInfoSearchPath() {
        return useBeanInfoSearchPath;
    }

    /**
     * Specifies if you want to use the beanInfoSearchPath 
     * @see java.beans.Introspector for more details
     * @param useBeanInfoSearchPath 
     */
    public void setUseBeanInfoSearchPath(boolean useBeanInfoSearchPath) {
        this.useBeanInfoSearchPath = useBeanInfoSearchPath;
    }
    
    /** 
     * A Factory method to lazily create a new strategy 
     * to detect matching singular and plural properties.
     *
     * @return new defualt PluralStemmer implementation
     */
    protected PluralStemmer createPluralStemmer() {
        return new DefaultPluralStemmer();
    }
    
    /** 
     * A Factory method to lazily create a strategy 
     * used to convert bean type names into element names.
     *
     * @return new default NameMapper implementation
     */
    protected NameMapper createNameMapper() {
        return new DefaultNameMapper();
    }
}
