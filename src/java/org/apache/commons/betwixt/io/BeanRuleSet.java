/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/BeanRuleSet.java,v 1.14 2003/10/05 14:21:28 rdonkin Exp $
 * $Revision: 1.14 $
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
package org.apache.commons.betwixt.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.TextDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.digester.XMLIntrospectorHelper;
import org.apache.commons.betwixt.expression.MethodUpdater;
import org.apache.commons.betwixt.expression.Updater;
import org.apache.commons.betwixt.io.read.ReadContext;
import org.apache.commons.betwixt.io.read.ReadConfiguration;
import org.apache.commons.betwixt.io.read.ElementMapping;


import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

/** <p>Sets <code>Betwixt</code> digestion rules for a bean class.</p>
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @version $Revision: 1.14 $
  */
public class BeanRuleSet implements RuleSet {
    
    
    /** Logger */
    private static Log log = LogFactory.getLog( BeanRuleSet.class );
    
    /** 
    * Set log to be used by <code>BeanRuleSet</code> instances 
    * @param aLog the <code>Log</code> implementation for this class to log to
    */
    public static void setLog(Log aLog) {
        log = aLog;
    }
    
    /** Use this to introspect beans */
    private XMLIntrospector introspector;
    /** The base path under which the rules will be attached */
    private String basePath;
    /** The element descriptor for the base  */
    private ElementDescriptor baseElementDescriptor;
    /** The bean based  */
    private Class baseBeanClass;
    /** The (empty) base context from which all Contexts 
    with beans are (directly or indirectly) obtained */
    private ReadContext baseContext;
    /** allows an attribute to be specified to overload the types of beans used */
    private String classNameAttribute = "className";
    
    /**
     * Base constructor.
     *
     * @param introspector the <code>XMLIntrospector</code> used to introspect 
     * @param basePath specifies the (Digester-style) path under which the rules will be attached
     * @param baseElementDescriptor the <code>ElementDescriptor</code> used to create the rules
     * @param baseBeanClass the <code>Class</code> whose mapping rules will be created
     * @param matchIDs should ID/IDREFs be used to match beans?
     * @deprecated use constructor which takes a ReadContext instead
     */
    public BeanRuleSet(
                        XMLIntrospector introspector,
                        String basePath, 
                        ElementDescriptor baseElementDescriptor, 
                        Class baseBeanClass,
                        boolean matchIDs) {
        this.introspector = introspector;
        this.basePath = basePath;
        this.baseElementDescriptor = baseElementDescriptor;
        this.baseBeanClass = baseBeanClass;
        BindingConfiguration bindingConfiguration = new BindingConfiguration();
        bindingConfiguration.setMapIDs( matchIDs );
        baseContext = new ReadContext( log , bindingConfiguration, new ReadConfiguration() );
    }
    
    /**
     * Base constructor.
     *
     * @param introspector the <code>XMLIntrospector</code> used to introspect 
     * @param basePath specifies the (Digester-style) path under which the rules will be attached
     * @param baseElementDescriptor the <code>ElementDescriptor</code> used to create the rules
     * @param baseBeanClass the <code>Class</code> whose mapping rules will be created
     * @param context the root Context that bean carrying Contexts should be obtained from, 
     * not null
     * @deprecated use the constructor which takes a ReadContext instead
     */
    public BeanRuleSet(
                        XMLIntrospector introspector,
                        String basePath, 
                        ElementDescriptor baseElementDescriptor, 
                        Class baseBeanClass,
                        Context context) {
        this.introspector = introspector;
        this.basePath = basePath;
        this.baseElementDescriptor = baseElementDescriptor;
        this.baseBeanClass = baseBeanClass;
        this.baseContext = new ReadContext( context, new ReadConfiguration() );
    }
    
    /**
     * Base constructor.
     *
     * @param introspector the <code>XMLIntrospector</code> used to introspect 
     * @param basePath specifies the (Digester-style) path under which the rules will be attached
     * @param baseElementDescriptor the <code>ElementDescriptor</code> used to create the rules
     * @param baseBeanClass the <code>Class</code> whose mapping rules will be created
     * @param baseContext the root Context that bean carrying Contexts should be obtained from, 
     * not null
     */
    public BeanRuleSet(
                        XMLIntrospector introspector,
                        String basePath, 
                        ElementDescriptor baseElementDescriptor, 
                        Class baseBeanClass,
                        ReadContext baseContext) {
        this.introspector = introspector;
        this.basePath = basePath;
        this.baseElementDescriptor = baseElementDescriptor;
        this.baseBeanClass = baseBeanClass;
        this.baseContext = baseContext;
    }

