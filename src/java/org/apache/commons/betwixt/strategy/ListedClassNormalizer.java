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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>ClassNormalizer that uses a list of substitutions.</p>
 * <p>
 * This <code>ClassNormalizer</code> checks a list (in order) to find a matching
 * Class.
 * This match can be performed either strictly (using equality) or taking into account
 * inheritance and implementation.
 * If a match is found then the first substituted class is returned as the normalization.
 * </p>
 *
 * @author Robert Burrell Donkin
 * @since 0.5
 */
public class ListedClassNormalizer extends ClassNormalizer {

    /**
     * Entries to be normalized
     */
    private final ArrayList normalizations = new ArrayList();
    /**
     * Should the equality (rather than isAssignabledFrom) be used to check
     */
    private final boolean strickCheck = false;

    /**
     * Adds this given substitution to the list.
     * No warning is given if the match has already been added to the list.
     *
     * @param match      if any classes matching this then the normal class will be substituted
     * @param substitute the normalized Class if the primary class is matched
     */
    private void addSubstitution(Class match, Class substitute) {
        //noinspection unchecked
        normalizations.add(new ListEntry(match, substitute));
    }

    /**
     * Adds the given substitute to the list.
     *
     * @param substitute sustitude this Class
     */
    public void addSubstitution(Class substitute) {
        addSubstitution(substitute, substitute);
    }

    /**
     * Normalize given class.
     * The normalized Class is the Class that Betwixt should
     * introspect.
     * This strategy class allows the introspected Class to be
     * varied.
     *
     * @param clazz the class to normalize, not null
     * @return this implementation check it's list of substitutations in order and returns the first that matches.
     */
    public Class normalize(Class clazz) {
        Iterator it = normalizations.iterator();
        while (it.hasNext()) {
            ListEntry entry = (ListEntry) it.next();
            //noinspection unchecked
            if (entry.match.isAssignableFrom(clazz)) {
                return entry.substitute;
            }
        }

        return clazz;
    }

    /**
     * Holds list entries
     */
    private class ListEntry {
        /**
         * Class to be check
         */
        final Class match;
        /**
         * Substituted to be returned
         */
        final Class substitute;

        /**
         * Base constructor
         *
         * @param match      match this Class
         * @param substitute substitute matches with this Class
         */
        ListEntry(Class match, Class substitute) {
            this.match = match;
            this.substitute = substitute;
        }
    }
}
