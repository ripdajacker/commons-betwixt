/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/DeltaBean.java,v 1.3 2003/10/09 20:52:07 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 20:52:07 $
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

/** <p>A simple bean that demonstrates conversions of primitives and objects.</p>
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.3 $
  */
public class DeltaBean {

    private java.sql.Date sqlDate;
    private java.sql.Time sqlTime;
    private java.sql.Timestamp sqlTimestamp;
    private java.util.Date utilDate;
    private String name;
    private Float objFloat;
    private float primitiveFloat;
    
    public DeltaBean() {
    }
    
    public DeltaBean(
                    java.sql.Date sqlDate, 
                    java.sql.Time sqlTime, 
                    java.sql.Timestamp sqlTimestamp, 
                    java.util.Date utilDate,
                    String name,
                    Float objFloat,
                    float primitiveFloat) {
        setSqlDate(sqlDate);
        setSqlTime(sqlTime);
        setSqlTimestamp(sqlTimestamp);
        setUtilDate(utilDate);
        setName(name);
        setObjFloat(objFloat);
        setPrimitiveFloat(primitiveFloat);
    }
    
    public java.sql.Date getSqlDate() {
        return sqlDate;
    }
    
    public void setSqlDate(java.sql.Date sqlDate) {
        this.sqlDate = sqlDate;
    }
    
    public java.sql.Time getSqlTime() {
        return sqlTime;
    }
    
    public void setSqlTime(java.sql.Time sqlTime) {
        this.sqlTime = sqlTime;
    }
    
    public java.sql.Timestamp getSqlTimestamp() {
        return sqlTimestamp;
    }
    
    public void setSqlTimestamp(java.sql.Timestamp sqlTimestamp) {
        this.sqlTimestamp = sqlTimestamp;
    }
    
    public java.util.Date getUtilDate() {
        return utilDate;
    }
    
    public void setUtilDate(java.util.Date utilDate) {
        this.utilDate = utilDate;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Float getObjFloat() {
        return objFloat;
    }	
    
    public void setObjFloat(Float objFloat) {
        this.objFloat = objFloat;
    }
    
    public float getPrimitiveFloat() {
        return primitiveFloat;
    }
    
    public void setPrimitiveFloat(float primitiveFloat) {
        this.primitiveFloat = primitiveFloat;
    }
}
