/*
 * $Header: /home/cvs/jakarta-commons/beanutils/LICENSE.txt,v 1.3 2003/01/15 21:59:38 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/01/15 21:59:38 $
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
package org.apache.commons.betwixt;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.iterators.IteratorEnumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** <p><code>CustomerBean</code> is a sample bean for use by the test cases.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:michael.davey@coderage.org">Michael Davey</a>
  * @version $Revision: 1.10 $
  */
public class CustomerBean implements Serializable {

    /** Logger */
    private static final Log log = LogFactory.getLog( CustomerBean.class );
    
    private String id;
    private String name;
    private String nickName;
    private String[] emails;
    private int[] numbers;
    private AddressBean address;
    private Map projectMap;
    private List locations = new ArrayList();
	private Date date;
	private Time time;
	private Timestamp timestamp;
	private BigDecimal bigDecimal;
	private BigInteger bigInteger;
	    
    public CustomerBean() {
    }

    public String getID() {
        return id;
    }
    
    public String getNickName() {
       return nickName;
    }

    
    public String getName() {
        return name;
    }
    
    public String[] getEmails() {
        return emails;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public AddressBean getAddress() {
        return address;
    }

    public Map getProjectMap() {
        return projectMap;
    }
    
    public Iterator getProjectNames() {
        if ( projectMap == null ) {
            return null;
        }
        return projectMap.keySet().iterator();
    }
    
    public Enumeration getProjectURLs() {
        if ( projectMap == null ) {
            return null;
        }
        return new IteratorEnumeration( projectMap.values().iterator() );
    }
    
    public List getLocations() {
        return locations;
    }
    
    /** An indexed property */
    public String getLocation(int index) {
        return (String) locations.get(index);
    }
    
    public void setID(String id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
 
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }    
    
    public void setEmails(String[] emails) {
        this.emails = emails;
    }
    
    public void addEmail(String email) {
        int newLength = (emails == null) ? 1 : emails.length+1;
        String[] newArray = new String[newLength];
        for (int i=0; i< newLength-1; i++) {
            newArray[i] = emails[i];
        }
        newArray[newLength-1] = email;
        emails = newArray;
    }    
    
    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }

    public void addNumber(int number) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Adding number: " + number );
        }
        
        int newLength = (numbers == null) ? 1 : numbers.length+1;
        int[] newArray = new int[newLength];
        for (int i=0; i< newLength-1; i++) {
            newArray[i] = numbers[i];
        }
        newArray[newLength-1] = number;
        numbers = newArray;
    }
    
    public void setAddress(AddressBean address) {
        this.address = address;
        
        if ( log.isDebugEnabled() ) {
            log.debug( "Setting the address to be: " + address );
        }
    }

    public void setProjectMap(Map projectMap) {
        this.projectMap = projectMap;
    }
    
    public void addLocation(String location) {
        locations.add(location);
    }
    
    /** An indexed property */
    public void setLocation(int index, String location) {
        if ( index == locations.size() ) {
            locations.add( location );
        }
        else {
            locations.set(index, location);
        }
    }

    public String toString() {
        return "[" + this.getClass().getName() + ": ID=" + id + ", name=" + name
                + ", address=" + address + "]";
    }
    
    public boolean equals( Object obj ) {
        if ( obj == null ) return false;
        return this.hashCode() == obj.hashCode();
    }
    
    public int hashCode() {
        return toString().hashCode();
    }
	/**
	 * Returns the date.
	 * @return Date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the time.
	 * @return Time
	 */
	public Time getTime() {
		return time;
	}

	/**
	 * Returns the timestamp.
	 * @return Timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the date.
	 * @param date The date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Sets the time.
	 * @param time The time to set
	 */
	public void setTime(Time time) {
		this.time = time;
	}

	/**
	 * Sets the timestamp.
	 * @param timestamp The timestamp to set
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns the bigDecimal.
	 * @return BigDecimal
	 */
	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}

	/**
	 * Returns the bigInteger.
	 * @return BigInteger
	 */
	public BigInteger getBigInteger() {
		return bigInteger;
	}

	/**
	 * Sets the bigDecimal.
	 * @param bigDecimal The bigDecimal to set
	 */
	public void setBigDecimal(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}

	/**
	 * Sets the bigInteger.
	 * @param bigInteger The bigInteger to set
	 */
	public void setBigInteger(BigInteger bigInteger) {
		this.bigInteger = bigInteger;
	}

}
