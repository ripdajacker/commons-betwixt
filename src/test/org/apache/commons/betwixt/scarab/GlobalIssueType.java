package org.apache.commons.betwixt.scarab;

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

import java.io.Serializable;

/**
 * <p><code>GlobalIssueType</code> is a sample bean for use by the test cases.</p>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id: GlobalIssueType.java,v 1.2 2002/06/01 18:28:06 jon Exp $
 */
public class GlobalIssueType implements Serializable
{
    private String name;
    
    /**
     * Constructor for the ScarabSettings object
     */
    public GlobalIssueType()
    { 
    }

    public void setName(String name)
    {
        this.name = name;
    }        

    public String getName()
    {
        return name;
    }        
}
