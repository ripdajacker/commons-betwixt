package org.apache.commons.betwixt.nowrap;

public class Componenttest
{

    private String compDescription = "";

    public Componenttest()
    {
        System.out.println("-- INSTANTIATING NEW COMPONENTTEST");
    }

    public void setCompDescription(String s)
    {
        System.out.println("SET component description");
        compDescription = s;
    }

    public String getCompDescription()
    {
        System.out.println("GET component description");
        return compDescription;
    }
}
