/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author jorgen
 */
public class FitMessageTest
{
    private FitMessage instance;
    private FitMessage developerFieldDefinition;    
    
    public FitMessageTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
        developerFieldDefinition=new FitMessage(122, FitMessage.HeaderType.NORMAL, false);
        developerFieldDefinition.setGlobalMessageNumber(206);          // 'field_description'
        developerFieldDefinition.addMessageField(206,  3, 10,   7);    // 'field_name'
        developerFieldDefinition.addMessageField(206,  8, 10,   7);    // 'units'
        developerFieldDefinition.addMessageField(206, 14,  2, 132);    // 'native_mesg_num' - uint16
        developerFieldDefinition.addMessageField(206, 15,  1,   2);    // 'native_field_num'
        developerFieldDefinition.addMessageField(206,  2,  1,   2);    // 'fit_base_type_id'
        developerFieldDefinition.addMessageField(206,  0,  1,   2);    // 'developer_data_index'
        developerFieldDefinition.addMessageField(206,  1,  1,   2);    // 'field_definition_number'
        int[] record1={'t', 'e', 's', 't', '1', 0, 0, 0, 0, 0,
                       'b', 'p', 'm',   0, 0, 0, 0, 0, 0, 0,
                       123, 0, 234, 2, 3, 2};
        int[] record2={'t', 'e', 's', 't', '2', 0, 0, 0, 0, 0,
                      'm', 'm', 0,   0, 0, 0, 0, 0, 0, 0,
                       124, 0, 235, 2, 3, 3};
        int[] record3={'t', 'e', 's', 't', '3', 0, 0, 0, 0, 0,
                      'm', 'm', 0,   0, 0, 0, 0, 0, 0, 0,
                       125, 0, 236, 7, 3, 4};
        developerFieldDefinition.addDataRecord(record1);
        developerFieldDefinition.addDataRecord(record2);
        developerFieldDefinition.addDataRecord(record3);
        
        
        instance=new FitMessage(123, FitMessage.HeaderType.NORMAL, true);
        instance.setGlobalMessageNumber(20);          // 'record' message
        instance.addMessageField(20,   0, 4, 133);    // 'position_lat'
        instance.addMessageField(20,   1, 4, 133);    // 'position_long'
        instance.addMessageField(20,   2, 2, 132);    // 'altitude'
        instance.addMessageField(20,   3, 1, 2);      // 'heart_rate'
        instance.addMessageField(20, 253, 4, 133);    // 'timestamp'
        instance.addMessageField(20,   5, 4, 134);    // 'distance'
        instance.addMessageField(20,   6, 2, 132);    // 'speed'
        instance.addMessageField(20, 114, 4, 0x88);    // 'grit' (FLOAT32)
        
        instance.addDeveloperField(20, 2, 1, 3, developerFieldDefinition);
        instance.addDeveloperField(20, 4, 8, 3, developerFieldDefinition);

        
        int[] record4={0xD4, 0x11, 0x8E, 0x03,
                      0x2C, 0xEE, 0x71, 0xFC,
                      0xC9, 0x09,
                      100,
                      0, 0, 0, 0,
                      100,0,0,0,
                      200,0,
                      0x00, 0x40, 0x9a, 0x44, //1234.0
                      10,
                      's', 't', 'r', 'i', 'n', 'g', 0, 0};
        int[] record5={0xD4, 0x11, 0x8E, 0x03,
                      0x2C, 0xEE, 0x71, 0xFC,
                      0xC9, 0x09,
                      100,
                      60, 0, 0, 0,
                      100,0,0,0,
                      200,0,
                      0x44, 0x9a, 0x40, 0x00, //1234.0
                      10,
                      's', 't', 'r', 'i', 'n', 'g', 0, 0};
        int[] record6={0xD4, 0x11, 0x8E, 0x03,
                      0x2C, 0xEE, 0x71, 0xFC,
                      0xC9, 0x09,
                      100,
                      0xFF, 0xFF, 0xFF, 0xFF,
                      100,0,0,0,
                      200,0,
                      0x44, 0x9a, 0x40, 0x00, //1234.0
                      10,
                      's', 't', 'r', 'i', 'n', 'g', 0, 0};
        instance.addDataRecord(record4);
        instance.addDataRecord(record5);        
        instance.addDataRecord(record6);          
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of getLocalMessageType method, of class FitRecord.
     */
    @Test
    public void testGetLocalMessageType()
    {
        System.out.println("getLocalMessageType");
        assertEquals(123, instance.getLocalMessageType());
    }

