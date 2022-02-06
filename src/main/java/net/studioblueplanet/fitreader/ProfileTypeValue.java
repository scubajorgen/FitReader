/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

/**
 *
 * @author jorgen
 */
public class ProfileTypeValue
{
    private String valueName;
    private long   value;

    public ProfileTypeValue(String valueName, long value)
    {
        this.valueName  =valueName;
        this.value      =value;
    }
    
    public String getValueName()
    {
        return valueName;
    }

    public void setValueName(String valueName)
    {
        this.valueName = valueName;
    }

    public long getValue()
    {
        return value;
    }

    public void setValue(long value)
    {
        this.value = value;
    }

    
}
