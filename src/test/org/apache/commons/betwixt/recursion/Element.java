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
package org.apache.commons.betwixt.recursion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is just a simple element bean..
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id: Element.java,v 1.6 2004/02/28 13:38:36 yoavs Exp $
 */
public class Element
{
    ArrayList elements;
    String name;
   

    /**
     * Constructor for ElementBean.
     */
    public Element()
    {
        elements = new ArrayList();
    }
    
    public Element(String name) 
    {
        this();
        setName(name);
    }
    
    public void addElement(Element element)
    {
        elements.add(element);
    }
    
    public List getElements()
    {
        return elements;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer(getName() + "==>list: ");
        Iterator it = getElements().iterator();
        boolean first=true;
        while (it.hasNext()) {
            Element element = (Element) it.next();
            if (first) {
                first = false;
            } else {
                buffer.append(",");
            }
            buffer.append(element.getName());
        }	
        return buffer.toString();
    }
        

}
