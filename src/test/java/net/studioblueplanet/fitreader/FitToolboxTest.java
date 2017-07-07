/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 *
 * @author Jorgen
 */
public class FitToolboxTest
{
    
    public FitToolboxTest()
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
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of readInt method, of class FitToolbox.
     */
    @Test
    public void testReadInt() throws Exception
    {
        InputStream in;
        byte[]      inputBytes;
        int         bytes;
        boolean     isLittleEndian;
        int         expResult;
        int         result;
        
        System.out.println("readInt");
  
        inputBytes=javax.xml.bind.DatatypeConverter.parseHexBinary("12345678abcdef");
        in=new ByteArrayInputStream(inputBytes);
        
        
        bytes           =1;
        isLittleEndian  =false;
        expResult       =0x12;
        result          = FitToolbox.readInt(in, bytes, isLittleEndian);
        assertEquals(expResult, result);
    }

    /**
     * Test of readString method, of class FitToolbox.
     */
    @Test
    public void testReadString() throws Exception
    {
        InputStream in;
        String      inputString;
        int         bytes;
        String      expResult;
        String      result;
        
        System.out.println("readString");
  
        inputString="This is a test";
        in=new ByteArrayInputStream(inputString.getBytes("UTF-8"));
        
        
        bytes           =4;
        expResult       ="This";
        result          = FitToolbox.readString(in, bytes);
        assertEquals(expResult, result);

        bytes           =3;
        expResult       =" is";
        result          = FitToolbox.readString(in, bytes);
        assertEquals(expResult, result);

        bytes           =7;
        expResult       =" a test";
        result          = FitToolbox.readString(in, bytes);
        assertEquals(expResult, result);

    }

    
}
