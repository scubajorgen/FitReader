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
public class ProfileTypeValueTest
{
    private static ProfileTypeValue instance;
    
    public ProfileTypeValueTest()
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
        instance=new ProfileTypeValue("test", 5);
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of getValueName method, of class ProfileTypeValue.
     */
    @Test
    public void testGetValueName()
    {
        System.out.println("getValueName");
        String expResult = "test";
        String result = instance.getValueName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setValueString method, of class ProfileTypeValue.
     */
    @Test
    public void testSetValueString()
    {
        System.out.println("setValueString");
        String valueName = "test2";
        instance.setValueName(valueName);
        String result = instance.getValueName();
        assertEquals(valueName, result);
    }

    /**
     * Test of getValue method, of class ProfileTypeValue.
     */
    @Test
    public void testGetValue()
    {
        System.out.println("getValue");
        long expResult = 5;
        long result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class ProfileTypeValue.
     */
    @Test
    public void testSetValue()
    {
        System.out.println("setValue");
        long value = 7;
        instance.setValue(value);
        long result = instance.getValue();
        assertEquals(value, result);
    }
    
}
