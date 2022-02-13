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
public class FitDeveloperField
{
    public int fieldNumber;
    public int size;
    public int developerDataIndex;
    public int byteArrayPosition;
            
    public byte[]   applicationId;
    public int      baseTypeId;
    public String   baseType;
    public String   fieldName;
    public String   units;
    public int      nativeMessageNumber;
    public int      nativeFieldNumber;
}
