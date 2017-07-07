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
    
    
    @Test
    public void testReadFile()
    {
        FitRecordRepository repository;
        FitRecord           record;
        int                 size;
        int                 i;

        System.out.println("readFile");
        DebugLogger.setDebugLevel(DebugLogger.DEBUGLEVEL_INFO);

        repository=this.readTestFitFile();
        
        record=repository.getFitRecord("waypoints");
        size=record.getNumberOfRecordValues();
    
        assertEquals(size                                                     , 2);
        assertEquals(record.getIntValue         (0, "message_index")          , 0);
        assertEquals(record.getStringValue      (0, "name")                   ,"test_trk - Star");
        assertEquals(record.getStringValue      (0, "description")            ,"");
        assertEquals(record.getTimeValue        (0, "timestamp").toString()   ,"2016-10-15 15:20:39.000000000");
        assertEquals(record.getLatLonValue      (0, "position_lat")           ,52.21951995044947, 0.00000001);
        assertEquals(record.getLatLonValue      (0, "position_long")          ,6.945969918742776, 0.00000001);
        assertEquals(record.getIntValue         (0, "symbol")                 ,95);
        assertEquals(record.getAltitudeValue    (0, "altitude")               ,12607.0, 0.1);
        assertEquals(record.getIntValue         (0, "unknown")                ,0xffff);
        
        
        assertEquals(record.getIntValue         (1, "message_index")          , 5);
        assertEquals(record.getStringValue      (1, "name")                   ,"Garmin Taiwan");
        assertEquals(record.getStringValue      (1, "description")            ,"this is some funny text that serves as description");
        assertEquals(record.getTimeValue        (1, "timestamp").toString()   ,"1989-12-30 23:59:59.000000000");
        assertEquals(record.getLatLonValue      (1, "position_lat")           ,25.061783362179995, 0.00000001);
        assertEquals(record.getLatLonValue      (1, "position_long")          ,121.64026667363942, 0.00000001);
        assertEquals(record.getIntValue         (1, "symbol")                 ,94);
        assertEquals(record.getAltitudeValue    (1, "altitude")               ,38.0, 0.1);
        assertEquals(record.getIntValue         (1, "unknown")                ,0xffff);
        
    }
    
    @Test
    public void testRecordRepository()
    {
        FitRecordRepository repository;
        ArrayList<String>   messages;
        
        System.out.println("FitRecordRepository");
        DebugLogger.setDebugLevel(DebugLogger.DEBUGLEVEL_INFO);
        
        repository=this.readTestFitFile();
        
        messages=repository.getMessages();
        
        assertEquals(messages.get(0), "file_id");
        assertEquals(messages.get(1), "file_creator");
        assertEquals(messages.get(2), "waypoints");
        
        assertEquals(repository.recordExists(0), true);
        assertEquals(repository.recordExists(1), true);
        assertEquals(repository.recordExists(2), true);
        assertEquals(repository.recordExists(3), false);
        
        assertNotEquals(repository.getFitRecord("file_id"), null);
        assertEquals(repository.getFitRecord("blah_blah"), null);
        
        assertNotEquals(repository.getFitRecord(0), null);
        assertEquals(repository.getFitRecord(4), null);
        assertEquals(repository.getFitRecord(-1), null);
        
     
    }

}
