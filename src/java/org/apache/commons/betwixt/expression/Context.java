/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.commons.betwixt.expression;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.betwixt.strategy.IdStoringStrategy;
import org.apache.commons.betwixt.strategy.ObjectStringConverter;
import org.apache.commons.betwixt.strategy.ValueSuppressionStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** <p><code>Context</code> describes the context used to evaluate
  * bean expressions.
  * This is mostly a bean together with a number of context variables.
  * Context variables are named objects.
  * In other words, 
  * a context variable associates an object with a string.</p>
  *
  * <p> Logging during expression evaluation is done through the logging
  * instance held by this class. 
  * The object initiating the evaluation should control this logging 
  * and so passing a <code>Log</code> instance is enforced by the constructors.</p>
  *
  * <p><code>Context</code> is a natural place to include shared evaluation code.
  * One of the problems that you get with object graphs is that they can be cyclic.
  * Xml cannot (directly) include cycles. 
  * Therefore <code>betwixt</code> needs to find and deal properly with cycles.
  * The algorithm used is to check the parentage of a new child.
  * If the child is a parent then that operation fails. </p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */
public class Context {

    /** Evaluate this bean */
    private Object bean;
    /** Variables map */
    private Map variables;
    /** 
     * Logging uses commons-logging <code>Log</code> 
     * named <code>org.apache.commons.betwixt</code> 
     */
    private Log log; 
    /** Configuration for dynamic binding properties */
    private BindingConfiguration bindingConfiguration;
    
    /** 
     * Construct context with default log 
     */
    public Context() {
        this( null, LogFactory.getLog( Context.class ) );
    }
    
    /** Convenience constructor sets evaluted bean and log.
      *
      * @param bean evaluate expressions against this bean
      * @param log log to this logger
      * @deprecated 0.5 use constructor which takes a BindingConfiguration
      */
    public Context(Object bean, Log log) {
        this( bean, log, new BindingConfiguration() );
    }

    
    /** Convenience constructor sets evaluted bean and log.
      *
      * @param bean evaluate expressions against this bean
      * @param log log to this logger
      * @param bindingConfiguration not null
      */
    public Context(Object bean, Log log, BindingConfiguration bindingConfiguration) {
        this( bean, new HashMap(), log,  bindingConfiguration );
    }
    
    /**
      * Construct a cloned context.
      * The constructed context should share bean, variables, log and binding configuration.
      * @param context duplicate the attributes of this bean
      */
    public Context( Context context ) {
        this(context.bean, context.variables, context.log, context.bindingConfiguration);
    }
    
    
    /** Convenience constructor sets evaluted bean, context variables and log.
      *
      * @param bean evaluate expressions against this bean 
      * @param variables context variables
      * @param log log to this logger
      * @deprecated 0.5 use constructor which takes a converter
      */
    public Context(Object bean, Map variables, Log log) {
        this( bean, variables, log, new BindingConfiguration() );
    }
    
    /** Convenience constructor sets evaluted bean, context variables and log.
      *
      * @param bean evaluate expressions against this bean 
      * @param variables context variables
      * @param log log to this logger
      * @param bindingConfiguration not null
      */
    public Context(Object bean, Map variables, Log log, BindingConfiguration bindingConfiguration) {
        this.bean = bean;
        this.variables = variables;
        this.log = log;
        this.bindingConfiguration = bindingConfiguration;
    }

    /** Returns a new child context with the given bean but the same log and variables. 
     * 
     * @param newBean create a child context for this bean
     * @return new Context with new bean but shared variables 
     */
    // TODO: need to think about whether this is a good idea and how subclasses
    // should handle this
    public Context newContext(Object newBean) {
        Context context = new Context(this);
        context.setBean( newBean );
        return context;
    }
    
    /** 
     * Gets the current bean.
     * @return the bean against which expressions are evaluated
     */
    public Object getBean() {
        return bean;
    }

    /** 
     * Set the current bean.
     * @param bean the Object against which expressions will be evaluated
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }    
    
    /** 
      * Gets context variables.
      * @return map containing variable values keyed by variable name
      */
    public Map getVariables() {
        return variables;
    }

    /** 
     * Sets context variables. 
     * @param variables map containing variable values indexed by varibable name Strings
     */
    public void setVariables(Map variables) {
        this.variables = variables;
    }    

    /** 
     * Gets the value of a particular context variable.
     * @param name the name of the variable whose value is to be returned
     * @return the variable value or null if the variable isn't set
     */
    public Object getVariable(String name) {
        return variables.get( name );
    }

    /** 
     * Sets the value of a particular context variable.
     * @param name the name of the variable
     * @param value the value of the variable
     */    
    public void setVariable(String name, Object value) {
        variables.put( name, value );
    }
    
    /** 
     * Gets the current log.  
     *
     * @return the implementation to which this class logs
     */
    public Log getLog() {
        return log;
    }

    /** 
     * Set the log implementation to which this class logs
     * 
     * @param log the implemetation that this class should log to
     */
    public void setLog(Log log) {
        this.log = log;
    }
    
    /** 
     * Gets object &lt;-&gt; string converter.
     * @return the Converter to be used for conversions, not null
     * @since 0.5 
     */
    public ObjectStringConverter getObjectStringConverter() {
        return bindingConfiguration.getObjectStringConverter();
    }
    
    /** 
     * Should <code>ID</code>'s and <code>IDREF</code> attributes 
     * be used to cross-reference matching objects? 
     *
     * @return true if <code>ID</code> and <code>IDREF</code> 
     * attributes should be used to cross-reference instances
     * @since 0.5
     */
    public boolean getMapIDs() {
        return bindingConfiguration.getMapIDs();
    }
    
    /**
     * The name of the attribute which can be specified in the XML to override the
     * type of a bean used at a certain point in the schema.
     *
     * <p>The default value is 'className'.</p>
     * 
     * @return The name of the attribute used to overload the class name of a bean
     * @since 0.5
     */
    public String getClassNameAttribute() {
        return bindingConfiguration.getClassNameAttribute();
    }

    /**
     * Sets the name of the attribute which can be specified in 
     * the XML to override the type of a bean used at a certain 
     * point in the schema.
     *
     * <p>The default value is 'className'.</p>
     * 
     * @param classNameAttribute The name of the attribute used to overload the class name of a bean
     * @since 0.5
     */
    public void setClassNameAttribute(String classNameAttribute) {
        bindingConfiguration.setClassNameAttribute( classNameAttribute );
    }
    
    /**
     * Gets the <code>ValueSuppressionStrategy</code>.
     * This is used to control the expression of attributes with certain values.
     * @return <code>ValueSuppressionStrategy</code>, not null
     */
    public ValueSuppressionStrategy getValueSuppressionStrategy() {
        return bindingConfiguration.getValueSuppressionStrategy();
    }
    
    /**
     * Sets the <code>ValueSuppressionStrategy</code>.
     * This is used to control the expression of attributes with certain values.
     * @param valueSuppressionStrategy <code>ValueSuppressionStrategy</code>, not null
     */
    public void setValueSuppressionStrategy(
            ValueSuppressionStrategy valueSuppressionStrategy) {
        bindingConfiguration.setValueSuppressionStrategy(valueSuppressionStrategy);
    }
    
    /**
     * Gets the strategy used to manage storage and retrieval of id's.
     * 
     * @return Returns the idStoringStrategy, not null
     */
    public IdStoringStrategy getIdMappingStrategy() {
        return bindingConfiguration.getIdMappingStrategy();
    }
    
}
