/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/XMLBeanInfo.java,v 1.8 2003/10/09 20:52:03 rdonkin Exp $
 * $Revision: 1.8 $
 * $Date: 2003/10/09 20:52:03 $
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
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
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
package org.apache.commons.betwixt;

/** <p><code>XMLBeanInfo</code> represents the XML metadata information
  * used to map a Java Bean cleanly to XML. This provides a default
  * introspection mechansim, rather like {@link java.beans.BeanInfo} 
  * which can be customized through some mechansim, either via Java code 
  * or XSLT for example.</p>
  *
  * <h4><code>ID</code> and <code>IDREF</code> Attribute Names</h4>
  * <p>These special attributes are defined in the xml specification.
  * They are used by Betwixt to write bean graphs with cyclic references.
  * In most cases, these will take the values 'id' and 'idref' respectively 
  * but these names can be varied in the DTD.
  * Therefore, these names are specified by this class but default to the
  * usual values.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.8 $
  */
public class XMLBeanInfo {
    /** Descriptor for main element */
    private ElementDescriptor elementDescriptor;
    
    /** the beans class that this XML info refers to */
    private Class beanClass;
    /** <code>ID</code> attribute name */
    private String idAttributeName = "id";
    /** <code>IDREF</code> attribute name */
    private String idrefAttributeName = "idref";
    /** Have we already cached the <code>idAttributeDescriptor</code>? */
    private boolean cachedIDAttribute = false;
    /** Cached <code>ID</code> attribute descriptor */
    private AttributeDescriptor idAttributeDescriptor;
    
    /** 
     * Base constructor 
     * @param beanClass for this Class
     */
    public XMLBeanInfo( Class beanClass ) {
        this.beanClass = beanClass;        
    }

    /** 
     * Gets descriptor for bean represention 
     *
     * @return ElementDescriptor describing root element
     */
    public ElementDescriptor getElementDescriptor() {
        return elementDescriptor;
    }

    /** 
     * Sets descriptor for bean represention 
     *
     * @param elementDescriptor descriptor for bean
     */
    public void setElementDescriptor(ElementDescriptor elementDescriptor) {
        this.elementDescriptor = elementDescriptor;
    }    
    
    /**  
     * Gets the beans class that this XML info refers to
     *
     * @return the beans class that this XML info refers to 
     */
    public Class getBeanClass() {
        return beanClass;
    }
    
    /** 
     * Sets the beans class that this XML info refers to 
     *
     * @param beanClass the class that this refers to
     */
    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }
    
    /** 
     * Search attributes for one matching <code>ID</code> attribute name 
     *
     * @return the xml ID attribute
     */
    public AttributeDescriptor getIDAttribute() {
        //
        // XXX for some reason caching isn't working at the moment
        // it could be that this method is called too early
        // and not reset when attributes change
        // on the other hand, the speed gain is likely to be too
        // small to bother about
        //
        //if ( cachedIDAttribute = false ) {
            idAttributeDescriptor = findIDAttribute();
          //  cachedIDAttribute = true;
        //}
        return idAttributeDescriptor;
    }
    
    /** 
     * ID attribute search implementation 
     * @return the AttributeDescriptor for the <code>ID</code> attribute
     */
    private AttributeDescriptor findIDAttribute() {
        // we'll check to see if the bean already has an id
        if ( getElementDescriptor().hasAttributes() ) {
            AttributeDescriptor[] attributes = getElementDescriptor().getAttributeDescriptors();
            if ( attributes != null ) {
                for ( int i = 0, size = attributes.length; i < size; i++ ) {
                    // support a match either on local or qualified name
                    if ( getIDAttributeName().equals( attributes[i].getQualifiedName() ) 
                        || getIDAttributeName().equals( attributes[i].getLocalName() )) {
                        // we've got a match so use this attribute
                        return attributes[i];
                        
                    }
                }
            }
        }
        return null;
    }
    
    /** 
      * <p>Get name of <code>ID</code> attribute.
      * This is used to write (for example) automatic <code>ID</code>
      * attribute values.</p>
      * 
      * <p>The default name is 'id'.</p>
      *
      * @return name for the special <code>ID</code> attribute
      */
    public String getIDAttributeName() {
        return idAttributeName;
    }
    
    /** 
      * Set name of <code>ID</code> attribute 
      * This is used to write (for example) automatic <code>ID</code>
      * attribute values.</p>
      * 
      * <p>The default name is 'id'.</p>
      *
      * @param idAttributeName the attribute name for the special <code>ID</code> attribute
      */
    public void setIDAttributeName(String idAttributeName) {
        this.idAttributeName = idAttributeName;
    }
    
    /** 
      * <p>Get <code>IDREF</code> attribute name 
      * This is used (for example) to deal with cyclic references.
      *
      * <p>The default name is 'idref'.</p>
      *
      * @return name for the special <code>IDREF</code> attribute
      */
    public String getIDREFAttributeName() {
        return idrefAttributeName;
    }
    
    /** 
      * Set <code>IDREF</code> attribute name 
      * This is used (for example) to deal with cyclic references.
      *
      * <p>The default name is 'idref'.</p>
      *
      * @param idrefAttributeName the attribute name for the special <code>IDREF</code> attribute
      */
    public void setIDREFAttributeName(String idrefAttributeName) {
        this.idrefAttributeName = idrefAttributeName;
    }
    
    /**
     * Gets log-friendly string representation.
     *
     * @return something useful for logging
     */
    public String toString() {
        return 
                "XMLBeanInfo [class=" + getBeanClass() 
                + ", descriptor=" + getElementDescriptor() + "]";
    }
}
