/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.commons.betwixt;
 /**
  * <p><code>XMLUtils</code> contains basic utility methods for XML.</p>
  * 
  * <p>The code for {@link #isWellFormedXMLName} is based on code in 
  * <code>org.apache.xerces.util.XMLChar</code> 
  * in <a href='http://xml.apache.org/xerces2-j/index.html'>Apache Xerces</a>.
  * The authors of this class are credited below.</p>
  *
  * @author Glenn Marcy, IBM
  * @author Andy Clark, IBM
  * @author Eric Ye, IBM
  * @author Arnaud  Le Hors, IBM
  * @author Rahul Srivastava, Sun Microsystems Inc.  
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.8 $
  */
public class XMLUtils {

    // Constants
    //-------------------------------------------------------------------------   

    /** Escaped <code>&lt;</code> entity */
    public static final String LESS_THAN_ENTITY = "&lt;";
    /** Escaped <code>&gt;</code> entity */
    public static final String GREATER_THAN_ENTITY = "&gt;";
    /** Escaped <code>&amp;</code> entity */
    public static final String AMPERSAND_ENTITY = "&amp;";
    /** Escaped <code>'</code> entity */
    public static final String APOSTROPHE_ENTITY = "&apos;";
    /** Escaped <code>"</code> entity */
    public static final String QUOTE_ENTITY = "&quot;";

    // Used by isWellFormedXMLName
    /** Name start character mask. */
    private static final int MASK_NAME_START = 0x01;
    /** Name character mask. */
    private static final int MASK_NAME = 0x02;
    
    // Class attributes
    //-------------------------------------------------------------------------   
    
    /** Character flags. */
    private static final byte[] CHARS = new byte[1 << 16];

    //
    // Static initialization
    //

