/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/digester/RuleSupport.java,v 1.3 2003/01/07 22:32:57 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/01/07 22:32:57 $
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
 * 
 * $Id: RuleSupport.java,v 1.3 2003/01/07 22:32:57 rdonkin Exp $
 */
package org.apache.commons.betwixt.digester;

import java.util.Set;

import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** <p><code>RuleSupport</code> is an abstract base class containing useful
  * helper methods.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.3 $
  */
public class RuleSupport extends Rule {

    /** Logger */
    private static final Log log = LogFactory.getLog( RuleSupport.class );
    /** Base constructor */
    public RuleSupport() {
    }
    
    

    // Implementation methods
    //-------------------------------------------------------------------------    
    /** 
     * Gets <code>XMLBeanInfoDigester</code> using this rule.
     *
     * @return <code>XMLBeanInfoDigester</code> for this rule
     */
    protected XMLBeanInfoDigester getXMLInfoDigester() {
        return (XMLBeanInfoDigester) getDigester();
    }
    
     /** 
     * Gets <code>XMLIntrospector</code> to be used for introspection
     *
     * @return <code>XMLIntrospector</code> to use
     */
    protected XMLIntrospector getXMLIntrospector() {
        return getXMLInfoDigester().getXMLIntrospector();
    }
    
    /** 
     * Gets the class of the bean whose .betwixt file is being digested
     *
     * @return the <code>Class</code> of the bean being processed 
     */
    protected Class getBeanClass() {
        return getXMLInfoDigester().getBeanClass();
    }
    
    /** 
     * Gets the property names already processed
     *
     * @return the set of property names that have been processed so far 
     */
    protected Set getProcessedPropertyNameSet() {
        return getXMLInfoDigester().getProcessedPropertyNameSet();
    }
}
