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
import org.apache.commons.betwixt.AttributeDescriptor
import org.apache.commons.betwixt.ElementDescriptor
import org.xml.sax.Attributes

/**
 * Executes mapping action for a subgraph.
 * It is intended that most MappingAction's will not need to maintain state.
 *
 * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
 * @version $Revision$
 */
@SuppressWarnings("UnnecessaryQualifiedReference")
@TypeChecked
public abstract class MappingAction {
    public abstract MappingAction next(String namespace, String name, Attributes attributes, ReadContext context)

    /**
     * Executes mapping action on new element.
     *
     * @param namespace
     * @param name
     * @param attributes Attributes not null
     * @param context Context not null
     * @return the MappingAction to be used to map the sub-graph
     * under this element

     */
    public abstract MappingAction begin(String namespace, String name, Attributes attributes, ReadContext context)

    /**
     * Executes mapping action for element body text
     *
     * @param text
     * @param context

     */
    public abstract void body(String text, ReadContext context)

    /**
     * Executes mapping action one element ends
     *
     * @param context

     */
    public abstract void end(ReadContext context)

    public static final MappingAction EMPTY = new MappingAction.Base()

    public static final MappingAction IGNORE = new MappingAction.Ignore()

    @TypeChecked
    private static final class Ignore extends MappingAction {
        public MappingAction next(String namespace, String name, Attributes attributes, ReadContext context) {
            return this
        }

        public MappingAction begin(String namespace, String name, Attributes attributes, ReadContext context) {
            return this
        }

        public void body(String text, ReadContext context) {
            // do nothing
        }

        public void end(ReadContext context) {
            // do nothing
        }

    }

    /**
     * Basic action.
     *
     * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
     * @version $Revision$
     */
    public static class Base extends MappingAction {
        public MappingAction next(String namespace, String name, Attributes attributes, ReadContext context) {
            return context.getActionMappingStrategy().getMappingAction(namespace, name, attributes, context)
        }

        /**
         * @see org.apache.commons.betwixt.io.read.MappingAction#begin(String, String, Attributes, ReadContext)
         */
        public MappingAction begin(String namespace, String name, Attributes attributes, ReadContext context) {
            ElementDescriptor descriptor = context.getCurrentDescriptor()
            if (descriptor != null) {
                List<AttributeDescriptor> attributeDescriptors = descriptor.getAttributeDescriptors()
                context.populateAttributes(attributeDescriptors, attributes)
            }
            return this
        }

        public void body(String text, ReadContext context) {
            // do nothing
        }

        public void end(ReadContext context) {
            context.popElement()
        }

    }
}
