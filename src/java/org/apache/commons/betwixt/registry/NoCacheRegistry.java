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
 
import org.apache.commons.betwixt.XMLBeanInfo;

/** This plug-in registry does not cache at all.
  * In effect, this turns caching off.
  *
  * @author <a href="mailto:rdonkin@apache.org">Robert Burrell Donkin</a>
  * @version $Id: NoCacheRegistry.java,v 1.6 2004/02/28 13:38:33 yoavs Exp $
  */
public final class NoCacheRegistry implements XMLBeanInfoRegistry {
    
    /** Always return null.
      *
      * @param forThisClass this parameter is ignored
      * @return <code>null</code>
      */
    public XMLBeanInfo get(Class forThisClass) {
        return null;
    }
    
    /** 
     * Do nothing (this implementation does not cache)
     *
     * @param forThisClass the class is ignored
     * @param beanInfo the <code>XMLBeanInfo</code> is ignored
     */
    public void put(Class forThisClass, XMLBeanInfo beanInfo) {}
    
    /**
     * Nothing cached so nothing to flush 
     *
     * @see org.apache.commons.betwixt.registry.XMLBeanInfoRegistry#flush()
     * 
     */
    public void flush() {
    }

}