    /**
     * Test of getEndianness method, of class FitRecord.
     */
    @Test
    public void testGetSetEndianness()
    {
        System.out.println("getSetEndianness");
        instance.setEndianness(FitMessage.Endianness.BIGENDIAN);
        assertEquals(FitMessage.Endianness.BIGENDIAN, instance.getEndianness());
        instance.setEndianness(FitMessage.Endianness.LITTLEENDIAN);
        assertEquals(FitMessage.Endianness.LITTLEENDIAN, instance.getEndianness());
    }

    /**
     * Test of isLittleEndian method, of class FitRecord.
     */
    @Test
    public void testIsLittleEndian()
    {
        System.out.println("isLittleEndian");
        instance.setEndianness(FitMessage.Endianness.BIGENDIAN);
        assertEquals(false, instance.isLittleEndian());
        instance.setEndianness(FitMessage.Endianness.LITTLEENDIAN);
        assertEquals(true, instance.isLittleEndian());
    }

    /**
     * Test of setHasDeveloperData method, of class FitRecord.
     */
    @Test
    public void testSetHasDeveloperData()
    {
        System.out.println("setHasDeveloperData");
        instance.setHasDeveloperData(true);
    }

    /**
     * Test of setNumberOfFields method, of class FitRecord.
     */
    @Test
    public void testGetNumberOfFields()
    {
        System.out.println("getNumberOfFields");
        assertEquals(8, instance.getNumberOfFields());
    }


    /**
     * Test of setNumberOfDeveloperFields method, of class FitRecord.
     */
    @Test
    public void testGetNumberOfDeveloperFields()
    {
        System.out.println("getNumberOfDeveloperFields");
        assertEquals(2, instance.getNumberOfDeveloperFields());
        instance.addDeveloperField(20, 3, 1, 3, developerFieldDefinition);
        assertEquals(3, instance.getNumberOfDeveloperFields());
    }

    /**
     * Test of setGlobalMessageNumber method, of class FitRecord.
     */
    @Test
    public void testSetGetGlobalMessageNumber()
    {
        System.out.println("setGlobalMessageNumber");
        int globalMessageNumber = 124;
        instance.setGlobalMessageNumber(globalMessageNumber);
        assertEquals(globalMessageNumber, instance.getGlobalMessageNumber());
    }

    /**
     * Test of addMessageField method, of class FitRecord.
     */
    @Test
    public void testAddMessageField()
    {
        System.out.println("addMessageField");
        
        assertEquals(8, instance.getNumberOfFields());
        assertNull(instance.getMessageField("power"));
        instance.addMessageField(20, 7, 2, 132);      // 'power'
        assertEquals(9, instance.getNumberOfFields());
        assertEquals("power", instance.getMessageField("power").definition.fieldName);
        assertEquals(7, instance.getMessageField("power").definition.fieldNumber);
    }

    /**
     * Test of addDeveloperField method, of class FitRecord.
     */
    @Test
    public void testAddDeveloperField()
    {
        System.out.println("addDeveloperField");

        assertEquals(2, instance.getNumberOfDeveloperFields());
        instance.addDeveloperField(20, 3, 1, 3, developerFieldDefinition);
        assertEquals(3, instance.getNumberOfDeveloperFields());
        FitDeveloperField field=instance.getDeveloperFieldDefintions().get(2);
        assertEquals("test2", field.fieldName);
        assertEquals(124, field.nativeMessageNumber);
        assertEquals(235, field.nativeFieldNumber);
   }

    /**
     * Test of getFieldSize method, of class FitRecord.
     */
    @Test
    public void testGetFieldSize()
    {
        System.out.println("getFieldSize");
        assertEquals(4, instance.getFieldSize(0));

    }

    /**
     * Test of getRecordSize method, of class FitRecord.
     */
    @Test
    public void testGetRecordSize()
    {
        System.out.println("getRecordLength");
        assertEquals(26, this.developerFieldDefinition.getRecordSize());
    }

