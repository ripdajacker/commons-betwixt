/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/BeanCreateRule.java,v 1.10 2002/08/29 21:22:52 rdonkin Exp $
 * $Revision: 1.10 $
 * $Date: 2002/08/29 21:22:52 $
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
 * $Id: BeanCreateRule.java,v 1.10 2002/08/29 21:22:52 rdonkin Exp $
 */
package org.apache.commons.betwixt.io;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.expression.MethodUpdater;
import org.apache.commons.betwixt.expression.Updater;
import org.apache.commons.betwixt.digester.XMLIntrospectorHelper;

import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.Digester;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.Attributes;

/** <p><code>BeanCreateRule</code> is a Digester Rule for creating beans
  * from the betwixt XML metadata.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
  * @version $Revision: 1.10 $
  */
public class BeanCreateRule extends Rule {

    /** Logger */
    private static final Log log = LogFactory.getLog( BeanCreateRule.class );
    
    /** Set log to be used by <code>BeanCreateRule</code> instances */
    public static void setLog(Log log) {
        log = log;
    }
    
    /** The descriptor of this element */
    private ElementDescriptor descriptor;
    /** The Context used when evaluating Updaters */
    private Context context;
    /** Have we added our child rules to the digester? */
    private boolean addedChildren;
    /** In this begin-end loop did we actually create a new bean */
    private boolean createdBean;
    /** The type of the bean to create */
    private Class beanClass;
    /** The prefix added to digester rules */
    private String pathPrefix;
    /** Beans digested indexed by <code>ID</code> */
    private Map beansById = new HashMap();
    /** Use id's to match beans */
    private boolean matchIDs = true;
    
    public BeanCreateRule(
                            ElementDescriptor descriptor, 
                            Class beanClass, 
                            String pathPrefix, 
                            boolean matchIDs) {
        this( 
                descriptor, 
                beanClass, 
                new Context(), 
                pathPrefix,
                matchIDs);
    }
    
    public BeanCreateRule(ElementDescriptor descriptor, Class beanClass, boolean matchIDs) {
        this( descriptor, beanClass, descriptor.getQualifiedName() + "/" , matchIDs);
    }
    
    public BeanCreateRule(
                            ElementDescriptor descriptor, 
                            Context context, 
                            String pathPrefix,
                            boolean matchIDs) {
        this( 
                descriptor, 
                descriptor.getSingularPropertyType(), 
                context, 
                pathPrefix,
                matchIDs);
    }
    
    private BeanCreateRule(
                            ElementDescriptor descriptor, 
                            Class beanClass,
                            Context context, 
                            String pathPrefix,
                            boolean matchIDs) {
        this.descriptor = descriptor;        
        this.context = context;
        this.beanClass = beanClass;
        this.pathPrefix = pathPrefix;
        this.matchIDs = matchIDs;
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
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
     */
    public void begin(Attributes attributes) throws Exception {
        log.debug( "Called with descriptor: " + descriptor + " propertyType: " + descriptor.getPropertyType() );
        
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
            instance = createBean(attributes);
            if ( instance != null ) {
                createdBean = true;

                context.setBean( instance );
                digester.push(instance);
                
        
                // if we are a reference to a type we should lookup the original
                // as this ElementDescriptor will be 'hollow' and have no child attributes/elements.
                // XXX: this should probably be done by the NodeDescriptors...
                ElementDescriptor typeDescriptor = getElementDescriptor( descriptor );
                //ElementDescriptor typeDescriptor = descriptor;
        
                // iterate through all attributes        
                AttributeDescriptor[] attributeDescriptors = typeDescriptor.getAttributeDescriptors();
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
                            value = attributes.getValue(attributeDescriptor.getQualifiedName());
                        }
                        
                        if (log.isTraceEnabled()) {
                            log.trace("Attr URL:" + attributeDescriptor.getURI());
                            log.trace("Attr LocalName:" + attributeDescriptor.getLocalName() );
                            log.trace(value);
                        }
                        
                        Updater updater = attributeDescriptor.getUpdater();
                        log.trace(updater);
                        if ( updater != null && value != null ) {
                            updater.update( context, value );
                        }
                    }
                }
                
                addChildRules();
                
