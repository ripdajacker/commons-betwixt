/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/NodeDescriptor.java,v 1.5 2003/01/13 18:07:52 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/01/13 18:07:52 $
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
 * $Id: NodeDescriptor.java,v 1.5 2003/01/13 18:07:52 rdonkin Exp $
 */
package org.apache.commons.betwixt;

import org.apache.commons.betwixt.expression.Expression;
import org.apache.commons.betwixt.expression.Updater;

/** <p> Common superclass for <code>ElementDescriptor</code> 
  * and <code>AttributeDescriptor</code>.</p>
  *
  * <p> Nodes can have just a local name
  * or they can have a local name, qualified name and a namespace uri.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.5 $
  */
public class NodeDescriptor {

    /** The local name of this node without any namespace prefix */
    private String localName;
    /** The qualified name of the xml node associated with this descriptor. */
    private String qualifiedName;
    /** The namespace URI of this node */
    private String uri = "";
    /** the expression used to evaluate the text value of this node */
    private Expression textExpression;
    /** the updater used to update the current bean from the text value of this node */
    private Updater updater;
    /** The property expression to which this node refers to, or null if it is just a constant */
    private String propertyName;
    /** the property type associated with this node, if any */
    private Class propertyType;
    /** the singular property type (i.e. the type ignoring the Collection or Array */
    private Class singularPropertyType;
    
    
    /** Base constructor */
    public NodeDescriptor() {
    }

    /** 
     * Creates a NodeDescriptor with no namespace URI or prefix.
     *
     * @param localName the (xml) local name of this node. 
     * This will be used to set both qualified and local name for this name.
     */
    public NodeDescriptor(String localName) {
        this.localName = localName;
        this.qualifiedName = localName;
    }


    /**  
     * Creates a NodeDescriptor with namespace URI and qualified name 
     * @param localName the (xml) local name of this  node
     * @param qualifiedName the (xml) qualified name of this node
     * @param uri the (xml) namespace prefix of this node
     */
    public NodeDescriptor(String localName, String qualifiedName, String uri) {
        this.localName = localName;
        this.qualifiedName = qualifiedName;
        this.uri = uri;
    }

    /** 
     * Gets the local name, excluding any namespace prefix 
     * @return the (xml) local name of this node
     */
    public String getLocalName() {
        return localName;
    }

    /** 
     * Sets the local name 
     * @param localName the (xml) local name of this node
     */
    public void setLocalName(String localName) {
        this.localName = localName;
    }    
    
    /** 
     * Gets the qualified name, including any namespace prefix 
     * @return the (xml) qualified name of this node. This may be null.
     */
    public String getQualifiedName() {
        if ( qualifiedName == null ) {
            qualifiedName = localName;
        }
        return qualifiedName;
    }
    
    /** 
     * Sets the qualified name
     * @param qualifiedName the new (xml) qualified name for this node
     */
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }    
    
    /**  
     * Gets the (xml) namespace URI prefix for this node.
     * @return the namespace URI that this node belongs to 
     * or "" if there is no namespace defined 
     */
    public String getURI() {
        return uri;
    }
    

    /** 
     * Sets the namespace URI that this node belongs to.
     * @param uri the new namespace uri for this node
     */
    public void setURI(String uri) {
        if ( uri == null ) {
            throw new IllegalArgumentException( 
                "The namespace URI cannot be null. " 
                + "No namespace URI is specified with the empty string" 
            );
        }
        this.uri = uri;
    }
    
    /** 
     * Gets the expression used to evaluate the text value of this node 
     * for a particular <code>Context</code>.
     * @return the expression used to evaluate the text value of this node 
     */
    public Expression getTextExpression() {
        return textExpression;
    }
    
    /** 
     * Sets the expression used to evaluate the text value of this node
     * for a particular <code>Context</code>
     * @param textExpression the Expression to be used to evaluate the value of this node
     */
    public void setTextExpression(Expression textExpression) {
        this.textExpression = textExpression;
    }
    
    /** 
     * Gets the <code>Updater</code> used to update a <code>Context</code> from the text value
     * corresponding to this node in an xml document
     * @return the Update that should be used to update the value of this node
     */
    public Updater getUpdater() {
        return updater;
    }
    
    /**
     * Sets the <code>Updater</code> used to update a <code>Context</code> from the text value
     * corresponding to this node in an xml document
     * @param updater the Updater to be used to update the values of this node
     */
    public void setUpdater(Updater updater) {
        this.updater = updater;
    }
    
    /** 
     * Gets the type of the bean property associated with this node, if any
     * @return the property type associated with this node, if any 
     */
    public Class getPropertyType() {
        return propertyType;
    }
    
    /** 
     * Sets the type of the bean property associated with this node, if any 
     * @param propertyType the Class of the bean property
     */
    public void setPropertyType(Class propertyType) {
        this.propertyType = propertyType;
    }

    
    /** 
     * Gets the name of the bean property to which this node refers
     * @return the name of the bean property to which this node refers to, 
     * or null if it is just a constant 
     */
    public String getPropertyName() {
        return propertyName;
    }
    
    /** 
     * Sets the name of the bean property to which this node refers
     * @param propertyName the name of the bean property. 
     * Or null, if this node is not mapped to to a bean property
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    /** 
     * Gets the underlying type ignoring any wrapping a Collection or Array.
     *
     * @return if this property is a 1-N relationship then this returns the type
     * of a single property value.
     */
    public Class getSingularPropertyType() {
        if ( singularPropertyType == null ) {
            return getPropertyType();
        }
        return singularPropertyType;
    }
    
    /** 
     * Sets the underlying type ignoring any wrapping Collection or Array.
     *
     * @param singularPropertyType the Class of the items in the Collection or Array. 
     * If node is associated with a collective bean property, then this should not be null.
     */
    public void setSingularPropertyType(Class singularPropertyType) {
        this.singularPropertyType = singularPropertyType;
    }

}
