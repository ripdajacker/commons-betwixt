/*
 * Copyright 2006 The Apache Software Foundation.
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
 package org.apache.commons.betwixt.poly;

public class AlphaTwoImpl implements IAlpha {

	public String alpha() {
        return getTwo();
	}

    private String two;

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }
    
    
    public AlphaTwoImpl(String two) {
        super();
        
        setTwo(two);
    }
    
    public AlphaTwoImpl() {
        super();
    }
    
}
