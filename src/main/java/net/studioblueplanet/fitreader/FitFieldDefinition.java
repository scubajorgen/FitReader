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
    private static  FitGlobalProfile profile=null;
    
    public int      messageNumber;
    public int      fieldNumber;
    public String   fieldDescription;
    public String   fieldTypeDescription;

    @Override
    public String   toString()
    {
        if (profile==null)
        {
            profile=FitGlobalProfile.getInstance();
        }
        
        return profile.getGlobalMessageDescription(messageNumber)+"("+messageNumber+") , " + 
              fieldDescription+"("+fieldNumber+"), "+fieldTypeDescription;        
    }
    
}
