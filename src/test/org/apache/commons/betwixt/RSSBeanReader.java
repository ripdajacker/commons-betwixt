/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: RSSBeanReader.java,v 1.4 2002/05/28 11:49:29 jstrachan Exp $
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

