/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/expression/Context.java,v 1.1 2002/06/10 17:53:33 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2002/06/10 17:53:33 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * $Id: Context.java,v 1.1 2002/06/10 17:53:33 jstrachan Exp $
 */
package org.apache.commons.betwixt.expression;

import java.util.HashMap;
import java.util.Map;

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
  * @version $Revision: 1.1 $
  */
public class Context {

    /** Evaluate this bean */
    private Object bean;
    /** Variables map */
    private Map variables = new HashMap();
    /** Logging uses commons-logging <code>Log</code> named <code>org.apache.commons.betwixt</code> */
    private Log log; 
    
    /** Construct context with default log */
    public Context() {
        this.log = LogFactory.getLog( getClass() );
    }
    
    /** Convenience constructor sets evaluted bean and log.
      *
      * @param bean evaluate expressions against this bean
      * @param log log to this logger
      */
    public Context(Object bean, Log log) {
        this.bean = bean;
        this.log = log;
    }
    
    /** Convenience constructor sets evaluted bean, context variables and log.
      *
      * @param bean evaluate expressions against this bean 
      * @param variables context variables
      * @param log log to this logger
      */
    public Context(Object bean, Map variables, Log log) {
        this.bean = bean;
        this.variables = variables;
        this.log = log;
    }

    /** Returns a new child context with the given bean but the same log and variables. 
     *
     * @param newBean create a child context for this bean
     */
    public Context newContext(Object newBean) {
        return new Context(newBean, variables, log);
    }
    
    /** Returns the current bean.
      */
    public Object getBean() {
        return bean;
    }

    /** Set the current bean.
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }    
    
    /** Get context variables.
     */
    public Map getVariables() {
        return variables;
    }

    /** Set context variables. 
     */
    public void setVariables(Map variables) {
        this.variables = variables;
    }    

    /** Get the value of a particular context variable.
     */
    public Object getVariable(String name) {
        return variables.get( name );
    }

    /** Set the value of a particular context variable.
     */    
    public void setVariable(String name, Object value) {
        variables.put( name, value );
    }
    
    /** Get the current log.  
      */
    public Log getLog() {
        return log;
    }

    /** Set the logger used to log (Doh!).
     */
    public void setLog() {
        this.log = log;
    }
}
