/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/examples/rss/TestRSS.java,v 1.2.2.1 2004/01/15 20:41:28 rdonkin Exp $
 * $Revision: 1.2.2.1 $
 * $Date: 2004/01/15 20:41:28 $
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
 * 5. Products derived from this software may not be called "Apache",
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


package org.apache.commons.betwixt.examples.rss;

import java.io.File;

import org.apache.commons.betwixt.AbstractTestCase;

/**
 * <p>Test case for example.</p>
 *
 * @author Robert Burrell Donkin
 * @version $Revision: 1.2.2.1 $ $Date: 2004/01/15 20:41:28 $
 */

public class TestRSS extends AbstractTestCase {

    public TestRSS(String testName) {
        super(testName);
    }

    public void testPrintTextSummary() throws Exception {
        RSSApplication rssApplication = new RSSApplication();
        File file = new File(
            getTestFile("src/test/org/apache/commons/betwixt/examples/rss/rss-example.xml"));
        String output = rssApplication.plainTextSummary(file);
        String expected =   "channel: MozillaZine\n" +
                            "url: http://www.mozillazine.org\n" +
                            "copyright: Public Domain\n\n" +
                            "title: Java2 in Navigator 5?\n" +
                            "link: http://www.mozillazine.org/talkback.html?article=607\n" +
                            "description: Will Java2 be an integrated part of Navigator 5? " +
                            "Read more about it in this discussion...\n\n" +
                            "title: Communicator 4.61 Out\n" +
                            "link: http://www.mozillazine.org/talkback.html?article=606\n" +
                            "description: The latest version of Communicator is now " +
                            "available.  It includes security enhancements " +
                            "and various bug fixes.\n\n" +
                            "title: Mozilla Dispenses with Old, Proprietary DOM\n" +
                            "link: http://www.mozillazine.org/talkback.html?article=604\n" +
                            "description: \n\n" +
                            "title: The Animation Contest is Now Closed\n" +
                            "link: http://www.mozillazine.org/talkback.html?article=603\n" +
                            "description: \n";
        assertEquals(output, expected);
    }
}
