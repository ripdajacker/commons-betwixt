package org.apache.commons.betwixt.recursion;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class ElementBean
{
    ArrayList elements;

    /**
     * Constructor for ElementBean.
     */
    public ElementBean()
    {
        elements = new ArrayList();
    }
    
    public void addElement(Element element)
    {
        elements.add(element);
    }
    
    public List getElements()
    {
        return elements;
    }

    public String toString()
    {
       return "list : "+getElements();
    }
    

}
