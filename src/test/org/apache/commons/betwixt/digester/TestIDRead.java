
/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/digester/TestIDRead.java,v 1.4 2002/12/30 18:16:48 mvdb Exp $
 * $Revision: 1.4 $
 * $Date: 2002/12/30 18:16:48 $
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
 * $Id: TestIDRead.java,v 1.4 2002/12/30 18:16:48 mvdb Exp $
 */
package org.apache.commons.betwixt.digester;

import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.AbstractTestCase;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;

/** Test harness for ID-IDRef reading.
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.4 $
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
//        SimpleLog log = new SimpleLog("[testSimpleRead:XMLIntrospectorHelper]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        XMLIntrospectorHelper.setLog(log);
//        
//        log = new SimpleLog("[testSimpleRead:MethodUpdater]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        MethodUpdater.setLog(log);
        
//        log = new SimpleLog("[testSimpleRead:BeanCreateRule]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        BeanCreateRule.setLog(log);

//        log = new SimpleLog("[testSimpleRead:IDBean]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        IDBean.log = log;

//        log = new SimpleLog("[testSimpleRead:BeanReader]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);        
//        reader.setLog(log);

//        log = new SimpleLog("[testSimpleRead:XMLIntrospector]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        reader.getXMLIntrospector().setLog(log);
        
        reader.registerBeanClass( IDBean.class );

        InputStream in = new FileInputStream( 
            getTestFile("src/test/org/apache/commons/betwixt/digester/SimpleReadTest.xml") );
            
        try {        
//            log = new SimpleLog("[testSimpleRead]");
//            log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
            Object obj = reader.parse( in );
//            log.debug(obj);
            
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
    
    public void testIDRead() throws Exception {
        
        BeanReader reader = new BeanReader();
        
//         logging just for this method
//        SimpleLog log = new SimpleLog("[testIDRead:XMLIntrospectorHelper]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        XMLIntrospectorHelper.setLog(log);
//        
//        log = new SimpleLog("[testIDRead:BeanCreateRule]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        BeanCreateRule.setLog(log);
//
//        log = new SimpleLog("[testIDRead:BeanReader]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);        
//        reader.setLog(log);
//
//        log = new SimpleLog("[testIDRead:XMLIntrospector]");
//        log.setLevel(SimpleLog.LOG_LEVEL_TRACE);
//        reader.getXMLIntrospector().setLog(log);
        
        reader.registerBeanClass( IDBean.class );

        InputStream in = new FileInputStream( 
            getTestFile("src/test/org/apache/commons/betwixt/digester/IDTest1.xml") );
            
        try {
            Object obj = reader.parse( in );
            
            assertEquals("Read bean type is incorrect", true, (obj instanceof IDBean) );
            IDBean alpha = (IDBean) obj;
            
            assertEquals("Wrong list size (A)", 2 ,  alpha.getChildren().size());
            
            IDBean beta = (IDBean) alpha.getChildren().get(0);
            assertEquals("Wrong name (A)", "beta" ,  beta.getName());
            
            IDBean gamma = (IDBean) alpha.getChildren().get(1);
            assertEquals("Wrong name (B)", "gamma" ,  gamma.getName());
            assertEquals("Wrong list size (B)", 2 ,  gamma.getChildren().size());
            
            IDBean sonOfGamma = (IDBean) gamma.getChildren().get(1);
            
            assertEquals("Wrong id (A)", "two" ,  sonOfGamma.getId());
            assertEquals("Wrong name (C)", "beta" ,  sonOfGamma.getName());
            
            assertEquals("IDREF bean not equal to ID bean", beta,  sonOfGamma);
        }
        finally {
            in.close();
        }
    }
}
