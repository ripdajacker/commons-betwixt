/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/strategy/DefaultPluralStemmer.java,v 1.3 2002/09/20 14:00:41 jvanzyl Exp $
 * $Revision: 1.3 $
 * $Date: 2002/09/20 14:00:41 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * $Id: DefaultPluralStemmer.java,v 1.3 2002/09/20 14:00:41 jvanzyl Exp $
 */
package org.apache.commons.betwixt.strategy;

import org.apache.commons.betwixt.ElementDescriptor;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * A default implementation of the plural name stemmer which
 * tests for some common english plural/singular patterns and
 * then uses a simple starts-with algorithm 
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Revision: 1.3 $
 */
public class DefaultPluralStemmer implements PluralStemmer {

    /** Log used for logging (Doh!) */
    protected static Log log = LogFactory.getLog( DefaultPluralStemmer.class );

    /**
     * @return the plural descriptor for the given singular property name
     */
    public ElementDescriptor findPluralDescriptor( String propertyName, Map map) {
        int foundKeyCount = 0;
        String keyFound = null;
        ElementDescriptor answer = (ElementDescriptor) map.get( propertyName + "s" );

        if ( answer == null && !propertyName.endsWith( "y" )) {
            answer = (ElementDescriptor) map.get( propertyName + "es" );
        }

        if ( answer == null ) {
            int length = propertyName.length();
            if ( propertyName.endsWith( "y" ) && length > 1 ) {
                String key = propertyName.substring(0, length - 1) + "ies";                
                answer = (ElementDescriptor) map.get( key );             
            }
            
            if ( answer == null ) {
                // lets find the first one that starts with the propertyName
                for ( Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
                    String key = (String) iter.next();
                    if ( key.startsWith( propertyName ) ) {
                        if (answer == null) {
                            answer = (ElementDescriptor) map.get(key);
                            if (key.equals(propertyName)) {
                                // we found the best match..
                                break;
                            }
                            foundKeyCount++;
                            keyFound = key;
                        }
                        else
                        {
                            // check if we have a better match,,
                            if (keyFound.length() > key.length()) {
                                answer = (ElementDescriptor) map.get(key);
                                keyFound = key;
                            }
                            foundKeyCount++;

                        }
                    }
                }
            }
        }
        if (foundKeyCount > 1) {
            log.warn("More than one type matches, using closest match "+keyFound);
        }
        return answer;
        
    }
}
