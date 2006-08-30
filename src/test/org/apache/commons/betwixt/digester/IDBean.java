
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.apache.commons.betwixt.digester;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Bean for testing ID-IDRef reading.
  *
  * @author Robert Burrell Donkin
  * @version $Revision$
  */
public class IDBean {
    
    static Log log = LogFactory.getLog( IDBean.class );
    
    private String id;
    private String name;
    
    private IDBean child;
    
    private List children = new ArrayList();
    
    public IDBean() { log.debug("Created"); }
    
    public IDBean(String id, String name) {
        setId(id);
        setName(name);
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }	
    
    public void setName(String name) {
        log.debug("Set name: " + name);
        this.name = name;
    }

    public List getChildren() {
        return children;
    }
    
    public void addChild(IDBean child) {
        log.debug("Added child " + child + " to bean " + this);
        children.add(child);
    }
    
    public String toString() {
        return "IDBean[name=" + getName() + ",id=" + getId() + ", children=" + children.size() + "] " + super.toString();
    }
}
