/*
 * $Header: /home/cvs/jakarta-commons/beanutils/LICENSE.txt,v 1.3 2003/01/15 21:59:38 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/01/15 21:59:38 $
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

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.betwixt.xmlunit.XmlTestCase;

/** Abstract base class for test cases.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.6 $
  */
public abstract class AbstractTestCase extends XmlTestCase {
    
    /**
     * Basedir for all i/o
     */
    public String basedir = System.getProperty("basedir");
    
    public AbstractTestCase(String testName) {
        super(testName);
    }

    public String getTestFile(String path)
    {
        return new File(basedir,path).getAbsolutePath();
    }

    public String getTestFileURL(String path) throws MalformedURLException
    {
        return new File(basedir,path).toURL().toString();
    }
    
    protected Object createBean() {
        CustomerBean bean = new CustomerBean();
        bean.setID( "1" );
        bean.setName( "James" );
        bean.setEmails( new String[] { "jstrachan@apache.org", "james_strachan@yahoo.co.uk" } );
        bean.setNumbers( new int[] { 3, 4, 5 } );
        bean.setLocation(0, "Highbury Barn" );
        bean.setLocation(1, "Monument" );
        bean.setLocation(2, "Leeds" );
        
        Map projects = new HashMap();
        projects.put( "dom4j", "http://dom4j.org" );
        projects.put( "jaxen", "http://jaxen.org" );
        projects.put( "jakarta-commons", "http://jakarta.apache.org/commons/" );
        projects.put( "jakarta-taglibs", "http://jakarta.apache.org/taglibs/" );
        bean.setProjectMap( projects );
        
        AddressBean address = new AddressBean();
        address.setStreet( "Near the park" );
        address.setCity( "London" );
        address.setCountry( "UK" );
        address.setCode( "N5" );
        
        bean.setAddress( address );
        
        bean.setDate((Date) ConvertUtils.convert("2002-03-17", Date.class));
        bean.setTime((Time) ConvertUtils.convert("20:30:40", Time.class));
        bean.setTimestamp((Timestamp) ConvertUtils.convert("2002-03-17 20:30:40.0", Timestamp.class));
        
        bean.setBigDecimal(new BigDecimal("1234567890.12345"));
        bean.setBigInteger(new BigInteger("1234567890"));
        
        return bean;
    }
}

