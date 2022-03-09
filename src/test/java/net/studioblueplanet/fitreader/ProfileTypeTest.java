/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import java.util.ArrayList;
import java.util.List;
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
public class ProfileTypeTest
{
    private ProfileType instance;
    
    public ProfileTypeTest()
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
        instance=new ProfileType("gender", "enum");
        instance.addTypeValue("female", 0);
        instance.addTypeValue("male"  , 1);
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of addTypeValue method, of class ProfileType.
     */
    @Test
    public void testAddTypeValue()
    {
        System.out.println("addTypeValue");
        String valueName = "neutral";
        int value = 2;
        instance.addTypeValue(valueName, value);
        
        assertEquals(value, instance.getValues().get(2).getValue());
        assertEquals(valueName, instance.getValues().get(2).getValueName());
    }

    /**
     * Test of getType method, of class ProfileType.
     */
    @Test
    public void testGetSetType()
    {
        System.out.println("getSetType");
        String expResult = "genderr";
        instance.setType(expResult);
        String result = instance.getType();
        assertEquals(expResult, result);
    }



    /**
     * Test of getValues method, of class ProfileType.
     */
    @Test
    public void testGetSetValues()
    {
        System.out.println("getSetValues");
        List<ProfileTypeValue> expResult = new ArrayList<>();
        instance.setValues(expResult);
        List<ProfileTypeValue> result = instance.getValues();
        assertEquals(expResult, result);

    }

    /**
     * Test of getBaseType method, of class ProfileType.
     */
    @Test
    public void testGetSetBaseType()
    {
        System.out.println("getSetBaseType");
        String expResult = "uint16";
        instance.setBaseType(expResult);
        String result = instance.getBaseType();
        assertEquals(expResult, result);
    }


    /**
     * Test of getValueByName method, of class ProfileType.
     */
    @Test
    public void testGetValueByName()
    {
        System.out.println("getValueByName");
        String valueName = "male";
        long expResult = 1;
        long result = instance.getValueByName(valueName);
        assertEquals(expResult, result);
        
        result=instance.getValueByName("non existing name");
        assertEquals(-1, result);

    }

    /**
     * Test of getValueName method, of class ProfileType.
     */
    @Test
    public void testGetValueName()
    {
        System.out.println("getValueName");
        int value = 1;
        String expResult = "male";
        String result = instance.getValueName(value);
        assertEquals(expResult, result);
    }

    /**
     * Test of valueExists method, of class ProfileType.
     */
    @Test
    public void testValueExists()
    {
        System.out.println("valueExists");
        assertEquals(true, instance.valueExists("male"));
        assertEquals(true, instance.valueExists("female"));
        assertEquals(false, instance.valueExists("does not exist"));
    }

    /**
     * Test of removeValueByName method, of class ProfileType.
     */
    @Test
    public void testRemoveValueByName()
    {
        System.out.println("removeValueByName");
        assertEquals(true, instance.valueExists("male"));
        assertEquals(true, instance.valueExists("female"));
        instance.removeValueByName("female");
        assertEquals(true, instance.valueExists("male"));
        assertEquals(false, instance.valueExists("female"));
    }

}
