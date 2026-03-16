/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import jakarta.xml.bind.DatatypeConverter;
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
        // Nothing to be done here
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        // Nothing to be done here
    }
    
    @AfterClass
    public static void tearDownClass()
    {
        // Nothing to be done here
    }
    
    @Before
    public void setUp()
    {
        // Nothing to be done here
    }
    
    @After
    public void tearDown()
    {
        // Nothing to be done here
    }

    /**
     * Test of readHeader method, of class FitHeader.
     */
    @Test
    public void testReadHeader() throws Exception
    {
        InputStream in;
        byte[]      inputBytes;
        FitHeader   header;
        
        System.out.println("readHeader");


        inputBytes=DatatypeConverter.parseHexBinary("0F121122123456782e4649541C25");
        in=new ByteArrayInputStream(inputBytes);

        header = FitHeader.readHeader(in, false);
        
        assertEquals(0x0F, header.getHeaderSize());
        assertEquals(0x12, header.getProtocolVersion());
        assertEquals(0x2211, header.getProfileVersion());
        assertEquals(0x78563412, header.getDataSize());
        assertEquals(".FIT", header.getDataType());
        assertEquals(0x251C, header.getCrc());

    }

    /**
     * Test of readHeader method, of class FitHeader.
     */
    @Test
    public void testReadHeaderCrc() throws Exception
    {
        InputStream in;
        byte[]      inputBytes;
        FitHeader   header;
        
        System.out.println("readHeaderCrc");

        // Correct CRC
        inputBytes=DatatypeConverter.parseHexBinary("0E105C08B42301002E464954DEAF");
        in=new ByteArrayInputStream(inputBytes);
        header = FitHeader.readHeader(in, false);
        assertNotNull(header);

        // No CRC
        inputBytes=DatatypeConverter.parseHexBinary("0E105C08B42301002E4649540000");
        in=new ByteArrayInputStream(inputBytes);
        header = FitHeader.readHeader(in, false);
        assertNotNull(header);

        // invalid CRC
        inputBytes=DatatypeConverter.parseHexBinary("0E105C08B42301002E464954DEAE");
        in=new ByteArrayInputStream(inputBytes);
        header = FitHeader.readHeader(in, false);
        assertNull(header);
        header = FitHeader.readHeader(in, true);
        assertNotNull(header);
    }
}
