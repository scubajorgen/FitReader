/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import net.studioblueplanet.fitreader.FitMessage.Endianness;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
/**
 *
 * @author jorgen
 */
public class FitDataRecord
{
    private final static Logger     LOGGER = LogManager.getLogger(FitDataRecord.class);
    public static final int         BASETYPE_ENUM   =0x00;
    public static final int         BASETYPE_SINT8  =0x01;
    public static final int         BASETYPE_UINT8  =0x02;
    public static final int         BASETYPE_SINT16 =0x83;
    public static final int         BASETYPE_UINT16 =0x84;
    public static final int         BASETYPE_SINT32 =0x85;
    public static final int         BASETYPE_UINT32 =0x86;
    public static final int         BASETYPE_STRING =0x07;
    public static final int         BASETYPE_FLOAT32=0x88;
    public static final int         BASETYPE_FLOAT64=0x89;
    public static final int         BASETYPE_UINT8Z =0x0A;
    public static final int         BASETYPE_UINT16Z=0x8B;
    public static final int         BASETYPE_UINT32Z=0x8C;
    public static final int         BASETYPE_BYTE   =0x8D;
    public static final int         BASETYPE_SINT64 =0x8E;
    public static final int         BASETYPE_UINT64 =0x8F;
    public static final int         BASETYPE_UINT64Z=0x90;
    
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
     * @return The integer as long
     */
    public long bytesToUnsignedInt(int offset, int size)
    {
        int  i;
        long value;
        
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
        value&=0xFFFFFFFFL;
        return value;
    }
    
    /**
     * Helper method. Converts a section of the byte array to  
     * signed integer value.
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The integer
     */
    public long bytesToSignedInt(int offset, int size)
    {
        long    value;
        boolean sign;
        
        value=this.bytesToUnsignedInt(offset, size);
        

        sign=(value & (1<<(8*size-1)))>0;
        
        if (sign)
        {
            value|= ((-1L)<<(8*size));
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
           value|= (-1L<<(8*size));
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
    
    /**
     * Checks if the value represents the invalid value.
     * @param value Value as long
     * @param baseType Base type of the value
     * @return True if invalid, false if not
     */
    public static boolean isInvalidValue(long value, int baseType)
    {
        boolean isInvalid;
        long invalidValue;
        
        switch (baseType)
        {
            case BASETYPE_ENUM: 
                invalidValue=0xFF;
                break;
            case BASETYPE_SINT8: 
                invalidValue=0x7F;
                break;
            case BASETYPE_UINT8: 
                invalidValue=0xFF;
                break;
            case BASETYPE_SINT16: 
                invalidValue=0x7FFF;
                break;
            case BASETYPE_UINT16: 
                invalidValue=0xFFFF;
                break;
            case BASETYPE_SINT32: 
                invalidValue=0x7FFFFFFF;
                break;
            case BASETYPE_UINT32: 
                invalidValue=0xFFFFFFFF;
                break;
            case BASETYPE_STRING: 
                invalidValue=0x00;
                break;
            case BASETYPE_FLOAT32: 
                invalidValue=0xFFFFFFFF;
                break;
            case BASETYPE_FLOAT64: 
                invalidValue=0xFFFFFFFFFFFFFFFFL;
                break;
            case BASETYPE_UINT8Z: 
                invalidValue=0x00;
                break;
            case BASETYPE_UINT16Z: 
                invalidValue=0x0000;
                break;
            case BASETYPE_UINT32Z: 
                invalidValue=0x00000000;
                break;
            case BASETYPE_BYTE: 
                invalidValue=0xFF;
                break;
            case BASETYPE_SINT64: 
                invalidValue=0x7FFFFFFFFFFFFFFFL;
                break;
            case BASETYPE_UINT64: 
                invalidValue=0xFFFFFFFFFFFFFFFFL;
                break;
            case BASETYPE_UINT64Z: 
                invalidValue=0x0000000000000000;
                break;
            default:
                invalidValue=0x00;
                LOGGER.error("Invalid base type {}", baseType);
                break;
                
        }
        return (value==invalidValue);
    }
}
