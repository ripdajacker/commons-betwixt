/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/expression/TestUpdaters.java,v 1.1 2003/04/11 21:29:46 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/04/11 21:29:46 $
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
 * $Id: TestUpdaters.java,v 1.1 2003/04/11 21:29:46 rdonkin Exp $
 */
package org.apache.commons.betwixt.expression;

import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.betwixt.AbstractTestCase;

/** Test harness for map updating 
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public class TestUpdaters extends AbstractTestCase {
    
    public static Test suite() {
        return new TestSuite(TestUpdaters.class);
    }
    
    public TestUpdaters(String testName) {
        super(testName);
    }
    
    public void testMapUpdate() throws Exception {	
        Class[] params = { String.class, String.class } ;
        Method method = AdderBean.class.getMethod("add", params);
        MapEntryAdder adder = new MapEntryAdder(method);
        
        AdderBean bean = new AdderBean();
        bean.add("UNSET", "UNSET");
        
        Updater keyUpdater = adder.getKeyUpdater();
        Updater valueUpdater = adder.getValueUpdater();
        
        Context context = new Context();
        context.setBean(bean);
        
        keyUpdater.update(context, "key");
        valueUpdater.update(context, "value");
        
        assertEquals("AdderBean not updated (1)", "key", bean.getKey());
        assertEquals("AdderBean not updated (2)", "value", bean.getValue());
        
        keyUpdater.update(context, "new-key");
        valueUpdater.update(context, "new-value");
        
        assertEquals("AdderBean not updated (1)", "new-key", bean.getKey());
        assertEquals("AdderBean not updated (2)", "new-value", bean.getValue());        
        
    }
}

