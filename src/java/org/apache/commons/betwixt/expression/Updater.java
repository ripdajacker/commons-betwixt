/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 * 
 * $Id: Updater.java,v 1.1 2002/06/10 17:53:33 jstrachan Exp $
 */
package org.apache.commons.betwixt.expression;

/** <p><code>Updater</code> acts like an lvalue which updates the current 
  * context bean from some text from an XML attribute or element.</p>
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.1 $
  */
public interface Updater {

    /** Updates the current bean context with a new String value.
     * This is typically used when parsing XML and updating a beans value
     * from XML 
     */
    public void update(Context context, Object newValue);
}
