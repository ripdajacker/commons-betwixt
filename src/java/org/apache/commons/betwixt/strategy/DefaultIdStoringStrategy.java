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

package org.apache.commons.betwixt.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Stores every ID that given to it into an internal <code>HashMap</code> and
 * returns it on request.
 * </p><p>
 * {@link #DefaultIdStoringStrategy(Map, Map)} allows the implementations
 * to be specified.
 * For example, those who want to use identity (rather than equality)
 * should pass a <code>IdentityHashMap</code> instance.
 * </p>
 *
 * @author <a href="mailto:christian@wilde-welt.de">Christian Aust</a>
 * @since 0.7
 */
public class DefaultIdStoringStrategy extends IdStoringStrategy {
   private final Map<Object, String> idByBeanMap = new HashMap<>();
   private final Map<String, Object> beanByIdMap = new HashMap<>();


   /**
    * Returns a String id for the given bean if it has been stored previously.
    * Otherwise returns null.
    *
    * @param bean
    *            the instance, not null
    * @return id as String, or null if not found
    * @see #getReferenceFor(Object)
    */
   public String getReferenceFor(Object bean) {
      return idByBeanMap.get(bean);
   }

   /**
    * Stores an ID for the given instance and context. It will check first if
    * this ID has been previously stored and will do nothing in that case.
    *
    * @param bean
    *            current instance, not null
    * @param id
    *            the ID to store
    * @see #setReference(Object, String)
    */
   public void setReference(Object bean, String id) {
      if (!idByBeanMap.containsKey(bean)) {
         idByBeanMap.put(bean, id);
         beanByIdMap.put(id, bean);
      }
   }

   /**
    * Gets an object matching the given reference.
    * @param id the reference id
    * @return an bean matching the given reference,
    * or null if there is no bean matching the given reference
    */
   public Object getReferenced(String id) {
      return beanByIdMap.get(id);
   }

   /**
    * Clears all beans.
    */
   public void reset() {
      idByBeanMap.clear();
      beanByIdMap.clear();
   }
}
