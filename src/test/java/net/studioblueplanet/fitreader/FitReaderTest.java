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

    
    private void waypointTest()
    {
        FitRecordRepository repository;
        FitRecord           record;
        int                 size;
        int                 i;
        FitReader           reader;

        DebugLogger.setDebugLevel(DebugLogger.DEBUGLEVEL_INFO);

        FitGlobalProfile.getInstance();
        
        reader=FitReader.getInstance();
        
        System.out.println(System.getProperty("user.dir"));

        repository=reader.readFile("target/classes/test_waypoints.fit");

        
        record=repository.getFitRecord("user_profile");

        DebugLogger.info("User name: "+record.getStringValue(0, "friendly_name"));
        
        record=repository.getFitRecord("record");
        size=record.getNumberOfRecordValues();
        i=0;
        while (i<size)
        {
            DebugLogger.info("Coor "+record.getTimeValue(i, "timestamp").toString()+" "+
                             record.getLatLonValue(i, "position_lat")+", "+record.getLatLonValue(i, "position_long")+
                             " alt "+record.getAltitudeValue(i, "altitude")+
                             " speed "+record.getSpeedValue(i, "speed")+
                             " distance "+record.getDistanceValue(i, "distance")+
                             " temp "+record.getIntValue(i, "temperature"));
            i++;
        }
        
        repository=reader.readFile("target/classes/Locations.fit");
        record=repository.getFitRecord("waypoints");
        size=record.getNumberOfRecordValues();
        i=0;
        while (i<size)
        {
            DebugLogger.info("Waypoint "+record.getIntValue(i, "message_index")+
                             ": "+record.getStringValue(i, "name")+
                             " "+record.getStringValue(i, "description")+
                             " "+record.getTimeValue(i, "timestamp").toString()+
                             " "+record.getLatLonValue(i, "position_lat")+", "+record.getLatLonValue(i, "position_long")+
                             " symbol "+record.getIntValue(i, "symbol")+
                             " alt "+record.getAltitudeValue(i, "altitude")+
                             " unknown "+record.getIntValue(i, "unknown")
                              );
            i++;
        }        
        
    }
    
    
    
    /**
     * Test of readFile method, of class FitReader.
     */
    @Test
    public void testReadFile()
    {
        this.waypointTest();
    }
    
}
