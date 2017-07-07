/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.studioblueplanet.fitreader;

import net.studioblueplanet.logger.DebugLogger;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Jorgen
 */
public class FitRecordRepository
{
    ArrayList<FitRecord> records;
    
    public FitRecordRepository()
    {
        records=new ArrayList<FitRecord>();
    }

    /**
     * Check if there is a message definition for the given Local Message Type 
     * value.
     * @param localMessageType Local Message Type value (0..15)
     * @return True if there exits a record, false if not.
     */
    public boolean recordExists(int localMessageType)
    {
        boolean             found;
        Iterator<FitRecord> iterator;
        FitRecord           record;
        
        iterator=records.iterator();
        found=false;
        
        while(iterator.hasNext() && !false)
        {
            record=iterator.next();
            if (record.getLocalMessageType()==localMessageType)
            {
                found=true;
            }
        }
        return found;
    }
    
    /**
     * This method returns the last added record with given local message type.
     * According to the FIT spec there may be redefinitions with the same
     * local message type, therefore the last added is returned.
     * @param localMessageType The local message type (0..15)
     * @return The record, or null if it does not exist
     */
    public FitRecord getFitRecord(int localMessageType)
    {
        FitRecord   record;
        int         i;
        int         numOfRecords;
        boolean     found;
        
        numOfRecords    =this.records.size();
        found           =false;
        record          =null;
        
        // Start parsing the list from the end, moving to the start
        // We need to find the last record that has given local message type value.
        // There may be more redefinitions according to the FIT specification. 
        // We therefore need the last added record. 
        i=numOfRecords-1;
        while ((i>=0) && !found)
        {
            record=records.get(i);
            if (record.getLocalMessageType()==localMessageType)
            {
                found=true;
            }
            i--;
        }
        
        if (!found)
        {
            record=null;
            DebugLogger.debug("Record with local message type "+localMessageType+" not found.");
        }
        
        return record;
    }
    
    /**
     * This method returns the last added record with given name.
     * @param recordName Name of the record according to the global profile)
     * @return The record, or null if it does not exist
     */
    public FitRecord getFitRecord(String recordName)
    {
        FitRecord           record;
        int                 i;
        int                 numOfRecords;
        boolean             found;
        FitGlobalProfile    profile;
        int                 globalMessageNumber;
       
        profile=FitGlobalProfile.getInstance();
        globalMessageNumber=profile.getGlobalMessageNumber(recordName);
        
        numOfRecords    =this.records.size();
        found           =false;
        record          =null;
        
        // Start parsing the list from the end, moving to the start
        // We need to find the last record that has given local message type value.
        // There may be more redefinitions according to the FIT specification. 
        // We therefore need the last added record. 
        i=numOfRecords-1;
        while ((i>=0) && !found)
        {
            record=records.get(i);
            if (record.getGlobalMessageNumber()==globalMessageNumber)
            {
                found=true;
            }
            i--;
        }
        
        if (!found)
        {
            record=null;
            DebugLogger.debug("Record with name "+recordName+" not found.");
        }
        
        return record;
    }
    
    
    
    
    /**
     * Add the record to the repository
     * @param record The record to add
     */
    public void addFitRecord(FitRecord record)
    {
        this.records.add(record);
    }
}