    static {

        //
        // [4] NameChar ::= Letter | Digit | '.' | '-' | '_' | ':' |
        //                  CombiningChar | Extender
        //

        int nameChar[] = { 
            0x002D, 0x002E, // '-' and '.'
        };

        //
        // [5] Name ::= (Letter | '_' | ':') (NameChar)*
        //

        int nameStartChar[] = { 
            0x003A, 0x005F, // ':' and '_'
        };

        //
        // [84] Letter ::= BaseChar | Ideographic
        //

        int letterRange[] = {
            // BaseChar
            0x0041, 0x005A, 0x0061, 0x007A, 0x00C0, 0x00D6, 0x00D8, 0x00F6,
            0x00F8, 0x0131, 0x0134, 0x013E, 0x0141, 0x0148, 0x014A, 0x017E,
            0x0180, 0x01C3, 0x01CD, 0x01F0, 0x01F4, 0x01F5, 0x01FA, 0x0217,
            0x0250, 0x02A8, 0x02BB, 0x02C1, 0x0388, 0x038A, 0x038E, 0x03A1,
            0x03A3, 0x03CE, 0x03D0, 0x03D6, 0x03E2, 0x03F3, 0x0401, 0x040C,
            0x040E, 0x044F, 0x0451, 0x045C, 0x045E, 0x0481, 0x0490, 0x04C4,
            0x04C7, 0x04C8, 0x04CB, 0x04CC, 0x04D0, 0x04EB, 0x04EE, 0x04F5,
            0x04F8, 0x04F9, 0x0531, 0x0556, 0x0561, 0x0586, 0x05D0, 0x05EA,
            0x05F0, 0x05F2, 0x0621, 0x063A, 0x0641, 0x064A, 0x0671, 0x06B7,
            0x06BA, 0x06BE, 0x06C0, 0x06CE, 0x06D0, 0x06D3, 0x06E5, 0x06E6,
            0x0905, 0x0939, 0x0958, 0x0961, 0x0985, 0x098C, 0x098F, 0x0990,
            0x0993, 0x09A8, 0x09AA, 0x09B0, 0x09B6, 0x09B9, 0x09DC, 0x09DD,
            0x09DF, 0x09E1, 0x09F0, 0x09F1, 0x0A05, 0x0A0A, 0x0A0F, 0x0A10,
            0x0A13, 0x0A28, 0x0A2A, 0x0A30, 0x0A32, 0x0A33, 0x0A35, 0x0A36,
            0x0A38, 0x0A39, 0x0A59, 0x0A5C, 0x0A72, 0x0A74, 0x0A85, 0x0A8B,
            0x0A8F, 0x0A91, 0x0A93, 0x0AA8, 0x0AAA, 0x0AB0, 0x0AB2, 0x0AB3,
            0x0AB5, 0x0AB9, 0x0B05, 0x0B0C, 0x0B0F, 0x0B10, 0x0B13, 0x0B28,
            0x0B2A, 0x0B30, 0x0B32, 0x0B33, 0x0B36, 0x0B39, 0x0B5C, 0x0B5D,
            0x0B5F, 0x0B61, 0x0B85, 0x0B8A, 0x0B8E, 0x0B90, 0x0B92, 0x0B95,
            0x0B99, 0x0B9A, 0x0B9E, 0x0B9F, 0x0BA3, 0x0BA4, 0x0BA8, 0x0BAA,
            0x0BAE, 0x0BB5, 0x0BB7, 0x0BB9, 0x0C05, 0x0C0C, 0x0C0E, 0x0C10,
            0x0C12, 0x0C28, 0x0C2A, 0x0C33, 0x0C35, 0x0C39, 0x0C60, 0x0C61,
            0x0C85, 0x0C8C, 0x0C8E, 0x0C90, 0x0C92, 0x0CA8, 0x0CAA, 0x0CB3,
            0x0CB5, 0x0CB9, 0x0CE0, 0x0CE1, 0x0D05, 0x0D0C, 0x0D0E, 0x0D10,
            0x0D12, 0x0D28, 0x0D2A, 0x0D39, 0x0D60, 0x0D61, 0x0E01, 0x0E2E,
            0x0E32, 0x0E33, 0x0E40, 0x0E45, 0x0E81, 0x0E82, 0x0E87, 0x0E88,
            0x0E94, 0x0E97, 0x0E99, 0x0E9F, 0x0EA1, 0x0EA3, 0x0EAA, 0x0EAB,
            0x0EAD, 0x0EAE, 0x0EB2, 0x0EB3, 0x0EC0, 0x0EC4, 0x0F40, 0x0F47,
            0x0F49, 0x0F69, 0x10A0, 0x10C5, 0x10D0, 0x10F6, 0x1102, 0x1103,
            0x1105, 0x1107, 0x110B, 0x110C, 0x110E, 0x1112, 0x1154, 0x1155,
            0x115F, 0x1161, 0x116D, 0x116E, 0x1172, 0x1173, 0x11AE, 0x11AF,
            0x11B7, 0x11B8, 0x11BC, 0x11C2, 0x1E00, 0x1E9B, 0x1EA0, 0x1EF9,
            0x1F00, 0x1F15, 0x1F18, 0x1F1D, 0x1F20, 0x1F45, 0x1F48, 0x1F4D,
            0x1F50, 0x1F57, 0x1F5F, 0x1F7D, 0x1F80, 0x1FB4, 0x1FB6, 0x1FBC,
            0x1FC2, 0x1FC4, 0x1FC6, 0x1FCC, 0x1FD0, 0x1FD3, 0x1FD6, 0x1FDB,
            0x1FE0, 0x1FEC, 0x1FF2, 0x1FF4, 0x1FF6, 0x1FFC, 0x212A, 0x212B,
            0x2180, 0x2182, 0x3041, 0x3094, 0x30A1, 0x30FA, 0x3105, 0x312C,
            0xAC00, 0xD7A3,
            // Ideographic
            0x3021, 0x3029, 0x4E00, 0x9FA5,
        };
        int letterChar[] = {
            // BaseChar
            0x0386, 0x038C, 0x03DA, 0x03DC, 0x03DE, 0x03E0, 0x0559, 0x06D5,
            0x093D, 0x09B2, 0x0A5E, 0x0A8D, 0x0ABD, 0x0AE0, 0x0B3D, 0x0B9C,
            0x0CDE, 0x0E30, 0x0E84, 0x0E8A, 0x0E8D, 0x0EA5, 0x0EA7, 0x0EB0,
            0x0EBD, 0x1100, 0x1109, 0x113C, 0x113E, 0x1140, 0x114C, 0x114E,
            0x1150, 0x1159, 0x1163, 0x1165, 0x1167, 0x1169, 0x1175, 0x119E,
            0x11A8, 0x11AB, 0x11BA, 0x11EB, 0x11F0, 0x11F9, 0x1F59, 0x1F5B,
            0x1F5D, 0x1FBE, 0x2126, 0x212E,
            // Ideographic
            0x3007,
        };

        //
        // [87] CombiningChar ::= ...
        //

        int combiningCharRange[] = {
            0x0300, 0x0345, 0x0360, 0x0361, 0x0483, 0x0486, 0x0591, 0x05A1,
            0x05A3, 0x05B9, 0x05BB, 0x05BD, 0x05C1, 0x05C2, 0x064B, 0x0652,
            0x06D6, 0x06DC, 0x06DD, 0x06DF, 0x06E0, 0x06E4, 0x06E7, 0x06E8,
            0x06EA, 0x06ED, 0x0901, 0x0903, 0x093E, 0x094C, 0x0951, 0x0954,
            0x0962, 0x0963, 0x0981, 0x0983, 0x09C0, 0x09C4, 0x09C7, 0x09C8,
            0x09CB, 0x09CD, 0x09E2, 0x09E3, 0x0A40, 0x0A42, 0x0A47, 0x0A48,
            0x0A4B, 0x0A4D, 0x0A70, 0x0A71, 0x0A81, 0x0A83, 0x0ABE, 0x0AC5,
            0x0AC7, 0x0AC9, 0x0ACB, 0x0ACD, 0x0B01, 0x0B03, 0x0B3E, 0x0B43,
            0x0B47, 0x0B48, 0x0B4B, 0x0B4D, 0x0B56, 0x0B57, 0x0B82, 0x0B83,
            0x0BBE, 0x0BC2, 0x0BC6, 0x0BC8, 0x0BCA, 0x0BCD, 0x0C01, 0x0C03,
            0x0C3E, 0x0C44, 0x0C46, 0x0C48, 0x0C4A, 0x0C4D, 0x0C55, 0x0C56,
            0x0C82, 0x0C83, 0x0CBE, 0x0CC4, 0x0CC6, 0x0CC8, 0x0CCA, 0x0CCD,
            0x0CD5, 0x0CD6, 0x0D02, 0x0D03, 0x0D3E, 0x0D43, 0x0D46, 0x0D48,
            0x0D4A, 0x0D4D, 0x0E34, 0x0E3A, 0x0E47, 0x0E4E, 0x0EB4, 0x0EB9,
            0x0EBB, 0x0EBC, 0x0EC8, 0x0ECD, 0x0F18, 0x0F19, 0x0F71, 0x0F84,
            0x0F86, 0x0F8B, 0x0F90, 0x0F95, 0x0F99, 0x0FAD, 0x0FB1, 0x0FB7,
            0x20D0, 0x20DC, 0x302A, 0x302F,
        };

        int combiningCharChar[] = {
            0x05BF, 0x05C4, 0x0670, 0x093C, 0x094D, 0x09BC, 0x09BE, 0x09BF,
            0x09D7, 0x0A02, 0x0A3C, 0x0A3E, 0x0A3F, 0x0ABC, 0x0B3C, 0x0BD7,
            0x0D57, 0x0E31, 0x0EB1, 0x0F35, 0x0F37, 0x0F39, 0x0F3E, 0x0F3F,
            0x0F97, 0x0FB9, 0x20E1, 0x3099, 0x309A,
        };

        //
        // [88] Digit ::= ...
        //

        int digitRange[] = {
            0x0030, 0x0039, 0x0660, 0x0669, 0x06F0, 0x06F9, 0x0966, 0x096F,
            0x09E6, 0x09EF, 0x0A66, 0x0A6F, 0x0AE6, 0x0AEF, 0x0B66, 0x0B6F,
            0x0BE7, 0x0BEF, 0x0C66, 0x0C6F, 0x0CE6, 0x0CEF, 0x0D66, 0x0D6F,
            0x0E50, 0x0E59, 0x0ED0, 0x0ED9, 0x0F20, 0x0F29,
        };

        //
        // [89] Extender ::= ...
        //

        int extenderRange[] = {
            0x3031, 0x3035, 0x309D, 0x309E, 0x30FC, 0x30FE,
        };

        int extenderChar[] = {
            0x00B7, 0x02D0, 0x02D1, 0x0387, 0x0640, 0x0E46, 0x0EC6, 0x3005,
        };

        //
        // Initialize
        //

        // set name start characters
        for (int i = 0; i < nameStartChar.length; i++) {
            CHARS[nameStartChar[i]] |= MASK_NAME_START | MASK_NAME;
        }
        for (int i = 0; i < letterRange.length; i += 2) {
            for (int j = letterRange[i]; j <= letterRange[i + 1]; j++) {
                CHARS[j] |= MASK_NAME_START | MASK_NAME;
            }
        }
        for (int i = 0; i < letterChar.length; i++) {
            CHARS[letterChar[i]] |= MASK_NAME_START | MASK_NAME;
        }

        // set name characters
        for (int i = 0; i < nameChar.length; i++) {
            CHARS[nameChar[i]] |= MASK_NAME;
        }
        for (int i = 0; i < digitRange.length; i += 2) {
            for (int j = digitRange[i]; j <= digitRange[i + 1]; j++) {
                CHARS[j] |= MASK_NAME;
            }
        }
        for (int i = 0; i < combiningCharRange.length; i += 2) {
            for (int j = combiningCharRange[i]; j <= combiningCharRange[i + 1]; j++) {
                CHARS[j] |= MASK_NAME;
            }
        }
        for (int i = 0; i < combiningCharChar.length; i++) {
            CHARS[combiningCharChar[i]] |= MASK_NAME;
        }
        for (int i = 0; i < extenderRange.length; i += 2) {
            for (int j = extenderRange[i]; j <= extenderRange[i + 1]; j++) {
                CHARS[j] |= MASK_NAME;
            }
        }
        for (int i = 0; i < extenderChar.length; i++) {
            CHARS[extenderChar[i]] |= MASK_NAME;
        }

    }
        
