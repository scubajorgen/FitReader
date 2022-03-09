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
public class FitMessageField
{
    public FitFieldDefinition           definition;
    public boolean                      isArray;
    public int                          size;
    public int                          baseType;
    public int                          byteArrayPosition;

    @Override
    public String   toString()
    {
        String data;
        
        data=String.format("%30s (%05d) %20s (%03d) type: %15s/%15s, units: %12s, scale %f, offset %f", 
                           definition.messageName, definition.messageNumber, definition.fieldName, definition.fieldNumber, 
                           definition.fieldType, FitGlobalProfile.getInstance().getBaseTypeName(baseType), 
                           definition.units, definition.scale, definition.offset);
        return data;
    }
}
