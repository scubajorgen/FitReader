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

import net.studioblueplanet.fitreader.FitMessage.Endianness;

/**
 *
 * @author jorgen
 */
public class FitDataRecordTest
{
    private FitDataRecord instanceLE;
    private FitDataRecord instanceBE;
    
    public FitDataRecordTest()
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
        int[] data={0x01, 0x23, 0x45, 0x67, 0xFE, 0xDC, 0xBA, 0x98,
                    't', 'e', 's','t', 0, 0, 0, 0,
                    0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff};
        instanceLE=new FitDataRecord(data, Endianness.LITTLEENDIAN);
        instanceBE=new FitDataRecord(data, Endianness.BIGENDIAN);
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of bytesToUnsignedInt method, of class FitDataRecord.
     */
    @Test
    public void testBytesToUnsignedInt()
    {
        System.out.println("bytesToUnsignedInt");
        
        assertEquals(  0x01, instanceLE.bytesToUnsignedInt(0, 1));
        assertEquals(0xFE67, instanceLE.bytesToUnsignedInt(3, 2));
        assertEquals(0xFEDC, instanceBE.bytesToUnsignedInt(4, 2));
        assertEquals(0xFFFFFFFFL, instanceBE.bytesToUnsignedInt(16, 8));
        assertEquals(4294967295L, instanceBE.bytesToUnsignedInt(16, 8));
    }

    /**
     * Test of bytesToSignedInt method, of class FitDataRecord.
     */
    @Test
    public void testBytesToSignedInt()
    {
        System.out.println("bytesToSignedInt");
        assertEquals(-1, instanceLE.bytesToSignedInt(16, 1));
        assertEquals(-1, instanceLE.bytesToSignedInt(16, 2));
        assertEquals(-1, instanceLE.bytesToSignedInt(16, 4));
        assertEquals(-1, instanceBE.bytesToSignedInt(16, 1));
        assertEquals(-1, instanceBE.bytesToSignedInt(16, 2));
        assertEquals(-1, instanceBE.bytesToSignedInt(16, 4));

        assertEquals(   1, instanceLE.bytesToSignedInt(0, 1));
        assertEquals(-409, instanceLE.bytesToSignedInt(3, 2));

    }

    /**
     * Test of bytestToUnsignedLong method, of class FitDataRecord.
     */
    @Test
    public void testBytestToUnsignedLong()
    {
        System.out.println("bytestToUnsignedLong");
        assertEquals(0xFFFFFFFFFFFFFFFFL, instanceLE.bytesToUnsignedLong(16, 8)); // FAIL, this is not unsigned
        assertEquals(0x01234567FEDCBA98L, instanceBE.bytesToUnsignedLong( 0, 8)); // 
        assertEquals(0x98BADCFE67452301L, instanceLE.bytesToUnsignedLong( 0, 8)); // FAIL, this is not unsigned

        assertEquals(     1, instanceLE.bytesToUnsignedLong(0, 1));
        assertEquals( 65127, instanceLE.bytesToUnsignedLong(3, 2));
        assertEquals( 65244, instanceBE.bytesToUnsignedLong(4, 2));
    }

    /**
     * Test of bytesToSignedLong method, of class FitDataRecord.
     */
    @Test
    public void testBytesToSignedLong()
    {
        System.out.println("bytesToSignedLong");
        assertEquals(-1L, instanceLE.bytesToSignedLong(16, 8));
        assertEquals(0x01234567FEDCBA98L, instanceBE.bytesToSignedLong( 0, 8));
        assertEquals(0x98BADCFE67452301L, instanceLE.bytesToSignedLong( 0, 8));

        assertEquals(     1, instanceLE.bytesToSignedLong(0, 1));
        assertEquals(  -409, instanceLE.bytesToSignedLong(3, 2));
        assertEquals(  -292, instanceBE.bytesToSignedLong(4, 2));
    }

    /**
     * Test of bytesToString method, of class FitDataRecord.
     */
    @Test
    public void testBytesToString()
    {
        System.out.println("bytesToString");
        assertEquals("test", instanceLE.bytesToString(8, 8));
        assertEquals("test", instanceBE.bytesToString(8, 8));
    }

    /**
     * Test of isInvalidValue method, of class FitDataRecord.
     */
    @Test
    public void testIsInvalidValue()
    {
        assertEquals(true , FitDataRecord.isInvalidValue(0xFF, FitDataRecord.BASETYPE_BYTE));
        assertEquals(false, FitDataRecord.isInvalidValue(0x7F, FitDataRecord.BASETYPE_BYTE));
        
        assertEquals(true , FitDataRecord.isInvalidValue(0xFFFFFFFF, FitDataRecord.BASETYPE_UINT32));
        assertEquals(true , FitDataRecord.isInvalidValue(0x7FFFFFFF, FitDataRecord.BASETYPE_SINT32));
        assertEquals(true , FitDataRecord.isInvalidValue(0x00000000, FitDataRecord.BASETYPE_UINT32Z));
        assertEquals(false, FitDataRecord.isInvalidValue(0x00000000, FitDataRecord.BASETYPE_UINT32));
        assertEquals(false, FitDataRecord.isInvalidValue(0x00000000, FitDataRecord.BASETYPE_SINT32));
        assertEquals(false, FitDataRecord.isInvalidValue(0x00000001, FitDataRecord.BASETYPE_UINT32Z));
    }
    
}
