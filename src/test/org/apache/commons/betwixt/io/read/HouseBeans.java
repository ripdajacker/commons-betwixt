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
package org.apache.commons.betwixt.io.read;

import java.util.ArrayList;
import java.util.Iterator;

/** 
 * Collective class for HouseBean
 * 
 * @author Robert Burrell Donkin
 * @version $Id: HouseBeans.java,v 1.4 2004/02/28 13:38:36 yoavs Exp $
 */
public class HouseBeans {

    protected ArrayList houses = new ArrayList();
    
    public HouseBeans() {}
    
    public Iterator getHouses() {
        return houses.iterator();
    }
    
    public void addHouse(HouseBean house) {
        if (house != null) {
            houses.add(house);
        }
    }
}