    /**
     * The name of the attribute which can be specified in the XML to override the
     * type of a bean used at a certain point in the schema.
     *
     * <p>The default value is 'className'.</p>
     * 
     * @return The name of the attribute used to overload the class name of a bean
     */
    public String getClassNameAttribute() {
        return baseContext.getClassNameAttribute();
    }

    /**
     * Sets the name of the attribute which can be specified in 
     * the XML to override the type of a bean used at a certain 
     * point in the schema.
     *
     * <p>The default value is 'className'.</p>
     * 
     * @param classNameAttribute The name of the attribute used to overload the class name of a bean
     * @deprecated set the <code>ReadContext</code> property instead
     */
    public void setClassNameAttribute(String classNameAttribute) {
        baseContext.setClassNameAttribute(classNameAttribute);
    }
    
//-------------------------------- Ruleset implementation

    /** 
     * <p>Return namespace associated with this ruleset</p>
     *
     * <p><strong>Note</strong> namespaces are not currently supported.</p>
     * 
     * @return null
     */
    public String getNamespaceURI() {
        return null;
    }
    
    /**
     * Add rules for bean to given <code>Digester</code>.
     *
     * @param digester the <code>Digester</code> to which the rules for the bean will be added
     */
    public void addRuleInstances(Digester digester) {
        if (log.isTraceEnabled()) {
            log.trace("Adding rules to:" + digester);
        }
        
        ReadingContext readContext = new ReadingContext( digester );
    }
    
    /**
     * <p>A set of associated rules that maps a bean graph.
     * An instance will be created each time {@link #addRuleInstances} is called.</p>
     *
     * <p>When an instance is constructed, rules are created and added to digester.</p>
     */
    private class ReadingContext {

        /** The rules in this context indexed by path */
        private Map rulesByPath = new HashMap();
        
        /** 
         * Creates rules for bean and adds them to digester 
         * @param digester the <code>Digester</code> 
         * to which the bean mapping rules will be added
         */
        ReadingContext(Digester digester) {
            ReadContext context = new ReadContext( baseContext );
            // if the classloader is not set, set to the digester classloader
            if ( context.getClassLoader() == null ) {
                context.setClassLoader( digester.getClassLoader()  );
            }
            BeanRule rule 
                = new BeanRule( basePath + "/" , baseElementDescriptor, baseBeanClass, context );
            addRule( basePath, rule , baseElementDescriptor, context );
            
            if ( log.isDebugEnabled() ) {
                log.debug( "Added root rule to path: " + basePath + " class: " + baseBeanClass );
            } 
            
            
            Iterator it = rulesByPath.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if ( log.isTraceEnabled() ) {
                    log.trace("Added rule:" + entry.getValue() + " @path:" + entry.getKey());
                }
                digester.addRule( (String) entry.getKey() , (Rule) entry.getValue() );
            }
        }
                                                                    
