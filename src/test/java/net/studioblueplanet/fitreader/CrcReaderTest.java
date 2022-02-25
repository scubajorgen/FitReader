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
        byte[]       inputBytes;
        instance=new CrcReader();
        inputBytes=javax.xml.bind.DatatypeConverter.parseHexBinary("0F121122123456782e4649541C25");
        in=new ByteArrayInputStream(inputBytes);
        inputBytes=javax.xml.bind.DatatypeConverter.parseHexBinary("0F121122123456782e4649541C26");
        inFail=new ByteArrayInputStream(inputBytes);
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of reset method, of class CrcReader.
     */
    @Test
    public void testReset() throws IOException
    {
        System.out.println("reset");
        CrcReader instance = new CrcReader();

        in.reset();
        instance.read(in);
        assertEquals(1, instance.getNumberOfBytesRead());
        assertEquals(false, instance.isValid());
        instance.reset();
        assertEquals(0, instance.getNumberOfBytesRead());
        assertEquals(true, instance.isValid());
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
