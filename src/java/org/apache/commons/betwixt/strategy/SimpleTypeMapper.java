/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/strategy/SimpleTypeMapper.java,v 1.1.2.1 2004/02/03 22:29:43 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/02/03 22:29:43 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

package org.apache.commons.betwixt.strategy;

import org.apache.commons.betwixt.IntrospectionConfiguration;

/**
 * Strategy for binding simple types.
 * Simple types (in xml) have no attributes or child elements.
 * For Betwixt, these are converted to and from strings
 * and these strings used to populate either attributes or element body's.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public abstract class SimpleTypeMapper {

    /**
     * Enumerates binding options for simple types.
     * Simple types (in xml) have no attributes or child elements.
     * For Betwixt, these are converted to and from strings
     * and these strings used to populate either attributes or element body's.
     * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
     * @version $Revision: 1.1.2.1 $
     */
    public static class Binding {
        public static final Binding ELEMENT = new Binding(1);
        public static final Binding ATTRIBUTE = new Binding(2);
        
        private static final int ELEMENT_CODE = 1;
        private static final int ATTRIBUTE_CODE = 2;
        
        private int code;
        private Binding(int code) {
            this.code = code;
        }
        
        
        /**
         * Equals compatible with the enumeration.
         */
        public boolean equals( Object obj ) {
            boolean result = false;
            if ( obj == this ) {
                result = true; 
            }
            return result;
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
     * @param propertyName the name of the property (to be bound)
     * @param propertyType the type of the property (to be bound)
     * @param configuration the current IntrospectionConfiguration
     */
    public abstract SimpleTypeMapper.Binding bind(
                                                String propertyName, 
                                                Class propertyType, 
                                                IntrospectionConfiguration configuration);
}
