/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.betwixt

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.strategy.*
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

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
 * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
 * @version $Revision$
 */
@TypeChecked
public class IntrospectionConfiguration {

    /** should attributes or elements be used for primitive types */
    boolean attributesForPrimitives = false;

    /** should we wrap collections in an extra element? */
    boolean wrapCollectionsInElement = true;

    /** Should the existing bean info search path for java.reflect.Introspector be used? */
    boolean useBeanInfoSearchPath = false;

    /** Should existing BeanInfo classes be used at all for java.reflect.Introspector */
    boolean ignoreAllBeanInfo = false;

    // pluggable strategies
    /** The strategy used to detect matching singular and plural properties */
    PluralStemmer pluralStemmer;

    /** The strategy used to convert bean type names into element names */
    NameMapper elementNameMapper;

    /** Strategy normalizes the Class of the Object before introspection */
    ClassNormalizer classNormalizer = new ClassNormalizer();

    /** Log for introspection messages */
    Log introspectionLog = LogFactory.getLog(XMLIntrospector.class);

    /**
     * The strategy used to convert bean type names into attribute names
     * It will default to the normal nameMapper.
     */
    private NameMapper attributeNameMapper;

    /** Prefix naming strategy */
    NamespacePrefixMapper prefixMapper = new NamespacePrefixMapper();
    /** Mapping strategy for simple types */
    SimpleTypeMapper simpleTypeMapper = new StandardSimpleTypeMapper();
    /** Binding strategy for Java type */
    TypeBindingStrategy typeBindingStrategy = TypeBindingStrategy.DEFAULT;
    /** Strategy used for determining which types are collective */
    CollectiveTypeStrategy collectiveTypeStrategy = CollectiveTypeStrategy.DEFAULT;

    /** Strategy for suppressing attributes */
    AttributeSuppressionStrategy attributeSuppressionStrategy = AttributeSuppressionStrategy.DEFAULT;
    /** Strategy for suppressing elements */
    ElementSuppressionStrategy elementSuppressionStrategy = ElementSuppressionStrategy.DEFAULT;

    /**
     * Strategy used to determine whether the bind or introspection time type is to be used to
     * determine the mapping.
     */
    MappingDerivationStrategy mappingDerivationStrategy = MappingDerivationStrategy.DEFAULT;

    /**
     * Strategy used to determine which properties should be ignored
     */
    PropertySuppressionStrategy propertySuppressionStrategy = PropertySuppressionStrategy.DEFAULT;

    /**
     * Should the introspector use the context classloader. Defaults to true.
     */
    boolean useContextClassLoader = true;

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
     * Get singular and plural matching strategy.
     *
     * @return the strategy used to detect matching singular and plural properties
     */
    public PluralStemmer getPluralStemmer() {
        if (pluralStemmer == null) {
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
        if (elementNameMapper == null) {
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
     * A Factory method to lazily create a new strategy
     * to detect matching singular and plural properties.
     *
     * @return new defualt PluralStemmer implementation
     */
    protected static PluralStemmer createPluralStemmer() {
        return new DefaultPluralStemmer();
    }

    /**
     * A Factory method to lazily create a strategy
     * used to convert bean type names into element names.
     *
     * @return new default NameMapper implementation
     */
    protected static NameMapper createNameMapper() {
        return new DefaultNameMapper();
    }

    /**
     * Gets the simple type binding strategy.
     * @return SimpleTypeMapper , not null
     */
    public SimpleTypeMapper getSimpleTypeMapper() {
        return simpleTypeMapper;
    }

    /**
     * Sets the simple type binding strategy.
     * @param mapper SimpleTypeMapper, not null
     */
    public void setSimpleTypeMapper(SimpleTypeMapper mapper) {
        simpleTypeMapper = mapper;
    }

    /**
     * Is this a loop type class?
     * @since 0.7
     * @param type is this <code>Class</code> a loop type?
     * @return true if the type is a loop type, or if type is null
     */
    public boolean isLoopType(Class type) {
        return getCollectiveTypeStrategy().isCollective(type);
    }

}