    /**
     * Test of addDataRecord method, of class FitRecord.
     */
    @Test
    public void testAddDataRecord()
    {
        System.out.println("addDataRecord");
        int[] record={0x80, 0xF0, 0xFA, 0x02,
                      0x02, 0xFA, 0xF0, 0x80,
                      100, 0,
                      96,
                      0, 0, 0, 0,
                      100,0,0,0,
                      5,0,
                      0x00, 0x40, 0x9a, 0x44, //1234.0
                      11,
                      's', 't', 'r', 'i', 'n', 'g', 0, 0};
        assertEquals(3, instance.getNumberOfRecords());
        instance.addDataRecord(record);
        assertEquals(4, instance.getNumberOfRecords());
        assertEquals(96, instance.getIntValue(3, "heart_rate"));
        assertEquals(50000000, instance.getIntValue(3, "position_lat"));
        assertEquals(11, instance.getIntValue(3, "test1", true));

    }

    /**
     * Test of getNumberOfRecordValues method, of class FitRecord.
     */
    @Test
    public void testGetNumberOfRecordValues()
    {
        System.out.println("getNumberOfRecordValues");
        assertEquals(3, developerFieldDefinition.getNumberOfRecords());
        assertEquals(3, instance.getNumberOfRecords());
    }

    /**
     * Test of getMessageField method, of class FitRecord.
     */
    @Test
    public void testGetMessageField_string()
    {
        System.out.println("getMessageField based on fieldName");
        FitMessageField result = instance.getMessageField("position_lat");
        assertEquals("position_lat", result.definition.fieldName);
        assertEquals(0, result.definition.fieldNumber);
        assertEquals(0, result.byteArrayPosition);
        
        result = instance.getMessageField("position_long");
        assertEquals("position_long", result.definition.fieldName);
        assertEquals(4, result.byteArrayPosition);

        result = instance.getMessageField("heart_rate");
        assertEquals("heart_rate", result.definition.fieldName);
        assertEquals("bpm", result.definition.units);
        assertEquals(10, result.byteArrayPosition);

        result = instance.getMessageField("non_existent");
        assertNull(result);
    }
    
    /**
     * Test of getDeveloperField method, of class FitRecord.
     */
    @Test
    public void testGetDeveloperField_string()
    {
        System.out.println("getDeveloperField based on fieldName");
        FitDeveloperField result = instance.getDeveloperField("test1");
        assertEquals("test1", result.fieldName);
        assertEquals(2, result.fieldNumber);
        assertEquals(25, result.byteArrayPosition);

        result = instance.getDeveloperField("non_existent");
        assertNull(result);
    }
    
    /**
     * Test of the hasField method, of class FitRecord
     */
    @Test
    public void testHasField()
    {
        System.out.println("getField");
        assertEquals(true, instance.hasField("position_lat"));
        assertEquals(true, instance.hasField("position_long"));
        assertEquals(true, instance.hasField("heart_rate"));
        assertEquals(false, instance.hasField("non_existent"));
    }

    /**
     * Test of the hasDeveloperField method, of class FitRecord
     */
    @Test
    public void testHasDeveloperField()
    {
        System.out.println("getDeveloperField");
        assertEquals(true, instance.hasDeveloperField("test1"));
        assertEquals(false, instance.hasDeveloperField("test2"));
        assertEquals(false, instance.hasDeveloperField("non_existent"));
        instance.addDeveloperField(20, 3, 1, 3, developerFieldDefinition);
        assertEquals(true, instance.hasDeveloperField("test2"));

    }

    /**
     * Test of getMessageField method, of class FitRecord.
     */
    @Test
    public void testGetMessageField_int()
    {
        System.out.println("getMessageField based on field number");
        FitMessageField result = instance.getMessageField(0);
        assertEquals("position_lat", result.definition.fieldName);
        assertEquals(0, result.definition.fieldNumber);
        assertEquals(0, result.byteArrayPosition);
        
        result = instance.getMessageField(1);
        assertEquals("position_long", result.definition.fieldName);
        assertEquals(4, result.byteArrayPosition);

        result = instance.getMessageField(3);
        assertEquals("heart_rate", result.definition.fieldName);
        assertEquals("bpm", result.definition.units);
        assertEquals(10, result.byteArrayPosition);

        result = instance.getMessageField(255);
        assertNull(result);
    }
    
