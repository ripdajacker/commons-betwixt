package org.apache.commons.betwixt.registry;

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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.betwixt.XMLBeanInfo;

/** The default caching implementation.
  * A hashmap is used.
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @version $Id$
  */
public class DefaultXMLBeanInfoRegistry implements XMLBeanInfoRegistry {

    /** Used to associated <code>XMLBeanInfo</code>'s to classes */
    private Map xmlBeanInfos = new HashMap();
    
    /**
      * Get <code>XMLBeanInfo</code> from cache. 
      *
      * @param forThisClass the class for which to find a <code>XMLBeanInfo</code>
      * @return cached <code>XMLBeanInfo</code> associated with given class
      * or <code>null</code> if no <code>XMLBeanInfo</code> has been associated
      */
    public XMLBeanInfo get(Class forThisClass) {
        return (XMLBeanInfo) xmlBeanInfos.get(forThisClass);
    }
    
    /**
      * Put into cache
      *
      * @param forThisClass the class to cache the <code>XMLBeanInfo</code> for
      * @param beanInfo the <code>XMLBeanInfo</code> to cache
      */
    public void put(Class forThisClass, XMLBeanInfo beanInfo) {
        xmlBeanInfos.put(forThisClass, beanInfo);
    }
    
    /**
      * Flush existing cached <code>XMLBeanInfo</code>'s.
      */
    public void flush() {
        xmlBeanInfos.clear();
    }
}
