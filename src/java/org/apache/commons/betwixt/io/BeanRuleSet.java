/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/BeanRuleSet.java,v 1.16.2.1 2004/01/13 21:49:46 rdonkin Exp $
 * $Revision: 1.16.2.1 $
 * $Date: 2004/01/13 21:49:46 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
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
package org.apache.commons.betwixt.io;

import java.util.Map;

import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.digester.XMLIntrospectorHelper;
import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.io.read.BeanBindAction;
import org.apache.commons.betwixt.io.read.BodyUpdateAction;
import org.apache.commons.betwixt.io.read.MappingAction;
import org.apache.commons.betwixt.io.read.ReadConfiguration;
import org.apache.commons.betwixt.io.read.ReadContext;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

/** <p>Sets <code>Betwixt</code> digestion rules for a bean class.</p>
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
  * @version $Revision: 1.16.2.1 $
  */
public class BeanRuleSet implements RuleSet {

    /** Logger */
    private static Log log = LogFactory.getLog(BeanRuleSet.class);

    /** 
    * Set log to be used by <code>BeanRuleSet</code> instances 
    * @param aLog the <code>Log</code> implementation for this class to log to
    */
    public static void setLog(Log aLog) {
        log = aLog;
    }

    /** The base path under which the rules will be attached */
    private String basePath;
    /** The element descriptor for the base  */
    private ElementDescriptor baseElementDescriptor;
    /** The (empty) base context from which all Contexts 
    with beans are (directly or indirectly) obtained */
    private DigesterReadContext context;
    /** allows an attribute to be specified to overload the types of beans used */
    private String classNameAttribute = "className";

    /**
     * Base constructor.
     *
     * @param introspector the <code>XMLIntrospector</code> used to introspect 
     * @param basePath specifies the (Digester-style) path under which the rules will be attached
     * @param baseElementDescriptor the <code>ElementDescriptor</code> used to create the rules
     * @param baseBeanClass the <code>Class</code> whose mapping rules will be created
     * @param matchIDs should ID/IDREFs be used to match beans?
     * @deprecated use constructor which takes a ReadContext instead
     */
    public BeanRuleSet(
        XMLIntrospector introspector,
        String basePath,
        ElementDescriptor baseElementDescriptor,
        Class baseBeanClass,
        boolean matchIDs) {
        this.basePath = basePath;
        this.baseElementDescriptor = baseElementDescriptor;
        BindingConfiguration bindingConfiguration = new BindingConfiguration();
        bindingConfiguration.setMapIDs(matchIDs);
        context =
            new DigesterReadContext(
                log,
                bindingConfiguration,
                new ReadConfiguration());
        context.setRootClass(baseBeanClass);
        context.setXMLIntrospector(introspector);
    }

    /**
     * Base constructor.
     *
     * @param introspector the <code>XMLIntrospector</code> used to introspect 
     * @param basePath specifies the (Digester-style) path under which the rules will be attached
     * @param baseElementDescriptor the <code>ElementDescriptor</code> used to create the rules
     * @param baseBeanClass the <code>Class</code> whose mapping rules will be created
     * @param context the root Context that bean carrying Contexts should be obtained from, 
     * not null
     * @deprecated use the constructor which takes a ReadContext instead
     */
    public BeanRuleSet(
        XMLIntrospector introspector,
        String basePath,
        ElementDescriptor baseElementDescriptor,
        Context context) {

        this.basePath = basePath;
        this.baseElementDescriptor = baseElementDescriptor;
        this.context =
            new DigesterReadContext(context, new ReadConfiguration());
        this.context.setRootClass(
            baseElementDescriptor.getSingularPropertyType());
        this.context.setXMLIntrospector(introspector);
    }

    /**
     * Base constructor.
     *
     * @param introspector the <code>XMLIntrospector</code> used to introspect 
     * @param basePath specifies the (Digester-style) path under which the rules will be attached
     * @param baseElementDescriptor the <code>ElementDescriptor</code> used to create the rules
     * @param baseBeanClass the <code>Class</code> whose mapping rules will be created
     * @param baseContext the root Context that bean carrying Contexts should be obtained from, 
     * not null
     */
    public BeanRuleSet(
        XMLIntrospector introspector,
        String basePath,
        ElementDescriptor baseElementDescriptor,
        Class baseBeanClass,
        ReadContext baseContext) {
        this.basePath = basePath;
        this.baseElementDescriptor = baseElementDescriptor;
        this.context = new DigesterReadContext(baseContext);
        this.context.setRootClass(baseBeanClass);
        this.context.setXMLIntrospector(introspector);
    }

