/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/id/RandomIDGenerator.java,v 1.1 2002/06/10 17:53:34 jstrachan Exp $
 * $Revision: 1.1 $
 * $Date: 2002/06/10 17:53:34 $
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
 * $Id: RandomIDGenerator.java,v 1.1 2002/06/10 17:53:34 jstrachan Exp $
 */
package org.apache.commons.betwixt.io.id;

import java.util.Random;

/** <p>Generates random ids.
  * This class can generate positive-only ids (the default)
  * or it can generate a mix of negative and postive ones.
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @version $Revision: 1.1 $
  */
public final class RandomIDGenerator extends AbstractIDGenerator {
    
    /** Use simple java.util.Random as the source for our numbers */
    private Random random = new Random();
    /** Should only positive id's be generated? */
    private boolean onlyPositiveIds = true;
        
    /** Base constructor */
    public RandomIDGenerator() {} 
    
    /** Construct sets PositiveIds property */
    public RandomIDGenerator(boolean onlyPositiveIds) {
        setPositiveIds(onlyPositiveIds);
    }
    
    /** Next id implementation */
    public int nextIdImpl() {
        int next = random.nextInt();
        if (onlyPositiveIds && next<0) {
            // it's negative and we're ignoring them so get another
            return nextIdImpl();
        }
        return next;
    }
    
    /** Get whether only positive id's should be generated */
    public boolean getPositiveIds() {
        return onlyPositiveIds;
    }
    
    /** Set whether only positive id's should be generated */
    public void setPositiveIds(boolean onlyPositiveIds) {
        this.onlyPositiveIds = onlyPositiveIds;
    }
}
