/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a global fit profile type like 'file' or 'mesg_num'
 * @author jorgen
 */
public class ProfileType
{
    private String                 type;
    private String                 baseType;
    private List<ProfileTypeValue> values;
        
    public ProfileType(String type, String baseType)
    {
        this.type       =type;
        this.baseType   =baseType;
        values          =new ArrayList<>();
    }
    
    /**
     * Add a new type value
     * @param valueName Name of the value to add
     * @param value Integer representation of the value to add
     */
    public void addTypeValue(String valueName, long value)
    {
        ProfileTypeValue typeValue=new ProfileTypeValue(valueName, value);
        values.add(typeValue);
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public List<ProfileTypeValue> getValues()
    {
        return values;
    }

    public void setValues(List<ProfileTypeValue> values)
    {
        this.values = values;
    }

    public String getBaseType()
    {
        return baseType;
    }

    public void setBaseType(String baseType)
    {
        this.baseType = baseType;
    }

        
    /** 
     * Return the value as integer of the profile type value based on its 
     * name
     * @param valueName Name to look for
     * @return The integer value or -1 if not found.
     */
    public long getValueByName(String valueName)
    {
        Iterator<ProfileTypeValue>  it;
        ProfileTypeValue            value;
        long                        longValue;
        boolean                     found;
        
        it       =values.iterator();
        longValue=-1;
        found    =false;
        
        while (!found && it.hasNext())
        {
            value=it.next();
            if (value.getValueName().equals(valueName))
            {
                found=true;
                longValue=value.getValue();
            }
        }
        return longValue;
    }
    
    /**
     * Find the profile type value name based on integer value
     * @param value Value to look for
     * @return The name that is associated with the value or null if not found
     */
    public String getValueName(int value)
    {
        Iterator<ProfileTypeValue>  it;
        ProfileTypeValue            typeValue;
        String                      name;
        boolean                     found;
        
        it      =values.iterator();
        name    =null;
        found   =false;
        
        while (!found && it.hasNext())
        {
            typeValue=it.next();
            if (typeValue.getValue()==value)
            {
                found=true;
                name=typeValue.getValueName();
            }
        }
        return name;        
    }
}
