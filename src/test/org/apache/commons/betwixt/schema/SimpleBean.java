/*
 * Copyright 2004 The Apache Software Foundation.
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


package org.apache.commons.betwixt.schema;

/**
 * Very simple bean to allow basic tests for attribute and element
 * transcription.
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision$
 */
public class SimpleBean {
	private String one;
	private String two;
	private String three;
	private String four;
	
	public SimpleBean() {}

    public SimpleBean(String one, String two, String three, String four) {
        setOne(one);
        setTwo(two);
        setThree(three);
        setFour(four);
    }

    public String getOne() {
        return one;
    }

	public void setOne(String string) {
		one = string;
	}
	
	public String getTwo() {
		return two;
	}
	
	public void setTwo(String string) {
		two = string;
	}
	
    public String getThree() {
        return three;
    }
    
	public void setThree(String string) {
		three = string;
	}
	
	public String getFour() {
		return four;
	}

    public void setFour(String string) {
        four = string;
    }

}
