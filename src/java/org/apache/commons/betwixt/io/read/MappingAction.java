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
package org.apache.commons.betwixt.io.read;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.ElementDescriptor;
import org.xml.sax.Attributes;

/**
 * Executes mapping action for a subgraph.
 * It is intended that most MappingAction's will not need to maintain state.
 * 
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision$
 */
public abstract class MappingAction {

       
    public abstract MappingAction next(
        String namespace,
        String name,
        Attributes attributes,
        ReadContext context)
        throws Exception;

    /**
     * Executes mapping action on new element.
     * @param namespace
     * @param name
     * @param attributes Attributes not null
     * @param context Context not null
     * @return the MappingAction to be used to map the sub-graph 
     * under this element
     * @throws Exception
     */
    public abstract MappingAction begin(
        String namespace,
        String name,
        Attributes attributes,
        ReadContext context)
        throws Exception;

    /**
     * Executes mapping action for element body text
     * @param text
     * @param context
     * @throws Exception
     */
    public abstract void body(String text, ReadContext context)
        throws Exception;

    /**
     * Executes mapping action one element ends
     * @param context
     * @throws Exception
     */
    public abstract void end(ReadContext context) throws Exception;

    public static final MappingAction EMPTY = new MappingAction.Base();

    public static final MappingAction IGNORE = new MappingAction.Ignore();    
    
    private static final class Ignore extends MappingAction {

        public MappingAction next(String namespace, String name, Attributes attributes, ReadContext context) throws Exception {
            return this;
        }

        public MappingAction begin(String namespace, String name, Attributes attributes, ReadContext context) throws Exception {
            return this;
        }

        public void body(String text, ReadContext context) throws Exception {
            // do nothing
        }

        public void end(ReadContext context) throws Exception {
            // do nothing
        }
        
    }

    /**
     * Basic action.
     * 
     * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
     * @version $Revision$
     */
    public static class Base extends MappingAction {
        
        public MappingAction next(
            String namespace,
            String name,
            Attributes attributes,
            ReadContext context)
            throws Exception {       
        
            return context.getActionMappingStrategy().getMappingAction(namespace, name, attributes, context);
        }
        
        /* (non-Javadoc)
         * @see org.apache.commons.betwixt.io.read.MappingAction#begin(java.lang.String, java.lang.String, org.xml.sax.Attributes, org.apache.commons.betwixt.io.read.ReadContext, org.apache.commons.betwixt.XMLIntrospector)
         */
        public MappingAction begin(
            String namespace,
            String name,
            Attributes attributes,
            ReadContext context)
            throws Exception {
            // TODO: i'm not too sure about this part of the design
            // i'm not sure whether base should give base behaviour or if it should give standard behaviour
            // i'm hoping that things will become clearer once the descriptor logic has been cleared 
            ElementDescriptor descriptor = context.getCurrentDescriptor();
            if (descriptor != null) {

                AttributeDescriptor[] attributeDescriptors =
                    descriptor.getAttributeDescriptors();
                context.populateAttributes(attributeDescriptors, attributes);
            }
            return this;
        }

        /* (non-Javadoc)
         * @see org.apache.commons.betwixt.io.read.MappingAction#body(java.lang.String, org.apache.commons.betwixt.io.read.ReadContext, org.apache.commons.betwixt.XMLIntrospector)
         */
        public void body(String text, ReadContext context) throws Exception {
            // do nothing
        }

        /* (non-Javadoc)
         * @see org.apache.commons.betwixt.io.read.MappingAction#end(org.apache.commons.betwixt.io.read.ReadContext, org.apache.commons.digester.Digester, org.apache.commons.betwixt.XMLIntrospector)
         */
        public void end(ReadContext context) throws Exception {
            // do nothing
            // TODO: this is a temporary refactoring
            // it would be better to do this in the rule
            // need to move more logic into the context and out of the rule
            context.popElement();
        }

    }
}
