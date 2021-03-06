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

package org.apache.commons.betwixt.io.read

import groovy.transform.TypeChecked
import org.apache.commons.betwixt.ElementDescriptor
import org.apache.commons.betwixt.expression.Context
import org.apache.commons.betwixt.expression.Updater
import org.xml.sax.Attributes

/**
 * <p>Acts to bind an array property.
 * Note that this is intended to be used to map
 * properties with a setter taking an array
 * but which do not have an adder.</p>
 * <p>
 * <strong>Note</strong> this implementation has state
 * and therefore cannot be used concurrently (in simultaneous readings).
 * </p>
 *
 * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
 * @version $Revision$
 */
@TypeChecked
public class ArrayBindAction extends MappingAction.Base {

    /**
     * Factory method creates implementations to map arrays.
     *
     * @param descriptor <code>ElementDescriptor</code> to be mapped,
     *                          not null
     * @return <code>MappingAction</code>, not null
     */
    public static MappingAction createMappingAction(ElementDescriptor descriptor) {
        MappingAction result = new ArrayBindAction()
        if (descriptor.getSingularPropertyType() != null && !descriptor.getSingularPropertyType().isArray()) {
            result = BeanBindAction.INSTANCE
        }
        return result
    }

    private BeanMapping beanMapping = new BeanMapping()
    private Updater originalUpdater

    /**
     * Mapping arrays requires the addition of a temporary object
     * (an <code>ArrayList</code>) into the stack together with an
     * updater for that object.
     */
    public MappingAction begin(String namespace, String name, Attributes attributes, ReadContext context) {
        context.pushBean(new ArrayList())
        return this
    }

    /**
     * Pops the <code>ArrayList</code> and the updater from
     * their stacks. The original updater is called with the
     * result of the convertion.
     */
    public void end(ReadContext context) {
        if (originalUpdater != null) {
            // create an array of appropriate type
            List values = (List) context.popBean()
            originalUpdater.update(context, values)
        }
    }

    /**
     * Construct a delegating implmentation that wraps the real bean creator
     */
    public MappingAction next(String namespace, String name, Attributes attributes, ReadContext context) {
        originalUpdater = context.getCurrentUpdater()
        MappingAction nextBindAction = BeanBindAction.INSTANCE
        beanMapping.setDelegate(nextBindAction)
        return beanMapping
    }

    /**
     * Updates a list by adding the new value
     */
    private static class ListUpdater implements Updater {
        /**
         * Singleton
         */
        private static final ListUpdater INSTANCE = new ListUpdater()

        /**
         * Update by adding the new value to the list
         */
        public void update(Context context, Object newValue) {
            List values = (List) context.getBean()
            //noinspection unchecked
            values.add(newValue)
        }

    }

    @SuppressWarnings("UnnecessaryQualifiedReference")
    private static final class BeanMapping extends MappingAction.Base {
        private MappingAction delegate

        BeanMapping() {
        }

        /**
         * Sets the action to which the bean binding is delegated.
         *
         * @param action <code>MappingAction</code> delegate, not null
         */
        void setDelegate(MappingAction action) {
            delegate = action
        }

        /**
         * Push updater and then delegate
         */
        public MappingAction begin(String namespace, String name, Attributes attributes, ReadContext context) {
            context.pushUpdater(ListUpdater.INSTANCE)
            delegate = delegate.begin(namespace, name, attributes, context)
            return this
        }

        /**
         * Delegate to delegate (Doh!)
         */
        public void body(String text, ReadContext context) {
            delegate.body(text, context)
        }

        /**
         * Call delegate then pop <code>Updater</code>
         */
        public void end(ReadContext context) {
            delegate.end(context)
            context.popUpdater()
        }

        /**
         * Use delegate to create next action
         */
        public MappingAction next(String namespace, String name, Attributes attributes, ReadContext context) {
            return delegate.next(namespace, name, attributes, context)
        }
    }
}
