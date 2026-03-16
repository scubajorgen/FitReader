/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import jakarta.xml.bind.DatatypeConverter;


/**
 *
 * @author jorgen
 */
public class CrcReaderTest
{
    private CrcReader   instance;
    private             InputStream in;
    private             InputStream inFail;
    
    public CrcReaderTest()
    {
        // Nothing to be done here yet
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
        byte[]       inputBytes;
        instance=new CrcReader();
        inputBytes=DatatypeConverter.parseHexBinary("0F121122123456782e4649541C25");
        in=new ByteArrayInputStream(inputBytes);
        inputBytes=DatatypeConverter.parseHexBinary("0F121122123456782e4649541C26");
        inFail=new ByteArrayInputStream(inputBytes);
    }

    @After
    public void tearDown()
    {
        // Nothing to be done here
    }

    /**
     * Test of reset method, of class CrcReader.
     */
    @Test
    public void testReset() throws IOException
    {
        System.out.println("reset");
        CrcReader instance2 = new CrcReader();

        in.reset();
        instance2.read(in);
        assertEquals(1, instance2.getNumberOfBytesRead());
        assertEquals(false, instance2.isValid());
        instance2.reset();
        assertEquals(0, instance2.getNumberOfBytesRead());
        assertEquals(true, instance2.isValid());
    }

    /**
     * Test of read method, of class CrcReader.
     */
    @Test
    public void testRead() throws Exception
    {
        System.out.println("read");

        in.reset();
        instance.reset();
        assertEquals(0x0f, instance.read(in));
        assertEquals(0x12, instance.read(in));
        assertEquals(2, instance.getNumberOfBytesRead());
    }

    /**
     * Test of isValid method, of class CrcReader.
     */
    @Test
    public void testIsValid() throws IOException
    {
        System.out.println("isValid");

        instance.reset();
        in.reset();
        int i=0;
        while (i<14)
        {
            instance.read(in);
            i++;
        }
        assertEquals(true, instance.isValid());

        instance.reset();
        inFail.reset();
        i=0;
        while (i<14)
        {
            instance.read(inFail);
            i++;
        }
        assertEquals(false, instance.isValid());
        
    }

    /**
     * Test of getNumberOfBytesRead method, of class CrcReader.
     */
    @Test
    public void testGetNumberOfBytesRead() throws IOException
    {
        System.out.println("getNumberOfBytesRead");

        in.reset();
        instance.reset();
        assertEquals(0, instance.getNumberOfBytesRead());
        assertEquals(0x0f, instance.read(in));
        assertEquals(0x12, instance.read(in));
        assertEquals(2, instance.getNumberOfBytesRead());     
        
        CrcReader reader=new CrcReader();
        assertEquals(0, reader.getNumberOfBytesRead());
        assertEquals(true, reader.isValid());
    }

}
