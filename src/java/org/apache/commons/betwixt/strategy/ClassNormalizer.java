/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/strategy/ClassNormalizer.java,v 1.2 2003/10/05 14:10:35 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/05 14:10:35 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
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

/** 
 * <p>Class normalization strategy.</p>
 *
 * <p>
 * The normalized Class is the Class that Betwixt should 
 * introspect.
 * This strategy class allows the introspected Class to be 
 * varied.
 * This implementation simply returns the Class given.
 * </p>
 *
 * <p>
 * Used by Betwixt to allow superclasses or interfaces to be subsittuted
 * before an object is introspected. 
 * This allows users to feed in logical interfaces and make Betwixt ignore
 * properties other than those in the interface.
 * It also allows support for <code>Proxy</code>'s.
 * Together, these features allow Betwixt to deal with Entity Beans
 * properly by viewing them through their remote interfaces.
 * </p>
 * @author Robert Burrell Donkin
 * @version $Revision: 1.2 $
 */
public class ClassNormalizer {

    /** 
      * Gets the normalized class for the given Object.
      * The normalized Class is the Class that Betwixt should 
      * introspect. 
      * This strategy class allows the introspected Class to be 
      * varied.
      *
      * @param object the <code>Object</code> 
      * for which the normalized Class is to be returned.
      * @return the normalized Class
      */
    public Class getNormalizedClass( Object object ) {
        if ( object == null ) {
            throw new IllegalArgumentException("Cannot get class for null object.");
        }
        return normalize( object.getClass() );
    }

    /**
      * Normalize given class.
      * The normalized Class is the Class that Betwixt should 
      * introspect. 
      * This strategy class allows the introspected Class to be 
      * varied.
      *
      * @param clazz the class to normalize, not null
      * @return this implementation the same clazz, 
      * subclasses may return any compatible class.
      */
    public Class normalize( Class clazz ) {
        return clazz;
    }
}
