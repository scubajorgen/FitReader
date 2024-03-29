/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.studioblueplanet.fitreader;

/**
 *
 * @author Jorgen
 */
public class FitFieldDefinition
{
    public int      messageNumber;
    public String   messageName;
    public int      fieldNumber;
    public String   fieldName;
    public String   fieldType;
    public double   scale;
    public double   offset;
    public String   units;
    
    @Override
    public String   toString()
    {
        String data;
        
        data=String.format("%30s (%05d) %20s (%03d) type: %15s, units: %12s, scale %f, offset %f", 
                           messageName, messageNumber, fieldName, fieldNumber, fieldType, units, scale, offset);
        return data;
    }
    
}
