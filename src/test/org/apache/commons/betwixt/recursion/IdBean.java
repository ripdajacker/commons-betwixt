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
package org.apache.commons.betwixt.recursion;



/**
 * Used to test mapping of id's to 
 * 
 * @author Robert Burrell Donkin
 * @version $Id: IdBean.java,v 1.5 2004/02/28 13:38:36 yoavs Exp $
 */
public class IdBean
{
    private String id;
    private String notId;
    
    public IdBean(String id) 
    {
        setId(id);
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id) 
    {
        this.id = id;
    }
    
    public String getNotId()
    {
        return notId;
    }
    
    public void setNotId(String notId) 
    {
        this.notId = notId;
    }
}
