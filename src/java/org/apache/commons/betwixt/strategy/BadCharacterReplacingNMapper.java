/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/strategy/BadCharacterReplacingNMapper.java,v 1.2 2003/10/05 14:10:42 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/05 14:10:42 $
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

import org.apache.commons.betwixt.XMLUtils;

/**
 * <code>NameMapper</code> implementation that processes a name by replacing or stripping
 * illegal characters before passing result down the chain.
 * 
 * @author Robert Burrell Donkin
 * @version $Revision: 1.2 $
 */
public class BadCharacterReplacingNMapper implements NameMapper {
    /** Next mapper in chain, possibly null */
    private NameMapper chainedMapper;
    /** Replacement character, possibly null */
    private Character replacement = null;
    
    /**
      * Constructs a replacing mapper which delegates to given mapper.
      * @param chainedMapper next link in processing chain, possibly null
      */
    public BadCharacterReplacingNMapper(NameMapper chainedMapper) {
        this.chainedMapper = chainedMapper;
    }	

    /**
      * Gets the character that should be used to replace bad characters
      * if null then bad characters will be deleted.
      * @return the replacement Character possibly null
      */
    public Character getReplacement() {
        return replacement;
    }
    
    /**
      * Sets the character that should be used to replace bad characters.
      * @param replacement the Charcter to be used for replacement if not null.
      * Otherwise, indicates that illegal characters should be deleted.
      */
    public void setReplacement( Character replacement ) {
        this.replacement = replacement;
    } 

    /**
     * This implementation processes characters which are not allowed in xml
     * element names and then returns the result from the next link in the chain.
     * This processing consists of deleting them if no replacement character
     * has been set. 
     * Otherwise, the character will be replaced.
     *  
     * @param typeName the string to convert 
     * @return the processed input
     */
    public String mapTypeToElementName(String typeName) {
        
        StringBuffer buffer = new StringBuffer( typeName );
        for (int i=0, size = buffer.length(); i< size; i++) {
            char nextChar = buffer.charAt( i );
            boolean bad = false;
            if ( i==0 ) {
                bad = !XMLUtils.isNameStartChar( nextChar );
            } else {
                bad = !XMLUtils.isNameChar( nextChar );
            }
                
            if (bad) {
                if ( replacement != null ) {
                    buffer.setCharAt( i, replacement.charValue() );
                } else {
                    // delete
                    buffer.deleteCharAt( i );
                    i--;
                    size--;
                }
            }
        }
        
        if ( buffer.length() == 0 ) {
            throw new IllegalArgumentException(
"Element name contains no legal characters and no replacements have been set.");
        }
        
        typeName = buffer.toString();
        
        if ( chainedMapper == null ) {
            return typeName;
        }
        return chainedMapper.mapTypeToElementName( typeName );
    }
}
