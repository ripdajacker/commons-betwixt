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

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: DbDataType.java,v 1.5 2004/02/28 13:38:36 yoavs Exp $
 */
public class DbDataType
{
    private String name;

    public DbDataType()
    {
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String toString() {
        return getName();
    }
    
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        
        if (object instanceof DbDataType) {
            if (object.toString().equals(this.toString())) {
                return true;
            }
        }
        return false;
    }
        
}

