/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/ElementDescriptor.java,v 1.5 2003/01/06 22:50:44 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/01/06 22:50:44 $
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
 * $Id: ElementDescriptor.java,v 1.5 2003/01/06 22:50:44 rdonkin Exp $
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
  * @version $Revision: 1.5 $
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
     * The List used on construction. It will be GC'd
     * after initilization and the array is lazily constructed
     */
    private List attributeList;
    
    /** 
     * The List used on construction. It will be GC'd
     * after initilization and the array is lazily constructed
     */
    private List elementList;
        
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
    
    /** Base constructor */
    public ElementDescriptor() {
    }

    public ElementDescriptor(boolean primitiveType) {
        this.primitiveType = primitiveType;
    }

    /** Creates a <code>ElementDescriptor</code> with no namespace URI or prefix */
    public ElementDescriptor(String localName) {
        super( localName );
    }

    public String toString() {
        return 
            "ElementDescriptor[qname=" + getQualifiedName() + ",pname=" + getPropertyName() 
            + ",class=" + getPropertyType() + ",singular=" + getSingularPropertyType()
            + ",updater=" + getUpdater() + "]";
    }
    
    /** Creates a <code>ElementDescriptor</code> with namespace URI and qualified name */
    public ElementDescriptor(String localName, String qualifiedName, String uri) {
        super(localName, qualifiedName, uri);
    }

    /** Returns true if this element has child elements */
    public boolean hasChildren() {
        return elementDescriptors != null && elementDescriptors.length > 0;
    }
    
    /** Returns true if this element has attributes */
    public boolean hasAttributes() {
        return attributeDescriptors != null && attributeDescriptors.length > 0;
    }
    
    /** Specifies if this is a collection element
     * Normally only used with the WrapCollectionsInElement setting
     * @param isCollection
     */
    public void setWrapCollectionsInElement(boolean wrapCollectionsInElement) {
        this.wrapCollectionsInElement = wrapCollectionsInElement;
    }

    /**
     * Returns if this element is a collection element
     */
    public boolean isWrapCollectionsInElement() {
        return this.wrapCollectionsInElement;
    }

    public void addAttributeDescriptor(AttributeDescriptor descriptor) {
        if ( attributeList == null ) {
            attributeList = new ArrayList();
        }
        getAttributeList().add( descriptor );
        attributeDescriptors = null;
    }
    
    
    /** Returns the attribute descriptors for this element */
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
    
    /** Set <code>AttributesDescriptors</code> for this element */
    public void setAttributeDescriptors(AttributeDescriptor[] attributeDescriptors) {
        this.attributeDescriptors = attributeDescriptors;
        this.attributeList = null;
    }
    
    public void addElementDescriptor(ElementDescriptor descriptor) {
        if ( elementList == null ) {
            elementList = new ArrayList();
        }
        getElementList().add( descriptor );
        elementDescriptors = null;
    }
    
    /** Returns the child element descriptors for this element */
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

    /** Set descriptors for child element of this element */
    public void setElementDescriptors(ElementDescriptor[] elementDescriptors) {
        this.elementDescriptors = elementDescriptors;
        this.elementList = null;
    }
    
    /** Returns the expression used to evaluate the new context of this element */
    public Expression getContextExpression() {
        return contextExpression;
    }
    
    /** Sets the expression used to evaluate the new context of this element */
    public void setContextExpression(Expression contextExpression) {
        this.contextExpression = contextExpression;
    }
    
    /** @return whether this element refers to a primitive type (or property of a parent object) */
    public boolean isPrimitiveType() {
        return primitiveType;
    }
    
    /** Sets whether this element refers to a primitive type (or property of a parent object) */
    public void setPrimitiveType(boolean primitiveType) {
        this.primitiveType = primitiveType;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------    
        
    /** 
     * Lazily creates the mutable List, nullifiying the array so that
     * as items are added to the list the Array is ignored until it is
     * explicitly asked for
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
    
    /** Lazily creates the mutable List */
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
    
}