        /** 
        * Add child rules for given descriptor at given prefix 
        *
        * @param prefix add child rules at this (digester) path prefix
        * @param currentDescriptor add child rules for this descriptor
        * @param context the <code>Context</code> against which beans will be evaluated 
        */
        private void addChildRules( 
                                    String prefix, 
                                    ElementDescriptor currentDescriptor, 
                                    ReadContext context ) {
            
            if (log.isTraceEnabled()) {
                log.trace("Adding child rules for " + currentDescriptor + "@" + prefix);
            }
            
            // if we are a reference to a type we should lookup the original
            // as this ElementDescriptor will be 'hollow' and have no child attributes/elements.
            // XXX: this should probably be done by the NodeDescriptors...
            ElementDescriptor typeDescriptor = getElementDescriptor( currentDescriptor );
            //ElementDescriptor typeDescriptor = descriptor;
    
            
            ElementDescriptor[] childDescriptors = typeDescriptor.getElementDescriptors();
            if ( childDescriptors != null ) {
                for ( int i = 0, size = childDescriptors.length; i < size; i++ ) {
                    final ElementDescriptor childDescriptor = childDescriptors[i];
                    if (log.isTraceEnabled()) {
                        log.trace("Processing child " + childDescriptor);
                    }
                    
                    String qualifiedName = childDescriptor.getQualifiedName();
                    if ( qualifiedName == null ) {
                        log.trace( "Ignoring" );
                        continue;
                    }
                    String path = prefix + qualifiedName;
                    // this code is for making sure that recursive elements
                    // can also be used..
                    
                    if ( qualifiedName.equals( currentDescriptor.getQualifiedName() ) 
                            && currentDescriptor.getPropertyName() != null ) {
                        log.trace("Creating generic rule for recursive elements");
                        int index = -1;
                        if (childDescriptor.isWrapCollectionsInElement()) {
                            index = prefix.indexOf(qualifiedName);
                            if (index == -1) {
                                // shouldn't happen.. 
                                log.debug( "Oops - this shouldn't happen" );
                                continue;
                            }
                            int removeSlash = prefix.endsWith("/")?1:0;
                            path = "*/" + prefix.substring(index, prefix.length()-removeSlash);
                            if (log.isTraceEnabled()) {
                                log.trace("Added wrapped rule for " + childDescriptor);
                            }
                        } else {
                            // we have a element/element type of thing..
                            ElementDescriptor[] desc = currentDescriptor.getElementDescriptors();
                            if (desc.length == 1) {
                                path = "*/"+desc[0].getQualifiedName();
                            }
                            if (log.isTraceEnabled()) {
                                log.trace("Added not wrapped rule for " + childDescriptor);
                            }
                        }
                        addRule( path, childDescriptor, context );
                        continue;
                    }
                    if ( childDescriptor.getUpdater() != null ) {
                        if (
                            log.isTraceEnabled() 
                            && childDescriptor.getUpdater() instanceof MethodUpdater) {
                            
                            log.trace("Element has updater "
                            + ((MethodUpdater) childDescriptor.getUpdater()).getMethod().getName());
                        }
                        if ( childDescriptor.isPrimitiveType() ) {
                            addPrimitiveTypeRule( path, childDescriptor, context );
                            
                        } else {
                            // add the first child to the path
                            ElementDescriptor[] grandChildren 
                                = childDescriptor.getElementDescriptors();
                            if ( grandChildren != null && grandChildren.length > 0 ) {
                                ElementDescriptor grandChild = grandChildren[0];
                                String grandChildQName = grandChild.getQualifiedName();
                                if ( grandChildQName != null && grandChildQName.length() > 0 ) {
                                    if (childDescriptor.isWrapCollectionsInElement()) {
                                        path += '/' + grandChildQName;
                                        if (log.isTraceEnabled()) {
                                            log.trace(
                                    "Descriptor wraps elements in collection, path:" 
                                                + path);
                                        }
                                        
                                    } else {
                                        path = prefix 
                                            + (prefix.endsWith("/")?"":"/") + grandChildQName;
                                        if (log.isTraceEnabled()) {
                                            log.trace(
                                    "Descriptor does not wrap elements in collection, path:" 
                                            + path);
                                        }
                                    }
                                }
                            }
                            
                            // maybe we are adding a primitve type to a collection/array
                            Class beanClass = childDescriptor.getSingularPropertyType();
                            if ( XMLIntrospectorHelper.isPrimitiveType( beanClass ) ) {
                                addPrimitiveTypeRule( path, childDescriptor, context );
                                
                            } else {
                                addRule( path, childDescriptor,  context );
                            }
                        }
                    } else {
                        if ( log.isTraceEnabled() ) {
                            log.trace("Element does not have updater: " + childDescriptor);
                        }
                    }
    
                    ElementDescriptor[] grandChildren = childDescriptor.getElementDescriptors();
                    if ( grandChildren != null && grandChildren.length > 0 ) {
                        if ( log.isTraceEnabled() ) {
                            log.trace("Adding grand children @path:" + path);
                        }
                        addChildRules( path + '/', childDescriptor, context );
                    } else if ( log.isTraceEnabled() ) {
                        log.trace( "No children for " + childDescriptor);
                    }
                }
            }
        }
        
