/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: BeanCreateRule.java,v 1.3 2002/06/14 21:39:16 mvdb Exp $
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
  * @version $Revision: 1.3 $
  */
public class BeanCreateRule extends Rule {

    /** Logger */
    private static final Log log = LogFactory.getLog( BeanCreateRule.class );
    
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
    
    public BeanCreateRule(ElementDescriptor descriptor, Class beanClass, String pathPrefix) {
        this.descriptor = descriptor;
        this.context = new Context();
        this.beanClass = beanClass;
        this.pathPrefix = pathPrefix;
    }
    
    public BeanCreateRule(ElementDescriptor descriptor, Class beanClass) {
        this( descriptor, beanClass, descriptor.getQualifiedName() + "/" );
    }
    
    public BeanCreateRule(ElementDescriptor descriptor, Context context, String pathPrefix) {
        this.descriptor = descriptor;        
        this.context = context;
        this.beanClass = descriptor.getSingularPropertyType();
        this.pathPrefix = pathPrefix;
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


    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /** Factory method to create new bean instances */
    protected Object createBean(Attributes attributes) throws Exception {
        try {
            return beanClass.newInstance();
        }
        catch (Exception e) {
            log.debug( "Could not create instance of type: " + beanClass.getName() );
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

        // if we are a reference to a type we should lookup the original
        // as this ElementDescriptor will be 'hollow' and have no child attributes/elements.
        // XXX: this should probably be done by the NodeDescriptors...
        ElementDescriptor typeDescriptor = getElementDescriptor( currentDescriptor );
        //ElementDescriptor typeDescriptor = descriptor;

        ElementDescriptor[] childDescriptors = typeDescriptor.getElementDescriptors();
        if ( childDescriptors != null ) {
            for ( int i = 0, size = childDescriptors.length; i < size; i++ ) {
                final ElementDescriptor childDescriptor = childDescriptors[i];

                String propertyName = childDescriptor.getPropertyName();
                String qualifiedName = childDescriptor.getQualifiedName();
                if ( qualifiedName == null ) {
                    continue;
                }
                String path = prefix + qualifiedName;
                
                if ( childDescriptor.getUpdater() != null ) {
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
                            Rule rule = new BeanCreateRule( childDescriptor, context, path + '/' );
                            addRule( path, rule );
                        }
                    }
                }

                ElementDescriptor[] grandChildren = childDescriptor.getElementDescriptors();
                if ( grandChildren != null && grandChildren.length > 0 ) {
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

}
