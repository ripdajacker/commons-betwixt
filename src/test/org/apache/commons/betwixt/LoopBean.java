/*
 * $Header: /home/cvs/jakarta-commons-sandbox/betwixt/src/test/org/apache/commons/betwixt/LoopBean.java,v 1.4 2002/05/31 22:39:31 jon Exp $
 * $Revision: 1.4 $
 * $Date: 2002/05/31 22:39:31 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * $Id: LoopBean.java,v 1.4 2002/05/31 22:39:31 jon Exp $
 */
package org.apache.commons.betwixt;

/** <p> This is a bean specifically designed to test cyclic references. 
  * The idea is that there's a count that counts every time <code>getFriend</code>
  * gets called and throws a <code>RuntimeException</code> if the count gets too high.</p>
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.4 $
  */
public class LoopBean
{
    private static int count = 0;
    
    private static final int max_count = 100;

    private LoopBean friend;
    
    private String name;
    
    public static final LoopBean createNoLoopExampleBean()
    {
        LoopBean root = new LoopBean("Root");
        LoopBean levelOne = new LoopBean("level1");
        LoopBean levelTwo = new LoopBean("level2");
        LoopBean levelThree = new LoopBean("level3");
        LoopBean levelFour = new LoopBean("level4");
        LoopBean levelFive = new LoopBean("level5");
        
        root.setFriend(levelOne);
        levelOne.setFriend(levelTwo);
        levelTwo.setFriend(levelThree);
        levelThree.setFriend(levelFour);
        levelFour.setFriend(levelFive);
        
        return root;
    }    
        
    public static final LoopBean createLoopExampleBean()
    {
        LoopBean root = new LoopBean("Root");
        LoopBean levelOne = new LoopBean("level1");
        LoopBean levelTwo = new LoopBean("level2");
        LoopBean levelThree = new LoopBean("level3");
        LoopBean levelFour = new LoopBean("level4");
        LoopBean levelFive = new LoopBean("level5");
        
        root.setFriend(levelOne);
        levelOne.setFriend(levelTwo);
        levelTwo.setFriend(levelThree);
        levelThree.setFriend(levelFour);
        levelFour.setFriend(levelFive);
        levelFive.setFriend(root);
        
        return root;
    }
    
    
    public static final LoopBean createIdOnlyLoopExampleBean()
    {
        LoopBean root = new LoopBean("Root");
        LoopBean levelOne = new LoopBean("level1");
        LoopBean levelTwo = new LoopBean("level2");
        LoopBean levelThree = new LoopBean("level3");
        LoopBean levelFour = new LoopBean("level4");
        LoopBean levelFive = new LoopBean("level5");
        LoopBean notRoot = new LoopBean("Root");
        
        root.setFriend(levelOne);
        levelOne.setFriend(levelTwo);
        levelTwo.setFriend(levelThree);
        levelThree.setFriend(levelFour);
        levelFour.setFriend(levelFive);
        levelFive.setFriend(notRoot);
        
        return root;
    }
    
    public LoopBean(String name) 
    {
        this.name = name;
    }
    
    public LoopBean getFriend()
    {
        if (++count > max_count)
        {
            throw new RuntimeException("Looping!");
        }
        return friend;
    }
    
    public void setFriend(LoopBean friend)
    {
        this.friend = friend;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String toString()
    {
        return "[LoopBean] name=" + name;
    }
}