    /**
     * Test of getMessageField method, of class FitRecord.
     */
    @Test
    public void testGetDeveloperField_int()
    {
        System.out.println("getDeveloperField based on field number");
        FitDeveloperField result = instance.getDeveloperField(2);
        assertEquals("test1", result.fieldName);
        assertEquals(2, result.fieldNumber);
        assertEquals(25, result.byteArrayPosition);
    
        result = instance.getDeveloperField(255);
        assertNull(result);
    }
    
    
    /**
     * Test of getIntValue method, of class FitRecord.
     */
    @Test
    public void testGetIntValue()
    {
        System.out.println("getIntValue");
        // Message field value
        assertEquals(59642324, instance.getIntValue(0, "position_lat", false));
        assertEquals(0, instance.getIntValue(0, "non_existent", false));

        assertEquals(59642324, instance.getIntValue(0, "position_lat"));
        assertEquals(0, instance.getIntValue(0, "non_existent"));
        
        
        // Developer field value
        assertEquals(10, instance.getIntValue(0, "test1", true));
        assertEquals(0, instance.getIntValue(0, "non_existent", true));
    }

    /**
     * Test of getLongValue method, of class FitRecord.
     */
    @Test
    @Ignore
    public void testGetLongValue()
    {
        System.out.println("getLongValue");
    }

    /**
     * Test of getLongValue method, of class FitRecord.
     */
    @Test
    public void testGetFloatValue()
    {
        System.out.println("getFloatValue");
        assertEquals(1234, instance.getFloatValue(0, "grit", false), 0.00001);
    }
    
    
    /**
     * Test of getTimeValue method, of class FitRecord.
     */
    @Test
    public void testGetTimeValue_int_String()
    {
        System.out.println("getTimeValue");
        
        assertEquals("1989-12-31 00:00:00", instance.getTimeValue(0, "timestamp").format("YYYY-MM-DD hh:mm:ss"));
        assertEquals("1989-12-31 00:01:00", instance.getTimeValue(1, "timestamp").format("YYYY-MM-DD hh:mm:ss"));
        assertEquals("2126-02-06 06:28:15", instance.getTimeValue(2, "timestamp").format("YYYY-MM-DD hh:mm:ss"));
    }

    /**
     * Test of getTimeValue method, of class FitRecord.
     */
    @Test
    public void testGetTimeValue_3args()
    {
        System.out.println("getTimeValue");
        assertEquals("1989-12-31 01:00:00.0", instance.getTimeValue(0, "timestamp",  1).toString());
        assertEquals("1989-12-30 22:00:00.0", instance.getTimeValue(0, "timestamp", -2).toString());
        assertEquals("1989-12-31 02:01:00.0", instance.getTimeValue(1, "timestamp", 2).toString());
        assertEquals("2126-02-06 06:28:15.0", instance.getTimeValue(2, "timestamp", 0).toString());
    }

    /**
     * Test of getAltitudeValue method, of class FitRecord.
     */
    @Test
    public void testGetScaledValue()
    {
        System.out.println("getScaledValue");
        assertEquals(0.2, instance.getScaledValue(0, "speed"), 0.0001);
        assertEquals(1.0, instance.getScaledValue(0, "altitude"), 0.0001);
        assertEquals(0.0, instance.getScaledValue(0, "non_existing"), 0.0001);
    }

    /**
     * Test of getLatLonValue method, of class FitRecord.
     */
    @Test
    public void testGetLatLonValue()
    {
        System.out.println("getLatLonValue");
        assertEquals(4.99916, instance.getLatLonValue(0, "position_lat"), 0.0001);
        assertEquals(-4.99916, instance.getLatLonValue(0, "position_long"), 0.0001);
    }

    /**
     * Test of getAltitudeValue method, of class FitRecord.
     */
    @Test
    @Ignore
    public void testGetAltitudeValue()
    {
        System.out.println("getAltitudeValue");
    }

    /**
     * Test of getSpeedValue method, of class FitRecord.
     */
    @Test
    public void testGetSpeedValue()
    {
        System.out.println("getSpeedValue");
        assertEquals(0.2, instance.getSpeedValue(0, "speed"), 0.0001);
    }

