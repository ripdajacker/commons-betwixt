/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/BeanReader.java,v 1.5 2002/10/24 11:13:22 jstrachan Exp $
 * $Revision: 1.5 $
 * $Date: 2002/10/24 11:13:22 $
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
 * $Id: BeanReader.java,v 1.5 2002/10/24 11:13:22 jstrachan Exp $
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
  * @version $Revision: 1.5 $
  */
public class BeanReader extends Digester {

    /** Introspector used */
    private XMLIntrospector introspector = new XMLIntrospector();    
    /** Log used for logging (Doh!) */
    private Log log = LogFactory.getLog( BeanReader.class );
    /** The registered classes */
    private Set registeredClasses = new HashSet();
    /** Should the reader use <code>ID</code>'s to match */
    private boolean matchIDs = true;
    
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

    
    /** 
     * Registers a bean class which is used by the reader
     * to deduce the digester rules.
     */
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
            addBeanCreateRule( "*/" + path, elementDescriptor, beanClass );
        }
    }
    
    /** 
     * Registers a bean class at the given path expression 
     * which is used by the reader to deduce the digester rules.
     */
    public void registerBeanClass(String path, Class beanClass) throws IntrospectionException {
        if ( ! registeredClasses.contains( beanClass ) ) {
            registeredClasses.add( beanClass );
            
            // introspect and find the ElementDescriptor to use as the root
            XMLBeanInfo xmlInfo = introspector.introspect( beanClass );
            ElementDescriptor elementDescriptor = xmlInfo.getElementDescriptor();        

            addBeanCreateRule( path, elementDescriptor, beanClass );
        } else {
            log.warn("Cannot add class "  + beanClass.getName() + " since it already exists");
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
        setLogger(log);
    }
    
    /** 
     * Should the reader use <code>ID</code> attributes to match beans.
     */
    public boolean getMatchIDs() {
        return matchIDs;
    }
    
    /**
     * Set whether the read should use <code>ID</code> attributes to match beans.
     */
    public void setMatchIDs(boolean matchIDs) {
        this.matchIDs = matchIDs;
    }
        
    // Implementation methods
    //-------------------------------------------------------------------------    
    
    /** 
     * Adds a new bean create rule for the specified path
     */
    protected void addBeanCreateRule( String path, ElementDescriptor elementDescriptor, Class beanClass ) {
        Rule rule = new BeanCreateRule( elementDescriptor, beanClass, path + "/" , matchIDs);
        addRule( path, rule );

        if ( log.isDebugEnabled() ) {
            log.debug( "Added root rule to path: " + path + " rule: " + rule );
        }
    }
        
}
