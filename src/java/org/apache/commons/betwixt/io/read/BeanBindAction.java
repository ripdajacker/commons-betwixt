/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/read/BeanBindAction.java,v 1.1.2.4 2004/02/21 16:34:57 rdonkin Exp $
 * $Revision: 1.1.2.4 $
 * $Date: 2004/02/21 16:34:57 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 * $Id: BeanBindAction.java,v 1.1.2.4 2004/02/21 16:34:57 rdonkin Exp $
 */
package org.apache.commons.betwixt.io.read;

import java.util.Map;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.TextDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.expression.Updater;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;

/**
 * Action that creates and binds a new bean instance.
 * 
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.4 $
 */
public class BeanBindAction extends MappingAction.Base {

    /** Singleton instance */
    public static final BeanBindAction INSTANCE = new BeanBindAction();

    public void body(String text, ReadContext context) throws Exception {
        Log log = context.getLog();
        // Take the first content descriptor
        ElementDescriptor pathDescriptor = context.getCurrentDescriptor();
        if (pathDescriptor == null) {
            if (log.isTraceEnabled()) {
                log.trace("path descriptor is null:");
            }
        } else {
            ElementDescriptor typeDescriptor =
                getElementDescriptor(
                    pathDescriptor,
                    context.getXMLIntrospector());
            TextDescriptor descriptor =
                typeDescriptor.getPrimaryBodyTextDescriptor();
            if (descriptor != null) {
                if (log.isTraceEnabled()) {
                    log.trace("Setting mixed content for:");
                    log.trace(descriptor);
                }
                Updater updater = descriptor.getUpdater();
                log.trace("Updating mixed content with:");
                log.trace(updater);
                if (updater != null && text != null) {
                    updater.update(context, text);
                }
            }
        }
    }

    public void end(ReadContext context) throws Exception {
        // force any setters of the parent bean to be called for this new bean instance
        Object instance = context.popBean();
        update(context, instance);
    }

    private void update(ReadContext context, Object value) throws Exception {
        Log log = context.getLog();
        boolean popped = false;
        //TODO: add dyna-bean support!
        // probably refactoring needed
        
        ElementDescriptor currentDescriptor = context.getCurrentDescriptor();
        ElementDescriptor parentDescriptor = context.getParentElementDescriptor();
        Updater updater = currentDescriptor.getUpdater();
        if (updater == null) {
            if (parentDescriptor != null) {
                updater = parentDescriptor.getUpdater();         
            }
            String poppedElement = context.popElement();
            popped = true;
        }
        
        if ( updater == null ) {
            if (!context.isAtRootElement() && !context.isStackEmpty()) {
                if ( context.getLog().isDebugEnabled() ) {
                    context.getLog().debug("Cannot find updater for " + context.getCurrentElement());
                }
            }
        } else {
            updater.update(context, value);
        }

        if (!popped) {
            String poppedElement = context.popElement();
        }
    }

 

    ElementDescriptor getElementDescriptor(
        ElementDescriptor propertyDescriptor,
        XMLIntrospector introspector) {
        if (propertyDescriptor == null) {
            throw new RuntimeException("Property descriptor is null");
        }
        Class beanClass = propertyDescriptor.getSingularPropertyType();
        if (beanClass != null && !Map.class.isAssignableFrom(beanClass)) {
            try {
                XMLBeanInfo xmlInfo = introspector.introspect(beanClass);

                return xmlInfo.getElementDescriptor();

            } catch (Exception e) {
                // TODO: WORK OUT EXCEPTION HANDLING STRATEGY
                System.out.println(
                    "Could not introspect class: " + beanClass + e);
            }
        }
        // could not find a better descriptor so use the one we've got
        return propertyDescriptor;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.betwixt.io.read.MappingAction#begin(java.lang.String, java.lang.String, org.xml.sax.Attributes, org.apache.commons.betwixt.io.read.ReadContext)
     */
    public MappingAction begin(
        String namespace,
        String name,
        Attributes attributes,
        ReadContext context)
        throws Exception {
        Log log = context.getLog();

        ElementDescriptor computedDescriptor = context.getActiveDescriptor();

        if (computedDescriptor == null) {
            log.trace("No Descriptor");
        }

        if (log.isTraceEnabled()) {
            log.trace("Element Pushed: " + name);
        }

        MappingAction action = MappingAction.EMPTY;

        Object instance = null;
        Class beanClass = computedDescriptor.getSingularPropertyType();

        if (beanClass != null) {

            instance =
                createBean(
                    namespace,
                    name,
                    attributes,
                    computedDescriptor,
                    context);
            if (instance != null) {
                action = this;
                context.markClassMap(beanClass);

                if (log.isTraceEnabled()) {
                    log.trace("Marked: " + beanClass);
                }

                context.pushBean(instance);

                // if we are a reference to a type we should lookup the original
                // as this ElementDescriptor will be 'hollow' 
                // and have no child attributes/elements.
                // XXX: this should probably be done by the NodeDescriptors...
                ElementDescriptor typeDescriptor =
                    getElementDescriptor(computedDescriptor, context);
                //ElementDescriptor typeDescriptor = descriptor;

                // iterate through all attributes        
                AttributeDescriptor[] attributeDescriptors =
                    typeDescriptor.getAttributeDescriptors();
                context.populateAttributes(attributeDescriptors, attributes);

                if (log.isTraceEnabled()) {
                    log.trace("Created bean " + instance);
                }

                // add bean for ID matching
                if (context.getMapIDs()) {
                    // XXX need to support custom ID attribute names
                    // XXX i have a feeling that the current mechanism might need to change
                    // XXX so i'm leaving this till later
                    String id = attributes.getValue("id");
                    if (id != null) {
                        context.putBean(id, instance);
                    }
                }
            }
        }
        return action;
    }


    /** 
    * Factory method to create new bean instances 
    *
    * @param namespace the namespace for the element
    * @param name the local name
    * @param attributes the <code>Attributes</code> used to match <code>ID/IDREF</code>
    * @return the created bean
    */
    protected Object createBean(
        String namespace,
        String name,
        Attributes attributes,
        ElementDescriptor descriptor,
        ReadContext context) {
        // TODO: recycle element mappings 
        // Maybe should move the current mapping into the context
        ElementMapping mapping = new ElementMapping();
        Class beanClass = descriptor.getSingularPropertyType();

        // TODO: beanClass can be deduced from descriptor
        // so probably 
        mapping.setType(beanClass);
        mapping.setNamespace(namespace);
        mapping.setName(name);
        mapping.setAttributes(attributes);
        mapping.setDescriptor(descriptor);

        Object newInstance =
            context.getBeanCreationChain().create(mapping, context);

        return newInstance;
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
    ElementDescriptor getElementDescriptor(
        ElementDescriptor propertyDescriptor,
        ReadContext context) {
        Log log = context.getLog();
        Class beanClass = propertyDescriptor.getSingularPropertyType();
        if (beanClass != null && !Map.class.isAssignableFrom(beanClass)) {
            if (log.isTraceEnabled()) {
                log.trace("Filling descriptor for: " + beanClass);
            }
            try {
                XMLBeanInfo xmlInfo =
                    context.getXMLIntrospector().introspect(beanClass);
                return xmlInfo.getElementDescriptor();

            } catch (Exception e) {
                log.warn("Could not introspect class: " + beanClass, e);
            }
        }
        // could not find a better descriptor so use the one we've got
        return propertyDescriptor;
    }

}
