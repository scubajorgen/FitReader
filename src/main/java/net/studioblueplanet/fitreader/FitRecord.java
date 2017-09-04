/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.studioblueplanet.fitreader;

import net.studioblueplanet.logger.DebugLogger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import hirondelle.date4j.DateTime;
import java.util.TimeZone;


/**
 * This class represents a full FIT record. It contains the record 
 * defintion, but also the record values.
 * @author Jorgen
 */
public class FitRecord
{
    public enum HeaderType              {NORMAL, COMPRESSED_TIMESTAMP};
    public enum RecordType              {DEFINITION, DATA};
    public enum Endianness              {LITTLEENDIAN, BIGENDIAN};
    
    
    private final HeaderType                    headerType;
    private int                                 localMessageType;
    private int                                 numberOfFields;
    private int                                 numberOfDeveloperFields;
    private Endianness                          endianness;
    private boolean                             hasDeveloperData;
    private int                                 globalMessageNumber;
    private final ArrayList<FitMessageField>    fieldDefinitions;
    private final ArrayList<int[]>              recordData;
    
    private int                                 recordLength;
    
    private int                                 byteArrayPosition;
    
    /***************************************************************************\
     * CONSTRUCTOR/DESTRUCTOR
     ***************************************************************************/
    /**
     * Constructor. The values from the global header are set.
     * @param localMessageType
     * @param headerType
     * @param hasDeveloperData 
     */
    public FitRecord(int localMessageType, HeaderType headerType, boolean hasDeveloperData)
    {
        this.localMessageType   =localMessageType;
        this.headerType         =headerType;
        this.hasDeveloperData   =hasDeveloperData;
        globalMessageNumber     =0xff;
        numberOfFields          =0;
        numberOfDeveloperFields =0;
        recordLength            =0;
        endianness              =Endianness.LITTLEENDIAN;
        byteArrayPosition       =0;
        
        fieldDefinitions        =new ArrayList<FitMessageField>();
        recordData              =new ArrayList<int[]>();
    }
    
    /***************************************************************************\
     * GETTERS/SETTERS
     ***************************************************************************/
    /**
     * Request the local message type value 
     * @return The local message type (0-15)
     */
    public int getLocalMessageType()
    {
        return this.localMessageType;
    }
   

    
    /**
     * Returns the endianness that is used in the file for this record.
     * It does not affect the way the data is stored in this class!
     * @return The BIGENDIAN or LITTLEENDIAN
     */
    public Endianness getEndianness()
    {
        return this.endianness;
    }
    
    /**
     * Returns whether the endianness is little endian
     * @return True if little endian, false if big endian
     */
    public boolean isLittleEndian()
    {
        return (this.endianness==Endianness.LITTLEENDIAN);
    }
    
    /**
     * Sets the endianness
     * @param endianness The Endianness: BIGENDIAN, LITTLEENDIAN
     */
    public void setEndianness(Endianness endianness)
    {
        this.endianness=endianness;
    }
    
    public void setHasDeveloperData(boolean hasDeveloperData)
    {
        this.hasDeveloperData=hasDeveloperData;
    }
    
    /**
     * Sets the number of fields in this record
     * @param number The number of fields
     */
    public void setNumberOfFields(int number)
    {
        if (number>=0)
        {
            this.numberOfFields=number;
        }
    }
    
    /**
     * Returns the number of fields
     * @return The number of fields defined in this record
     */
    public int getNumberOfFields()
    {
        return this.numberOfFields;
    }

    /**
     * Sets the number of developer fields in this record
     * @param number The number of fields
     */
    public void setNumberOfDeveloperFields(int number)
    {
        if (number>=0)
        {
            this.numberOfDeveloperFields=number;
        }
    }
    
    /**
     * Returns the number of fields
     * @return The number of fields defined in this record
     */
    public int getNumberOfDeveloperFields()
    {
        return this.numberOfDeveloperFields;
    }
    
