/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/ElementDescriptor.java,v 1.8 2003/03/19 22:59:01 rdonkin Exp $
 * $Revision: 1.8 $
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
 * $Id: ElementDescriptor.java,v 1.8 2003/03/19 22:59:01 rdonkin Exp $
 */
package org.apache.commons.betwixt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.betwixt.expression.Expression;

/** <p><code>ElementDescriptor</code> describes the XML elements
  * to be created for a bean instance.</p>
  *
  * <p> It contains <code>AttributeDescriptor</code>'s for all it's attributes
  * and <code>ElementDescriptor</code>'s for it's child elements.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
  * @version $Revision: 1.8 $
  */
public class ElementDescriptor extends NodeDescriptor {

    /** 
     * Descriptors for attributes this element contains.
     * Constructed lazily on demand from a List
     */
    private AttributeDescriptor[] attributeDescriptors;
    /** 
     * Descriptors for child elements.
     * Constructed lazily on demand from a List
     */
    private ElementDescriptor[] elementDescriptors;
    
    /** 
     * Descriptors for child content
     * Constructed lazily on demand from a List.
     */
    private Descriptor[] contentDescriptors;
    
    /** 
     * The List used on construction. It will be GC'd
     * after initilization and the array is lazily constructed
     */
    private List attributeList;
    
    /** 
     * The List used on construction. It will be GC'd
     * after initilization and the array is lazily constructed
     */
    private List elementList;
    
    /** 
     * The list used o construct array. It will be GC'd after
     * initialization when the array is lazily constructed.
     */
    private List contentList;
        
    /** the expression used to evaluate the new context of this node 
     * or null if the same context is to be used */
    private Expression contextExpression;

    /** Whether this element refers to a primitive type (or property of a parent object) */
    private boolean primitiveType;
    
    /** 
     * Whether this collection element can be used
     * as a collection element. Defaults to true
     */
    private boolean wrapCollectionsInElement = true;
    
    /**  
     * Constructs an <code>ElementDescriptor</code> that refers to a primitive type.
     */
    public ElementDescriptor() {
    }
    
    /**
     * Base constructor.
     * @param primitiveType if true, this element refers to a primitive type
     */
    public ElementDescriptor(boolean primitiveType) {
        this.primitiveType = primitiveType;
    }

    /** 
     * Creates a ElementDescriptor with no namespace URI or prefix.
     *
     * @param localName the (xml) local name of this node. 
     * This will be used to set both qualified and local name for this name.
     */
    public ElementDescriptor(String localName) {
        super( localName );
    }


    
    /** 
     * Creates a <code>ElementDescriptor</code> with namespace URI and qualified name
     * @param localName the (xml) local name of this  node
     * @param qualifiedName the (xml) qualified name of this node
     * @param uri the (xml) namespace prefix of this node
     */
    public ElementDescriptor(String localName, String qualifiedName, String uri) {
        super(localName, qualifiedName, uri);
    }

    /** 
     * Returns true if this element has child <code>ElementDescriptors</code>
     * @return true if this element has child elements 
     * @see #getElementDescriptors
     */
    public boolean hasChildren() {
        return elementDescriptors != null && elementDescriptors.length > 0;
    }
    
    /** 
     * Returns true if this element has <code>AttributeDescriptors</code>
     * @return true if this element has attributes
     * @see #getAttributeDescriptors
     */
    public boolean hasAttributes() {
        return attributeDescriptors != null && attributeDescriptors.length > 0;
    }
    
    /** 
     * Returns true if this element has child content.
     * @return true if this element has either child mixed content or child elements
     * @see #getContentDescriptors
     */
    public boolean hasContent() {
        return contentDescriptors != null && contentDescriptors.length > 0; 
     } 
    
    
    /** 
     * Sets whether <code>Collection</code> bean properties should wrap items in a parent element.
     * In other words, should the mapping for bean properties which are <code>Collection</code>s 
     * enclosed the item elements within a parent element.
     * Normally only used when this describes a collection bean property.
     *
     * @param wrapCollectionsInElement true if the elements for the items in the collection 
     * should be contained in a parent element
     */
    public void setWrapCollectionsInElement(boolean wrapCollectionsInElement) {
        this.wrapCollectionsInElement = wrapCollectionsInElement;
    }

