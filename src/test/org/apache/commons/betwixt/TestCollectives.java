/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/TestCollectives.java,v 1.1.2.2 2004/02/08 12:11:17 rdonkin Exp $
 * $Revision: 1.1.2.2 $
 * $Date: 2004/02/08 12:11:17 $
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
 * 5. Products derived from this software may not be called "Apache"
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

package org.apache.commons.betwixt;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.strategy.CapitalizeNameMapper;

/**
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.2 $
 */
public class TestCollectives extends AbstractTestCase{
    
    private IntrospectionConfiguration categoriesIntrospectionConfiguration = new IntrospectionConfiguration();
    private BindingConfiguration noIDsBindingConfiguration = new BindingConfiguration();
    
    public TestCollectives(String testName) {
        super(testName);
        
        CapitalizeNameMapper capitalizeNameMapper = new CapitalizeNameMapper();
        categoriesIntrospectionConfiguration.setAttributesForPrimitives(false);
        categoriesIntrospectionConfiguration.setElementNameMapper(capitalizeNameMapper);
        categoriesIntrospectionConfiguration.setAttributeNameMapper(capitalizeNameMapper);
        categoriesIntrospectionConfiguration.setWrapCollectionsInElement(false);    
        
        noIDsBindingConfiguration.setMapIDs(false);    
    }


    public void testWriteCategories() throws Exception {
        StringWriter out = new StringWriter();
        out.write("<?xml version='1.0'?>");
        BeanWriter writer = new BeanWriter(out);
        writer.getXMLIntrospector().setConfiguration(categoriesIntrospectionConfiguration);
        writer.setBindingConfiguration(noIDsBindingConfiguration);
        
        Categories categories = new Categories();
        categories.addCategory(new Category("Runs"));
        categories.addCategory(new Category("Innings"));
        categories.addCategory(new Category("Dismissals"));
        categories.addCategory(new Category("High Score"));
        categories.addCategory(new Category("Average"));
        
        writer.write(categories);
        
        String xml = out.getBuffer().toString();
        String expected = "<?xml version='1.0'?><Categories>" +            "<Category><Name>Runs</Name></Category>" +            "<Category><Name>Innings</Name></Category>" +            "<Category><Name>Dismissals</Name></Category>" +            "<Category><Name>High Score</Name></Category>" +            "<Category><Name>Average</Name></Category>" +            "</Categories>";
            
       xmlAssertIsomorphicContent(parseString(expected), parseString(xml));
    }   
    
    public void testReadCategories() throws Exception {
        BeanReader beanReader = new BeanReader();
        beanReader.getXMLIntrospector().setConfiguration(categoriesIntrospectionConfiguration);
        beanReader.setBindingConfiguration(noIDsBindingConfiguration);
        beanReader.registerBeanClass(Categories.class);

        String xml = "<?xml version='1.0'?><Categories>" +
            "<Category><Name>Runs</Name></Category>" +
            "<Category><Name>Innings</Name></Category>" +
            "<Category><Name>Dismissals</Name></Category>" +
            "<Category><Name>High Score</Name></Category>" +
            "<Category><Name>Average</Name></Category>" +
            "</Categories>";
       
       StringReader in = new StringReader(xml);
       
       Categories bean = (Categories) beanReader.parse(in);    
       
       assertEquals("5 categories", 5, bean.size());
       
       Iterator it = bean.getCategories();  
       assertEquals("Runs category", new Category("Runs"), it.next());
       assertEquals("Runs category", new Category("Innings"), it.next());
       assertEquals("Runs category", new Category("Dismissals"), it.next());
       assertEquals("Runs category", new Category("High Score"), it.next());
       assertEquals("Runs category", new Category("Average"), it.next());
       
    }
    

}
