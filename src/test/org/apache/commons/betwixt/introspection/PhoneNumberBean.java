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


package org.apache.commons.betwixt.introspection;

/**
 * Bean models a telephone number entry in a phone book.
 * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
 * @version $Revision$
 */
public class PhoneNumberBean {
   private String phoneNumber;
   private String name;
   //TODO: replace with enumerated type
   private String type;

   public PhoneNumberBean() {
   }

   public PhoneNumberBean(String phoneNumber, String type) {
      setPhoneNumber(phoneNumber);
      setType(type);
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String string) {
      phoneNumber = string;
   }


   public String getType() {
      return type;
   }

   public void setType(String string) {
      type = string;
   }

   public String getName() {
      return name;
   }

   public void setName(String string) {
      name = string;
   }

}