    /**
     * Test of getElapsedTimeValue method, of class FitRecord.
     */
    @Test
    @Ignore
    public void testGetElapsedTimeValue()
    {
        System.out.println("getElapsedTimeValue");
    }

    /**
     * Test of getDistanceValue method, of class FitRecord.
     */
    @Test
    public void testGetDistanceValue()
    {
        System.out.println("getDistanceValue");
        assertEquals(1.0, instance.getDistanceValue(0, "distance"), 0.001);
    }

    /**
     * Test of getStringValue method, of class FitRecord.
     */
    @Test
    public void testGetStringValue()
    {
        // regular message fields
        assertEquals("test1", developerFieldDefinition.getStringValue(0, "field_name", false));
        assertEquals("test1", developerFieldDefinition.getStringValue(0, "field_name"));

        // developer field
        assertEquals("string", instance.getStringValue(0, "test3", true));
        
        // Non existing
        assertNull(developerFieldDefinition.getStringValue(0, "non_existent"));
        assertNull(developerFieldDefinition.getStringValue(0, "non_existent", true));
        assertNull(developerFieldDefinition.getStringValue(0, "non_existent", false));
    }


    /**
     * Test of getGlobalFieldDefintions method, of class FitRecord.
     */
    @Test
    public void testGetGlobalFieldDefintions()
    {
        System.out.println("getGlobalFieldDefintions");
        List<FitMessageField> result = instance.getFieldDefintions();
        assertEquals(8, result.size());
        assertEquals("position_lat", result.get(0).definition.fieldName);
    }

    /**
     * Test of getDeveloperFieldDefintions method, of class FitRecord.
     */
    @Test
    public void testGetDeveloperFieldDefintions()
    {
        System.out.println("getDeveloperFieldDefintions");
        List<FitDeveloperField> result = instance.getDeveloperFieldDefintions();
        
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).fieldNumber);
        assertEquals(3, result.get(0).developerDataIndex);
        assertEquals("test1", result.get(0).fieldName);
        assertEquals("bpm", result.get(0).units);
        assertEquals(123, result.get(0).nativeMessageNumber);
        assertEquals(234, result.get(0).nativeFieldNumber);
        assertEquals("uint8", result.get(0).baseType);

        assertEquals(4, result.get(1).fieldNumber);
        assertEquals(3, result.get(1).developerDataIndex);
        assertEquals(7, result.get(1).baseTypeId);
        assertEquals("string", result.get(1).baseType);
        assertEquals("test3", result.get(1).fieldName);
    }
    
    @Test
    public void testGetMessageFieldNames()
    {
        System.out.println("getMessageFieldNames");
        List<String> names=instance.getMessageFieldNames();
        
        assertEquals(8, names.size());
        assertEquals("position_lat", names.get(0));
        assertEquals("position_long", names.get(1));
        assertEquals("altitude", names.get(2));
        assertEquals("heart_rate", names.get(3));
        assertEquals("timestamp", names.get(4));
        assertEquals("distance", names.get(5));
        assertEquals("speed", names.get(6));
        assertEquals("grit", names.get(7));
        
    }

    @Test
    public void testGetDeveloperFieldNames()
    {
        System.out.println("getDeveloperFieldNames");
        List<String> names=instance.getDeveloperFieldNames();
        
        assertEquals(2, names.size());
        assertEquals("test1", names.get(0));
        assertEquals("test3", names.get(1));
    }
 
    
    @Test
    public void testGetMessageName()
    {
        System.out.println("getMessageName");
        assertEquals("record", instance.getMessageName());
        
        instance.setGlobalMessageNumber(19);
        assertEquals("lap", instance.getMessageName());

        instance.setGlobalMessageNumber(222);
        assertEquals("not found", instance.getMessageName());
        
        instance.setGlobalMessageNumber(65535);
        assertEquals("not found", instance.getMessageName());
    }

    @Test
    public void testGetDataRecords()
    {
        System.out.println("getDataRecords");
        List<FitDataRecord> records=instance.getDataRecords();
        assertNotNull(records);
        assertEquals(3, records.size());
    }

}
