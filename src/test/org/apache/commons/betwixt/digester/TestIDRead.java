
/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/digester/TestIDRead.java,v 1.1 2002/08/29 19:28:50 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2002/08/29 19:28:50 $
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
 * $Id: TestIDRead.java,v 1.1 2002/08/29 19:28:50 rdonkin Exp $
 */
package org.apache.commons.betwixt.digester;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanCreateRule;

import org.apache.commons.betwixt.AbstractTestCase;

import org.apache.commons.logging.impl.SimpleLog;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** Test harness for ID-IDRef reading.
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.1 $
  */
public class TestIDRead extends AbstractTestCase {

    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite(TestIDRead.class);
    }

    public TestIDRead(String testName) {
        super(testName);
    }

    public void testSimpleRead() throws Exception {
        BeanWriter writer = new BeanWriter();
        IDBean bean = new IDBean("alpha","one");
        bean.addChild(new IDBean("beta","two"));
        bean.addChild(new IDBean("gamma","three"));
        writer.write(bean);
        
        BeanReader reader = new BeanReader();
        
//         logging just for this method
//        SimpleLog log = new SimpleLog("[XMLIntrospectorHelper]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        XMLIntrospectorHelper.setLog(log);
//        
//        log = new SimpleLog("[BeanCreateRule]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        BeanCreateRule.setLog(log);
//
//        log = new SimpleLog("[BeanReader]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);        
//        reader.setLog(log);
//
//        log = new SimpleLog("[XMLIntrospector]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        reader.getXMLIntrospector().setLog(log);
        
        reader.registerBeanClass( IDBean.class );
        //reader.registerBeanClass( "IDBean/children/IDBean", IDBean.class );

        System.out.println(reader.getRules().rules());

        InputStream in = new FileInputStream( 
            getTestFile("src/test/org/apache/commons/betwixt/digester/SimpleReadTest.xml") );
            
        try {
            Object obj = reader.parse( in );
            
            assertEquals("Read bean type is incorrect", true, (obj instanceof IDBean) );
            IDBean alpha = (IDBean) obj;
            
            assertEquals("Wrong list size", 2 ,  alpha.getChildren().size());
            
            IDBean beta = (IDBean) alpha.getChildren().get(0);
            assertEquals("Wrong name (A)", "beta" ,  beta.getName());
            
            IDBean gamma = (IDBean) alpha.getChildren().get(1);
            assertEquals("Wrong name (B)", "gamma" ,  gamma.getName());
        }
        finally {
            in.close();
        }
    }
}
