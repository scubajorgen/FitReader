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

/**
 *
 * @author Jorgen
 */
public class FitHeaderTest
{
    
    public FitHeaderTest()
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
     * Test of readHeader method, of class FitHeader.
     */
    @Test
    public void testReadHeader() throws Exception
    {
        InputStream in;
        FitHeader   expResult;
        byte[]      inputBytes;
        FitHeader   header;
        
        System.out.println("readHeader");


        inputBytes=javax.xml.bind.DatatypeConverter.parseHexBinary("0F121122123456782e4649541234");
        in=new ByteArrayInputStream(inputBytes);

        
        
        header = FitHeader.readHeader(in);
        
        assertEquals(header.getHeaderSize()     , 0x0F);
        assertEquals(header.getProtocolVersion(), 0x12);
        assertEquals(header.getProfileVersion() , 0x2211);
        assertEquals(header.getDataSize()       , 0x78563412);
        assertEquals(header.getDataType()       , ".FIT");
        assertEquals(header.getCrc()            , 0x3412);

    }
}
