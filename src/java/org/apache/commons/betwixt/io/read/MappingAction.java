/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/read/MappingAction.java,v 1.1.2.3 2004/02/21 17:20:06 rdonkin Exp $
 * $Revision: 1.1.2.3 $
 * $Date: 2004/02/21 17:20:06 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 * $Id: MappingAction.java,v 1.1.2.3 2004/02/21 17:20:06 rdonkin Exp $
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
 * @version $Revision: 1.1.2.3 $
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

    /**
     * Basic action.
     * 
     * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
     * @version $Revision: 1.1.2.3 $
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