    /**
     * The name of the attribute which can be specified in the XML to override the
     * type of a bean used at a certain point in the schema.
     *
     * <p>The default value is 'className'.</p>
     * 
     * @return The name of the attribute used to overload the class name of a bean
     */
    public String getClassNameAttribute() {
        return context.getClassNameAttribute();
    }

    /**
     * Sets the name of the attribute which can be specified in 
     * the XML to override the type of a bean used at a certain 
     * point in the schema.
     *
     * <p>The default value is 'className'.</p>
     * 
     * @param classNameAttribute The name of the attribute used to overload the class name of a bean
     * @deprecated set the <code>ReadContext</code> property instead
     */
    public void setClassNameAttribute(String classNameAttribute) {
        context.setClassNameAttribute(classNameAttribute);
    }

    //-------------------------------- Ruleset implementation

    /** 
     * <p>Return namespace associated with this ruleset</p>
     *
     * <p><strong>Note</strong> namespaces are not currently supported.</p>
     * 
     * @return null
     */
    public String getNamespaceURI() {
        return null;
    }

    /**
     * Add rules for bean to given <code>Digester</code>.
     *
     * @param digester the <code>Digester</code> to which the rules for the bean will be added
     */
    public void addRuleInstances(Digester digester) {
        if (log.isTraceEnabled()) {
            log.trace("Adding rules to:" + digester);
        }

        context.setDigester(digester);

        // if the classloader is not set, set to the digester classloader
        if (context.getClassLoader() == null) {
            context.setClassLoader(digester.getClassLoader());
        }

        // TODO: need to think about strategy for paths
        // may need to provide a default path and then allow the user to override
        digester.addRule("!" + basePath + "/*", new ActionMappingRule());
    }

    /**
     * Single rule that is used to map all elements.
     * 
     * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
     */
    private final class ActionMappingRule extends Rule {

        /**
          * @see Rule#begin(String, String, Attributes)
          */
        public void begin(String namespace, String name, Attributes attributes)
            throws Exception {

            if (log.isTraceEnabled()) {
                int attributesLength = attributes.getLength();
                if (attributesLength > 0) {
                    log.trace("Attributes:");
                }
                for (int i = 0, size = attributesLength; i < size; i++) {
                    log.trace("Local:" + attributes.getLocalName(i));
                    log.trace("URI:" + attributes.getURI(i));
                    log.trace("QName:" + attributes.getQName(i));
                }
            }

            context.pushElement(name);
            //TODO: we should probably create the next action from the last
            MappingAction action =
                createAction(namespace, name, attributes, context);

            context.pushMappingAction(action);
        }

