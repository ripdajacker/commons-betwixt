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
package org.apache.commons.betwixt.introspection;

import junit.framework.TestCase;

import org.apache.commons.beanutils.*;
import org.apache.commons.betwixt.*;

/**
 * @author <a href='http://jakarta.apache.org/commons'>Jakarta Commons Team</a>, <a href='http://www.apache.org'>Apache Software Foundation</a>
 */
public class TestDynaBeanIntrospection extends TestCase {

    public void testSimpleIntrospectionTest() throws Exception {
        DynaProperty[] dynaProperties = {
                new DynaProperty("one", Integer.class),
                new DynaProperty("two", String.class)};
        BasicDynaClass dynaClass = new BasicDynaClass("WibbleDynaBean", BasicDynaBean.class, 
                dynaProperties);
        DynaBean dynaBean = dynaClass.newInstance();
        XMLIntrospector xmlIntrospector = new XMLIntrospector();
        XMLBeanInfo xmlBeanInfo = xmlIntrospector.introspect(dynaBean);
        
        ElementDescriptor dynaBeanDescriptor = xmlBeanInfo.getElementDescriptor();
        ElementDescriptor[] dynaPropertyDescriptors = dynaBeanDescriptor.getElementDescriptors();
        assertEquals("Two dyna properties expected", 2, dynaPropertyDescriptors.length);
        
        for (int i=0; i<2; i++) {
            if ("one".equals(dynaPropertyDescriptors[i].getPropertyName()) 
                    || "two".equals(dynaPropertyDescriptors[i].getPropertyName())) {
                assertNotNull("Property updater", dynaPropertyDescriptors[1].getUpdater());
            } else {
                fail("Properties should be named one and two");
            }
        }
    }
    
}