                // add bean for ID matching
                if ( matchIDs ) {
                    // XXX need to support custom ID attribute names
                    // XXX i have a feeling that the current mechanism might need to change
                    // XXX so i'm leaving this till later
                    String id = attributes.getValue( "id" );
                    if ( id != null ) {
                        beansById.put( id, instance );
                    }
                }
            }
        }
    }

    /**
     * Process the end of this element.
     */
    public void end() throws Exception {
        if ( createdBean ) {
            
            // force any setters of the parent bean to be called for this new bean instance
            Updater updater = descriptor.getUpdater();
            Object instance = context.getBean();

            Object top = digester.pop();
            if (digester.getCount() == 0) {
                context.setBean(null);
            }else{
                context.setBean( digester.peek() );
            }

            if ( updater != null ) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "Calling updater for: " + descriptor + " with: " + instance + " on bean: " + context.getBean() );
                }
                updater.update( context, instance );
            }
            
        }
    }

    /** 
     * Tidy up.
     */
    public void finish() {
        // clear beans map
        beansById.clear();
    }


    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /** Factory method to create new bean instances */
    protected Object createBean(Attributes attributes) throws Exception {
        //
        // See if we've got an IDREF
        //
        // XXX This should be customizable but i'm not really convinced by the existing system
        // XXX maybe it's going to have to change so i'll use 'idref' for nows
        //
        if ( matchIDs ) {
            String idref = attributes.getValue( "idref" );
            if ( idref != null ) {
                // XXX need to check up about ordering
                // XXX this is a very simple system that assumes that id occurs before idrefs
                // XXX would need some thought about how to implement a fuller system
                Object bean = beansById.get( idref );
                if ( bean != null ) {
                    return bean;
                }
            }
        }
        
        try {
            return beanClass.newInstance();
        }
        catch (Exception e) {
            log.warn( "Could not create instance of type: " + beanClass.getName() );
            return null;
        }
    }    
        
    /** Adds the rules to the digester for all child elements */
    protected void addChildRules() {
        if ( ! addedChildren ) {
            addedChildren = true;
            
            addChildRules( pathPrefix, descriptor );
        }
    }
                        
    protected void addChildRules(String prefix, ElementDescriptor currentDescriptor ) {
        BeanReader digester = getBeanReader();            
        
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
                
                String propertyName = childDescriptor.getPropertyName();
                String qualifiedName = childDescriptor.getQualifiedName();
                if ( qualifiedName == null ) {
                    log.trace("Ignoring");
                    continue;
                }
                String path = prefix + qualifiedName;
                // this code is for making sure that recursive elements
                // can also be used..
                if (qualifiedName.equals(currentDescriptor.getQualifiedName())) {
                    log.trace("Creating generic rule for recursive elements");
                    int index = -1;
                    if (childDescriptor.isWrapCollectionsInElement()) {
                        index = prefix.indexOf(qualifiedName);
                        if (index == -1) {
                            // shouldn't happen.. 
                            continue;
                        }
                        int removeSlash = prefix.endsWith("/")?1:0;
                        path = "*/" + prefix.substring(index, prefix.length()-removeSlash);
                    }else{
                        // we have a element/element type of thing..
                        ElementDescriptor[] desc = currentDescriptor.getElementDescriptors();
                        if (desc.length == 1) {
                            path = "*/"+desc[0].getQualifiedName();
                        }
                    }
                    Rule rule = new BeanCreateRule( childDescriptor, context, path, matchIDs);
                    addRule(path, rule);
                    continue;
                }
                if ( childDescriptor.getUpdater() != null ) {
                    if (log.isTraceEnabled()) {
                        log.trace("Element has updater "
                                +((MethodUpdater) childDescriptor.getUpdater()).getMethod().getName());
                    }
                    if ( childDescriptor.isPrimitiveType() ) {
                        addPrimitiveTypeRule(path, childDescriptor);
                    }
                    else {
                        // add the first child to the path
                        ElementDescriptor[] grandChildren = childDescriptor.getElementDescriptors();
                        if ( grandChildren != null && grandChildren.length > 0 ) {
                            ElementDescriptor grandChild = grandChildren[0];
                            String grandChildQName = grandChild.getQualifiedName();
                            if ( grandChildQName != null && grandChildQName.length() > 0 ) {
                                if (childDescriptor.isWrapCollectionsInElement()) {
                                    path += '/' + grandChildQName;
                                }
                                else{
                                    path = prefix + grandChildQName;
                                }
                            }
                        }
                        
                        // maybe we are adding a primitve type to a collection/array
                        Class beanClass = childDescriptor.getSingularPropertyType();
                        if ( XMLIntrospectorHelper.isPrimitiveType( beanClass ) ) {
                            addPrimitiveTypeRule(path, childDescriptor);
                        }
                        else {
                            Rule rule = new BeanCreateRule( childDescriptor, context, path + '/', matchIDs );
                            addRule( path, rule );
                        }
                    }
                } else {
                    log.trace("Element does not have updater");
                }

                ElementDescriptor[] grandChildren = childDescriptor.getElementDescriptors();
                if ( grandChildren != null && grandChildren.length > 0 ) {
                    log.trace("Adding grand children");
                    addChildRules( path + '/', childDescriptor );
                }
            }
        }
    }

    protected BeanReader getBeanReader() {
        return (BeanReader) getDigester();
    }
    
    /** Allows the navigation from a reference to a property object to the descriptor defining what 
     * the property is. i.e. doing the join from a reference to a type to lookup its descriptor.
     * This could be done automatically by the NodeDescriptors. Refer to TODO.txt for more info.
     */
    protected ElementDescriptor getElementDescriptor( ElementDescriptor propertyDescriptor ) {
        Class beanClass = propertyDescriptor.getSingularPropertyType();
        if ( beanClass != null ) {
            XMLIntrospector introspector = getBeanReader().getXMLIntrospector();
            try {
                XMLBeanInfo xmlInfo = introspector.introspect( beanClass );
                return xmlInfo.getElementDescriptor();
            }
            catch (Exception e) {
                log.warn( "Could not introspect class: " + beanClass, e );
            }
        }
        // could not find a better descriptor so use the one we've got
        return propertyDescriptor;
    }
    
    /** 
     * Adds a new Digester rule to process the text as a primitive type
     */
    protected void addPrimitiveTypeRule(String path, final ElementDescriptor childDescriptor) {
        Rule rule = new Rule() {
            public void body(String text) throws Exception {
                childDescriptor.getUpdater().update( context, text );
            }        
        };
        addRule( path, rule );
    }
    
    protected void addRule(String path, Rule rule) {
        Rules rules = digester.getRules();
        List matches = rules.match(null, path);
        if ( matches.isEmpty() ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Adding digester rule for path: " + path + " rule: " + rule );
            }
            digester.addRule( path, rule );
        }
        else {
            if ( log.isDebugEnabled() ) {
                log.debug( "Ignoring duplicate digester rule for path: " + path + " rule: " + rule );
            }
        }
    }

    public String toString() {
        return "BeanCreateRule [path prefix=" + pathPrefix + " descriptor=" + descriptor + "]";
    }
    
}
