/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.betwixt.io.read.impl;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.read.BeanCreationChain;
import org.apache.commons.betwixt.io.read.ChainedBeanCreator;
import org.apache.commons.betwixt.io.read.ElementMapping;
import org.apache.commons.betwixt.io.read.ReadConfiguration;
import org.apache.commons.betwixt.io.read.ReadContext;
import org.apache.commons.betwixt.BindingConfiguration;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import java.net.URL;
import java.io.IOException;
import java.beans.IntrospectionException;
import java.util.List;

/**
 * BeanCreator that allows for "include" files.  The current element's "include-file" property
 * indicates the name of the XML file that should be processed.  For example, the following XML indicates
 * that the "venue" property should be created by parsing the file "org/apache/commons/betwixt/io/read/venue.xml":
 * <p/>
 * <p/>
 * <PRE>
 * &lt;?xml version="1.0"?&gt;
 * &lt;PartyBean&gt;
 * &lt;dateOfParty&gt;Wed Apr 14 19:46:55 MDT 2004&lt;/dateOfParty&gt;
 * &lt;excuse&gt;tired&lt;/excuse&gt;
 * &lt;fromHour&gt;2&lt;/fromHour&gt;
 * &lt;venue include-file="org/apache/commons/betwixt/io/read/venue.xml"/&gt;
 * &lt;/PartyBean&gt;
 * </PRE>
 * <p/>
 * The venue.xml file would be a standard XML input for betwixt:
 * <p/>
 * <PRE>
 * &lt;?xml version="1.0"?&gt;
 * &lt;venue&gt;
 * &lt;city&gt;San Francisco&lt;/city&gt;
 * &lt;code&gt;94404&lt;/code&gt;
 * &lt;country&gt;USA&lt;/country&gt;
 * &lt;street&gt;123 Here&lt;/street&gt;
 * &lt;/venue&gt;
 * </PRE>
 * <p/>
 * <p/>
 * The include file is loaded as a resource using the current <code>ClassLoader</code> following the rules
 * as described by the <code>ClassLoader.getResource</code> method.
 * <p/>
 * The include file is parsed using a <code>BeanReader</code> with the <code>BindingConfiguration</code>
 * and <code>ReadConfiguration</code> which can be explicitly set, or if it is not set,
 * a default will be used.
 * <p/>
 * The current class (obtained from the <code>ElementMapping</code>) will be registered with the <code>BeanReader</code>
 * using the "name" of the current <code>ElementMapping</code>.
 *
 * @author Brian Pugh
 */
public class IncludeBeanCreator implements ChainedBeanCreator {
    private BindingConfiguration bindingConfiguration;
    private ReadConfiguration readConfiguration;
    private List parsedFiles;

    /**
     * Constructs a new IncludeBeanCreator.
     */
    public IncludeBeanCreator() {}
    
    /**
     * Constructs a new IncludeBeanCreator.
     *
     * @param bindingConfiguration the <code>BindingConfiguration</code> that will be used to parse include files.
     */
    public IncludeBeanCreator(
        BindingConfiguration bindingConfiguration,
        ReadConfiguration readConfiguration) {
        setBindingConfiguration(bindingConfiguration);
        setReadConfiguration(readConfiguration);
    }

    /**
     * Get the <code>BindingConfiguration</code> that will be used to parse include files.
     *
     * @return the <code>BindingConfiguration</code> that will be used to parse include files.
     */
    public BindingConfiguration getBindingConfiguration() {
        return bindingConfiguration;
    }
    /**
     * Set the <code>BindingConfiguration</code> that will be used to parse include files.
     *
     * @param bindingConfiguration the <code>BindingConfiguration</code> that will be used to parse include files.
     */
    public void setBindingConfiguration(BindingConfiguration bindingConfiguration) {
        this.bindingConfiguration = bindingConfiguration;
    }
    /**
     * Get the <code>ReadConfiguration</code> that will be used to parse include files.
     * @return the <code>ReadConfiguration</code> that will be used to parse include files.
     */
    public ReadConfiguration getReadConfiguration() {
        return readConfiguration;
    }

    /**
     * Set the <code>ReadConfiguration</code> that will be used to parse include files.
     * @param readConfiguration the <code>ReadConfiguration</code> that will be used to parse include files.
     */
    public void setReadConfiguration(ReadConfiguration readConfiguration) {
        this.readConfiguration = readConfiguration;
    }

    /**
     * If the "include-file" attribute is found, the bean is created by parsing the include xml file.
     * Otherwise bean creation is delegated to the other members of the chain.
     *
     * @param elementMapping specifies the mapping between the type and element.
     * @param context        the context in which this converision happens, not null
     * @param chain          not null
     * @return the Object created, possibly null
     */
    public Object create(
        ElementMapping elementMapping,
        ReadContext context,
        BeanCreationChain chain) {
        Attributes attributes = elementMapping.getAttributes();
        String file = attributes.getValue("include-file");
        if (file != null && !file.equals("")) {
            BeanReader beanReader = new BeanReader();
            if (bindingConfiguration != null) {
                beanReader.setBindingConfiguration(bindingConfiguration);
            }
            if (readConfiguration != null) {
                beanReader.setReadConfiguration(readConfiguration);
            }
            String name = elementMapping.getName();
            Class clazz = elementMapping.getType();
            ClassLoader loader = context.getClassLoader();
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            URL url = loader.getResource(file);
            if (url == null) {
                url = loader.getResource("/" + file);
            }
            if (url == null) {
                throw new RuntimeException(
                    "Unable to locate include file: " + file);
            }
            try {
                beanReader.registerBeanClass(name, clazz);
                return beanReader.parse(url.toString());
            } catch (IntrospectionException e) {
                throw new RuntimeException(
                    "Unable to register class with beanReader.  Classname: "
                        + clazz,
                    e);
            } catch (IOException e) {
                throw new RuntimeException(
                    "Unable to process include file: " + file,
                    e);
            } catch (SAXException e) {
                throw new RuntimeException(
                    "Unable to process include file: " + file,
                    e);
            }
        }
        return chain.create(elementMapping, context);
    }
}
