/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/digester/XMLBeanInfoDigester.java,v 1.4 2003/03/19 22:59:01 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/03/19 22:59:01 $
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
 * $Id: XMLBeanInfoDigester.java,v 1.4 2003/03/19 22:59:01 rdonkin Exp $
 */
package org.apache.commons.betwixt.digester;

import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;

import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.XMLReader;

/** <p><code>XMLBeanInfoDigester</code> is a digester of XML files
  * containing XMLBeanInfo definitions for a JavaBean.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.4 $
  */
public class XMLBeanInfoDigester extends Digester {

    /** Logger */
    private static final Log log = LogFactory.getLog( XMLBeanInfoDigester.class );
    
    /** the beans class for this XML descriptor */
    private Class beanClass;
    
    /** should attributes or elements be used for primitive types */
    private boolean attributesForPrimitives;
    
    /** the set of property names processed so far */
    private Set processedPropertyNameSet = new HashSet();

    /** the introspector that is using me */
    private XMLIntrospector introspector;
    
    /**
     * Construct a new XMLBeanInfoDigester with default properties.
     */
    public XMLBeanInfoDigester() {
    }

    /**
     * Construct a new XMLBeanInfoDigester, allowing a SAXParser to be passed in.  This
     * allows XMLBeanInfoDigester to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Thanks for the request to change go to
     * James House (james@interobjective.com).  This may help in places where
     * you are able to load JAXP 1.1 classes yourself.
     *
     * @param parser the <code>SAXParser</code> to be used to parse the xml
     */
    public XMLBeanInfoDigester(SAXParser parser) {
        super(parser);
    }

    /**
     * Construct a new XMLBeanInfoDigester, allowing an XMLReader to be passed in.  This
     * allows XMLBeanInfoDigester to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Note that if you use this option you
     * have to configure namespace and validation support yourself, as these
     * properties only affect the SAXParser and emtpy constructor.
     *
     * @param reader the <code>XMLReader</code> to be used to parse the xml
     */
    public XMLBeanInfoDigester(XMLReader reader) {
        super(reader);
    }
    
    /**
     * Gets the class of the bean whose .betwixt file is being processed 
     *
     * @return the beans class for this XML descriptor 
     */
    public Class getBeanClass() {
        return beanClass;
    }
    
    /** 
     * Sets the beans class for this XML descriptor 
     *
     * @param beanClass the <code>Class</code> of the bean being processed
     */
    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }
    
    
    /** 
     * Gets the property names already processed
     *
     * @return the set of property names that have been processed so far 
     */
    public Set getProcessedPropertyNameSet() {
        return processedPropertyNameSet;
    }
    
    /** 
     * Should attributes (or elements) be used for primitive types?
     * @return true if primitive properties should be written as attributes in the xml
     */
    public boolean isAttributesForPrimitives() {
        return attributesForPrimitives;
    }

    /** 
     * Set whether attributes (or elements) should be used for primitive types. 
     * @param attributesForPrimitives pass true if primitive properties should be 
     * written as attributes
     */
    public void setAttributesForPrimitives(boolean attributesForPrimitives) {
        this.attributesForPrimitives = attributesForPrimitives;
        if ( introspector != null ) {
            introspector.setAttributesForPrimitives( attributesForPrimitives );
        }
    }

    /** 
     * Gets the XMLIntrospector that's using this digester.
     *
     * @return the introspector that is using me 
     */
    public XMLIntrospector getXMLIntrospector() {
        return introspector;
    }
    
    /** 
     * Sets the introspector that is using me 
     * @param introspector the <code>XMLIntrospector</code> that using this for .betwixt 
     * digestion
     */
    public void setXMLIntrospector(XMLIntrospector introspector) {
        this.introspector = introspector;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------        
    /** Reset configure for new digestion */
    protected void configure() {
        if (! configured) {
            configured = true;
         
            // add the various rules
            
            addRule( "info", new InfoRule() );
            addRule( "*/element", new ElementRule() );
            addRule( "*/text", new TextRule() );
            addRule( "*/attribute", new AttributeRule() );
            addRule( "*/hide", new HideRule() );
            addRule( "*/addDefaults", new AddDefaultsRule() );
        }
        
        // now initialize
        attributesForPrimitives = true;
        processedPropertyNameSet.clear();
    }
    
}