    /**
     * Returns true if collective bean properties should wrap the items in a parent element.
     * In other words, should the mapping for bean properties which are <code>Collection</code>s 
     * enclosed the item elements within a parent element.
     * Normally only used when this describes a collection bean property.
     *
     * @return true if the elements for the items in the collection should be contained 
     * in a parent element
     */
    public boolean isWrapCollectionsInElement() {
        return this.wrapCollectionsInElement;
    }

    /**
     * Adds an attribute to the element this <code>ElementDescriptor</code> describes
     * @param descriptor the <code>AttributeDescriptor</code> that will be added to the 
     * attributes associated with element this <code>ElementDescriptor</code> describes
     */
    public void addAttributeDescriptor(AttributeDescriptor descriptor) {
        if ( attributeList == null ) {
            attributeList = new ArrayList();
        }
        getAttributeList().add( descriptor );
        attributeDescriptors = null;
    }
    
    
    /** 
     * Returns the attribute descriptors for this element 
     *
     * @return descriptors for the attributes of the element that this 
     * <code>ElementDescriptor</code> describes
     */
    public AttributeDescriptor[] getAttributeDescriptors() {
        if ( attributeDescriptors == null ) {
            if ( attributeList == null ) {
                attributeDescriptors = new AttributeDescriptor[0];
            } else {
                attributeDescriptors = new AttributeDescriptor[ attributeList.size() ];
                attributeList.toArray( attributeDescriptors );
                
                // allow GC of List when initialized
                attributeList = null;
            }
        }
        return attributeDescriptors;
    }
    
    /** 
     * Sets the <code>AttributesDescriptors</code> for this element.
     * This sets descriptors for the attributes of the element describe by the 
     * <code>ElementDescriptor</code>.
     *
     * @param attributeDescriptors the <code>AttributeDescriptor</code> describe the attributes
     * of the element described by this <code>ElementDescriptor</code>
     */
    public void setAttributeDescriptors(AttributeDescriptor[] attributeDescriptors) {
        this.attributeDescriptors = attributeDescriptors;
        this.attributeList = null;
    }
    
    /**
     * Adds a descriptor for a child element.
     * 
     * @param descriptor the <code>ElementDescriptor</code> describing the child element to add
     */
    public void addElementDescriptor(ElementDescriptor descriptor) {
        if ( elementList == null ) {
            elementList = new ArrayList();
        }
        getElementList().add( descriptor );
        elementDescriptors = null;
        addContentDescriptor( descriptor );
    }
    
    /** 
     * Returns descriptors for the child elements of the element this describes.
     * @return the <code>ElementDescriptor</code> describing the child elements
     * of the element that this <code>ElementDescriptor</code> describes
     */
    public ElementDescriptor[] getElementDescriptors() {
        if ( elementDescriptors == null ) {
            if ( elementList == null ) {
                elementDescriptors = new ElementDescriptor[0];
            } else {
                elementDescriptors = new ElementDescriptor[ elementList.size() ];
                elementList.toArray( elementDescriptors );
                
                // allow GC of List when initialized
                elementList = null;
            }
        }
        return elementDescriptors;
    }

    /** 
     * Sets the descriptors for the child element of the element this describes. 
     * Also sets the child content descriptors for this element
     *
     * @param elementDescriptors the <code>ElementDescriptor</code>s of the element 
     * that this describes
     */
    public void setElementDescriptors(ElementDescriptor[] elementDescriptors) {
        this.elementDescriptors = elementDescriptors;
        this.elementList = null;
        setContentDescriptors( elementDescriptors );
    }
    
    /**
     * Adds a descriptor for child content.
     * 
     * @param descriptor the <code>Descriptor</code> describing the child content to add
     */
    public void addContentDescriptor(Descriptor descriptor) {
        if ( contentList == null ) {
            contentList = new ArrayList();
        }
        getContentList().add( descriptor );
        contentDescriptors = null;
    }
    
    /** 
     * Returns descriptors for the child content of the element this describes.
     * @return the <code>Descriptor</code> describing the child elements
     * of the element that this <code>ElementDescriptor</code> describes
     */
    public Descriptor[] getContentDescriptors() {
        if ( contentDescriptors == null ) {
            if ( contentList == null ) {
                contentDescriptors = new Descriptor[0];
            } else {
                contentDescriptors = new Descriptor[ contentList.size() ];
                contentList.toArray( contentDescriptors );
                
                // allow GC of List when initialized
                contentList = null;
            }
        }
        return contentDescriptors;
    }