    // Constructor
    //-------------------------------------------------------------------------   

    /** 
     * <p>Constructor for use by tools that required <code>JavaBean</code> instances.</p>
     * 
     * <p>This constructor is public <strong>only</strong> 
     * to permit tools that require a JavaBean instance to operate.
     * <code>XMLUtils</code> instances should <strong>not</strong> be constructed in standard 
     * programming. Instead, the class methods should be called directly.</p>
     */
    public XMLUtils() {}

    // Class methods
    //-------------------------------------------------------------------------  
    
    /** 
     * <p>Escape the <code>toString</code> of the given object.
     * For use as body text.</p>
     *
     * @param value escape <code>value.toString()</code>
     * @return text with escaped delimiters 
     */
    public static final String escapeBodyValue(Object value) {
        StringBuffer buffer = new StringBuffer(value.toString());
        for (int i=0, size = buffer.length(); i <size; i++) {
            switch (buffer.charAt(i)) {
                case '<':
                    buffer.replace(i, i+1, LESS_THAN_ENTITY);
                    size += 3;
                    i+=3;
                    break;
                 case '>':
                    buffer.replace(i, i+1, GREATER_THAN_ENTITY);
                    size += 3;
                    i += 3;
                    break;
                 case '&':
                    buffer.replace(i, i+1, AMPERSAND_ENTITY);
                    size += 4;
                    i += 4;
                    break;        
            }
        }
        return buffer.toString();
    }

