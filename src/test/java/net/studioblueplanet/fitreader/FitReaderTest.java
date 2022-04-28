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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author Jorgen
 */
public class FitReaderTest
{
    private FitReader instance; 
    
    public FitReaderTest()
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
        instance=FitReader.getInstance();
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of getInstance method, of class FitReader.
     */
    @Test
    public void testGetInstance()
    {
        System.out.println("getInstance");
        FitReader expResult = null;
        FitReader result = FitReader.getInstance();
        
        assertNotEquals(expResult, result);
        FitReader result2 = FitReader.getInstance();
        assertEquals(result, result2);
    }

    
    /**
     * Reads a test FIT file.
     * @return 
     */
    private FitMessageRepository readTestFitFile()
    {
        FitMessageRepository repository;     
        FitReader           reader;
        InputStream         in;
        byte[]              inputBytes;        
      
        reader=FitReader.getInstance();
        
        // Simple fit file: file_id (1 record), file_created (1 record), waypoints (2 records). All one record
        inputBytes=javax.xml.bind.DatatypeConverter.parseHexBinary("0E107D06180100002E4649540000"+                          // header (no CRC, CRC=0)
                
                                                                   "40000000000603048C040486010284020284050284000100"+      // definition message 0x00 - file_id
                                                                   "0082224BEAFFFFFFFF01001F06FFFF08"+                      // data message 0x01
                                                                   
                                                                   "410000310002000284010102"+                              // definition message 0x01 - file_created
                                                                   "01FE01FF"+
                
                                                                   "4200001D0009FD0486001007010485020485FE0284030284040284050284063207"+    // definition message 0x02 - waypoints
                                                                   "0247FD6432746573745F74726B202D205374617200"+                            // data message 0x02
                                                                   "04462225A979F00400005F00FFFFFFFF"+
                                                                   "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"+
                                                                   "02FFFFFFFF4761726D696E2054616977616E000000"+                            // data message 0x02
                                                                   "C25BD2114DEF7F5605005E00820AFFFF"+
                                                                   "7468697320697320736f6d652066756e6e792074657874207468617420736572766573206173206465736372697074696f6E"+
                
                                                                   "0000");                                                                 // crc
        in=new ByteArrayInputStream(inputBytes);

        repository=reader.readInputStream(in, false);

        return repository;
    }


    /**
     * Reads a test FIT file.
     * @return 
     */
    private FitMessageRepository compressedTimeStampFile()
    {
        FitMessageRepository repository;     
        FitReader           reader;
        InputStream         in;
        byte[]              inputBytes;        
      
        reader=FitReader.getInstance();
        
        // Simple fit file: file_id (1 record), file_created (1 record), waypoints (2 records). All one record
        inputBytes=javax.xml.bind.DatatypeConverter.parseHexBinary("0E107D064C0000002E4649540000"+                          // header (no CRC: CRC=0)
                
                                                                   "400000140003FD0486000485010485"+                        // 15 definition message 0x00 - record - timestamp position_lat position_long
                                                                   "4100001D0002010485020485"+                              // 12 definition message 0x01 - waypoints - position_lat position_long

                                                                   "00250AC628D4118E032CEE71FC"+                           // 13 data message 0x00
                                                                   "A5D4118E032CEE71FC"+                                   //  9 data message 0x01 - compressed
                                                                   "A7D4118E032CEE71FC"+                                   //  9 data message 0x01 - compressed
                                                                   "AFD4118E032CEE71FC"+                                   //  9 data message 0x01 - compressed
                                                                   "A2D4118E032CEE71FC"+                                   //  9 data message 0x01 - compressed
                                                                   "8AE0");                                                // crc
        in=new ByteArrayInputStream(inputBytes);

        repository=reader.readInputStream(in, false);

        return repository;
    }
    
    @Test
    public void testReadFile()
    {
        FitMessageRepository    repository;
        FitMessage               message;
        int                     size;
        int                     i;

        System.out.println("readFile");

        repository=instance.readFile("src/test/resources/Activity.fit", false);
        
        message=repository.getFitMessage("record");
        size=message.getNumberOfRecords();
    
        assertEquals(3601, size);
        assertEquals(9, message.getNumberOfFields());
        assertEquals(1, message.getNumberOfDeveloperFields());
        
        assertEquals("2021-07-20T21:11:20Z[UTC]"    , message.getTimeValue(0, "timestamp").toString());
        assertEquals(   0, message.getIntValue(0, "distance", false));
        assertEquals(1000, message.getIntValue(1, "speed", false));
        assertEquals( 1.0, message.getSpeedValue(1, "speed"), 0.0001);
        assertEquals( 126, message.getIntValue(0, "heart_rate", false));
        
        // Developer field
        assertEquals( 126, message.getIntValue(0, "Heart Rate", true));
        
        
        message=repository.getFitMessage("device_info");
        size=message.getNumberOfRecords();
        assertEquals(1, size);
        assertEquals("FIT Cookbook", message.getStringValue(0, "product_name"));
        assertEquals(1457061125, message.getIntValue(0, "serial_number", false));

        
        repository=instance.readFile("src/test/resources/ActivityEdge830.fit", false);
        
        message=repository.getFitMessage("record");
        size=message.getNumberOfRecords();
    
        assertEquals(2023, size);
        
        assertEquals(53.0190818477, message.getLatLonValue(100, "position_lat"), 0.00000001);
        assertEquals(6.7377460096, message.getLatLonValue(100, "position_long"), 0.00000001);
        assertEquals(17.0, message.getAltitudeValue(100, "altitude"), 0.000001);
        
    }
        
