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
package org.apache.commons.betwixt.io.read;

import org.apache.commons.betwixt.strategy.ActionMappingStrategy;

/**
 * Stores mapping phase configuration settings that apply only for bean reading.
 *
 * @author Robert Burrell Donkin
 * @since 0.5
 */
public class ReadConfiguration {

    /** Chain used to create beans defaults to BeanCreationChain.createDefaultChain() */
    BeanCreationChain beanCreationChain = BeanCreationChain.createDefaultChain();
    /** Pluggable strategy used to determine free mappings */
    ActionMappingStrategy actionMappingStrategy = ActionMappingStrategy.DEFAULT;


}
