/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/id/AbstractIDGenerator.java,v 1.4 2003/01/06 22:50:44 rdonkin Exp $
 * $Revision: 1.4 $
 * $Date: 2003/01/06 22:50:44 $
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
 * $Id: AbstractIDGenerator.java,v 1.4 2003/01/06 22:50:44 rdonkin Exp $
 */
package org.apache.commons.betwixt.io.id;

import org.apache.commons.betwixt.io.IDGenerator;

/** <p>Abstract superclass for {@link IDGenerator} implementations.</p>
  *
  * <p>It implements the entire <code>IDGenerator</code> interface.
  * When <code>nextId</code> is called, 
  * this class sets the <code>LastId</code> property (as well
  * as returning the value).
  * Subclasses should override {@link #nextIdImpl}.</p>
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @version $Revision: 1.4 $
  */
public abstract class AbstractIDGenerator implements IDGenerator {
    
    /** Last <code>ID</code> returned */
    private String lastId = "0";
    
    /** 
     * Gets last <code>ID</code> returned. 
     *
     * @return the last id created by the generated
     */
    public final String getLastId() {
        return lastId;
    }
    
    /** 
      * <p>Generate next <code>ID</code>.</p>
      *
      * <p>This method obtains the next <code>ID</code> from subclass
      * and then uses this to set the <code>LastId</code> property.</p>
      *
      * @return the next id generated 
      */
    public final String nextId() {
        lastId = nextIdImpl();
        return lastId;
    }
    
    /** 
      * Subclasses should <strong>provide an implementation</strong> for this method.
      * This implementation needs only provide the next <code>ID</code>
      * value (according to it's algorithm).
      * Setting the <code>LastId</code> property can be left to this class.
      *
      * @return the next id generated
      */
    protected abstract String nextIdImpl();
}
