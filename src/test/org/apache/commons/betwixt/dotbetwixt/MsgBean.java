/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/test/org/apache/commons/betwixt/dotbetwixt/MsgBean.java,v 1.1 2003/11/24 01:58:24 mvdb Exp $
 * $Revision: 1.1 $
 * $Date: 2003/11/24 01:58:24 $
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
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
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
package org.apache.commons.betwixt.dotbetwixt;

/**
 * The bean used to identify a problem there was when a dotbetwixt file
 * did not have any update methods on the element, but on the attributes.
 * 
 * @author <a href="mstanley@cauldronsolutions.com">Mike Stanley</a>
 * @version $Id: MsgBean.java,v 1.1 2003/11/24 01:58:24 mvdb Exp $
 */
public class MsgBean
{
    private String type;
    private String status;
    private String name;
    private String description;
    private String toAddress;
    private String fromAddress;
    private String optionalField1;
    private String optionalField2;    

    /**
     * 
     */
    public MsgBean()
    {
        super();
    }

    /**
     * @return
     */
    public String getFromAddress()
    {
        return fromAddress;
    }

    /**
     * @param fromAddress
     */
    public void setFromAddress(String fromAddress)
    {
        this.fromAddress = fromAddress;
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * @return
     */
    public String getToAddress()
    {
        return toAddress;
    }

    /**
     * @param toAddress
     */
    public void setToAddress(String toAddress)
    {
        this.toAddress = toAddress;
    }

    /**
     * @return
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return
     */
    public String getOptionalField1()
    {
        return optionalField1;
    }

    /**
     * @param optionalField1
     */
    public void setOptionalField1(String optionalField1)
    {
        this.optionalField1 = optionalField1;
    }

    /**
     * @return
     */
    public String getOptionalField2()
    {
        return optionalField2;
    }

    /**
     * @param optionalField2
     */
    public void setOptionalField2(String optionalField2)
    {
        this.optionalField2 = optionalField2;
    }

}
