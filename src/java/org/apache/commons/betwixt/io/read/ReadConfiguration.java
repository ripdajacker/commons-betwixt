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
package org.apache.commons.betwixt.io.read;

/**  
  * Stores mapping phase configuration settings that apply only for bean reading.
  *
  * @author Robert Burrell Donkin
  * @since 0.5
  */
public class ReadConfiguration {
    
    /** Chain used to create beans defaults to BeanCreationChain.createDefaultChain() */
    private BeanCreationChain beanCreationChain = BeanCreationChain.createDefaultChain();
    
    /**
      * Gets the BeanCreationChain that should be used to construct beans.
      * @return the BeanCreationChain to use, not null
      */
    public BeanCreationChain getBeanCreationChain() {
        return beanCreationChain;
    }
    
    /**
      * Sets the BeanCreationChain that should be used to construct beans.
      * @param beanCreationChain the BeanCreationChain to use, not null
      */
    public void setBeanCreationChain( BeanCreationChain beanCreationChain ) {
        this.beanCreationChain = beanCreationChain;
    }
    
}
