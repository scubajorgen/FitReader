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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;


import net.studioblueplanet.logger.DebugLogger;

/**
 *
 * @author Jorgen
 */
public class FitReaderTest
{
    
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
    private FitRecordRepository readTestFitFile()
    {
        FitRecordRepository repository;     
        FitReader           reader;
        InputStream         in;
        byte[]              inputBytes;        
      
        reader=FitReader.getInstance();
        
        // Simple fit file: file_id (1 record), file_created (1 record), waypoints (2 records). All one record
        inputBytes=javax.xml.bind.DatatypeConverter.parseHexBinary("0E107D06180100002E4649540000"+                          // header
                
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

        repository=reader.readInputStream(in);

        return repository;
    }
    
    /**
     * Reads a test FIT file.
     * @return 
     */
    private FitRecordRepository readFitFile(String fileName)
    {
        FitRecordRepository repository;     
        FitReader           reader;
        InputStream         in;
        byte[]              inputBytes;        
      
        repository=null;
        reader=FitReader.getInstance();

        repository=reader.readFile(fileName);

        return repository;
    }
    
   
    @Test
    public void testReadFile()
    {
        FitRecordRepository repository;
        FitRecord           record;
        int                 size;
        int                 i;

        System.out.println("readFile");
        DebugLogger.setDebugLevel(DebugLogger.DEBUGLEVEL_INFO);

        repository=this.readFitFile("src/test/resources/Activity.fit");
        
        record=repository.getFitRecord("record");
        size=record.getNumberOfRecordValues();
    
        assertEquals(3601, size);
        assertEquals(9, record.getNumberOfFields());
        assertEquals(1, record.getNumberOfDeveloperFields());
        
        assertEquals("2021-07-20 21:11:20.000000000"    , record.getTimeValue        (0, "timestamp").toString());
        assertEquals(   0, record.getIntValue(0, "distance"));
        assertEquals(1000, record.getIntValue(1, "speed"));
        assertEquals( 1.0, record.getSpeedValue(1, "speed"), 0.0001);
        assertEquals( 126, record.getIntValue(0, "heart_rate"));
        
        
        record=repository.getFitRecord("device_info");
        size=record.getNumberOfRecordValues();
        assertEquals(1, size);
        assertEquals("FIT Cookbook", record.getStringValue(0, "product_name"));
        assertEquals(1457061125, record.getIntValue(0, "serial_number"));

        
        repository=this.readFitFile("src/test/resources/ActivityEdge830.fit");
        
        record=repository.getFitRecord("record");
        size=record.getNumberOfRecordValues();
    
        assertEquals(2023, size);
        
        assertEquals(53.0190818477, record.getLatLonValue(100, "position_lat"), 0.00000001);
        assertEquals(6.7377460096, record.getLatLonValue(100, "position_long"), 0.00000001);
        assertEquals(17.0, record.getAltitudeValue(100, "altitude"), 0.000001);
        
    }
    
    @Test
    public void testRecordRepository()
    {
        FitRecordRepository repository;
        ArrayList<String>   messages;
        
        System.out.println("FitRecordRepository");
        DebugLogger.setDebugLevel(DebugLogger.DEBUGLEVEL_INFO);
        
        repository=this.readFitFile("src/test/resources/Activity.fit");
        
        System.out.println("Fields: "+repository.getMessages().toString());
        
        messages=repository.getMessages();
        
        assertEquals("file_id", messages.get(0));
        assertEquals("device_info", messages.get(1));
        assertEquals("event", messages.get(2));
        assertEquals("activity", messages.get(10));
        
        assertEquals(true, repository.recordExists(0));
        assertEquals(false, repository.recordExists(1));
        assertEquals(false, repository.recordExists(4));
        
        assertNotEquals(null, repository.getFitRecord("file_id"));
        assertEquals(null, repository.getFitRecord("blah_blah"));
        
        assertNotNull(repository.getFitRecord(0));
        assertNull(repository.getFitRecord(4));
        assertNull(repository.getFitRecord(-1));
    }

    @Test
    public void testLocationsRepository()
    {
        FitRecordRepository repository;
        ArrayList<String>   messages;
        
        System.out.println("FitRecordRepository");
        DebugLogger.setDebugLevel(DebugLogger.DEBUGLEVEL_DEBUG);
        
        repository=this.readFitFile("src/test/resources/LocationsEdge810.fit");
        
        System.out.println("Fields: "+repository.getMessages().toString());
        
        messages=repository.getMessages();
        
        // The 'waypoint' field (id=29) no longer exists...
        assertEquals("waypoints", messages.get(2));
        
        repository=this.readFitFile("src/test/resources/LocationsEdge830.fit");
        
        System.out.println("Fields: "+repository.getMessages().toString());
        
        messages=repository.getMessages();
        
        // The 'waypoint' field (id=29) no longer exists...
        assertEquals("waypoints", messages.get(2));
    }

}
