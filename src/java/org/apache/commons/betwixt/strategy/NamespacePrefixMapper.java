/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/strategy/NamespacePrefixMapper.java,v 1.1.2.1 2004/02/01 22:55:02 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/02/01 22:55:02 $
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

import java.util.HashMap;

/**
 * <p>Maps namespace <code>URI</code>'s to prefixes.
 * </p><p>
 * When validating xml documents including namespaces,
 * the issue of prefixes (the short expression before the colon in a universal name)
 * becomes important.
 * DTDs are not namespace aware and so a fixed prefixed must be chosen 
 * and used consistently.
 * This class is used to supply consistent, user specified prefixes.
 * </p><
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class NamespacePrefixMapper {
    
    private int count = 0;
    private HashMap prefixesByUri = new HashMap();
    
    /**
     * Gets the prefix to be used with the given namespace URI
     * @param namespaceUri
     * @return prefix, not null
     */
    public String getPrefix(String namespaceUri) {
        String prefix = (String) prefixesByUri.get(namespaceUri);    
        if (prefix == null) {
            prefix = generatePrefix(namespaceUri);
            setPrefix(namespaceUri, prefix);
        }
        return prefix;
    }
    
    /**
     * Sets the prefix to be used for the given namespace URI.
     * This method does not check for clashes amongst the namespaces.
     * Possibly it should.
     * @param namespaceUri
     * @param prefix
     */
    public void setPrefix(String namespaceUri, String prefix) {
        prefixesByUri.put(namespaceUri, prefix);
    }
    
    /**
     * Generates a prefix for the given namespace Uri.
     * Used to assign prefixes for unassigned namespaces.
     * Subclass may wish to override this method to provide more
     * sophisticated implementations. 
     * @param namespaceUri URI, not null
     * @return prefix, not null
     */
    protected String generatePrefix(String namespaceUri) {
        String prefix = "bt" + ++count;
        if (prefixesByUri.values().contains(prefix)) {
            prefix = generatePrefix(namespaceUri);
        }
        return prefix;
    }
    
}