    /** 
     * <p>Escape the <code>toString</code> of the given object.
     * For use in an attribute value.</p>
     *
     * @param value escape <code>value.toString()</code>
     * @return text with characters restricted (for use in attributes) escaped
     */
    public static final String escapeAttributeValue(Object value) {
        StringBuffer buffer = new StringBuffer(value.toString());
        for (int i=0, size = buffer.length(); i <size; i++) {
            switch (buffer.charAt(i)) {
                case '<':
                    buffer.replace(i, i+1, LESS_THAN_ENTITY);
                    size += 3;
                    i+=3;
                    break;
                 case '>':
                    buffer.replace(i, i+1, GREATER_THAN_ENTITY);
                    size += 3;
                    i += 3;
                    break;
                 case '&':
                    buffer.replace(i, i+1, AMPERSAND_ENTITY);
                    size += 4;
                    i += 4;
                    break;
                 case '\'':
                    buffer.replace(i, i+1, APOSTROPHE_ENTITY);
                    size += 5;
                    i += 5;
                    break;
                 case '\"':
                    buffer.replace(i, i+1, QUOTE_ENTITY);
                    size += 5;
                    i += 5;
                    break;           
            }
        }
        return buffer.toString();
    }    
    
    /**
     * <p>Is this string a well formed xml name?</p>
     *
     * <p>Only certain characters are allowed in well formed element and attribute
     * names in xml. For example, white space is not allowed in a name.</p>
     *
     * <p>The code for this method is based on code in 
     * <code>org.apache.xerces.util.XMLChar</code> 
     * in <a href='http://xml.apache.org/xerces2-j/index.html'>Apache Xerces</a>.
     * The authors of this class are credited at the top of this class.</p>
     *
     * @param name the <code>String</code> to be checked for use as an xml attribute 
     * or element name. Returns false if <code>name</code> is null
     * @return true if this string would be a well-formed name
     */
    public static boolean isWellFormedXMLName( String name ) {
        if ( name == null ) {
            return false;
        }
        
        if ( name.length() == 0 ) {
            return false;
        }
        
        char ch = name.charAt(0);
        if( isNameStartChar(ch) == false) {
           return false;
           
        }
        
        for (int i = 1; i < name.length(); i++ ) {
           ch = name.charAt(i);
           if( isNameChar( ch ) == false ) {
              return false;
           }
        }
        return true;
    }

    /**
     * Returns true if the specified character is a valid name
     * character as defined by the XML 1.0 specification.
     *
     * @param c The character to check.
     * @return true if this is an XML name character
     */
    public static boolean isNameChar(int c) {
        return c < 0x10000 && (CHARS[c] & MASK_NAME) != 0;
    }
    
    /**
     * Returns true if the specified character is a valid name start
     * character as defined in the XML 1.0 specification.
     *
     * @param c The character to check.
     * @return trus if this is an XML name start character
     */
    public static boolean isNameStartChar(int c) {
        return c < 0x10000 && (CHARS[c] & MASK_NAME_START) != 0;
    }
}
