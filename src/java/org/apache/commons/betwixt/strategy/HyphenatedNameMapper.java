/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/strategy/HyphenatedNameMapper.java,v 1.5 2003/01/05 17:18:32 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/01/05 17:18:32 $
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
 * $Id: HyphenatedNameMapper.java,v 1.5 2003/01/05 17:18:32 rdonkin Exp $
 */
package org.apache.commons.betwixt.strategy;



/** 
 * A name mapper which converts types to a hypenated String. So
 * a bean type of FooBar will be converted to the element name "foo-bar".
 * The name mapper can be configured to convert to upper case and to
 * use a different separator via the <code>separator</code> and 
 * <code>upperCase</code> properties, so that FooBar can be converted
 * to FOO_BAR if needed, by calling the constructor
 * <code>new HyphenatedNameMapper(true, "_")</code>.
 * 
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.5 $
 */
public class HyphenatedNameMapper implements NameMapper {

    /** the separator used to seperate words, which defaults to '-' */
    private String separator = "-";

    /** whether upper or lower case conversions should be performed */
    private boolean upperCase = false;
    
    /** 
     * Construct a hyphenated name mapper that converts the name to lower case 
     * and uses the default separator. 
     */
    public HyphenatedNameMapper() {
    }
    
    /** 
     * Construct a hyphenated name mapper with default separator.
     *
     * @param upperCase should the type name be converted (entirely) to upper case 
     */
    public HyphenatedNameMapper(boolean upperCase) {
        this.upperCase = upperCase;
    }
    
    /** 
     * Construct a hyphenated name mapper.
     *
     * @param upperCase should the type name be converted (entirely) to upper case 
     * @param separator use this string to separate the words in the name returned. 
     * The words in the bean name are deduced by relying on the standard camel's hump
     * property naming convention.
     */
    public HyphenatedNameMapper(boolean upperCase, String separator) {
        this.upperCase = upperCase;
        this.separator = separator;
    }
    
    /**
     * <p>The words within the bean name are deduced assuming the camel's hump naming
     * convention. For example, the words in <code>FooBar</code> are <code>foo</code> 
     * and <code>bar</code>.</p>
     * 
     * <p>Next convert all letter in the bean name to either upper case or lower case
     * based on the {@link #isUpperCase} property value.</p>
     *
     * <p>Then the {@link #getSeparator} property value is inserted so that it separates
     * each word.</p>
     *
     * @return the bean name converted to either upper or lower case with words separated 
     * by the separator.
     */
    public String mapTypeToElementName(String typeName) {
        
        int length = typeName.length();
        if (length == 0) {
            return "";
        }
        
        StringBuffer sb = new StringBuffer();

        sb.append(convertChar(typeName.charAt(0)));        
        
        for (int i = 1; i < length; i++) {
            if (Character.isUpperCase(typeName.charAt(i))) {
                sb.append(separator);
                sb.append(convertChar(typeName.charAt(i)));
            }
            else {
                if ( upperCase ) {
                    sb.append(convertChar(typeName.charAt(i)));
                }
                else {
                    sb.append(typeName.charAt(i));
                }
            }
        } 
        
        return sb.toString();
    }
    
    // Properties
    //-------------------------------------------------------------------------        
    /** 
     * This separator will be inserted between the words in the bean name.
     *
     * @return the separator used to seperate words, which defaults to '-' 
     */
    public String getSeparator() {
        return separator;
    }
    
    /** 
     * Sets the separator used to seperate words, which defaults to '-' 
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    
    /** 
     * Should the bean name be converted to upper case?
     * Otherwise, it will be converted to lower case.
     *
     * @return whether upper or lower case conversions should be performed, 
     * which defaults to false for lower case
     */    
    public boolean isUpperCase() {
        return upperCase;
    }
    
    /** 
     * Sets whether upper or lower case conversions should be performed,
     * which defaults to false for lower case
     */    
    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------        
    
    /** 
     * Performs type conversion on the given character based on whether
     * upper or lower case conversions are being used
     */
    protected char convertChar(char ch) {
        if ( upperCase ) {
            return Character.toUpperCase(ch);
        }
        else {
            return Character.toLowerCase(ch);
        }
    }
}
