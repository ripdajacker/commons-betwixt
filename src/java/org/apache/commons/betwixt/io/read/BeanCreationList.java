/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/read/BeanCreationList.java,v 1.1 2003/08/21 22:38:17 rdonkin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/08/21 22:38:17 $
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
 * $Id: BeanCreationList.java,v 1.1 2003/08/21 22:38:17 rdonkin Exp $
 */
package org.apache.commons.betwixt.io.read;

import java.util.ArrayList;
import java.util.Iterator;

/**  
  * <p>Chain implementation that's backed by a list.
  * This is the default implementation used by Betwixt.
  * </p><p>
  * <strong>Note</strong> this implementation is <em>not</em>
  * intended to allow multiple threads of execution to perform
  * modification operations concurrently with traversal of the chain.
  * Users who require this behaviour are advised to create their own implementation.
  * </p>
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.1 $
  */
public class BeanCreationList extends BeanCreationChain {
    
//-------------------------------------------------------- Class Methods

    /** 
     * Creates the default <code>BeanCreationChain</code> used when reading beans.
     * @return a <code>BeanCreationList</code> with the default creators loader in order, not null
     */
    public static final BeanCreationList createStandardChain() {
        BeanCreationList chain = new BeanCreationList();
        chain.addBeanCreator( ChainedBeanCreatorFactory.createIDREFBeanCreator() );
        chain.addBeanCreator( ChainedBeanCreatorFactory.createDerivedBeanCreator() );
        chain.addBeanCreator( ChainedBeanCreatorFactory.createElementTypeBeanCreator() );
        return chain;
    }
    

    
//-------------------------------------------------------- Attributes
    /** The list backing this chain */
    private ArrayList beanCreators = new ArrayList();
    
//-------------------------------------------------------- Methods
    
    /**
      * Creates an Object based on the given element mapping and read context.
      * Delegates to chain.
      *
      * @param elementMapping the element mapping details
      * @param readContext create against this context 
      * @return the created bean, possibly null
      */
    public Object create( ElementMapping elementMapping, ReadContext readContext ) {
        ChainWorker worker = new ChainWorker();
        return worker.create( elementMapping, readContext );
    }
    
//-------------------------------------------------------- Properties
    
    /**
      * Gets the number of BeanCreators in the wrapped chain.
      * @param the number of <code>Bean</code> in the current chain
      */
    public int getSize() {
        return beanCreators.size();
    }
    
    /**
      * Inserts a <code>BeanCreator</code> at the given position in the chain.
      * Shifts the object currently in that position - and any subsequent elements -
      * to the right.
      *
      * @param index index at which the creator should be inserted
      * @param beanCreator the <code>BeanCreator</code> to be inserted, not null
      * @throws IndexOutOfBoundsException if the index is out of the range <code>(index < 0 || index > getSize())
      */
    public void insertBeanCreator(
                                int index, 
                                ChainedBeanCreator beanCreator ) 
                                    throws IndexOutOfBoundsException {
        beanCreators.add( index, beanCreator );
    }
    
    /**
      * Adds a <code>BeanCreator</code> to the end of the chain.
      * @param beanCreator the <code>BeanCreator</code> to be inserted, not null
      */
    public void addBeanCreator( ChainedBeanCreator beanCreator ) {
        beanCreators.add( beanCreator );
    }
    
    /** 
      * Clears the creator chain.
      */
    public void clearBeanCreators() {
        beanCreators.clear();
    }
     
    /** Worker class walks a chain */
    private class ChainWorker extends BeanCreationChain {
        /** Iterator for the creator list */
        Iterator iterator;
        /** Creates the iterator */
        ChainWorker() {
            iterator = beanCreators.iterator();
        }
    
        /**
          * see BeanCreationChain#create
          */
        public Object create( ElementMapping elementMapping, ReadContext readContext ) {
            if ( iterator.hasNext() ) {
                ChainedBeanCreator beanCreator = (ChainedBeanCreator) iterator.next();
                return beanCreator.create( elementMapping, readContext, this );
            }
            
            return null;
        }
    }
}