        /** Allows the navigation from a reference to a property object to the 
        * descriptor defining what the property is. i.e. doing the join from a reference 
        * to a type to lookup its descriptor.
        * This could be done automatically by the NodeDescriptors. 
        * Refer to TODO.txt for more info.
        *
        * @param propertyDescriptor find descriptor for property object 
        * referenced by this descriptor
        * @return descriptor for the singular property class type referenced.
        */
        ElementDescriptor getElementDescriptor( ElementDescriptor propertyDescriptor ) {
            Class beanClass = propertyDescriptor.getSingularPropertyType();
            if ( beanClass != null && !Map.class.isAssignableFrom( beanClass ) ) {
                if (log.isTraceEnabled()) {
                    log.trace("Filling descriptor for: " + beanClass);
                }
                try {
                    XMLBeanInfo xmlInfo = introspector.introspect( beanClass );
                    if (log.isTraceEnabled()) {
                        log.trace("Is wrapped? " 
                        + xmlInfo.getElementDescriptor().isWrapCollectionsInElement());
                    }
                    return xmlInfo.getElementDescriptor();
                    
                } catch (Exception e) {
                    log.warn( "Could not introspect class: " + beanClass, e );
                }
            }
            // could not find a better descriptor so use the one we've got
            return propertyDescriptor;
        }
        
        /** 
        * Adds a new Digester rule to process the text as a primitive type
        *
        * @param path digester path where this rule will be attached
        * @param childDescriptor update this <code>ElementDescriptor</code> with the body text
        * @param context the <code>ReadContext</code> against which the elements will be evaluated 
        */
        void addPrimitiveTypeRule(
                                String path, 
                                final ElementDescriptor childDescriptor, 
                                final ReadContext context) {
                                
            Rule rule = new Rule() {
                public void body(String text) throws Exception {
                    childDescriptor.getUpdater().update( context, text );
                }        
            };
            add( path, rule );
        }
        
        /** 
        * Adds a new Digester rule to process the text as a primitive type
        *
        * @param path digester path where this rule will be attached
        * @param elementDescriptor update this <code>ElementDescriptor</code> with the body text
        * @param context the <code>ReadContext</code> against which the elements will be evaluated 
        */
        private void addRule( 
                            String path, 
                            ElementDescriptor elementDescriptor, 
                            ReadContext context ) {
            BeanRule rule = new BeanRule( path + '/', elementDescriptor, context );
            addRule( path, rule, elementDescriptor, context );
        }
        
        /**
        * Safely add a rule with given path.
        *
        * @param path the digester path to add rule at
        * @param rule the <code>Rule</code> to add
        * @param elementDescriptor the <code>ElementDescriptor</code> 
        * associated with this rule
        * @param context the <code>ReadContext</code> against which the elements 
        * will be evaluated        
        */
        private void addRule(
                            String path, 
                            Rule rule, 
                            ElementDescriptor elementDescriptor, 
                            ReadContext context) {
            if ( add( path, rule ) ) {
                // stop infinite recursion by allowing only one rule per path
                addChildRules( path + '/', elementDescriptor, context );
            }
        }    
        
