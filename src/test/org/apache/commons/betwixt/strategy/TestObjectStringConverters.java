/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/strategy/TestObjectStringConverters.java,v 1.1 2003/07/31 21:39:31 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/07/31 21:39:31 $
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
 * $Id: TestObjectStringConverters.java,v 1.1 2003/07/31 21:39:31 rdonkin Exp $
 */
package org.apache.commons.betwixt.strategy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConvertUtils;

import org.apache.commons.betwixt.expression.Context;

/**
 * Test harness for ObjectStringConverter implementations
 * 
 * @author <a href="mailto:rdonkin at apache.org">Robert Burrell Donkin</a>
 * @version $Id: TestObjectStringConverters.java,v 1.1 2003/07/31 21:39:31 rdonkin Exp $
 */
public class TestObjectStringConverters extends TestCase
{

    private Context dummyContext = new Context();

    public static Test suite() {
        return new TestSuite(TestObjectStringConverters.class);
    }
    
    public TestObjectStringConverters(String testName) {
        super(testName);
    }
    
    public void testBaseConverter() {
        Object test = new Object () {
            public String toString() {
                return "funciporcini";
            }
        };
        
        ObjectStringConverter converter = new ObjectStringConverter();
        String stringFromObject = converter.objectToString( null, Object.class, "raspberry", dummyContext );
        assertEquals("Null should return empty string", "", stringFromObject);
        stringFromObject = converter.objectToString( test, Object.class, "raspberry", dummyContext );
        assertEquals("Object should return toString", "funciporcini", stringFromObject);       
        
        Object objectFromString = converter.stringToObject( "Mungo Jerry", Object.class, "strawberry", dummyContext );
        assertEquals("String should return itself", "Mungo Jerry", objectFromString);  
    }
    
    
    public void testConvertUtilsConverter() throws Exception {
        ObjectStringConverter converter = new ConvertUtilsObjectStringConverter();
        commonTestForConvertUtilsConverters( converter );
    }
    
    private void commonTestForConvertUtilsConverters(ObjectStringConverter objectStringConverter) {
        Converter converter = new Converter() {
            public Object convert(Class type, Object value) {
                if ( type == SecurityManager.class) {
                    return "Life, The Universe And Everything";
                }
                return "The answer is " + value.toString();
            }
        };
        
        Long test = new Long(42);
        
        ConvertUtils.register( converter, Object.class );
        ConvertUtils.register( converter, String.class );
        ConvertUtils.register( converter, SecurityManager.class );
        
        String stringFromObject = objectStringConverter.objectToString( null, Object.class, "gooseberry", dummyContext );
        assertEquals("Null should return empty string", "", stringFromObject);
        stringFromObject = objectStringConverter.objectToString( test, Object.class, "logonberry", dummyContext );
        assertEquals("Normal object conversion (1)", "The answer is 42", stringFromObject); 

        
        Object objectFromString = objectStringConverter.stringToObject( 
                        "Forty Two", Object.class, "damsen", dummyContext );
        assertEquals("Normal object conversion (2)", "The answer is Forty Two", objectFromString); 
        objectFromString = objectStringConverter.stringToObject( 
                        "Trillian", SecurityManager.class, "cranberry", dummyContext );
        assertEquals("Special object conversion", "Life, The Universe And Everything", objectFromString); 
        
        ConvertUtils.deregister();
    }
    
    public void testDefaultOSConverter() {
        ObjectStringConverter converter = new DefaultObjectStringConverter();
        commonTestForConvertUtilsConverters( converter );
    }
    
    public void testDefaultOSConverterDates() {
        
    
        Converter converter = new Converter() {
            public Object convert(Class type, Object value) {
                return "Arthur Dent";
            }
        };
        
        ConvertUtils.register( converter, java.sql.Date.class );
        
        converter = new Converter() {
            public Object convert(Class type, Object value) {
                return "Ford Prefect";
            }
        };
        
        ConvertUtils.register( converter, String.class );
        
        converter = new Converter() {
            public Object convert(Class type, Object value) {
                return "Marvin";
            }
        };
        
        ConvertUtils.register( converter, java.util.Date.class );
    
        java.util.Date utilNow = new java.util.Date();
        String nowAsString = utilNow.toString();
        java.sql.Date sqlNow = new java.sql.Date(System.currentTimeMillis());
        ObjectStringConverter objectStringConverter = new DefaultObjectStringConverter();
        
        String stringFromObject = objectStringConverter.objectToString( 
                                        utilNow, java.util.Date.class, "blackcurrent", dummyContext );
        assertEquals( "String output same as java.util.Date.toString() (1)", utilNow.toString(), stringFromObject );

        stringFromObject = objectStringConverter.objectToString( 
                                        sqlNow, java.util.Date.class, "redcurrent", dummyContext );      
        assertEquals( "String output same as java.util.Date.toString() (2)", utilNow.toString(), stringFromObject );
        
        stringFromObject = objectStringConverter.objectToString( 
                                        utilNow, java.sql.Date.class, "whitecurrent", dummyContext );      
        assertEquals( "Should use converter (2)", "Ford Prefect", stringFromObject ); 
        
        Object objectFromString = objectStringConverter.stringToObject( 
                                        nowAsString, java.sql.Date.class, "blackberry", dummyContext );      
        assertEquals( "Should use converter (3)", "Ford Prefect", stringFromObject ); 
        objectFromString = objectStringConverter.stringToObject( 
                                        nowAsString, java.util.Date.class, "tayberry", dummyContext );      
        assertTrue( "Date should be returned", objectFromString instanceof java.util.Date); 
        assertEquals( "Date returned should be the same", nowAsString,  objectFromString.toString()); 
        
        ConvertUtils.deregister();
    }
}


