/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: BeanReader.java,v 1.1 2002/06/10 17:53:32 jstrachan Exp $
 */
package org.apache.commons.betwixt.io;

import java.beans.IntrospectionException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.SAXParser;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.expression.Expression;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.XMLReader;

/** <p><code>BeanReader</code> reads a tree of beans from an XML document.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public class BeanReader extends Digester {

    /** Introspector used */
    private XMLIntrospector introspector = new XMLIntrospector();    
    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( BeanReader.class );
    /** The registered classes */
    private Set registeredClasses = new HashSet();
    
    /**
     * Construct a new BeanReader with default properties.
     */
    public BeanReader() {
    }

    /**
     * Construct a new BeanReader, allowing a SAXParser to be passed in.  This
     * allows BeanReader to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Thanks for the request to change go to
     * James House (james@interobjective.com).  This may help in places where
     * you are able to load JAXP 1.1 classes yourself.
     */
    public BeanReader(SAXParser parser) {
        super(parser);
    }

    /**
     * Construct a new BeanReader, allowing an XMLReader to be passed in.  This
     * allows BeanReader to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Note that if you use this option you
     * have to configure namespace and validation support yourself, as these
     * properties only affect the SAXParser and emtpy constructor.
     */
    public BeanReader(XMLReader reader) {
        super(reader);
    }

    
    /** Registers a bean class for use by the writer */
    public void registerBeanClass(Class beanClass) throws IntrospectionException {
        if ( ! registeredClasses.contains( beanClass ) ) {
            registeredClasses.add( beanClass );
            
            // introspect and find the ElementDescriptor to use as the root
            XMLBeanInfo xmlInfo = introspector.introspect( beanClass );
            ElementDescriptor elementDescriptor = xmlInfo.getElementDescriptor();        

            String path = elementDescriptor.getQualifiedName();
            if (log.isTraceEnabled()) {
                log.trace("Added path: " + path + ", mapped to: " + beanClass.getName());
            }
            addBeanCreateRule( path, elementDescriptor, beanClass );
        }
    }
    
    /** Registers a bean class for use by the writer at the given path */
    public void registerBeanClass(String path, Class beanClass) throws IntrospectionException {
        if ( ! registeredClasses.contains( beanClass ) ) {
            registeredClasses.add( beanClass );
            
            // introspect and find the ElementDescriptor to use as the root
            XMLBeanInfo xmlInfo = introspector.introspect( beanClass );
            ElementDescriptor elementDescriptor = xmlInfo.getElementDescriptor();        

            addBeanCreateRule( path, elementDescriptor, beanClass );
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------        

    /**
     * <p> Get the introspector used. </p>
     *
     * <p> The {@link XMLBeanInfo} used to map each bean is created by the <code>XMLIntrospector</code>.
     * One way in which the mapping can be customized is by altering the <code>XMLIntrospector</code>. </p>
     */
    public XMLIntrospector getXMLIntrospector() {
        return introspector;
    }
    

    /**
     * <p> Set the introspector to be used. </p>
     *
     * <p> The {@link XMLBeanInfo} used to map each bean is created by the <code>XMLIntrospector</code>.
     * One way in which the mapping can be customized is by altering the <code>XMLIntrospector</code>. </p>
     *
     * @param introspector use this introspector
     */
    public void setXMLIntrospector(XMLIntrospector introspector) {
        this.introspector = introspector;
    }

    /**
     * <p> Get the current level for logging. </p>
     *
     * @return a <code>org.apache.commons.logging.Log</code> level constant
     */ 
    public Log getLog() {
        return log;
    }

    /**
     * <p> Set the current logging level. </p>
     *
     * @param level a <code>org.apache.commons.logging.Log</code> level constant
     */ 
    public void setLog(Log log) {
        this.log = log;
    }
        
    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /** 
     * Adds a new bean create rule for the specified path
     */
    protected void addBeanCreateRule( String path, ElementDescriptor elementDescriptor, Class beanClass ) {
        Rule rule = new BeanCreateRule( elementDescriptor, beanClass, path + "/" );
        addRule( path, rule );

        if ( log.isDebugEnabled() ) {
            log.debug( "Added root rule to path: " + path + " rule: " + rule );
        }
    }
        
}
