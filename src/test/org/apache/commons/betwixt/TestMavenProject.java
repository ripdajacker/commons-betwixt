/*
 * $Header: /home/cvs/jakarta-commons-sandbox/betwixt/src/test/org/apache/commons/betwixt/TestRSSRoundTrip.java,v 1.4 2002/05/17 15:24:10 jstrachan Exp $
 * $Revision: 1.4 $
 * $Date: 2002/05/17 15:24:10 $
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
 * $Id: TestRSSRoundTrip.java,v 1.4 2002/05/17 15:24:10 jstrachan Exp $
 */
package org.apache.commons.betwixt;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Developer;
import org.apache.maven.project.Project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** Test harness which round trips a Maven project.xml file
  * using Bewixt and Maven's Project beans..
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.4 $
  */
public class TestMavenProject extends AbstractTestCase {
    
    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite(TestMavenProject.class);
    }
    
    public TestMavenProject(String testName) {
        super(testName);
    }

    /**
     * Tests the introspector
     */
    public void testIntrospector() throws Exception {
        XMLIntrospector introspector = createXMLIntrospector();
        XMLBeanInfo info = introspector.introspect( Project.class );
        ElementDescriptor root = info.getElementDescriptor();
        
        assertEquals( "project", root.getLocalName() );
        
        ElementDescriptor descriptor = findDescriptorsForLocalName( 
            root.getElementDescriptors(), "dependencies" 
        );
        assertTrue( "Should have an updater on the dependencies descriptor", descriptor.getUpdater() != null );
        assertEquals( "dependencies", descriptor.getPropertyName() );


        ElementDescriptor dependency = getFirstChildDependency(descriptor, "dependencies" );        
        
        assertEquals( "dependency", dependency.getLocalName() );
        assertTrue( "Should not have an updater on the dependency descriptor", dependency.getUpdater() == null );
        
        ElementDescriptor build = findDescriptorsForLocalName( 
            root.getElementDescriptors(), "build" 
        );
/*        
        ElementDescriptor sourceDirectories = findDescriptorsForLocalName( 
            build.getElementDescriptors(), "sourceDirectories" 
        );
        ElementDescriptor sourceDirectory = getFirstChildDependency(sourceDirectories, "sourceDirectories" );
*/        
    }    
    
    /** 
     * Tests we can parse a project.xml
     */
    public void testParse() throws Exception {
        BeanReader reader = createBeanReader();
        
        Project project = (Project) reader.parse( new FileInputStream( "project.xml" ) );

        testProject( project );
    }

    /**
     * Tests we can round trip from the XML -> bean -> XML -> bean.
     * Ideally this method should test both Project objects are identical
     */
    public void testRoundTrip() throws Exception {
        BeanReader reader = createBeanReader();
        
        Project project = (Project) reader.parse( new FileInputStream( "project.xml" ) );

        // now lets output it to a buffer
        StringWriter buffer = new StringWriter();
        write( project, buffer );
        

        // create a new BeanReader
        reader = createBeanReader();

        // now lets try parse the output sing the BeanReader 
        String text = buffer.toString();        
        
        System.out.println( text );

        Project newProject = (Project) reader.parse( new StringReader(text ) );
        
        // managed to parse it again!
        testProject( newProject );
        
        // #### should now test the old and new Project instances for equality.
    }


    // Implementation methods
    //-------------------------------------------------------------------------    
    
    protected BeanReader createBeanReader() throws Exception {
        BeanReader reader = new BeanReader();
        reader.setXMLIntrospector( createXMLIntrospector() );
        reader.registerBeanClass( Project.class );
        return reader;
    }

    /** 
     * ### it would be really nice to move this somewhere shareable across
     * Maven / Turbine projects. Maybe a static helper method - question is
     * what to call it???
     */
    protected XMLIntrospector createXMLIntrospector() {    
        XMLIntrospector introspector = new XMLIntrospector();
        
        // set elements for attributes to true
        introspector.setAttributesForPrimitives(false);
        
        // wrap collections in an XML element
        //introspector.setWrapCollectionsInElement(true);
        
        // turn bean elements into lower case
        introspector.setElementNameMapper( new DecapitalizeNameMapper() );
        
        return introspector;
    }
        
    /** 
     * Tests the value of the Project object that has just been parsed
     */        
    protected void testProject(Project project) throws Exception {    
        assertTrue( "Returned null project instance", project != null );        
        assertEquals( "commons-betwixt", project.getName() );
        assertEquals( "commons-betwixt", project.getId() );
        assertEquals( "org.apache.commons.betwixt", project.getPackage() );
        assertEquals( "jakarta.apache.org", project.getSiteAddress() );
        
        List developers = project.getDevelopers();
        assertTrue( "Found at least one developer", developers.size() > 0 );
        
        Developer developer = (Developer) developers.get(0);
        assertEquals( "James Strachan", developer.getName() );
        assertEquals( "jstrachan@apache.org", developer.getEmail() );
        
        List dependencies = project.getDependencies();
        assertTrue( "Found at least one dependency", dependencies.size() > 0 );
        
        Dependency dependency = (Dependency) dependencies.get(0);
        assertEquals( "commons-logging", dependency.getName() );
        
        String sourceDirectory = project.getBuild().getSourceDirectory();
        assertEquals("src/java", sourceDirectory);
    }        
        
    protected void write(Object bean, Writer out) throws Exception {
        BeanWriter writer = new BeanWriter(out);
        writer.setXMLIntrospector( createXMLIntrospector() );
        writer.enablePrettyPrint();
        writer.write( bean );
    }
    
    /** 
     * Finds a descriptor in the given array which matches the given local name.
     */
    protected ElementDescriptor findDescriptorsForLocalName( ElementDescriptor[] descriptors, String localName ) throws Exception {
        for ( int i = 0, size = descriptors.length; i < size; i++ ) {
            ElementDescriptor descriptor = descriptors[i];
            if ( localName.equals( descriptor.getLocalName() ) ) {
                return descriptor;
            }
        }
        fail( "Could not find a descriptor for '" + localName + "'" );
        return null;
    }
    
    /**
     * Returns the first child descriptor for the given descriptor or fails if one could not be found
     */
    protected ElementDescriptor getFirstChildDependency(ElementDescriptor descriptor, String localName ) throws Exception {        
        ElementDescriptor[] children = descriptor.getElementDescriptors();
        assertTrue( "Should find at least one child descriptor of '" + localName + "'", children != null && children.length > 0 );
        return children[0];
    }
}

