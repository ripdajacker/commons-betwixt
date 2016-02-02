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
package org.apache.commons.betwixt.io.read

import org.apache.commons.betwixt.ElementDescriptor
import org.xml.sax.Attributes

/**
 * Describes a mapping between an xml element and a betwixt element.
 *
 * @author Robert Burrell Donkin
 * @since 0.5
 */
public class ElementMapping {

    /** Namespace of the xml element */
    String namespace;
    /** Name of the element */
    String name;
    /** Attributes associated with this element */
    Attributes attributes;
    /** The base type of the mapped bean */
    Class type;
    /** The mapped descriptor */
    ElementDescriptor descriptor;

    public String toString() {
        return "ElementMapping[$name -> $type]"
    }
}
