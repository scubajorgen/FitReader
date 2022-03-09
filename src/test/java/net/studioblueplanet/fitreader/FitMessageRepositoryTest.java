/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import java.util.List;
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
public class FitMessageRepositoryTest
{
    private FitMessageRepository instance;
    
    public FitMessageRepositoryTest()
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
        FitReader reader;
        reader=FitReader.getInstance();
        instance=reader.readFile("src/test/resources/2022-01-08-10-37-14.fit", false);
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of messageExists method, of class FitMessageRepository.
     */
    @Test
    public void testMessageExists()
    {
        System.out.println("messageExists");
        
        assertEquals(true, instance.messageExists(7));
        assertEquals(false, instance.messageExists(16)); // 16 cannot exist
    }

    /**
     * Test of getFitMessage method, of class FitMessageRepository.
     */
    @Test
    public void testGetFitMessage_int()
    {
        System.out.println("getFitMessage");
        
        assertEquals("record", instance.getFitMessage(7).getMessageName());
        assertNull(instance.getFitMessage(16));
    }

    /**
     * Test of getFitMessage method, of class FitMessageRepository.
     */
    @Test
    public void testGetFitMessage_String()
    {
        System.out.println("getFitMessage");
        
        assertEquals(7, instance.getFitMessage("record").getLocalMessageType());
        assertNull(instance.getFitMessage("doesnt_exist"));
    }

    /**
     * Test of getAllFields method, of class FitMessageRepository.
     */
    @Test
    public void testGetAllFields()
    {
        System.out.println("getAllFields");

        List<FitMessage> messages=instance.getAllMessages("record");
        assertEquals(2, messages.size());
        FitMessage message=messages.get(0);
        assertEquals(14, message.getLocalMessageType());
        assertEquals("record", message.getMessageName());
        assertEquals(2822, message.getNumberOfRecords());

        message=messages.get(1);
        assertEquals(7, message.getLocalMessageType());
        assertEquals("record", message.getMessageName());
        assertEquals(1, message.getNumberOfRecords());
    }

    /**
     * Test of addFitMessage method, of class FitMessageRepository.
     */
    @Test
    public void testAddFitMessage()
    {
        System.out.println("addFitMessage");

        assertEquals(28, instance.getMessageNames().size());
        FitMessage message=new FitMessage(12, FitMessage.HeaderType.NORMAL, false);
        message.setGlobalMessageNumber(20);
        instance.addFitMessage(message);
        assertEquals(29, instance.getMessageNames().size());
        message=instance.getFitMessage(12);
        assertEquals("record", message.getMessageName());
        
    }

    /**
     * Test of getMessageNames method, of class FitMessageRepository.
     */
    @Test
    public void testGetMessageNames()
    {
        System.out.println("getMessageNames");
        List<String> result = instance.getMessageNames();
        
        assertEquals(28, result.size());
        assertEquals("file_id", result.get(0));
        assertEquals("not found", result.get(27));
    }

    @Test
    public void testMessageRepository()
    {
        FitMessageRepository    repository;
        List<String>            messages;
        
        System.out.println("FitMessageRepository");
       
        repository=FitReader.getInstance().readFile("src/test/resources/Activity.fit", false);
        
        System.out.println("Fields: "+repository.getMessageNames().toString());
        
        messages=repository.getMessageNames();
        
        assertEquals("file_id", messages.get(0));
        assertEquals("device_info", messages.get(1));
        assertEquals("event", messages.get(2));
        assertEquals("activity", messages.get(10));
        
        assertEquals(true, repository.messageExists(0));
        assertEquals(false, repository.messageExists(1));
        assertEquals(false, repository.messageExists(4));
        
        assertNotEquals(null, repository.getFitMessage("file_id"));
        assertEquals(null, repository.getFitMessage("blah_blah"));
        
        assertNotNull(repository.getFitMessage(0));
        assertNull(repository.getFitMessage(4));
        assertNull(repository.getFitMessage(-1));
    }

    @Test
    public void testLocationsRepository()
    {
        FitMessageRepository    repository;
        List<String>            messages;
        
        System.out.println("FitMessageRepository");
        
        // CRC ERROR!!!!!! So ignore CRC check
        repository=FitReader.getInstance().readFile("src/test/resources/LocationsEdge810.fit", true);
        
        System.out.println("Fields: "+repository.getMessageNames().toString());
        
        messages=repository.getMessageNames();
        
        // The 'waypoint' field (id=29) no longer exists...
        assertEquals("location", messages.get(2));
        
        repository=FitReader.getInstance().readFile("src/test/resources/LocationsEdge830.fit", false);
        
        System.out.println("Fields: "+repository.getMessageNames().toString());
        
        messages=repository.getMessageNames();
        
        // The 'waypoint' field (id=29) no longer exists...
        assertEquals("location", messages.get(2));
    }
}
