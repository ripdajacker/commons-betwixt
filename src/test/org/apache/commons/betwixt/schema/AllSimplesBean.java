/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/schema/AllSimplesBean.java,v 1.1.2.1 2004/02/23 21:55:35 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/02/23 21:55:35 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
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
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior 
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

package org.apache.commons.betwixt.schema;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class AllSimplesBean {

    private String string;
    private BigInteger bigInteger;
    private int primitiveInt;
    private Integer objectInt;
    private long primitiveLong;
    private Long objectLong;
    private short primitiveShort;
    private Short objectShort;
    private BigDecimal bigDecimal;
    private float primitiveFloat;
    private Float objectFloat;
    private double primitiveDouble;
    private Double objectDouble;
    private boolean primitiveBoolean;
    private Boolean objectBoolean;
    private byte primitiveByte;
    private Byte objectByte;
    private java.util.Date utilDate;
    private java.sql.Date sqlDate;
    private java.sql.Time sqlTime;
    
    
    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }

    public Boolean getObjectBoolean() {
        return objectBoolean;
    }

    public Byte getObjectByte() {
        return objectByte;
    }

    public Double getObjectDouble() {
        return objectDouble;
    }

    public Float getObjectFloat() {
        return objectFloat;
    }

    public Integer getObjectInt() {
        return objectInt;
    }

    public Long getObjectLong() {
        return objectLong;
    }

    public Short getObjectShort() {
        return objectShort;
    }

    public boolean isPrimitiveBoolean() {
        return primitiveBoolean;
    }

    public byte getPrimitiveByte() {
        return primitiveByte;
    }

    public double getPrimitiveDouble() {
        return primitiveDouble;
    }

    public float getPrimitiveFloat() {
        return primitiveFloat;
    }

    public int getPrimitiveInt() {
        return primitiveInt;
    }

    public long getPrimitiveLong() {
        return primitiveLong;
    }

    public short getPrimitiveShort() {
        return primitiveShort;
    }

    public java.sql.Date getSqlDate() {
        return sqlDate;
    }

    public java.sql.Time getSqlTime() {
        return sqlTime;
    }

    public String getString() {
        return string;
    }

    public java.util.Date getUtilDate() {
        return utilDate;
    }

    public void setBigDecimal(BigDecimal decimal) {
        bigDecimal = decimal;
    }

    public void setBigInteger(BigInteger integer) {
        bigInteger = integer;
    }

    public void setObjectBoolean(Boolean boolean1) {
        objectBoolean = boolean1;
    }

    public void setObjectByte(Byte byte1) {
        objectByte = byte1;
    }

    public void setObjectDouble(Double double1) {
        objectDouble = double1;
    }

    public void setObjectFloat(Float float1) {
        objectFloat = float1;
    }

    public void setObjectInt(Integer integer) {
        objectInt = integer;
    }

    public void setObjectLong(Long long1) {
        objectLong = long1;
    }

    public void setObjectShort(Short short1) {
        objectShort = short1;
    }
    
    public void setPrimitiveBoolean(boolean b) {
        primitiveBoolean = b;
    }

    public void setPrimitiveByte(byte b) {
        primitiveByte = b;
    }
    
    public void setPrimitiveDouble(double d) {
        primitiveDouble = d;
    }

    public void setPrimitiveFloat(float f) {
        primitiveFloat = f;
    }

    public void setPrimitiveInt(int i) {
        primitiveInt = i;
    }

    public void setPrimitiveLong(long l) {
        primitiveLong = l;
    }

    public void setPrimitiveShort(short s) {
        primitiveShort = s;
    }

    public void setSqlDate(java.sql.Date date) {
        sqlDate = date;
    }

    public void setSqlTime(java.sql.Time time) {
        sqlTime = time;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setUtilDate(java.util.Date date) {
        utilDate = date;
    }

}
