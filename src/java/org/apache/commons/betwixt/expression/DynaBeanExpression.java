/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/expression/DynaBeanExpression.java,v 1.4 2003/10/09 20:52:04 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/10/09 20:52:04 $
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

package org.apache.commons.betwixt.expression;

import org.apache.commons.beanutils.DynaBean;

/**
 * An Expression that gets a property value from a DynaBean.
 * 
 * @see org.apache.commons.beanutils.DynaBean
 * 
 * @author Michael Becke
 */
public class DynaBeanExpression implements Expression {

    /** The name of the DynaBean property to get */
    private String propertyName;

    /**
     * Crates a new DynaBeanExpression.
     */
    public DynaBeanExpression() {
        super();
    }

    /**
     * Crates a new DynaBeanExpression.
     * 
     * @param propertyName the name of the DynaBean property to use
     */
    public DynaBeanExpression(String propertyName) {
        super();
        setPropertyName(propertyName);
    }

    /**
     * Returns the value of a DynaBean property from the bean stored in 
     * the Context.  Returns <code>null</code> if no DynaBean is stored 
     * in the Context or if the propertyName has not been set.
     * 
     * @param context the content containing the DynaBean
     * 
     * @return the DynaBean property value or <code>null</code>
     */
    public Object evaluate(Context context) {
        
        if (context.getBean() instanceof DynaBean && propertyName != null) {
            return ((DynaBean)context.getBean()).get(propertyName);
        } else {
            return null;
        }
    }

    /**
     * Do nothing.
     * @see Expression#update
     */
    public void update(Context context, String newValue) {
        // do nothing
    }

    /**
     * Gets the name of the property to get from the DynaBean.
     * @return the name of the property that this expression reads
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the name of the property to get from the DynaBean.
     * @param propertyName the property that this expression reads, not null
     */
    public void setPropertyName(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName is null");
        }
        this.propertyName = propertyName;
    }

}
