/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License") you may not use this file except in compliance with
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

package org.apache.commons.betwixt.io

import groovy.transform.CompileStatic
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.betwixt.io.read.BeanBindAction
import org.apache.commons.betwixt.io.read.MappingAction
import org.apache.commons.betwixt.io.read.ReadContext
import org.apache.commons.digester.Digester
import org.apache.commons.digester.Rule
import org.apache.commons.digester.RuleSet
import org.xml.sax.Attributes

/**
 * <p>Sets <code>Betwixt</code> digestion rules for a bean class.</p>
 *
 * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @since 0.5
 */
@CompileStatic
public class BeanRuleSet implements RuleSet {
    /** The base path under which the rules will be attached */
    private String basePath

    /**
     * The (empty) base context from which all Contexts
     * with beans are (directly or indirectly) obtained
     */
    private DigesterReadContext context

    /**
     * Base constructor.
     *
     * @param introspector the <code>XMLIntrospector</code> used to introspect
     * @param basePath specifies the (Digester-style) path under which the rules will be attached
     * @param baseBeanClass the <code>Class</code> whose mapping rules will be created
     * @param baseContext the root Context that bean carrying Contexts should be obtained from,
     *                      not null
     */
    public BeanRuleSet(XMLIntrospector introspector, String basePath, Class baseBeanClass, ReadContext baseContext) {
        this.basePath = basePath
        this.context = new DigesterReadContext(baseContext)
        this.context.setRootClass(baseBeanClass)
        this.context.setIntrospector(introspector)
    }

    //-------------------------------- Ruleset implementation

    /**
     * <p>Gets the namespace associated with this ruleset.</p>
     * <p/>
     * <p><strong>Note</strong> namespaces are not currently supported.</p>
     *
     * @return null
     */
    public String getNamespaceURI() {
        return null
    }

    /**
     * Add rules for bean to given <code>Digester</code>.
     *
     * @param digester the <code>Digester</code> to which the rules for the bean will be added
     */
    public void addRuleInstances(Digester digester) {
        context.setDigester(digester)

        // if the classloader is not set, set to the digester classloader
        if (context.getClassLoader() == null) {
            context.setClassLoader(digester.getClassLoader())
        }

        digester.addRule("!$basePath/*", new ActionMappingRule())
    }

    /**
     * Single rule that is used to map all elements.
     *
     * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
     */
    private final class ActionMappingRule extends Rule {

        /**
         * Processes the start of a new <code>Element</code>.
         * The actual processing is delegated to <code>MappingAction</code>'s.
         *
         * @see Rule#begin(String, String, Attributes)
         */
        public void begin(String namespace, String name, Attributes attributes) {
            context.pushElement(name)
            MappingAction nextAction = nextAction(namespace, name, attributes, context)
            context.pushMappingAction(nextAction)
        }

        /**
         * Gets the next action to be executed
         *
         * @param namespace the element's namespace, not null
         * @param name the element name, not null
         * @param attributes the element's attributes, not null
         * @param context the <code>ReadContext</code> against which the xml is being mapped.
         * @return the initialized <code>MappingAction</code>, not null
         * @throws Exception
         */
        private MappingAction nextAction(String namespace, String name, Attributes attributes, ReadContext context) {

            MappingAction result
            MappingAction lastAction = context.currentMappingAction()
            if (lastAction == null) {
                result = BeanBindAction.INSTANCE
            } else {
                result = lastAction.next(namespace, name, attributes, context)
            }
            return result.begin(namespace, name, attributes, context)
        }

        /**
         * Processes the body text for the current element.
         * This is delegated to the current <code>MappingAction</code>.
         *
         * @see Rule#body(String, String, String)
         */
        public void body(String namespace, String name, String text) {

            if (digester.getCount() > 0) {
                MappingAction action = context.currentMappingAction()
                action.body(text, context)
            }
        }

        /**
         * Process the end of this element.
         * This is delegated to the current <code>MappingAction</code>.
         */
        public void end(String namespace, String name) {
            MappingAction action = context.popMappingAction()
            action.end(context)
        }

        /**
         * Tidy up.
         */
        public void finish() {
            context.clearBeans()
        }

    }

    /**
     * Specialization of <code>ReadContext</code> when reading from <code>Digester</code>.
     *
     * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
     * @version $Revision$
     */
    private static class DigesterReadContext extends ReadContext {

        Digester digester

        public DigesterReadContext(ReadContext readContext) {
            super(readContext)
        }

        /* (non-Javadoc)
         * @see org.apache.commons.betwixt.io.read.ReadContext#pushBean(java.lang.Object)
         */

        public void pushBean(Object bean) {
            super.pushBean(bean)
            digester.push(bean)
        }

        /* (non-Javadoc)
         * @see org.apache.commons.betwixt.io.read.ReadContext#putBean(java.lang.Object)
         */

        public Object popBean() {
            Object bean = super.popBean()
            // don't pop the last from the stack
            if (digester.getCount() > 0) {
                digester.pop()
            }
            return bean
        }
    }

}
