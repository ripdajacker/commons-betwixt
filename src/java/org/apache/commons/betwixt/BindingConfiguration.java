/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/BindingConfiguration.java,v 1.5 2003/10/09 20:52:03 rdonkin Exp $
 * $Revision: 1.5 $
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

import java.io.Serializable;

import org.apache.commons.betwixt.strategy.ObjectStringConverter;
import org.apache.commons.betwixt.strategy.DefaultObjectStringConverter;

/** <p>Stores mapping phase binding configuration.</p>
  *
  * <p>There are two phase in Betwixt's processing.
  * The first phase is the introspection of the bean.
  * Strutural configuration settings effect this phase.
  * The second phase comes when Betwixt dynamically uses
  * reflection to execute the mapping.
  * This object stores configuration settings pertaining 
  * to the second phase.</p>
  *
  * <p>These common settings have been collected into one class
  * to make round tripping easier since the same <code>BindingConfiguration</code>
  * can be shared.</p> 
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @version $Revision: 1.5 $
  */
public class BindingConfiguration implements Serializable {

    /** Should <code>ID</code>'s and <code>IDREF</code> be used cross-reference matching objects? */
    private boolean mapIDs = true;
    /** Converts objects &lt-&gt; strings */
    private ObjectStringConverter objectStringConverter;
    /** The name of the classname attribute used when creating derived beans */
    private String classNameAttribute = "className";
    
    /**
     * Constructs a BindingConfiguration with default properties.
     */
    public BindingConfiguration() {
        this(new DefaultObjectStringConverter(), true);
    }
    
    /** 
     * Constructs a BindingConfiguration
     * @param objectStringConverter the <code>ObjectStringConverter</code>
     * to be used to convert Objects &lt;-&gt; Strings
     * @param mapIDs should <code>ID</code>'s and <code>IDREF</code> be used to cross-reference
     */ 
    public BindingConfiguration(ObjectStringConverter objectStringConverter, boolean mapIDs) {
        setObjectStringConverter(objectStringConverter);
        setMapIDs(mapIDs);
    }
    
    /**
      * Gets the Object &lt;-&gt; String converter.
      * @return the ObjectStringConverter to use, not null
      */
    public ObjectStringConverter getObjectStringConverter() {
        return objectStringConverter;
    }
    
    /**
      * Sets the Object &lt;-&gt; String converter.
      * @param objectStringConverter the ObjectStringConverter to be used, not null
      */
    public void setObjectStringConverter(ObjectStringConverter objectStringConverter) {
        this.objectStringConverter = objectStringConverter;
    }
    
    /** 
     * Should <code>ID</code>'s and <code>IDREF</code> attributes 
     * be used to cross-reference matching objects? 
     *
     * @return true if <code>ID</code> and <code>IDREF</code> 
     * attributes should be used to cross-reference instances
     */
    public boolean getMapIDs() {
        return mapIDs;
    }
    
    /**
     *Should <code>ID</code>'s and <code>IDREF</code> attributes 
     * be used to cross-reference matching objects? 
     *
     * @param mapIDs pass true if <code>ID</code>'s should be used to cross-reference
     */
    public void setMapIDs(boolean mapIDs) {
        this.mapIDs = mapIDs;
    }        
    
    /**
     * The name of the attribute which can be specified in the XML to override the
     * type of a bean used at a certain point in the schema.
     *
     * <p>The default value is 'className'.</p>
     * 
     * @return The name of the attribute used to overload the class name of a bean
     */
    public String getClassNameAttribute() {
        return classNameAttribute;
    }

    /**
     * Sets the name of the attribute which can be specified in 
     * the XML to override the type of a bean used at a certain 
     * point in the schema.
     *
     * <p>The default value is 'className'.</p>
     * 
     * @param classNameAttribute The name of the attribute used to overload the class name of a bean
     */
    public void setClassNameAttribute(String classNameAttribute) {
        this.classNameAttribute = classNameAttribute;
    }
}
