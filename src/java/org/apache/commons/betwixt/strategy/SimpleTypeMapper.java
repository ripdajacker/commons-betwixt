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

import org.apache.commons.betwixt.IntrospectionConfiguration;

/**
 * Strategy for binding simple types.
 * Simple types (in xml) have no attributes or child elements.
 * For Betwixt, these are converted to and from strings
 * and these strings used to populate either attributes or element body's.
 * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
 * @version $Revision$
 */
public abstract class SimpleTypeMapper {

   /**
    * Enumerates binding options for simple types.
    * Simple types (in xml) have no attributes or child elements.
    * For Betwixt, these are converted to and from strings
    * and these strings used to populate either attributes or element body's.
    * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
    * @version $Revision$
    */
   public static class Binding {
      public static final Binding ELEMENT = new Binding(1);
      public static final Binding ATTRIBUTE = new Binding(2);

      private static final int ELEMENT_CODE = 1;
      private static final int ATTRIBUTE_CODE = 2;

      private final int code;

      private Binding(int code) {
         this.code = code;
      }


      /**
       * Implementation compatible with equals
       */
      public int hashCode() {
         return code;
      }

      /**
       * Generate something appropriate for logging.
       */
      public String toString() {
         String result = "[Binding]";
         switch (code) {
            case ELEMENT_CODE:
               result = "[Binding: ELEMENT]";
               break;

            case ATTRIBUTE_CODE:
               result = "[Binding: ATTRIBUTE]";
               break;
         }
         return result;
      }
   }

   /**
    * <p>Specifies the binding of a simple type.
    * </p><p>
    * <strong>Note:</strong> the xml name to which this property will be bound
    * cannot be known at this stage (since it depends
    * </p>
    * @param configuration the current IntrospectionConfiguration
    */
   public abstract SimpleTypeMapper.Binding bind(
           IntrospectionConfiguration configuration);
}
