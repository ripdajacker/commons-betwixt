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
 * Configuration for XMLBeanInfo to XML schema transcription.
 * All settings are gathered into this one class for convenience.
 * 
 * @author <a href='http://jakarta.apache.org/'>Jakarta Commons Team</a>
 * @version $Revision: 1.2 $
 */
public class TranscriptionConfiguration {
    
    private DataTypeMapper dataTypeMapper = new DefaultDataTypeMapper();
    
    
    /**
     * Gets the <code>DataTypeMapper</code> to be used during the transcription.
     * @return DataTypeMapper, not null
     */
    public DataTypeMapper getDataTypeMapper() {
        return dataTypeMapper;
    }

    /**
     * Sets the <code>DataTypeMapper</code> to be used during the transcription/
     * @param mapper DataTypeMapper, not null
     */
    public void setDataTypeMapper(DataTypeMapper mapper) {
        dataTypeMapper = mapper;
    }

}
