/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.betwixt.examples.rss;

import org.apache.commons.betwixt.io.BeanReader;

import java.io.File;
import java.util.Iterator;


/**
 * <p>Example application using Betwixt to process RSS 0.91.
 * The intention is to provide illumination and education
 * rather than providing a .</p>
 *
 * @author Robert Burrell Donkin
 * @version $Revision$ $Date$
 */

public class RSSApplication {
    BeanReader reader = new BeanReader();

    public RSSApplication() throws Exception {
        configure();
    }

    public void configure() throws Exception {
        reader.registerBeanClass(Channel.class);
    }

    public String plainTextSummary(String filename) throws Exception {
        return plainTextSummary(new File(filename));
    }

    public String plainTextSummary(File file) throws Exception {
        Channel channel = (Channel) reader.parse(file);
        return plainTextSummary(channel);
    }

    public BeanReader getReader() {
        return reader;
    }

    public String plainTextSummary(Channel channel) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("channel: ");
        buffer.append(channel.getTitle());
        buffer.append('\n');
        buffer.append("url: ");
        buffer.append(channel.getLink());
        buffer.append('\n');
        buffer.append("copyright: ");
        buffer.append(channel.getCopyright());
        buffer.append('\n');

        for (Iterator it = channel.getItems().iterator(); it.hasNext(); ) {
            Item item = (Item) it.next();
            buffer.append('\n');
            buffer.append("title: ");
            buffer.append(item.getTitle());
            buffer.append('\n');
            buffer.append("link: ");
            buffer.append(item.getLink());
            buffer.append('\n');
            buffer.append("description: ");
            buffer.append(item.getDescription());
            buffer.append('\n');
        }

        return buffer.toString();
    }
}