    /**
     * Sets the global message number (ID for this record according to the
     * global profile)
     * @param globalMessageNumber The global message number 
     */
    public void setGlobalMessageNumber(int globalMessageNumber)
    {
        this.globalMessageNumber=globalMessageNumber;
    }
    
    public int getGlobalMessageNumber()
    {
        return this.globalMessageNumber;
    }
    
    /***************************************************************************\
     * MESSAGE DEFINITION AND HANDLING
     ***************************************************************************/
    
    /**
     * Adds a field definition to the array based on message number and field number
     * @param globalMessageNumber The global message number
     * @param size Field length in bytes
     * @param baseType baseType for this field. 
     * @param fieldNumber The field definition number
     */
    public void addMessageField(int globalMessageNumber, int fieldNumber, int size, int baseType)
    {
        FitFieldDefinition  fieldDefinition;
        FitGlobalProfile    profile;
        FitMessageField     field;
   
        profile=FitGlobalProfile.getInstance();
        fieldDefinition=profile.getMessageField(globalMessageNumber, fieldNumber);
        if (fieldDefinition!=null)
        {
            field                   =new FitMessageField();
            field.definition        =fieldDefinition;
            field.baseType          =baseType;
            field.size              =size;
            field.byteArrayPosition =byteArrayPosition; // start of the field data in the byte array
            byteArrayPosition       +=size;
            
            this.fieldDefinitions.add(field);
            recordLength            +=size;
        }
        else
        {
            DebugLogger.error("Field not found!");
        }
        
    }

    /**
     * Return the size in bytes of field indicated by the field index
     * @param fieldIndex Index of the field in the definition array
     * @return The size or 0 if the field does not exist
     */
    public int getFieldSize(int fieldIndex)
    {
        FitMessageField field;
        int             fieldSize;
        
        if (fieldIndex<fieldDefinitions.size())
        {
            field=this.fieldDefinitions.get(fieldIndex);
            fieldSize=field.size;
        }
        else
        {
            DebugLogger.error("Field not found");
            fieldSize=0;
        }
        return fieldSize;
    }
    
    /**
     * Get the total length record in bytes. It is the sum of the field sizes;
     * @return The record length
     */
    public int getRecordLength()
    {
        return recordLength;
    }
    
    /**
     * This method stores a record of field values, one value for each field.
     * The raw bytes are stored in the bytes array
     * @param bytes Array with the raw bytes 
     */
    public void addRecordValues(int[] bytes)
    {

/*        
        int i;
        DebugLogger.info("*** Datamessage "+this.localMessageType);
        i=0;
        while (i<this.recordLength)
        {
            System.out.print(String.format("%02x ", bytes[i]));
//            System.out.print(String.format("%c ", bytes[i]));
//            System.out.println("value "+bytes[i]);
            i++;
        }
        System.out.println();
*/
        this.recordData.add(bytes);
    }
    
    /**
     * Returns the number of values stored with this record
     */
    public int getNumberOfRecordValues()
    {
        return this.recordData.size();
    }
    
    /**
     * This method finds the field definition by the field name.
     * @param fieldName Name of the field as in the global profile.
     * @return The field definition or null if not found.
     */
    public FitMessageField getMessageField(String fieldName)
    {
        Iterator<FitMessageField>   iterator;
        FitMessageField             field;
        FitFieldDefinition          fieldDefinition;
        boolean                     found;
        
        iterator    =this.fieldDefinitions.iterator();
        found       =false;
        field       =null;
        
        while (iterator.hasNext() && !found)
        {
            field=iterator.next();
            fieldDefinition=field.definition;
            if (fieldDefinition!=null)
            {
                if (fieldDefinition.fieldDescription.equals(fieldName))
                {
                    found=true;
                }
            }
        }
        if (!found)
        {
            field=null;
        }
        return field;
    }

    /***************************************************************************\
     * REQUESTING VALUES FROM THE RECORD
     ***************************************************************************/
    
