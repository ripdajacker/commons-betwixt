/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/schema/SchemaDiff.java,v 1.1.2.1 2004/02/23 21:55:55 rdonkin Exp $
 * $Revision: 1.1.2.1 $
 * $Date: 2004/02/23 21:55:55 $
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

package org.apache.commons.betwixt.schema;

import java.io.PrintStream;
import java.util.Iterator;

/**
 * Helper class that prints differences between schema object models.
 * Useful for debugging.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.1.2.1 $
 */
public class SchemaDiff {
    
    private PrintStream out;
    
    public SchemaDiff() {
        this(System.err);
    }
    
    public SchemaDiff(PrintStream out) {
        this.out = out;
    }
    
    public void printDifferences(Schema one, Schema two) {
        for( Iterator it=one.getComplexTypes().iterator();it.hasNext(); ) {
            GlobalComplexType complexType = (GlobalComplexType)it.next();
            if (!two.getComplexTypes().contains(complexType)) {
                boolean matched = false;
                for (Iterator otherIter=two.getComplexTypes().iterator(); it.hasNext();) {
                    GlobalComplexType otherType = (GlobalComplexType) otherIter.next();
                    if (otherType.getName().equals(complexType.getName())) {
                        printDifferences(complexType, otherType);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    out.println("Missing Complex type: " + complexType);
                }
            }
        }          
        
    }
    
    public void printDifferences(GlobalComplexType one, GlobalComplexType two) {
        out.println("Type " + one + " is not equal to " + two);
        for (Iterator it = one.getElements().iterator(); it.hasNext();) {
            Element elementOne = (Element) it.next();
            if (!two.getElements().contains(elementOne)) {
                boolean matched = false;
                for (Iterator otherIter=two.getElements().iterator(); it.hasNext();) {
                    Element elementTwo = (Element) otherIter.next();
                    if (elementOne.getName().equals(elementTwo.getName())) {
                        printDifferences(elementOne, elementTwo);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    out.println("Missing Element: " + elementOne);
                }                
            }
        }
        for (Iterator it = one.getAttributes().iterator(); it.hasNext();) {
            Attribute attributeOne = (Attribute) it.next();
            if (!two.getAttributes().contains(attributeOne)) {
                boolean matched = false;
                for (Iterator otherIter=two.getAttributes().iterator(); it.hasNext();) {
                    Attribute attributeTwo = (Attribute) otherIter.next();
                    if (attributeTwo.getName().equals(attributeTwo.getName())) {
                        printDifferences(attributeOne, attributeTwo);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    out.println("Missing Attribute: " + attributeOne);
                }                
            }
        }
    }
    
    private void printDifferences(Attribute one , Attribute two) {
        out.println("Attribute " + one + " is not equals to " + two);
    }
    
    private void printDifferences(Element one , Element two) {
        out.println("Element " + one + " is not equals to " + two);
    }
}
