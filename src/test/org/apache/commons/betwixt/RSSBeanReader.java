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

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.digester.rss.Channel;
import org.apache.commons.digester.rss.RSSDigester;

/** Reads an RSS file using Betwixt's auto-digester rules then
  * outputs it again.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.4 $
  */
public class RSSBeanReader extends AbstractTestCase {
    
    /**
     * The set of public identifiers, and corresponding resource names,
     * for the versions of the DTDs that we know about.
     */
    protected static final String registrations[] = {
        "-//Netscape Communications//DTD RSS 0.9//EN",
        "/org/apache/commons/digester/rss/rss-0.9.dtd",
        "-//Netscape Communications//DTD RSS 0.91//EN",
        "/org/apache/commons/digester/rss/rss-0.91.dtd",
    };
    
    public RSSBeanReader(String testName) {
        super(testName);
    }

    public static void main(String[] args) throws Exception {
        RSSBeanReader sample = new RSSBeanReader("RSS");
        sample.run( args );
    }
    
    public void run(String[] args) throws Exception {
        BeanReader reader = new BeanReader();
        
        reader.registerBeanClass( Channel.class );
        
        // Register local copies of the DTDs we understand
        for (int i = 0; i < registrations.length; i += 2) {
            URL url = RSSDigester.class.getResource(registrations[i + 1]);
            if (url != null) {
                reader.register(registrations[i], url.toString());
            }
        }
        
        Object bean = null;
        if ( args.length > 0 ) {
            bean = reader.parse( args[0] );
        }
        else {
            InputStream in = new FileInputStream( getTestFile("src/test/org/apache/commons/betwixt/rss-example.xml") );
            bean = reader.parse( in ); 
            in.close();
        }
        
        write( bean );
    }
        
    public void write(Object bean) throws Exception {
        if ( bean == null ) {
            throw new Exception( "No bean read from the XML document!" );
        }
        BeanWriter writer = new BeanWriter();
        writer.getXMLIntrospector().setAttributesForPrimitives(false);
        writer.enablePrettyPrint();
        writer.write( bean );
    }
}

