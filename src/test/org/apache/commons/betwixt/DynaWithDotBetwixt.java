/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/DynaWithDotBetwixt.java,v 1.1 2003/07/27 17:55:02 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/07/27 17:55:02 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * $Id: DynaWithDotBetwixt.java,v 1.1 2003/07/27 17:55:02 rdonkin Exp $
 */
package org.apache.commons.betwixt;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;


/** <p>Test bean which extends DynaBean but has a .betwixt file.</p>
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.1 $
  */
public class DynaWithDotBetwixt implements DynaBean {
    
    private String notDynaProperty;
    private String dynaProperty;
    
    public DynaWithDotBetwixt() {
        this("DEFAUL_NOT_DYNA", "DEFAULT_DYNA");
    }
     
    
    public DynaWithDotBetwixt(String notDynaProperty, String dynaProperty) {
        this.notDynaProperty = notDynaProperty;
        this.dynaProperty = dynaProperty;
    }
    
    public String getNotDynaProperty() {
        return notDynaProperty;
    }
    
    public String fiddleDyna() {
        return dynaProperty;
    }
    
    public boolean contains(String name, String key) {
        return false;
    }
    
    public Object get(String name) {
        return dynaProperty;
    }
    
    public Object get(String name, int index) {
        return dynaProperty;
    } 
    
    public Object get(String name, String key) {
        return dynaProperty;
    }
    
    public DynaClass getDynaClass() {
        return new DynaClass() {
            public DynaProperty[] getDynaProperties() {
                DynaProperty[] properties = {new DynaProperty("DynaProp", String.class)};
                return properties;
            }
            
            public String getName() {
                return "DynaWithDotBetwixtClass";
            }
            
            public DynaBean newInstance() {
                return new DynaWithDotBetwixt();
            }
            
            public DynaProperty getDynaProperty(String name) {
                if ("DynaProp".equals(name)) {
                    return new DynaProperty("DynaProp", String.class);
                }
                return null;
            }	 
        };
    }
    
    public void remove(String name, String key) {}
    
    public void set(String name, Object value) {}
    
    public void set(String name, int index, Object value) {}
    
    public void set(String name, String key, Object value) {}

}  

