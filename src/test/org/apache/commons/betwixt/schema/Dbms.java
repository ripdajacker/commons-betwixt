/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/schema/Dbms.java,v 1.2 2003/02/09 22:27:18 rdonkin Exp $
 * $Revision: 1.2 $
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
 * $Id: Dbms.java,v 1.2 2003/02/09 22:27:18 rdonkin Exp $
 */

package org.apache.commons.betwixt.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: Dbms.java,v 1.2 2003/02/09 22:27:18 rdonkin Exp $
 */
public class Dbms
{
    private String kind;
    private ArrayList dbidCollection;
    
    public Dbms()
    {
        dbidCollection = new ArrayList();
    }
    
    public Dbms(String kind) 
    {
        System.out.println("kind constructor called");
        setKind(kind);
    }
    
    public void addDbid(Dbid dbid)
    {
        dbidCollection.add(dbid);
    }
    
    public List getDbids() {
        return this.dbidCollection;
    }

    public void setKind(String kind) 
    {
        this.kind = kind;
    }
    
    public String getKind()
    {
        return this.kind;
    }
    
    public boolean equals(Object object) 
    {
        if (object == null) {
            return false;
        }
        
        if (object instanceof Dbms) {
            Dbms dbms = (Dbms) object;
            if (dbms.getKind().equals(this.getKind())) {
                int count = 0;
                Iterator it = dbms.getDbids().iterator();
                while ( it.hasNext() ) {
                    if (count >= dbidCollection.size() ) {
                        return false;
                    }
                    if (! it.next().equals( dbidCollection.get(count++) ) ) {
                        return false;
                    }
                }
                
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        return "[DBMS: name='" + getKind() + "']";
    }
}

