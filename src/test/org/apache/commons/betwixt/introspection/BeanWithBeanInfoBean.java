/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/introspection/BeanWithBeanInfoBean.java,v 1.3 2003/10/05 13:54:31 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/05 13:54:31 $
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
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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
 
package org.apache.commons.betwixt.introspection;


/** <p>An example of a bean that has a BeanInfo for use with introspection.</p>
  *
  * <p>
  * Three different pseudo-properties:
  * <ul>
  * <li><strong>Alpha</strong> is a standard property.
  * <li><strong>Beta</strong> follows standard naming conventions but should be ignored. 
  * <li><strong>Gamma</strong> doesn't follow standard naming conventions
  * </ul>
  * </p>
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.3 $
  */
public class BeanWithBeanInfoBean {
    
    private String alpha;
    private String beta;
    private String gamma;
    
    public BeanWithBeanInfoBean() {}
    
    public BeanWithBeanInfoBean(String alpha, String beta, String gamma) {
        setAlpha(alpha);
        setBeta(beta);
        gammaSetter(gamma);
    }
    
    public String getAlpha() {
        return alpha;
    }
    
    public void setAlpha(String alpha) {
        this.alpha = alpha;
    } 
    
    public String getBeta() {
        return beta;
    }	
    
    public void setBeta(String beta) {
        this.beta = beta;
    }
    
    public String gammaGetter() {
        return gamma;
    }
    
    public void gammaSetter(String gamma) {
        this.gamma = gamma;
    }	
}

