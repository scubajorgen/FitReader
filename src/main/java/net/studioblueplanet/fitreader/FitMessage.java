/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.studioblueplanet.fitreader;

import net.studioblueplanet.logger.DebugLogger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import hirondelle.date4j.DateTime;


/**
 * This class represents a full FIT message. It contains the message 
 * definition, but also the records belonging to this message.
 * @author Jorgen
 */
public class FitMessage
{
    public enum HeaderType              {NORMAL, COMPRESSED_TIMESTAMP};
    public enum RecordType              {DEFINITION, DATA};
    public enum Endianness              {LITTLEENDIAN, BIGENDIAN};

    public static final int                     TIMESTAMP_INDEX=253;
    
    private final HeaderType                    headerType;
    private final int                           localMessageType;
    private Endianness                          endianness;
    private boolean                             hasDeveloperData;
    private int                                 globalMessageNumber;
    private final List<FitMessageField>         fieldDefinitions;
    private final List<FitDeveloperField>       developerFieldDefinitions;
    private final List<FitDataRecord>           records;
    private final Map<Integer, Integer>         timeStamps;
    
    private int                                 recordLength;
    
    private int                                 byteArrayPosition;
    
    private boolean                             hasTimeStamp;
    private static int                          mostRecentTimeStamp=0;
    private static int                          previousTimeStampOffset=0;
    
    /***************************************************************************\
     * CONSTRUCTOR/DESTRUCTOR
     ***************************************************************************/
    /**
     * Constructor. The values from the global header are set.
     * @param localMessageType
     * @param headerType
     * @param hasDeveloperData 
     */
    public FitMessage(int localMessageType, HeaderType headerType, boolean hasDeveloperData)
    {
        this.localMessageType   =localMessageType;
        this.headerType         =headerType;
        this.hasDeveloperData   =hasDeveloperData;
        globalMessageNumber     =0xff;
        recordLength            =0;
        endianness              =Endianness.LITTLEENDIAN;
        byteArrayPosition       =0;
        hasTimeStamp            =false;
        
        fieldDefinitions      =new ArrayList<>();
        developerFieldDefinitions   =new ArrayList<>();
        records                     =new ArrayList<>();
        timeStamps                  =new HashMap<>();
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
     * Returns the endianness that is used in the file for this message.
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
    
    /**
     * Defines whether this message contains developer data
     * @param hasDeveloperData New value for the definition
     */
    public void setHasDeveloperData(boolean hasDeveloperData)
    {
        this.hasDeveloperData=hasDeveloperData;
    }
    
    /**
     * Returns the number of fields
     * @return The number of fields defined in this message
     */
    public int getNumberOfFields()
    {
        return this.fieldDefinitions.size();
    }

    
    /**
     * Returns the number of fields
     * @return The number of fields defined in this message
     */
    public int getNumberOfDeveloperFields()
    {
        return this.developerFieldDefinitions.size();
    }
    
    /**
     * Sets the global message number (ID for this message according to the
     * global profile)
     * @param globalMessageNumber The global message number 
     */
    public void setGlobalMessageNumber(int globalMessageNumber)
    {
        this.globalMessageNumber=globalMessageNumber;
    }
    
    /**
     * Returns the global message number of this message
     * @return The global message number
     */
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
            fieldDefinitions.add(field);
            recordLength            +=size;
        }
        else
        {
            DebugLogger.error("Field not found!");
        }
        
