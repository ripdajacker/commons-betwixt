/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/ListOfNames.java,v 1.1 2003/02/09 22:27:18 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/02/09 22:27:18 $
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
 * $Id: ListOfNames.java,v 1.1 2003/02/09 22:27:18 rdonkin Exp $
 */
package org.apache.commons.betwixt;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/** <p>A simple collection of <code>NameBean</code>'s.</p>
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  */
public class ListOfNames {
    
    private List names = new ArrayList();
    
    public ListOfNames() {}
    
    public void addName(NameBean name) {
        names.add(name);
    }
    
    public List getNames() {
        return names;
    }
    
    public String toString() {  
        StringBuffer buffer = new StringBuffer("[");
        buffer.append("ListOfNames: ");
        boolean first = true;
        Iterator it = names.iterator();
        while ( it.hasNext() ) {
            if ( first ) {
                first = !first;
            } else {
                buffer.append(',');
            }
            buffer.append("'");
            buffer.append( ((NameBean) it.next()).getName() );
            buffer.append("'");
        }
        
        buffer.append("]");
        
        return buffer.toString();
    }
    
    public boolean equals( Object obj ) {
        if ( obj == null ) return false;
        if (obj instanceof ListOfNames) {
            ListOfNames otherList = (ListOfNames) obj;
            int count = 0;
            Iterator it = otherList.getNames().iterator();
            while (it.hasNext()) {
                if (! names.get(count++).equals(it.next())) {
                    return false;
                }
            }
                        
            return true;
        }
        
        return false;
    }
}