        /**
         * Add a rule at given path.
         *
         * @param path add rule at this path
         * @param rule the <code>Rule</code> to add
         *
         * @return true if this rule was successfully add at given path
         */
        private boolean add( String path, Rule rule ) {
            // only one bean rule allowed per path
            if ( ! rulesByPath.containsKey( path ) ) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "Added rule for path: " + path + " rule: " + rule );
                    if (log.isTraceEnabled()) {
                        log.trace( rulesByPath );
                    }
                }
                rulesByPath.put( path, rule );
                return true;
                
            } else {
                if ( log.isDebugEnabled() ) {
                    log.debug( "Ignoring duplicate digester rule for path: " 
                                + path + " rule: " + rule );
                    log.debug( "New rule (not added): " + rule );
                    log.debug( "Existing rule:" + rulesByPath.get(path) );
                }
            }
            return false;
        }
        
        /**
        * Rule that creates bean and updates methods.
        */
        private class BeanRule extends Rule {
            
            /** The descriptor of this element */
            private ElementDescriptor descriptor;
            /** The Context used when evaluating Updaters */
            private ReadContext context;
            /** In this begin-end loop did we actually create a new bean */
            private boolean createdBean;
            /** The type of the bean to create */
            private Class beanClass;
            /** The prefix added to digester rules */
            private String pathPrefix;
            
            
            /** 
            * Constructor uses standard qualified name from <code>ElementDescriptor</code>.
            * 
            * @param descriptor the <code>ElementDescriptor</code> describing the element mapped
            * @param beanClass the <code>Class</code> to be created
            * @param context the <code>ReadContext</code> for this rule
            */
            public BeanRule( ElementDescriptor descriptor, Class beanClass, ReadContext context ) {
                this( descriptor.getQualifiedName() + "/", descriptor, beanClass, context );
            }
            
            /**
            * Construct a rule based on singular property type of <code>ElementDescriptor</code>.
            *
            * @param descriptor the <code>ElementDescriptor</code> describing the element mapped
            * @param context the <code>Context</code> to be used to evaluate expressions
            * @param pathPrefix the digester path prefix
            */
            public BeanRule(
                                    String pathPrefix,
                                    ElementDescriptor descriptor, 
                                    ReadContext context ) {
                this( 
                        pathPrefix,
                        descriptor, 
                        descriptor.getSingularPropertyType(), 
                        context );
            }
            
            /**
            * Base constructor (used by other constructors).
            *
            * @param descriptor the <code>ElementDescriptor</code> describing the element mapped
            * @param beanClass the <code>Class</code> of the bean to be created
            * @param context the <code>Context</code> to be used to evaluate expressions
            * @param pathPrefix the digester path prefix
            */
            private BeanRule(
                                    String pathPrefix, 
                                    ElementDescriptor descriptor, 
                                    Class beanClass,
                                    ReadContext context ) {
                this.descriptor = descriptor;        
                this.context = context;
                this.beanClass = beanClass;
                this.pathPrefix = pathPrefix;
    
                if (log.isTraceEnabled()) {
                    log.trace("Created bean create rule");
                    log.trace("Descriptor=" + descriptor);
                    log.trace("Class=" + beanClass);
                    log.trace("Path prefix=" + pathPrefix);
                }
            }
                
            // Rule interface
            //-------------------------------------------------------------------------    
            
            /**
              * @see Rule#begin(String, String, Attributes)
              */
            public void begin(String namespace, String name, Attributes attributes) {
                log.debug( "Called with descriptor: " + descriptor 
                            + " propertyType: " + descriptor.getPropertyType() );
                
                if (log.isTraceEnabled()) {
                    int attributesLength = attributes.getLength();
                    if (attributesLength > 0) {
                        log.trace("Attributes:");
                    }
                    for (int i=0, size=attributesLength; i<size; i++) {
                        log.trace("Local:" + attributes.getLocalName(i));
                        log.trace("URI:" + attributes.getURI(i));
                        log.trace("QName:" + attributes.getQName(i));
                    }
                }
                
        
                
                // XXX: if a single rule instance gets reused and nesting occurs
                // XXX: we should probably use a stack of booleans to test if we created a bean
                // XXX: or let digester take nulls, which would be easier for us ;-)
                createdBean = false;
                        
                Object instance = null;
                if ( beanClass != null ) {
                    instance = createBean( namespace, name, attributes );
                    if ( instance != null ) {
                        createdBean = true;
                        context.setBean( instance );
                        digester.push( instance );
                        
                
                        // if we are a reference to a type we should lookup the original
                        // as this ElementDescriptor will be 'hollow' 
                        // and have no child attributes/elements.
                        // XXX: this should probably be done by the NodeDescriptors...
                        ElementDescriptor typeDescriptor = getElementDescriptor( descriptor );
                        //ElementDescriptor typeDescriptor = descriptor;
                
                        // iterate through all attributes        
                        AttributeDescriptor[] attributeDescriptors 
                            = typeDescriptor.getAttributeDescriptors();
                        if ( attributeDescriptors != null ) {
                            for ( int i = 0, size = attributeDescriptors.length; i < size; i++ ) {
                                AttributeDescriptor attributeDescriptor = attributeDescriptors[i];
                                
                                // The following isn't really the right way to find the attribute
                                // but it's quite robust.
                                // The idea is that you try both namespace and local name first
                                // and if this returns null try the qName.
                                String value = attributes.getValue( 
                                    attributeDescriptor.getURI(),
                                    attributeDescriptor.getLocalName() 
                                );
                                
                                if (value == null) {
                                    value = attributes.getValue(
                                        attributeDescriptor.getQualifiedName());
                                }
                                
                                if (log.isTraceEnabled()) {
                                    log.trace("Attr URL:" + attributeDescriptor.getURI());
                                    log.trace(
                                                "Attr LocalName:" 
                                                + attributeDescriptor.getLocalName() );
                                    log.trace(value);
                                }
                                
                                Updater updater = attributeDescriptor.getUpdater();
                                log.trace(updater);
                                if ( updater != null && value != null ) {
                                    updater.update( context, value );
                                }
                            }
                        }
                        
                        if (log.isTraceEnabled()) {
                            log.trace("Created bean " + instance);
                            log.trace("Path prefix: " + pathPrefix);
                        }
                        
                        // add bean for ID matching
                        if ( context.getMapIDs() ) {
                            // XXX need to support custom ID attribute names
                            // XXX i have a feeling that the current mechanism might need to change
                            // XXX so i'm leaving this till later
                            String id = attributes.getValue( "id" );
                            if ( id != null ) {
                                context.putBean( id, instance );
                            }
                        }
                    }
                }
            }
            
            /**
              * @see Rule#body(String, String, String)
              */
            public void body(String namespace, String name, String text) {
                
                log.trace("Body with text " + text);
                if ( digester.getCount() > 0 ) {
                    Context bodyContext = context.newContext( digester.peek() );
                    // Take the first content descriptor
                    ElementDescriptor typeDescriptor = getElementDescriptor( descriptor );
                    TextDescriptor descriptor = typeDescriptor.getPrimaryBodyTextDescriptor();
                    if ( descriptor != null ) {
                        if ( log.isTraceEnabled() ) {
                            log.trace("Setting mixed content for:");
                            log.trace(descriptor);
                        }
                        Updater updater = descriptor.getUpdater();
                        log.trace( "Updating mixed content with:" );
                        log.trace( updater );
                        if ( updater != null && text != null ) {
                            updater.update( bodyContext, text );
                        }
                    }
                }
            }

            /**
            * Process the end of this element.
            */
            public void end() {
                if ( createdBean ) {
                    
                    // force any setters of the parent bean to be called for this new bean instance
                    Updater updater = descriptor.getUpdater();
                    Object instance = context.getBean();
        
                    Object top = digester.pop();
                    if (log.isTraceEnabled()) {
                        log.trace("Popped " + top);
                    }
                    if (digester.getCount() == 0) {
                        context.setBean(null);
                    }else{
                        context.setBean( digester.peek() );
                    }
        
                    if ( updater != null ) {
                        if ( log.isDebugEnabled() ) {
                            log.debug( "Calling updater for: " + descriptor + " with: " 
                                + instance + " on bean: " + context.getBean() );
                        }
                        updater.update( context, instance );
                    } else {
                        if ( log.isDebugEnabled() ) {
                            log.debug( "No updater for: " + descriptor + " with: " 
                                + instance + " on bean: " + context.getBean() );
                        }
                    }
                }
            }
        
            /** 
             * Tidy up.
             */
            public void finish() {
                //
                // Clear indexed beans so that we're ready to process next document
                //
                baseContext.clearBeans();
            }
        
        
            // Implementation methods
            //-------------------------------------------------------------------------    
            
            /** 
            * Factory method to create new bean instances 
            *
            * @param namespace the namespace for the element
            * @param name the local name
            * @param attributes the <code>Attributes</code> used to match <code>ID/IDREF</code>
            * @return the created bean
            */
            protected Object createBean( String namespace, String name, Attributes attributes ) {
                // todo: recycle element mappings 
                ElementMapping mapping = new ElementMapping();
                mapping.setType( beanClass );
                mapping.setNamespace( namespace );
                mapping.setName( name );
                mapping.setAttributes( attributes );
                mapping.setDescriptor( descriptor );
                
                Object newInstance = context.getBeanCreationChain().create( mapping, context );
                
                return newInstance;
            }    
            
            /**
            * Return something meaningful for logging.
            *
            * @return something useful for logging
            */
            public String toString() {
                return "BeanRule [path prefix=" + pathPrefix + " descriptor=" + descriptor + "]";
            }
        }
    }
}  

