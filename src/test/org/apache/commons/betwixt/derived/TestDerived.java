/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/derived/TestDerived.java,v 1.4 2003/10/09 20:52:08 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/10/09 20:52:08 $
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
package org.apache.commons.betwixt.derived;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

import org.apache.commons.logging.impl.SimpleLog;

import org.apache.commons.digester.Rule;
import org.apache.commons.digester.ExtendedBaseRules;

import org.xml.sax.Attributes;


/** Test harness for the BeanReader
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.4 $
  */
public class TestDerived extends AbstractTestCase {
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestDerived.class);
    }
    
    public TestDerived(String testName) {
        super(testName);
    }
    
    public void testPersonList() throws Exception {

        BeanReader reader = new BeanReader();
//        reader.getXMLIntrospector().setLog(log);
              
//        SimpleLog log = new SimpleLog("[TestPersonList:BeanReader]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
        
//        reader.setLog(log);
        reader.registerBeanClass( PersonListBean.class );
        
        InputStream in =  
            new FileInputStream( getTestFile("src/test/org/apache/commons/betwixt/derived/person-list.xml") );
        try {
        
            checkBean((PersonListBean) reader.parse( in ));
            
        }
        finally {
            in.close();
        }   
    }
    
    protected void checkBean(PersonListBean bean) throws Exception {
        PersonBean owner = bean.getOwner();
        assertTrue("should have found an owner", owner != null );
        
        assertEquals("should be derived class", "org.apache.commons.betwixt.derived.EmployeeBean", owner.getClass().getName());
        
        
        assertEquals("PersonList size", 4, bean.getPersonList().size());
        assertEquals("PersonList value (1)", "Athos", ((PersonBean) bean.getPersonList().get(0)).getName());
        assertEquals("PersonList value (2)", "Porthos", ((PersonBean) bean.getPersonList().get(1)).getName());
        assertEquals("PersonList value (3)", "Aramis", ((PersonBean) bean.getPersonList().get(2)).getName());
        assertEquals("PersonList value (4)", "D'Artagnan", ((PersonBean) bean.getPersonList().get(3)).getName());
        
        PersonBean employee = (PersonBean) bean.getPersonList().get(1);
        assertEquals("should be derived class", "org.apache.commons.betwixt.derived.EmployeeBean", employee.getClass().getName());
        
        PersonBean manager = (PersonBean) bean.getPersonList().get(2);
        assertEquals("should be derived class", "org.apache.commons.betwixt.derived.ManagerBean", manager.getClass().getName());

        // derived properties are not implemented yet...        
/*
        ManagerBean manager2 = (ManagerBean) manager;
        assertEquals("should have a derived property", 12, manager2.getCheeseSize());
*/        
    }
    
}

