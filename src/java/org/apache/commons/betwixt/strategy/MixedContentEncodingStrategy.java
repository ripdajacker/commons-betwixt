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

package org.apache.commons.betwixt.strategy;

import org.apache.commons.betwixt.ElementDescriptor;

/**
 * <p>Encodes body content.
 * </p><p>
 * <strong>Usage:</strong> 
 * Used by {@link BeanWriter} to encode body content before it is written
 * into the textual output.
 * This gives flexibility in this stage allowing (for example)
 * some properties to use character escaping whilst others 
 * use <code>CDATA</code> wrapping.
 * </p>
 * <p><strong>Note:</strong> the word <code>encoding</code> here is used 
 * in the sense of escaping a sequence of character data.
 * </p>
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1 $
 */
public abstract class MixedContentEncodingStrategy {

    /**
     * The default implementation used by Betwixt.
     * This always encodes by escaping character data.
     * This is a singleton.
     */
    public static final MixedContentEncodingStrategy DEFAULT 
            = new BaseMixedContentEncodingStrategy() {
        /**
         * Always encode by escaping character data.
         */
        protected boolean encodeAsCDATA(ElementDescriptor element) {
            return false;       
        }
    };

    /**
     * Encodes the body content into a form suitable for output as 
     * (textual) xml.
     * @param bodyContent the raw (unescaped) character data, not null
     * @param element the <code>ElementDescriptor</code> describing the element
     * whose content is being encoded.
     * @return the encoded (escaped) character data, not null
     */
    public abstract String encode(String bodyContent, ElementDescriptor element);
}