    /**
     * Helper method. Converts a section of the byte array to an 
     * unsigned integer value.
     * @param bytes  Byte array
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The integer
     */
    private int bytesToUnsignedInt(int[] bytes, int offset, int size)
    {
        int i;
        int value;
        
        i=0;
        value=0;
        while (i<size)
        {
            if (this.endianness==Endianness.LITTLEENDIAN)
            {
                value |= bytes[offset+i]<<(8*i);
            }
            else
            {
                value |= bytes[offset+i]<<(8*(size-i-1));
            }
            i++;
        }
        return value;
    }
    
    /**
     * Helper method. Converts a section of the byte array to  
     * signed integer value.
     * @param bytes  Byte array
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The integer
     */
    private int bytesToSignedInt(int[] bytes, int offset, int size)
    {
        int     value;
        boolean sign;
        
        value=this.bytesToUnsignedInt(bytes, offset, size);
        

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
     * @param bytes  Byte array
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The integer
     */
    private long bytestToUnsignedLong(int[] bytes, int offset, int size)
    {
        int i;
        long value;
        
        i=0;
        value=0;
        while (i<size)
        {
            if (this.endianness==Endianness.LITTLEENDIAN)
            {
                value |= bytes[offset+i]<<(8*i);
            }
            else
            {
                value |= bytes[offset+i]<<(8*(size-i-1));
            }
            i++;
        }
        return value;
    }
    
    /**
     * Helper method. Converts a section of the byte array to  
     * signed long integer value.
     * @param bytes  Byte array
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The integer
     */
    private long bytesToSignedLong(int[] bytes, int offset, int size)
    {
        long    value;
        boolean sign;
        
        value=this.bytesToUnsignedInt(bytes, offset, size);
        
        sign=(value & (1<<(8*size-1)))>0;
        
        if (sign)
        {
           value|= (((long)-1)<<(8*size));
        }
        
        return value;
    }
    
    
    /**
     * Helper method. Converts a section of the byte array to  
     * a string. All bytes are copied to the string. Not used bytes
     * are assumed to be zero.
     * @param bytes  Byte array
     * @param offset Start within the array
     * @param size   Number of bytes
     * @return The String
     */
    private String bytesToString(int[] bytes, int offset, int size)
    {
        String string;
        int i;
        
        string="";

        i=0;
        while (i<size && bytes[offset+i]!='\0')
        {
            string+=String.format("%c", bytes[offset+i]);
            i++;
        }
        
        return string;
    }
    
    /**
     * This method returns a particular value of the given field at given index.
     * @param index Index in the array
     * @param fieldName Name of the field as in the global profile
     * @return The integer value or zero if an error occurred
     */
    public int getIntValue(int index, String fieldName)
    {
        int                         value;
        FitMessageField             field;
        
        value=0;
        
        field=this.getMessageField(fieldName);
        

        
        if (field!=null)
        {
            if (index<this.recordData.size() && index>=0)
            {
                switch (field.baseType)
                {
                    case 0x00: // enum
                        value=this.bytesToUnsignedInt(this.recordData.get(index), field.byteArrayPosition, 1);
                        break;
                    case 0x01: // sint8 - 2s complement
                        value=this.bytesToSignedInt(this.recordData.get(index), field.byteArrayPosition, 1);
                        break;
                    case 0x02: // uint8 
                        value=this.bytesToUnsignedInt(this.recordData.get(index), field.byteArrayPosition, 1);
                        break;
                    case 0x83: // sint16 - 2s complement 
                        value=this.bytesToSignedInt(this.recordData.get(index), field.byteArrayPosition, 2);
                        break;
                    case 0x84: // uint16 
                        value=this.bytesToUnsignedInt(this.recordData.get(index), field.byteArrayPosition, 2);
                        break;
                    case 0x85: // sint32 - 2s complement 
                        value=this.bytesToSignedInt(this.recordData.get(index), field.byteArrayPosition, 4);
                        break;
                    case 0x86: // uint32 
                        value=this.bytesToUnsignedInt(this.recordData.get(index), field.byteArrayPosition, 4);
                        break;
                    default:
                        DebugLogger.info("Retrieving record value: value is not a integer");
                        break;
                }
            }
            else
            {
                DebugLogger.error("Retrieving record value: index out of bounds");
            }
        }
        else
        {
            DebugLogger.debug("Retrieving record value: Field with name "+fieldName+" not found.");
        }
        
        
        return value;
    }

    /**
     * This method returns a particular value of the given field at given index.
     * @param index Index in the array
     * @param fieldName Name of the field as in the global profile
     * @return Long integer
     */
    public long getLongValue(int index, String fieldName)
    {
        long                         value;
        FitMessageField             field;
        
        value=0;
        
        field=this.getMessageField(fieldName);
        
        if (field!=null)
        {
            if (index<this.recordData.size() && index>=0)
            {
                switch (field.baseType)
                {
                    case 0x8E: // sint64 - 2s complement 
                        value=this.bytesToSignedInt(this.recordData.get(index), field.byteArrayPosition, 8);
                        break;
                    case 0x8F: // uint64 
                        value=this.bytesToUnsignedInt(this.recordData.get(index), field.byteArrayPosition, 8);
                        break;
                    default:
                        DebugLogger.info("Retrieving record value: value is not a long");
                        break;
                }
            }
            else
            {
                DebugLogger.error("Retrieving record value: index out of bounds");
            }
        }
        else
        {
            DebugLogger.info("Retrieving record value: Field with name "+fieldName+" not found.");
        }
        
        
        return value;
    }


    /**
     * This method returns a particular value of the given field at given index
     * as DateTime value. Many of the records in the FIT global profile
     * contain a uint32 DateTime value. It represents the number of seconds
     * since 31-12-1989 00:00.
     * @param index Index in the array
     * @param fieldName Name of the field as in the global profile
     * @return The DateTime value or null if an error occurred.
     */
    public DateTime getTimeValue(int index, String fieldName)
    {
        int                         value;
        DateTime                    dateTime;
        long                        milliseconds;

        value=this.getIntValue(index, fieldName);
        dateTime=new DateTime("1989-12-31 00:00:00");

        milliseconds=dateTime.getMilliseconds(TimeZone.getTimeZone("GMT"));
        milliseconds+=(long)value*1000;
        dateTime=DateTime.forInstant(milliseconds, TimeZone.getTimeZone("GMT"));
        return dateTime;
    }
    /**
     * This method returns a particular value of the given field at given index
     * as TimeStamp value, taking a time zone offset into account.
     * Many of the records in the FIT global profile
     * contain a uint32 DateTime value. It represents the number of seconds
     * since 31-12-1989 00:00.
     * @param index Index in the array
     * @param fieldName Name of the field as in the global profile
     * @param offset Number of hours difference to GMT
     * @return The DateTime value or null if an error occurred.
     */
    public Timestamp getTimeValue(int index, String fieldName, int offset)
    {
        int                         value;
        DateTime                    dateTime;
        long                        milliseconds;

        value=this.getIntValue(index, fieldName);
        dateTime=new DateTime("1989-12-31 00:00:00");

        milliseconds=dateTime.getMilliseconds(TimeZone.getTimeZone("GMT"));
        milliseconds+=(long)value*1000;
        dateTime=DateTime.forInstant(milliseconds, TimeZone.getTimeZone("GMT"));
        if (offset >= 0)
            dateTime = dateTime.plus(0,0,0,offset,0,0,0,null);
        else
            dateTime = dateTime.minus(0,0,0,-1 * offset,0,0,0,null);
        return Timestamp.valueOf(dateTime.toString());
    }
    
    /**
     * This method returns a particular value of the given field at given index
     * as Latitude or Longitude value. 
     * @param index Index in the array
     * @param fieldName Name of the field as in the global profile
     * @return The lat or lon value or 0.0 if an error occurred.
     */
    public double getLatLonValue(int index, String fieldName)
    {
        int value;
        double latlon;
        
        value=this.getIntValue(index, fieldName);
        // = 180 degrees * latlon / (2^31)
        latlon  =180.0*(double)value/(2147483648.0);  
        return latlon;        
    }
    
    /**
     * This method returns a particular value of the given field at given index
     * as enhanced height value. 
     * @param index Index in the array
     * @param fieldName Name of the field as in the global profile
     * @return The lat or lon value or 0.0 if an error occurred.
     */
    public double getAltitudeValue(int index, String fieldName)
    {
        int value;
        double altitude;
        
        value=this.getIntValue(index, fieldName);
        altitude=(double)value/5.0-500.0;
        return altitude;
    }
    
    /**
     * This method returns a particular value of the given field at given index
     * as enhanced speed value. 
     * @param index Index in the array
     * @param fieldName Name of the field as in the global profile
     * @return The speed value or 0.0 if an error occurred.
     */
    public double getSpeedValue(int index, String fieldName)
    {
        int value;
        double speed;
        
        value=this.getIntValue(index, fieldName);
        speed   =(double)value/1000;
        return speed;
    }

    /**
     * This method returns a particular value of the given field at given index
     * as a number of seconds
     * @param index Index in the array
     * @param fieldName Name of the field as in the global profile
     * @return The number of seconds elapsed
     */
    public double getElapsedTimeValue(int index, String fieldName)
    {
        int value;
        double seconds;

        value=this.getIntValue(index, fieldName);
        seconds   =(double)value/1000;
        return seconds;
    }

    /**
     * This method returns a particular value of the given field at given index
     * as enhanced distance value. 
     * @param index Index in the array
     * @param fieldName Name of the field as in the global profile
     * @return The distance value or 0.0 if an error occurred.
     */
    public double getDistanceValue(int index, String fieldName)
    {
        int value;
        double distance;
        
        value=this.getIntValue(index, fieldName);
        distance=value/100;
        return distance;
    }
    
    /**
     * This method returns a particular value of the given field at given index.
     * @param index Index in the array
     * @param fieldName Name of the field as in the global profile
     * @return The value as string
     */
    public String getStringValue(int index, String fieldName)
    {
        String                      value;
        FitMessageField             field;
        
        
        value   ="";
        field   =this.getMessageField(fieldName);
        
        if (field!=null)
        {
            if (index<this.recordData.size() && index>=0)
            {
                if (field.baseType==0x07) // String
                {
                    value=this.bytesToString(this.recordData.get(index), field.byteArrayPosition, field.size);
                }
                else
                {
                    DebugLogger.error("Retrieving string value: field does not represent a string");
                }
            }
            else
            {
                DebugLogger.error("Retrieving record value: index out of bounds");
            }
        }
        else
        {
            DebugLogger.info("Retrieving record value: Field with name "+fieldName+" not found.");
        }
        
        
        return value;
    }
    
    /***************************************************************************\
     * DEBUGGING
     ***************************************************************************/
    
    /**
     * Debug function: dump the record contents
     */
    public void dumpRecord()
    {
        Iterator<FitMessageField>   iterator;
        FitMessageField             field;
        FitGlobalProfile            profile;
        
        profile=FitGlobalProfile.getInstance();
        
        DebugLogger.debug("Local Message Type      :"+this.localMessageType);
        DebugLogger.debug("Global Message Number   :"+this.globalMessageNumber);
        DebugLogger.debug("Header Type             :"+this.headerType.toString());
        DebugLogger.debug("Endianness              :"+this.endianness.toString());
        DebugLogger.debug("Number of fields        :"+this.numberOfFields);
        DebugLogger.debug("Number of dev. fields   :"+this.numberOfDeveloperFields);
        
        iterator=fieldDefinitions.iterator();
        while (iterator.hasNext())
        {
            field=iterator.next();
            DebugLogger.debug("Field                   : "+field.definition.toString()+", size: "+field.size+", base type "+field.baseType+"("+profile.getBaseTypeDescription(field.baseType)+")");
        }
        
    }
    
    
}
