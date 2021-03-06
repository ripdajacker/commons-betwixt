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

import org.apache.commons.betwixt.AddressBean;

/**
 * @author <a href='http://commons.apache.org/'>Apache Commons Team</a>
 * @version $Revision$
 */
public class CompanyBean {

   private String name;
   private AddressBean registeredAddress;

   public CompanyBean() {
   }

   public CompanyBean(String name, AddressBean address) {
      setName(name);
      setRegisteredAddress(address);
   }


   public String getName() {
      return name;
   }

   public AddressBean getRegisteredAddress() {
      return registeredAddress;
   }

   public void setName(String string) {
      name = string;
   }

   public void setRegisteredAddress(AddressBean bean) {
      registeredAddress = bean;
   }

}
