/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/strategy/TestDefaultPluralStemmer.java,v 1.3 2002/12/30 18:16:48 mvdb Exp $
 * $Revision: 1.3 $
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
 * $Id: TestDefaultPluralStemmer.java,v 1.3 2002/12/30 18:16:48 mvdb Exp $
 */
package org.apache.commons.betwixt.strategy;

import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.betwixt.ElementDescriptor;

/**
 * Tests the defaultPluralStemmer
 * 
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: TestDefaultPluralStemmer.java,v 1.3 2002/12/30 18:16:48 mvdb Exp $
 */
public class TestDefaultPluralStemmer extends TestCase
{

    public static Test suite() {
        return new TestSuite(TestDefaultPluralStemmer.class);
    }

    public TestDefaultPluralStemmer(String testName)  {
        super(testName);
    }
    
    public void testNullMap() {
        DefaultPluralStemmer stemmer = new DefaultPluralStemmer();
        try {
            stemmer.findPluralDescriptor("test", null);
            fail("Should throw a nullpointer exception, since the map in the stemmer cannot be null");
        }catch(NullPointerException npe) {
        }
    }
    
    /**
     * This is the first match when calling the defaultStemmer.
     * It just adds an s to the the property and it should find it..
     */
    public void testFirstMatch() {
        
        ElementDescriptor des = new ElementDescriptor();
        des.setQualifiedName("FooBars");
        des.setPropertyType(java.util.List.class);
        HashMap map = new HashMap();
        map.put("FooBars", des);
        DefaultPluralStemmer dps = new DefaultPluralStemmer();
        ElementDescriptor result = dps.findPluralDescriptor("FooBar", map);
        assertEquals(des, result);
    }
    /**
     * Tests if the y is nicely replaces with ies and the correct
     * ElementDescriptor is returned
     */
    public void testSecondMatch() {
        ElementDescriptor des = new ElementDescriptor();
        des.setQualifiedName("FooBary");
        des.setPropertyType(java.util.List.class);
        HashMap map = new HashMap();
        map.put("FooBaries", des);
        DefaultPluralStemmer dps = new DefaultPluralStemmer();
        ElementDescriptor result = dps.findPluralDescriptor("FooBary", map);
        assertEquals(des, result);
    }
    
    /**
     * Tests if it actually skips the y if the length not greater than 1.
     */
    public void testSecondNonMatch() {
        ElementDescriptor des = new ElementDescriptor();
        des.setQualifiedName("y");
        des.setPropertyType(java.util.List.class);
        HashMap map = new HashMap();
        map.put("yies", des);
        DefaultPluralStemmer dps = new DefaultPluralStemmer();
        ElementDescriptor result = dps.findPluralDescriptor("y", map);
        assertNotNull(result);
    }
    
    /**
     * Uses the third if in pluralstemmer.
     * It should return the specified y, without any changing.
     */
    public void testThirdMatch() {
        ElementDescriptor des = new ElementDescriptor();
        des.setQualifiedName("y");
        des.setPropertyType(java.util.List.class);
        HashMap map = new HashMap();
        map.put("y", des);
        DefaultPluralStemmer dps = new DefaultPluralStemmer();
        ElementDescriptor result = dps.findPluralDescriptor("y", map);
        assertEquals(des, result);
    }
   
    /**
     * Tests to see if you get warned when there are multiple matches
     * found
     */
    public void testMultipleMatches() {
        ElementDescriptor des = new ElementDescriptor();
        des.setQualifiedName("y");
        des.setPropertyType(java.util.List.class);
        ElementDescriptor desyes = new ElementDescriptor();
        desyes.setQualifiedName("yes");
        desyes.setPropertyType(java.util.List.class);
        ElementDescriptor desyesno = new ElementDescriptor();
        desyesno.setQualifiedName("yesno");
        desyesno.setPropertyType(java.util.List.class);
        HashMap map = new HashMap();
        map.put("y", des);
        map.put("yes", desyes);
        map.put("yesno", desyesno);
        DefaultPluralStemmer dps = new DefaultPluralStemmer();
        ElementDescriptor result = dps.findPluralDescriptor("y", map);
        assertEquals(des, result);
        result = dps.findPluralDescriptor("yes", map);
        assertEquals(desyes, result);
        result = dps.findPluralDescriptor("yesno", map);
        assertEquals(desyesno, result);
    }
   
    /**
     *  Test to find matched where plural ending is "es" 
     */
    public void testESPluralEndingMatch() {
        HashMap map = new HashMap();

        ElementDescriptor index = new ElementDescriptor("index", "index","");
        map.put("index", index);
        ElementDescriptor indexes = new ElementDescriptor("indexes", "indexes","");
        map.put("indexes", indexes);

        ElementDescriptor patch = new ElementDescriptor("patch", "patch","");
        map.put("patch", patch);
        ElementDescriptor patches = new ElementDescriptor("patches", "patches","");
        map.put("patches", patches);

        DefaultPluralStemmer stemmer = new DefaultPluralStemmer();
        ElementDescriptor result = stemmer.findPluralDescriptor("index", map);
        assertEquals(indexes, result);

        result = stemmer.findPluralDescriptor("patches", map);
        assertEquals(patches, result);
    }
 
    /**
     *  Test if the closest match mechanisme is working
     */
    public void testClosestMatch() {
        HashMap map = new HashMap();
        ElementDescriptor yes1 = new ElementDescriptor("yes1", "yes1","");
        map.put("yes1", yes1);
        ElementDescriptor yes12 = new ElementDescriptor("yes12", "yes12","");
        map.put("yes12", yes12);
        ElementDescriptor yes123 = new ElementDescriptor("yes123", "yes123","");
        map.put("yes123", yes123);
        DefaultPluralStemmer stemmer = new DefaultPluralStemmer();
        ElementDescriptor result = stemmer.findPluralDescriptor("yes", map);
        assertEquals(yes1, result);
    }    
    
}

