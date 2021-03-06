/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.betwixt.digester;

import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.digester.Rule;

import java.util.Set;

/** <p><code>RuleSupport</code> is an abstract base class containing useful
 * helper methods.</p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision$
 */
class RuleSupport extends Rule {

   /** Base constructor */
   RuleSupport() {
   }


   // Implementation methods
   //-------------------------------------------------------------------------

   /**
    * Gets <code>XMLBeanInfoDigester</code> using this rule.
    *
    * @return <code>XMLBeanInfoDigester</code> for this rule
    */
   XMLBeanInfoDigester getXMLInfoDigester() {
      return (XMLBeanInfoDigester) getDigester();
   }

   /**
    * Gets <code>XMLIntrospector</code> to be used for introspection
    *
    * @return <code>XMLIntrospector</code> to use
    */
   XMLIntrospector getXMLIntrospector() {
      return getXMLInfoDigester().getXMLIntrospector();
   }

   /**
    * Gets the class of the bean whose .betwixt file is being digested
    *
    * @return the <code>Class</code> of the bean being processed
    */
   Class getBeanClass() {
      return getXMLInfoDigester().getBeanClass();
   }

   /**
    * Gets the property names already processed
    *
    * @return the set of property names that have been processed so far
    */
   Set<String> getProcessedPropertyNameSet() {
      return getXMLInfoDigester().getProcessedPropertyNameSet();
   }


   /**
    * Loads the given class using an appropriate <code>ClassLoader</code>.
    * Uses {@link org.apache.commons.digester.Digester#getClassLoader()}.
    * @param className  names the class to be loaded
    * @return <code>Class</code> loaded, not null
    * @throws ClassNotFoundException
    */
   Class loadClass(String className) throws ClassNotFoundException {
      ClassLoader classloader = digester.getClassLoader();
      Class clazz;
      if (classloader == null) {
         clazz = Class.forName(className);
      } else {
         clazz = classloader.loadClass(className);
      }
      return clazz;
   }
}
