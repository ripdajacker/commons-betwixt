/*
 * Copyright 2004 The Apache Software Foundation.
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
 
package org.apache.commons.betwixt.io.read.impl;
import junit.framework.TestCase;
import org.apache.commons.betwixt.PartyBean;
import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.read.BeanCreationList;

import java.net.URL;
import java.util.Date;
/**
 * Test the IncludeBeanCreator.
 *
 * @author Brian Pugh
 */
public class TestInclude extends TestCase {

  /**
   * Test IncludeBeanCreator.
   *
   * @throws Exception if test fails.
   */
  public void testInclude() throws Exception {
    URL url = getClass().getClassLoader().getResource("org/apache/commons/betwixt/io/read/impl/party.xml");
    assertNotNull("couldn't load party xml file!", url);
    BeanReader beanReader = new BeanReader();
    beanReader.registerBeanClass(PartyBean.class);
    BeanCreationList chain = BeanCreationList.createStandardChain();
    //put in second place (let the idref creator run first)
    chain.insertBeanCreator(2, new IncludeBeanCreator(beanReader.getBindingConfiguration(),
                                                      beanReader.getReadConfiguration()));
    beanReader.getReadConfiguration().setBeanCreationChain(chain);
    PartyBean result = (PartyBean)beanReader.parse(url.toString());
    assertNotNull("Couldn't read a PartyBean!", result);
    assertEquals("didn't get a Date right!", new Date("Wed Apr 14 19:46:55 MDT 2004"), result.getDateOfParty());
    assertEquals("didn't get excuse right!", "tired", result.getExcuse());
    assertEquals("didn't get fromhour right!", 2, result.getFromHour());
    assertNotNull("didn't get venue back!", result.getVenue());
    assertEquals("didn't get venue city right!", "San Francisco", result.getVenue().getCity());
    assertEquals("didn't get venue code right!", "94404", result.getVenue().getCode());
    assertEquals("didn't get venue country right!", "USA", result.getVenue().getCountry());
    assertEquals("didn't get venue street right!", "123 Here", result.getVenue().getStreet());
  }
}

