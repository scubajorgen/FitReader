/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import hirondelle.date4j.DateTime;
import java.sql.Timestamp;
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
public class FitRecordTest
{
    private FitRecord instance;
    private FitRecord developerFieldDefinition;    
    
    public FitRecordTest()
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
        developerFieldDefinition=new FitRecord(122, FitRecord.HeaderType.NORMAL, false);
        developerFieldDefinition.setGlobalMessageNumber(206);       // 'field_description'
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
        developerFieldDefinition.addRecordValues(record1);
        developerFieldDefinition.addRecordValues(record2);
        
        
        instance=new FitRecord(123, FitRecord.HeaderType.NORMAL, true);
        instance.setGlobalMessageNumber(20);         // 'record' message
        instance.addMessageField(20,   0, 4, 133);    // 'position_lat'
        instance.addMessageField(20,   1, 4, 133);    // 'position_long'
        instance.addMessageField(20,   2, 2, 132);    // 'altitude'
        instance.addMessageField(20,   3, 1, 2);      // 'heart_rate'
        instance.addMessageField(20, 253, 4, 133);    // 'timestamp'
        instance.addMessageField(20,   5, 4, 134);    // 'distance'
        instance.addMessageField(20,   6, 2, 132);    // 'speed'
        
        instance.addDeveloperField(20, 2, 1, 3, developerFieldDefinition);
        
        int[] record={0xD4, 0x11, 0x8E, 0x03,
                      0x2C, 0xEE, 0x71, 0xFC,
                      0xC9, 0x09,
                      100,
                      0, 0, 0, 0,
                      100,0,0,0,
                      200,0,
                      10};
        instance.addRecordValues(record);
        
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
        instance.setEndianness(FitRecord.Endianness.BIGENDIAN);
        assertEquals(FitRecord.Endianness.BIGENDIAN, instance.getEndianness());
        instance.setEndianness(FitRecord.Endianness.LITTLEENDIAN);
        assertEquals(FitRecord.Endianness.LITTLEENDIAN, instance.getEndianness());
    }

    /**
     * Test of isLittleEndian method, of class FitRecord.
     */
    @Test
    public void testIsLittleEndian()
    {
        System.out.println("isLittleEndian");
        instance.setEndianness(FitRecord.Endianness.BIGENDIAN);
        assertEquals(false, instance.isLittleEndian());
        instance.setEndianness(FitRecord.Endianness.LITTLEENDIAN);
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
        assertEquals(7, instance.getNumberOfFields());
    }


    /**
     * Test of setNumberOfDeveloperFields method, of class FitRecord.
     */
    @Test
    public void testGetNumberOfDeveloperFields()
    {
        System.out.println("getNumberOfDeveloperFields");
        assertEquals(1, instance.getNumberOfDeveloperFields());
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
        
        assertEquals(7, instance.getNumberOfFields());
        assertNull(instance.getMessageField("power"));
        instance.addMessageField(20, 7, 2, 132);      // 'power'
        assertEquals(8, instance.getNumberOfFields());
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

        assertEquals(1, instance.getNumberOfDeveloperFields());
        instance.addDeveloperField(20, 3, 1, 3, developerFieldDefinition);
        assertEquals(2, instance.getNumberOfDeveloperFields());
        FitDeveloperField field=instance.getDeveloperFieldDefintions().get(1);
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
     * Test of getRecordLength method, of class FitRecord.
     */
    @Test
    public void testGetRecordLength()
    {
        System.out.println("getRecordLength");
        assertEquals(26, this.developerFieldDefinition.getRecordLength());
    }

    /**
     * Test of addRecordValues method, of class FitRecord.
     */
    @Test
    public void testAddRecordValues()
    {
        System.out.println("addRecordValues");
        int[] record={0x80, 0xF0, 0xFA, 0x02,
                      0x02, 0xFA, 0xF0, 0x80,
                      100, 0,
                      96,
                      0, 0, 0, 0,
                      100,0,0,0,
                      5,0,
                      11};
        instance.addRecordValues(record);
        assertEquals(2, instance.getNumberOfRecordValues());
        assertEquals(96, instance.getIntValue(1, "heart_rate"));
        assertEquals(50000000, instance.getIntValue(1, "position_lat"));
    }

    /**
     * Test of getNumberOfRecordValues method, of class FitRecord.
     */
    @Test
    public void testGetNumberOfRecordValues()
    {
        System.out.println("getNumberOfRecordValues");
        assertEquals(2, developerFieldDefinition.getNumberOfRecordValues());
        assertEquals(1, instance.getNumberOfRecordValues());
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
     * Test of getIntValue method, of class FitRecord.
     */
    @Test
    public void testGetIntValue()
    {
        System.out.println("getIntValue");
        assertEquals(59642324, instance.getIntValue(0, "position_lat"));
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
     * Test of getTimeValue method, of class FitRecord.
     */
    @Test
    public void testGetTimeValue_int_String()
    {
        System.out.println("getTimeValue");
        assertEquals("1989-12-31 00:00:00", instance.getTimeValue(0, "timestamp").format("YYYY-MM-DD hh:mm:ss"));
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
        assertEquals("test1", developerFieldDefinition.getStringValue(0, "field_name"));
    }


    /**
     * Test of getGlobalFieldDefintions method, of class FitRecord.
     */
    @Test
    public void testGetGlobalFieldDefintions()
    {
        System.out.println("getGlobalFieldDefintions");
        List<FitMessageField> result = instance.getGlobalFieldDefintions();
        assertEquals(7, result.size());
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
        
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).fieldNumber);
        assertEquals(3, result.get(0).developerDataIndex);
        assertEquals("test1", result.get(0).fieldName);
        assertEquals("bpm", result.get(0).units);
        assertEquals(123, result.get(0).nativeMessageNumber);
        assertEquals(234, result.get(0).nativeFieldNumber);
        assertEquals("uint8", result.get(0).baseType);
    }
    
    @Test
    public void testGetMessageFieldNames()
    {
        System.out.println("getMessageFieldNames");
        List<String> names=instance.getMessageFieldNames();
        
        assertEquals(7, names.size());
        assertEquals("position_lat", names.get(0));
        assertEquals("position_long", names.get(1));
        assertEquals("altitude", names.get(2));
        assertEquals("heart_rate", names.get(3));
        assertEquals("timestamp", names.get(4));
        assertEquals("distance", names.get(5));
        assertEquals("speed", names.get(6));
        
    }

    @Test
    public void testGetDeveloperFieldNames()
    {
        System.out.println("getDeveloperFieldNames");
        List<String> names=instance.getDeveloperFieldNames();
        
        assertEquals(1, names.size());
        assertEquals("test1", names.get(0));
    }
    
}
