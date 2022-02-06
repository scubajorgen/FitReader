/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jorgen
 */
public class FitGlobalProfileTest
{
    FitGlobalProfile instance;
    
    public FitGlobalProfileTest()
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
        instance=FitGlobalProfile.getInstance();
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of getInstance method, of class FitGlobalProfile.
     */
    @Test
    public void testGetInstance()
    {
        System.out.println("getInstance");

        assertNotNull(instance);
        
        FitGlobalProfile nextResult = FitGlobalProfile.getInstance();
        assertEquals(instance, nextResult);
    }

    /**
     * Test of getGlobalMessageDescription method, of class FitGlobalProfile.
     */
    @Test
    public void testGetGlobalMessageName()
    {
        System.out.println("getGlobalMessageDescription");
        assertEquals("file_id", instance.getGlobalMessageName(0));
        assertEquals("met_zone", instance.getGlobalMessageName(10));
        assertEquals("not found", instance.getGlobalMessageName(999));
    }

    /**
     * Test of getGlobalMessageNumber method, of class FitGlobalProfile.
     */
    @Test
    public void testGetGlobalMessageNumber()
    {
        System.out.println("getGlobalMessageNumber");
        int expResult = 0;
        assertEquals(0, instance.getGlobalMessageNumber("file_id"));
        assertEquals(10, instance.getGlobalMessageNumber("met_zone"));
        assertEquals(65535, instance.getGlobalMessageNumber("non existing name"));
    }

    /**
     * Test of getMessageFieldDescription method, of class FitGlobalProfile.
     */
    @Test
    public void testGetMessageFieldName()
    {
        System.out.println("getMessageFieldName");
        assertEquals("type", instance.getMessageFieldName(0, 0));
        assertEquals("compressed_accumulated_power", instance.getMessageFieldName(20, 28));
    }

    /**
     * Test of getMessageField method, of class FitGlobalProfile.
     */
    @Test
    public void testGetMessageField()
    {
        System.out.println("getMessageField - by number");
        FitFieldDefinition definition = instance.getMessageField(0, 0);
        assertEquals(0, definition.fieldNumber);
        assertEquals("type", definition.fieldName);
        assertEquals(0, definition.messageNumber);
        assertEquals("file", definition.fieldType);

        definition = instance.getMessageField(9, 2);
        assertEquals(2, definition.fieldNumber);
        assertEquals("name", definition.fieldName);
        assertEquals(9, definition.messageNumber);
        assertEquals("string", definition.fieldType);
        
        definition = instance.getMessageField(999, 999);
        assertEquals(-1, definition.fieldNumber);
        assertEquals("not found", definition.fieldName);
        assertEquals(999, definition.messageNumber);
        assertEquals(null, definition.fieldType);
    }

    
    /**
     * Test of getMessageField method, of class FitGlobalProfile.
     */
    @Test
    public void testGetMessageFieldByName()
    {
        System.out.println("getMessageField - by name");
        FitFieldDefinition definition = instance.getMessageField("file_id", "serial_number");
        assertNotNull(definition);
        assertEquals(0, definition.messageNumber);
        assertEquals(3, definition.fieldNumber);

        definition = instance.getMessageField("file_id", "garmin_product");
        assertNotNull(definition);
        assertEquals(0, definition.messageNumber);
        assertEquals(-1, definition.fieldNumber);

        definition = instance.getMessageField("non", "existing");
        assertNull(definition);
    }
    
    /**
     * Test of getBaseTypeDescription method, of class FitGlobalProfile.
     */
    @Test
    public void testGetBaseTypeName()
    {
        System.out.println("getBaseTypeName");
        assertEquals("uint16z", instance.getBaseTypeName(139));
        assertEquals("enum", instance.getBaseTypeName(0));
        assertEquals("not found", instance.getBaseTypeName(999));
    }

    /**
     * Test of sintToLatLon method, of class FitGlobalProfile.
     */
    /*
    @Test
    public void testSintToLatLon()
    {
        System.out.println("sintToLatLon");
        int value = 0;
        double expResult = 0.0;
        double result = FitGlobalProfile.sintToLatLon(value);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
   
}
