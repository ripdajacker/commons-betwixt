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
 
package org.apache.commons.betwixt;


/** This is a child that has a property containing it's parent.
  *
  * @author Robert Burrell Donkin
  * @version $Revision$
  */
public class DoubleLinkedChildBean {

    private DoubleLinkedParentBean parent;
    private String name;
    
    public DoubleLinkedChildBean () {}
    
    public DoubleLinkedChildBean(DoubleLinkedParentBean parent, String name) {
        setParent(parent);
        setName(name);
    }

    public DoubleLinkedParentBean getParent() {
        return parent;
    }	
    
    public void setParent(DoubleLinkedParentBean parent) {
        this.parent = parent;
    }	
    
    public String getName() {
        return name;
    }	
    
    public void setName(String name) {
        this.name = name;
    }	
}


