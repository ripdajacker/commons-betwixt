package org.apache.commons.betwixt.nowrap;

// Java Core Classes
import java.util.ArrayList;
import java.util.List;


public class POTest
{

    private List componentTests;

    private String printingNumber = "";

    public POTest()
    {
        System.out.println("-- INSTANTIATING NEW PO");
        componentTests = new ArrayList();
    }

    public List getComponenttests()
    {
        System.out.println("-- GET PO.getComponents");
        return this.componentTests;
    }

    public void setComponenttests(List componentTests)
    {
    }

    public void addComponenttest(Componenttest c)
    {
        System.out.println("-- ADD PO.addComponent");
        componentTests.add(c);
    }

    public void setPrintingNumber(String s)
    {
        System.out.println("-- SET PO.setPrintingNumber");
        printingNumber = s;
    }

    public String getPrintingNumber()
    {
        System.out.println("-- GET PO.getPrintingNumber");
        return printingNumber;
    }
}
