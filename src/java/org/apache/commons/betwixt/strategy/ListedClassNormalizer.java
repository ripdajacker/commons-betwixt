/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/strategy/ListedClassNormalizer.java,v 1.1 2003/09/08 13:56:47 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/09/08 13:56:47 $
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
 * $Id: ListedClassNormalizer.java,v 1.1 2003/09/08 13:56:47 rdonkin Exp $
 */
package org.apache.commons.betwixt.strategy;

import java.util.Iterator;
import java.util.ArrayList;

/** 
 * <p>ClassNormalizer that uses a list of substitutions.</p>
 * <p>
 * This <code>ClassNormalizer</code> checks a list (in order) to find a matching
 * Class. 
 * This match can be performed either strictly (using equality) or taking into account
 * inheritance and implementation.
 * If a match is found then the first substituted class is returned as the normalization.
 * </p>
 * @author Robert Burrell Donkin
 * @version $Revision: 1.1 $
 */
public class ListedClassNormalizer extends ClassNormalizer {

    /** Entries to be normalized */
    private ArrayList normalizations = new ArrayList();
    /** Should the equality (rather than isAssignabledFrom) be used to check */
    private boolean strickCheck = false;

    /**
      * Is strict checking of substitutions on?
      * @return true is equality is used to compare classes when considering substition,
      * otherwise isAssignableFrom will be used so that super classes and super interfaces 
      * will be matched.
      */
    public boolean isStrickCheck() {
        return strickCheck;
    }

    /**
      * Sets strict checking of substitutions?
      * @param strickCheck if true then equality will be used to compare classes 
      * when considering substition,
      * otherwise isAssignableFrom will be used so that super classes and super interfaces 
      * will be matched.
      */
    public void setStrickCheck(boolean strickCheck) {
        this.strickCheck = strickCheck;
    }

    /**
      * Adds this given substitution to the list.
      * No warning is given if the match has already been added to the list.
      * @param match if any classes matching this then the normal class will be substituted
      * @param substitute the normalized Class if the primary class is matched
      */
    public void addSubstitution( Class match, Class substitute ) {
        normalizations.add( new ListEntry( match, substitute ));
    }
    
    /**
      * Adds the given substitute to the list.
      * This is a convenience method useful when {@link isStrickCheck} is false.
      * In this case, any subclasses (if this is a class) or implementating classes
      * if this is an interface) will be subsituted with this value.
      * @param substitute sustitude this Class
      */
    public void addSubstitution( Class substitute ) {
        addSubstitution( substitute, substitute );
    }

    /**
      * Normalize given class.
      * The normalized Class is the Class that Betwixt should 
      * introspect. 
      * This strategy class allows the introspected Class to be 
      * varied.
      *
      * @param clazz the class to normalize, not null
      * @return this implementation check it's list of substitutations in order
      * and returns the first that matchs. If {@link #isStrickCheck} then equality 
      * is used otherwise isAssignableFrom is used (so that super class and interfaces are matched).
      */
    public Class normalize( Class clazz ) {
        Iterator it = normalizations.iterator();
        while ( it.hasNext() ) {
            ListEntry entry = (ListEntry) it.next();
            if ( strickCheck ) {
                if ( entry.match.equals( clazz ) ) {
                    return entry.substitute;
                }
            } else {
                if ( entry.match.isAssignableFrom( clazz )) {
                    return entry.substitute;
                }
            }
        }
        
        return clazz;
    }
    
    /** Holds list entries */
    private class ListEntry {        
        /** Class to be check */
        Class match;
        /** Substituted to be returned */
        Class substitute;
        
        /** 
          * Base constructor 
          * @param match match this Class
          * @param subsistute substitute matches with this Class
          */
        ListEntry( Class match, Class substitute ) {
            this.match = match;
            this.substitute = substitute;
        }
    }
}
