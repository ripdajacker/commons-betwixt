/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: RSSBeanWriter.java,v 1.4 2002/05/28 11:49:29 jstrachan Exp $
 */
package org.apache.commons.betwixt;

import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.io.BeanWriter;

import org.apache.commons.digester.rss.RSSDigester;

/** Reads an RSS file using Digesters's RSS demo then uses Betwixt
  * to output it as XML again.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.4 $
  */
public class RSSBeanWriter extends AbstractTestCase {
    
    public RSSBeanWriter(String testName) {
        super(testName);
    }

    public static void main(String[] args) throws Exception {
        RSSBeanWriter sample = new RSSBeanWriter("RSS");
        sample.run( args );
    }
    
    public void run(String[] args) throws Exception {
        RSSDigester digester = new RSSDigester();
        Object bean = null;
        if ( args.length > 0 ) {
            bean = digester.parse( args[0] );
        }
        else {
            InputStream in = new FileInputStream( getTestFile("src/test/org/apache/commons/betwixt/rss-example.xml") );
            bean = digester.parse( in ); 
            in.close();
        }
        
        write( bean );
    }
        
    public void write(Object bean) throws Exception {
        BeanWriter writer = new BeanWriter();
        writer.getXMLIntrospector().setAttributesForPrimitives(false);
        writer.enablePrettyPrint();
        writer.write( bean );
    }
}