    /** 
     * Sets the descriptors for the child content of the element this describes. 
     * @param contentDescriptors the <code>Descriptor</code>s of the element 
     * that this describes
     */
    public void setContentDescriptors(Descriptor[] contentDescriptors) {
        this.contentDescriptors = contentDescriptors;
        this.contentList = null;
    }
    
    /** 
     * Returns the expression used to evaluate the new context of this element.
     * @return the expression used to evaluate the new context of this element
     */
    public Expression getContextExpression() {
        return contextExpression;
    }
    
    /** 
     * Sets the expression used to evaluate the new context of this element 
     * @param contextExpression the expression used to evaluate the new context of this element 
     */
    public void setContextExpression(Expression contextExpression) {
        this.contextExpression = contextExpression;
    }
    
    /** 
     * Returns true if this element refers to a primitive type property
     * @return whether this element refers to a primitive type (or property of a parent object) 
     */
    public boolean isPrimitiveType() {
        return primitiveType;
    }
    
    /** 
     * Sets whether this element refers to a primitive type (or property of a parent object) 
     * @param primitiveType true if this element refers to a primitive type
     */
    public void setPrimitiveType(boolean primitiveType) {
        this.primitiveType = primitiveType;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------    
        
    /** 
     * Lazily creates the mutable List.
     * This nullifies the attributeDescriptors array so that
     * as items are added to the list the Array is ignored until it is
     * explicitly asked for.
     * 
     * @return list of <code>AttributeDescriptors</code>'s describing the attributes
     * of the element that this <code>ElementDescriptor</code> describes
     */
    protected List getAttributeList() {
        if ( attributeList == null ) {
            if ( attributeDescriptors != null ) {
                int size = attributeDescriptors.length;
                attributeList = new ArrayList( size );
                for ( int i = 0; i < size; i++ ) {
                    attributeList.add( attributeDescriptors[i] );
                }
                // force lazy recreation later
                attributeDescriptors = null;
            } else {
                attributeList = new ArrayList();
            }            
        }
        return attributeList;
    }
    
    /**  
     * Lazily creates the mutable List of child elements.
     * This nullifies the elementDescriptors array so that
     * as items are added to the list the Array is ignored until it is
     * explicitly asked for.
     *
     * @return list of <code>ElementDescriptor</code>'s describe the child elements of 
     * the element that this <code>ElementDescriptor</code> describes
     */
    protected List getElementList() {
        if ( elementList == null ) {
            if ( elementDescriptors != null ) {
                int size = elementDescriptors.length;
                elementList = new ArrayList( size );
                for ( int i = 0; i < size; i++ ) {
                    elementList.add( elementDescriptors[i] );
                }
                // force lazy recreation later
                elementDescriptors = null;
            } else {
                elementList = new ArrayList();
            }            
        }
        return elementList;
    }
    
    /**  
     * Lazily creates the mutable List of child content descriptors.
     * This nullifies the contentDescriptors array so that
     * as items are added to the list the Array is ignored until it is
     * explicitly asked for.
     *
     * @return list of <code>Descriptor</code>'s describe the child content of 
     * the element that this <code>Descriptor</code> describes
     */
    protected List getContentList() {
        if ( contentList == null ) {
            if ( contentDescriptors != null ) {
                int size = contentDescriptors.length;
                contentList = new ArrayList( size );
                for ( int i = 0; i < size; i++ ) {
                    contentList.add( contentDescriptors[i] );
                }
                // force lazy recreation later
                contentDescriptors = null;
            } else {
                contentList = new ArrayList();
            }            
        }
        return contentList;
    }
    
    /**
     * Returns something useful for logging.
     *
     * @return a string useful for logging
     */ 
    public String toString() {
        return 
            "ElementDescriptor[qname=" + getQualifiedName() + ",pname=" + getPropertyName() 
            + ",class=" + getPropertyType() + ",singular=" + getSingularPropertyType()
            + ",updater=" + getUpdater() + "]";
    }    
}
