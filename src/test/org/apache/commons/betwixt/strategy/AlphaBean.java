/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.commons.betwixt.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class AlphaBean {
    
    private Collection children = new ArrayList();
    private Map mapped = new HashMap();
    private BetaBean betaBean;
    private String name;
   
    public BetaBean getBetaBean() {
        return betaBean;
    }
    
    public void setBetaBean(BetaBean betaBean) {
        this.betaBean = betaBean;
    }
    
    public Collection getChildren() {
        return children;
    }

    public void addChild(BetaBean bean) {
        this.children.add(bean);
    }

    public Map getMapped() {
        return mapped;
    }

    public void put(String key, BetaBean value) {
        this.mapped.put(key, value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