        private MappingAction createAction(
            String namespace,
            String name,
            Attributes attributes,
            ReadContext context)
            throws Exception {
            MappingAction result = MappingAction.EMPTY;

            // TODO: replace with declarative system
            // this is rubbish and needs to be replaced with something more declarative
            // may need to think about whether ElementDescritor would be the right place for this information
            ElementDescriptor activeDescriptor = context.getCurrentDescriptor();
            if (activeDescriptor != null) {
                Class lastMapped = context.getLastMappedClass();
                if (XMLIntrospectorHelper
                    .isPrimitiveType(activeDescriptor.getPropertyType())) {
                    // primitives are mapped to body update actions
                    result = BodyUpdateAction.INSTANCE;
                } else if (lastMapped == null) {
                    // The basic action must be a bean bind
                    result = BeanBindAction.INSTANCE;

                } else if (activeDescriptor.getUpdater() != null) {
                    Class singular = activeDescriptor.getPropertyType();
                    // TODO: this is a workaround and needs to be fixed!
                    // really, need a marker to indicate whether a element descriptor 
                    // is hollow
                    if (activeDescriptor.getElementDescriptors().length != 0
                        && XMLIntrospectorHelper.isLoopType(singular)) {
                        result = MappingAction.EMPTY;
                    } else if (singular == null) {
                        // workaround for map support
                        if ("value".equals(name)) {
                            result = BeanBindAction.INSTANCE;
                        } else {

                            result = BodyUpdateAction.INSTANCE;
                        }
                    } else {
                        if (XMLIntrospectorHelper
                            .isPrimitiveType(
                                activeDescriptor.getSingularPropertyType())) {
                            result = BodyUpdateAction.INSTANCE;

                        } else {
                            result = BeanBindAction.INSTANCE;
                        }
                    }

                } else if (context.currentMappingAction() == null) {
                    // the basic action is bean bind
                    result = BeanBindAction.INSTANCE;

                } else {
                    boolean isElementWithinLoop = false;
                    Class singularType = null;
                    Class loopType = null;
                    // TODO: this seems wrong to me
                    // the structure of the element descriptors should reflect
                    // the structure of the xml
                    XMLBeanInfo childXMLBeanInfo =
                        context.getLastMappedClassXMLBeanInfo();
                    ElementDescriptor parentDescriptor =
                        childXMLBeanInfo.getElementDescriptor().findParent(
                            activeDescriptor);
                    Class parent = parentDescriptor.getPropertyType();
                    if (XMLIntrospectorHelper.isLoopType(parent)) {
                        isElementWithinLoop = true;
                        singularType =
                            parentDescriptor.getSingularPropertyType();
                        loopType = parent;
                    }

                    //TODO: this is too much work to discover whether an element
                    //      is within a loop
                    //      should probably just mark extra elements in loops
                    //      as unmapped elements (to be ignored)
                    if (isElementWithinLoop) {
                    	//TODO: the ElementDescriptor.isPrimitiveType
                        //      does seems to work in the same way as
                    	//      the XMLIntrospectorHelper version
                    	//      Need to generalize 
                        if (XMLIntrospectorHelper
                            .isPrimitiveType(singularType)) {

                            result = BodyUpdateAction.INSTANCE;
                            
                        } else if (Map.class.isAssignableFrom(loopType)) {
                            //TODO: this is needed because there's no marking
                            // 		for element descriptors that have no
                            //		mapping  
                            result = MappingAction.EMPTY;
                        } else {
                            result = BeanBindAction.INSTANCE;
                        }
                        /*
                        if (childDescriptor.hasAttributes()) {
                            if ( log.isTraceEnabled() ) {
                                log.trace( "Element has attributes, so adding rule anyway : "
                                            + childDescriptor );
                            }
                            addRule(path,childDescriptor, context);
                        }*/
                    }
                }
            }

            return result.begin(namespace, name, attributes, context);
        }

        /**
          * @see Rule#body(String, String, String)
          */
        public void body(String namespace, String name, String text)
            throws Exception {

            log.trace("[BRS] Body with text " + text);
            if (digester.getCount() > 0) {
                MappingAction action = context.currentMappingAction();
                action.body(text, context);
            } else {
                log.trace("[BRS] ZERO COUNT");
            }
        }

        /**
        * Process the end of this element.
        */
        public void end(String namespace, String name) throws Exception {

            MappingAction action = context.popMappingAction();
            action.end(context);
            // TODO: need to think about the contract
            // should the rule or the action mapping manage the element stack?
        }

        /** 
         * Tidy up.
         */
        public void finish() {
            //
            // Clear indexed beans so that we're ready to process next document
            //
            context.clearBeans();
        }

    }

    private static class DigesterReadContext extends ReadContext {

        private Digester digester;

        /**
         * @param context
         * @param readConfiguration
         */
        public DigesterReadContext(
            Context context,
            ReadConfiguration readConfiguration) {
            super(context, readConfiguration);
            // TODO Auto-generated constructor stub
        }

        /**
         * @param bindingConfiguration
         * @param readConfiguration
         */
        public DigesterReadContext(
            BindingConfiguration bindingConfiguration,
            ReadConfiguration readConfiguration) {
            super(bindingConfiguration, readConfiguration);
        }

        /**
         * @param log
         * @param bindingConfiguration
         * @param readConfiguration
         */
        public DigesterReadContext(
            Log log,
            BindingConfiguration bindingConfiguration,
            ReadConfiguration readConfiguration) {
            super(log, bindingConfiguration, readConfiguration);
        }

        /**
         * @param log
         * @param bindingConfiguration
         * @param readConfiguration
         */
        public DigesterReadContext(ReadContext readContext) {
            super(readContext);
        }

        public Digester getDigester() {
            // TODO: replace with something better
            return digester;
        }

        public void setDigester(Digester digester) {
            // TODO: replace once moved to single Rule
            this.digester = digester;
        }

        /* (non-Javadoc)
         * @see org.apache.commons.betwixt.io.read.ReadContext#pushBean(java.lang.Object)
         */
        public void pushBean(Object bean) {
            super.pushBean(bean);
            digester.push(bean);
        }

        /* (non-Javadoc)
         * @see org.apache.commons.betwixt.io.read.ReadContext#putBean(java.lang.Object)
         */
        public Object popBean() {
            Object bean = super.popBean();
            Object top = digester.pop();
            return bean;
        }
    }

}