        // This message has a timestamp field
        if (fieldNumber==TIMESTAMP_INDEX)
        {
            hasTimeStamp=true;
        }
        
    }
    
    /**
     * Adds a field definition to the array based on message number and field number
     * @param globalMessageNumber The global message number
     * @param fieldNumber The field definition number
     * @param size Field length in bytes
     * @param developerDataIndex Index. 
     */
    public void addDeveloperField(int globalMessageNumber, int fieldNumber, int size, int developerDataIndex, FitMessage fieldDescription)
    {
        int                 i;
        int                 devIx;
        int                 num;
        FitDeveloperField   field;
        boolean             found;
        
        
        
        field                   =new FitDeveloperField();
        field.fieldNumber       =fieldNumber;
        field.developerDataIndex=developerDataIndex;
        field.size              =size;
        field.byteArrayPosition =byteArrayPosition; // start of the field data in the byte array
        byteArrayPosition       +=size;

        // Now get the description of the developer field...
        if (fieldDescription!=null)
        {
            i=0;
            found=false;
            while (i<fieldDescription.getRecordSize() && !found)
            {
                devIx   =fieldDescription.getIntValue(i, "developer_data_index");
                num     =fieldDescription.getIntValue(i, "field_definition_number");

                if (developerDataIndex==devIx && fieldNumber==num)
                {
                    field.fieldName             =fieldDescription.getStringValue(i, "field_name");
                    field.units                 =fieldDescription.getStringValue(i, "units");
                    field.nativeMessageNumber   =fieldDescription.getIntValue(i, "native_mesg_num");
                    field.nativeFieldNumber     =fieldDescription.getIntValue(i, "native_field_num");
                    field.baseTypeId            =fieldDescription.getIntValue(i, "fit_base_type_id");
                    field.baseType              =FitGlobalProfile.getInstance().getBaseTypeName(field.baseTypeId);

                    found=true;
                }
                i++;
            }        
        }  
        else
        {
            DebugLogger.error("No field description available for the developer field");
        }
        developerFieldDefinitions.add(field);
        recordLength            +=size;
        
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
     * Get the total size of a data record in bytes. It is the sum of the field sizes;
     * @return The record length
     */
    public int getRecordSize()
    {
        return recordLength;
    }
    
    /**
     * This method stores a record of field values, one value for each field.
     * The raw bytes are stored in the bytes array
     * @param bytes Array with the raw bytes 
     */
    public void addDataRecord(int[] bytes)
    {
        FitDataRecord record;
        
        if (bytes.length== recordLength)
        {
            record=new FitDataRecord(bytes, endianness);
            this.records.add(record);
            if (hasTimeStamp)
            {
                FitMessageField field=this.getMessageField(TIMESTAMP_INDEX);
                mostRecentTimeStamp=record.bytesToSignedInt(field.byteArrayPosition, 4);
                previousTimeStampOffset=mostRecentTimeStamp & 0x1F;
            }
        }
        else
        {
            DebugLogger.error("Record size not ok: expected "+recordLength+" bytes, received "+bytes.length+" bytes");
        }
    }
    
    /**
     * In case of a compressed timestamp header: register the offset. This must be done
     * after the record values have been added.
     * The FIT specification is not exactly clear. First we assume a compressed timestamp record does
     * NOT have a timestamp (253) field in the definition message. Prior to the first compressed timestamp
     * record there must be another record belonging to another message that contain a timestamp (253) field. 
     * Second, the specification does not define whether it is obligatory that each data record 
     * has a compressed timestamp or that it
     * is allowed to omit the compressed timestamp header (allowing a record not having
     * a timestamp value). We support the latter.
     * @param offset The offset to add
     */
    public void addTimeStampOffset(int offset)
    {
        int timeStamp;
        
        if (offset<previousTimeStampOffset)
        {
            timeStamp=(mostRecentTimeStamp&0xffffffe0 | offset) + 0x20;
        }
        else
        {
            timeStamp=mostRecentTimeStamp&0xffffffe0 | offset;
        }
        previousTimeStampOffset=offset;
        
        timeStamps.put(this.records.size()-1, timeStamp);
    }
    
    /**
     * Returns the number of records stored with this message
     */
    public int getNumberOfRecords()
    {
        return this.records.size();
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
                if (fieldDefinition.fieldName.equals(fieldName))
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

    
    /**
     * This method finds the field definition by the field number.
     * @param fieldNumber Number of the field as in the global profile.
     * @return The field definition or null if not found.
     */
    public FitMessageField getMessageField(int fieldNumber)
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
                if (fieldDefinition.fieldNumber==fieldNumber)
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
    
    /**
     * Return a list of field names that are in this message
     * @return List of field names
     */
    public List<String> getMessageFieldNames()
    {
        List<String> names;
        
        names=new ArrayList<>();
        for (FitMessageField field:this.fieldDefinitions)
        {
            names.add(field.definition.fieldName);
        }
        return names;
    }
    
    /**
     * Return a list of developer field names that are in this message
     * @return List of field names
     */
    public List<String> getDeveloperFieldNames()
    {
        List<String> names;
        
        names=new ArrayList<>();
        for (FitDeveloperField field:this.developerFieldDefinitions)
        {
            names.add(field.fieldName);
        }
        return names;
    }
    
    /***************************************************************************\
     * REQUESTING VALUES FROM THE RECORD
     ***************************************************************************/

    /**
     * Indicates whether this message has a field definition containing the 
     * given field name
     * @param fieldName Field name to look for
     * @return True if a field definition exists
     */
     public boolean hasField(String fieldName)
     {
        boolean hasField;
        
        hasField=false;
        
        for (FitMessageField field : fieldDefinitions)
        {
            if (field.definition.fieldName.equals(fieldName))
            {
                hasField=true;
            }
        }
        return hasField;
     }
     
    
    /**
     * This method returns a particular value of the given field at given index.
     * @param index Record index
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
            if (index<this.records.size() && index>=0)
            {
                switch (field.baseType)
                {
                    case 0x00: // enum
                        value=records.get(index).bytesToUnsignedInt(field.byteArrayPosition, 1);
                        break;
                    case 0x01: // sint8 - 2s complement
                        value=records.get(index).bytesToSignedInt(field.byteArrayPosition, 1);
                        break;
                    case 0x02: // uint8 
                        value=records.get(index).bytesToUnsignedInt(field.byteArrayPosition, 1);
                        break;
                    case 0x83: // sint16 - 2s complement 
                        value=records.get(index).bytesToSignedInt(field.byteArrayPosition, 2);
                        break;
                    case 0x84: // uint16 
                        value=records.get(index).bytesToUnsignedInt(field.byteArrayPosition, 2);
                        break;
                    case 0x85: // sint32 - 2s complement 
                        value=records.get(index).bytesToSignedInt(field.byteArrayPosition, 4);
                        break;
                    case 0x86: // uint32 
                        value=records.get(index).bytesToUnsignedInt(field.byteArrayPosition, 4);
                        break;
                    case 0x0A: // uint8z 
                        value=records.get(index).bytesToUnsignedInt(field.byteArrayPosition, 1);
                        break;
                    case 0x8B: // uint16z 
                        value=records.get(index).bytesToUnsignedInt(field.byteArrayPosition, 2);
                        break;
                    case 0x8C: // uint32z 
                        value=records.get(index).bytesToUnsignedInt(field.byteArrayPosition, 4);
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
     * @param index Index Record index
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
            if (index<records.size() && index>=0)
            {
                switch (field.baseType)
                {
                    case 0x8E: // sint64 - 2s complement 
                        value=records.get(index).bytesToSignedInt(field.byteArrayPosition, 8);
                        break;
                    case 0x8F: // uint64 
                        value=records.get(index).bytesToUnsignedInt(field.byteArrayPosition, 8);
                        break;
                    case 0x90: // uint64z 
                        value=records.get(index).bytesToUnsignedInt(field.byteArrayPosition, 8);
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
     * @param index Record index
     * @param fieldName Name of the field as in the global profile
     * @return The DateTime value or null if an error occurred.
     */
    public DateTime getTimeValue(int index, String fieldName)
    {
        Integer                     val;
        int                         value;
        DateTime                    dateTime;
        long                        milliseconds;

        if (hasField("timestamp"))
        {
            value=this.getIntValue(index, fieldName);
        }
        else
        {
            val=timeStamps.get(index);
            if (val!=null)
            {
                value=val.intValue();
            }
            else
            {
                value=-1;
            }
        }
        if (value>=0)
        {
            dateTime=new DateTime("1989-12-31 00:00:00");
            milliseconds=dateTime.getMilliseconds(TimeZone.getTimeZone("GMT"));
            milliseconds+=(long)value*1000;
            dateTime=DateTime.forInstant(milliseconds, TimeZone.getTimeZone("GMT"));
        }
        else
        {
            dateTime=null;
        }
        return dateTime;
    }
    
    /**
     * This method returns a particular value of the given field at given index
     * as TimeStamp value, taking a time zone offset into account.
     * Many of the records in the FIT global profile
     * contain a uint32 DateTime value. It represents the number of seconds
     * since 31-12-1989 00:00.
     * @param index Record index
     * @param fieldName Name of the field as in the global profile
     * @param offset Number of hours difference to GMT
     * @return The DateTime value or null if an error occurred.
     */
    public Timestamp getTimeValue(int index, String fieldName, int offset)
    {
        Integer                     val;
        int                         value;
        DateTime                    dateTime;
        long                        milliseconds;
        Timestamp                   timeStamp;
        
        if (hasField("timestamp"))
        {
            value=this.getIntValue(index, fieldName);
        }
        else
        {
            val=timeStamps.get(index);
            if (val!=null)
            {
                value=val.intValue();
            }
            else
            {
                value=-1;
            }
        }
        if (value>=0)
        {
            dateTime=new DateTime("1989-12-31 00:00:00");

            milliseconds=dateTime.getMilliseconds(TimeZone.getTimeZone("GMT"));
            milliseconds+=(long)value*1000;
            dateTime=DateTime.forInstant(milliseconds, TimeZone.getTimeZone("GMT"));
            if (offset >= 0)
                dateTime = dateTime.plus(0,0,0,offset,0,0,0,null);
            else
                dateTime = dateTime.minus(0,0,0,-1 * offset,0,0,0,null);
            timeStamp=Timestamp.valueOf(dateTime.toString());
        }
        else
        {
            timeStamp=null;
        }
        
        return timeStamp;
    }
    
    /**
     * This method returns a particular value of the given field at given index
     * as scaled value. Scale and Offset as in the global field definition are used
     * to scale and offset
     * @param index Record index
     * @param fieldName Name of the field as in the global profile
     * @return The scaled value or 0.0 if an error occurred.
     */
    public double getScaledValue(int index, String fieldName)
    {
        int value;
        double scaledValue;
        double scale;
        double offset;
        
        scale=this.getMessageField(fieldName).definition.scale;
        offset=this.getMessageField(fieldName).definition.offset;
        value=this.getIntValue(index, fieldName);
        scaledValue=(double)value/scale-offset;
        return scaledValue;
    }
    
    /**
     * This method returns a particular value of the given field at given index
     * as Latitude or Longitude value. 
     * @param index IRecord index
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
     * @param index Record index
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
     * @param index Record index
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
     * This method returns a particular value of the given field at given index
     * as String.
     * @param index Record index
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
            if (index<this.records.size() && index>=0)
            {
                if (field.baseType==0x07) // String
                {
                    value=records.get(index).bytesToString(field.byteArrayPosition, field.size);
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
    public void dumpMessage()
    {
        Iterator<FitMessageField>   iterator;
        FitMessageField             field;
        FitGlobalProfile            profile;
        
        profile=FitGlobalProfile.getInstance();
        
        DebugLogger.debug("Local Message Type      :"+this.localMessageType);
        DebugLogger.debug("Global Message Number   :"+this.globalMessageNumber);
        DebugLogger.debug("Header Type             :"+this.headerType.toString());
        DebugLogger.debug("Endianness              :"+this.endianness.toString());
        DebugLogger.debug("Number of fields        :"+this.fieldDefinitions.size());
        DebugLogger.debug("Number of dev. fields   :"+this.developerFieldDefinitions.size());
        
        iterator=fieldDefinitions.iterator();
        while (iterator.hasNext())
        {
            field=iterator.next();
            DebugLogger.debug("Field                   : "+field.definition.toString()+", size: "+field.size+", base type "+field.baseType+"("+profile.getBaseTypeName(field.baseType)+")");
        }
        
    }
    
    /**
     * Returns the list of message field definitions within this message
     * @return The list
     */
    public List<FitMessageField> getFieldDefintions()
    {
        return this.fieldDefinitions;
    }

    /**
     * Returns the list of devloper message field definitions within this message
     * @return The list
     */
    public List<FitDeveloperField> getDeveloperFieldDefintions()
    {
        return this.developerFieldDefinitions;
    }

}