/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/strategy/TestDefaultNameMapper.java,v 1.4 2003/10/05 13:52:52 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/10/05 13:52:52 $
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
 
package org.apache.commons.betwixt.strategy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Testcase that covers the DefaultNameMapper.
 * 
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: TestDefaultNameMapper.java,v 1.4 2003/10/05 13:52:52 rdonkin Exp $
 */
public class TestDefaultNameMapper extends TestCase
{
    
    public static Test suite() {
        return new TestSuite(TestDefaultNameMapper.class);
    }

    public TestDefaultNameMapper(String testName)
    {
        super(testName);
    }
    /**
     * Just put in some strings and expect them back unchanged.
     * This looks stupid, but enables us to check for unexpected
     * changes, which breaks the orignal behaviour.
     */
    public void testDefault() {
        String[] values = { "foo", "Foo", "FooBar", "fooBar", 
                            "FOOBAR", "FOOBar", "FoOBaR"};
        DefaultNameMapper mapper = new DefaultNameMapper();
        for (int i=0; i < values.length; i++) {
            String result = mapper.mapTypeToElementName(values[i]);
            assertEquals(values[i], result);
        }
    }
    
    public void testBadCharBadFirstOne() {
        String name="$LoadsOfMoney";
        DefaultNameMapper mapper = new DefaultNameMapper();
        String out = mapper.mapTypeToElementName(name);
        assertEquals("Expected", "LoadsOfMoney", out);
    }
    
    public void testBadCharBadFirstTwo() {
        String name="$LOADS£OF$MONEY";
        DefaultNameMapper mapper = new DefaultNameMapper();        
        String out = mapper.mapTypeToElementName(name);
        assertEquals("Expected", "LOADSOFMONEY", out);
    }
    
    public void testBadCharGoodFirstOne() {
        String name="L$oads%OfMone$y$";
        DefaultNameMapper mapper = new DefaultNameMapper();        
        String out = mapper.mapTypeToElementName(name);
        assertEquals("Expected", "LoadsOfMoney", out);
    }
    
    public void testBadCharGoodFirstTwo() {
        String name="LOADSOFMONE$$Y";
        DefaultNameMapper mapper = new DefaultNameMapper();        
        String out = mapper.mapTypeToElementName(name);
        assertEquals("Expected", "LOADSOFMONEY", out);
    }
}

