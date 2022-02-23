/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.studioblueplanet.fitreader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents the repository of messages enclosed in the FIT file.
 * In the scope of this application a Message contains a message and field definition
 * as well as all records of the message type
 * @author Jorgen
 */
public class FitMessageRepository
{
    private final static Logger     LOGGER = LogManager.getLogger(FitMessageRepository.class);
    private final List<FitMessage> messages;
    
    /**
     * Constructor. Creates the message list.
     */
    public FitMessageRepository()
    {
        messages=new ArrayList<>();
    }

    /**
     * Check if there is a message definition for the given Local Message Type 
     * value.
     * @param localMessageType Local Message Type value (0..15)
     * @return True if the message exists, false if not.
     */
    public boolean messageExists(int localMessageType)
    {
        boolean             found;
        Iterator<FitMessage> iterator;
        FitMessage           message;
        
        iterator=messages.iterator();
        found=false;
        
        while(iterator.hasNext() && !false)
        {
            message=iterator.next();
            if (message.getLocalMessageType()==localMessageType)
            {
                found=true;
            }
        }
        return found;
    }
    
    /**
     * This method returns the last added message with given local message type.
     * According to the FIT spec there may be redefinitions with the same
     * local message type, therefore the last defined message is returned.
     * @param localMessageType The local message type (0..15)
     * @return The message, or null if it does not exist
     */
    public FitMessage getFitMessage(int localMessageType)
    {
        FitMessage   message;
        int         i;
        int         numOfMessages;
        boolean     found;
        
        numOfMessages    =this.messages.size();
        found           =false;
        message          =null;
        
        // Start parsing the list from the end, moving to the start
        // We need to find the last message that has given local message type value.
        // There may be more redefinitions according to the FIT specification. 
        // We therefore need the last defined message of the message type 
        i=numOfMessages-1;
        while ((i>=0) && !found)
        {
            message=messages.get(i);
            if (message.getLocalMessageType()==localMessageType)
            {
                found=true;
            }
            i--;
        }
        
        if (!found)
        {
            message=null;
            LOGGER.debug("Message with local message type {} not found.", localMessageType);
        }
        
        return message;
    }
    
    /**
     * This method returns the last added message with given name.
     * @param messageName Name of the record according to the global profile)
     * @return The record, or null if it does not exist
     */
    public FitMessage getFitMessage(String messageName)
    {
        FitMessage           message;
        int                 i;
        int                 numOfMessages;
        boolean             found;
        FitGlobalProfile    profile;
        int                 globalMessageNumber;
       
        profile=FitGlobalProfile.getInstance();
        globalMessageNumber=profile.getGlobalMessageNumber(messageName);
        
        numOfMessages    =this.messages.size();
        found           =false;
        message          =null;
        
        // Start parsing the list from the end, moving to the start
        // We need to find the last record that has given local message type value.
        // There may be more redefinitions according to the FIT specification. 
        // We therefore need the last added record. 
        i=numOfMessages-1;
        while ((i>=0) && !found)
        {
            message=messages.get(i);
            if (message.getGlobalMessageNumber()==globalMessageNumber)
            {
                found=true;
            }
            i--;
        }
        
        if (!found)
        {
            message=null;
            LOGGER.debug("Message with name {} not found.", messageName);
        }
        
        return message;
    }
    
    /**
     * Add the message to the repository
     * @param message The record to add
     */
    public void addFitMessage(FitMessage message)
    {
        this.messages.add(message);
    }
    
    /**
     * Returns a list of messages defined in this repository
     * @return The list as an array list of strings
     */
    public List<String> getMessageNames()
    {
        ArrayList<String>   list;
        FitMessage           message;
        Iterator<FitMessage> it;
        int                 number;
        String              messageName;
        FitGlobalProfile    profile;
        
        list=new ArrayList();
        profile=FitGlobalProfile.getInstance();
        it=this.messages.iterator();
        
        while (it.hasNext())
        {
            message=it.next();
            
            number=message.getGlobalMessageNumber();
            messageName=profile.getGlobalMessageName(number);
            list.add(messageName);
        }
        return list;
    }

    /**
     * Debugging: dump the field definitions in each message
     */
    public void dumpMessageDefintions()
    {
        List<FitMessageField>   fields;
        
        for (FitMessage record : messages)
        {
            LOGGER.info("MESSAGE: {}: {} records", 
                    FitGlobalProfile.getInstance().getGlobalMessageName(record.getGlobalMessageNumber()),
                    record.getNumberOfRecords());
            fields=record.getFieldDefintions();
            for (FitMessageField field : fields)
            {
                LOGGER.info(field.definition.toString());
            }
        }
    }
}