    @Test
    public void testReadFile2()
    {
        FitMessageRepository    repository;
        FitMessage               message;
        int                     size;
        int                     i;

        System.out.println("readFile 2");

        repository=instance.readFile("src/test/resources/ActivityEdge830.fit", false);
        
        repository.dumpMessageDefintions();
        
        message=repository.getFitMessage("record");
        size=message.getNumberOfRecords();   
        
        assertEquals(2023, size);
        assertEquals(12, message.getNumberOfFields());
        assertEquals( 0, message.getNumberOfDeveloperFields());
        assertEquals(53.01270539, message.getLatLonValue(0, "position_lat") , 0.00001);
        assertEquals( 6.72536795, message.getLatLonValue(0, "position_long"), 0.00001);
    }
    
    @Test
    public void testReadFile3()
    {
        FitMessageRepository    repository;
        FitMessage               message;
        int                     size;
        int                     i;

        System.out.println("readFile 3");

        repository=instance.readFile("src/test/resources/ActivityFenix7.fit", false);
        
        repository.dumpMessageDefintions();
        
        message=repository.getFitMessage("record");
        size=message.getNumberOfRecords();   
        
        assertEquals(1372, size);
        assertEquals(11, message.getNumberOfFields());
        assertEquals( 0, message.getNumberOfDeveloperFields());
        assertEquals(69, message.getIntValue(1, "heart_rate"));
        
        assertEquals(53.01286557689309, message.getLatLonValue(0, "position_lat") , 0.00001);
        assertEquals(6.724741822108626, message.getLatLonValue(0, "position_long"), 0.00001);

        assertEquals(124, message.getIntValue(1371, "heart_rate"));
        assertEquals(1.39, message.getScaledValue(1371, "enhanced_speed"), 0.0001);
        assertEquals(20.2, message.getScaledValue(1371, "enhanced_altitude"), 0.0001);

        message=repository.getFitMessage("file_id");
        assertEquals(3906, message.getIntValue(0, "product"));
        // Not in the spec yet...
//        assertEquals("fenix 7 Solar", FitGlobalProfile.getInstance().getTypeValueName("garmin_product", (int)message.getIntValue(0, "product")));
    }    
  
    @Test
    public void testReadCompressedTimeStampFile()
    {
        FitMessageRepository    repository;
        FitMessage               message;
        int                     size;
        int                     i;

        System.out.println("compressedTimeStampFile");

        repository=compressedTimeStampFile();
        
        message=repository.getFitMessage("record");
        size=message.getNumberOfRecords();   
        
        assertEquals(1, size);
        assertEquals("2011-09-04T10:42:45Z[UTC]", message.getTimeValue(0, "timestamp").toString());
        assertEquals( 4.9991618469, message.getLatLonValue(0, "position_lat") , 0.0000001);
        assertEquals(-4.9991618469, message.getLatLonValue(0, "position_long"), 0.0000001);
         
        // Compressed timestamps
        message=repository.getFitMessage("location");
        size=message.getNumberOfRecords();   
        assertEquals(4, size);
        assertEquals("2011-09-04T10:42:45Z[UTC]", message.getTimeValue(0, "timestamp").toString());
        assertEquals("2011-09-04T10:42:47Z[UTC]", message.getTimeValue(1, "timestamp").toString());
        assertEquals("2011-09-04T10:42:55Z[UTC]", message.getTimeValue(2, "timestamp").toString());
        assertEquals("2011-09-04T10:43:14Z[UTC]", message.getTimeValue(3, "timestamp").toString());
    }  
    
    @Test
    public void testReadFileCrc()
    {
        FitMessageRepository    repository;
        List<String>            messages;
        
        System.out.println("readFile");
        
        // Apperently a file with a CRC ERROR!!!!!!
        FitReader instance=FitReader.getInstance();
        repository=instance.readFile("src/test/resources/LocationsEdge810.fit", false);
        assertNull(repository);
        repository=instance.readFile("src/test/resources/LocationsEdge810.fit", true);
        assertNotNull(repository);
        
        repository=instance.readFile("src/test/resources/LocationsEdge830.fit", false);
        assertNotNull(repository);
    }
}
