/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import net.studioblueplanet.fitreader.FitMessage.Endianness;
/**
 *
 * @author jorgen
 */
public class FitDataRecord
{
    private final int[]             recordData;
    private final Endianness        endianness;

    /**
     * Constructor
     * @param recordData The record data 
     */
    public FitDataRecord(int[] recordData, Endianness endianness)
    {
        this.recordData=recordData;
        this.endianness=endianness;
    }
    
    
    /**
     * Helper method. Converts a section of the byte array to an 
     * unsigned integer value.
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The integer
     */
    public int bytesToUnsignedInt(int offset, int size)
    {
        int i;
        int value;
        
        i=0;
        value=0;
        while (i<size)
        {
            if (this.endianness==Endianness.LITTLEENDIAN)
            {
                value |= recordData[offset+i]<<(8*i);
            }
            else
            {
                value |= recordData[offset+i]<<(8*(size-i-1));
            }
            i++;
        }
        return value;
    }
    
    /**
     * Helper method. Converts a section of the byte array to  
     * signed integer value.
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The integer
     */
    public int bytesToSignedInt(int offset, int size)
    {
        int     value;
        boolean sign;
        
        value=this.bytesToUnsignedInt(offset, size);
        

        sign=(value & (1<<(8*size-1)))>0;
        
        if (sign)
        {
            value|= (((int)-1)<<(8*size));
        }
        
        return value;
    }

    /**
     * Helper method. Converts a section of the byte array to an 
     * unsigned long integer value.
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The integer
     */
    public long bytesToUnsignedLong(int offset, int size)
    {
        int i;
        long value;
        
        value=0;
        i=0;
        while (i<size)
        {
            if (this.endianness==Endianness.LITTLEENDIAN)
            {
                value |= (long)recordData[offset+i]<<(8*i);
            }
            else
            {
                value |= (long)recordData[offset+i]<<(8*(size-i-1));
            }
            i++;
        }
        System.out.println(String.format("%x", value));
        return value;
    }
    
    /**
     * Helper method. Converts a section of the byte array to  
     * signed long integer value.
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The integer
     */
    public long bytesToSignedLong(int offset, int size)
    {
        long    value;
        boolean sign;
        
        value=this.bytesToUnsignedLong(offset, size);
        
        sign=(value & ((long)(1)<<(8*size-1)))>0;
        
        if (sign)
        {
           value|= ((long)(-1)<<(8*size));
        }
        
        return value;
    }
    
    
    /**
     * Helper method. Converts a section of the byte array to  
     * a string. All bytes are copied to the string. Not used bytes
     * are assumed to be zero.
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The String
     */
    public String bytesToString(int offset, int size)
    {
        String string;
        int i;
        
        string="";

        i=0;
        while (i<size && recordData[offset+i]!='\0')
        {
            string+=String.format("%c", recordData[offset+i]);
            i++;
        }
        return string;
    }
}
