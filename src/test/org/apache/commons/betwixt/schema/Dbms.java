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

package org.apache.commons.betwixt.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: Dbms.java,v 1.6 2004/02/28 13:38:36 yoavs Exp $
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

