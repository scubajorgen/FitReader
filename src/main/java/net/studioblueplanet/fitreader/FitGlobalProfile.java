/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.studioblueplanet.fitreader;

import net.studioblueplanet.logger.DebugLogger;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;


/**
 * This class contains the FIT profile definitions. It reads the  
 * definitions from files that have been derived from he excel file
 * in the FIT sdk
 * @author Jorgen
 */
public class FitGlobalProfile
{
    /**
     * This sub class represents the field description.
     * All fields with a global message number between min and max
     * have the given description.
     */
    class FitMessageNumber
    {
        public int     globalMessageNumberMin;
        public int     globalMessageNumberMax;
        public String  description;
    }
    
    
    
    /** The one and only instance */
    private static FitGlobalProfile                 theInstance=null;
    
    private ArrayList<FitMessageNumber>             messageNumbers;
    private ArrayList<FitFieldDefinition>           fieldDefinitions;
    private ArrayList<FitBaseType>                  baseTypes;
    
    private FitFieldDefinition                      fieldUnknown;
    
    /**
     * Constructor
     */
    private FitGlobalProfile()
    {
        readMessageNumbers();
        readFieldDescriptions();
        readBaseTypes();
        
        fieldUnknown=new FitFieldDefinition();
        fieldUnknown.fieldNumber=-1;
        fieldUnknown.fieldDescription="unknown";
        fieldUnknown.messageNumber=-1;
        fieldUnknown.fieldTypeDescription="unknown";
    }

    
    /**
     * Reads the message descriptions from the file fit_messages.csv.
     * A FIT file consists of records identified by a 'global
     * message number', defined in the 'definition message'.
     * This method reads the number and description.
     */
    private void readMessageNumbers()
    {
        String              csvFile      = "/fit_messages.csv";
        String              cvsSplitBy   = ",";
        BufferedReader      br           = null;
        String              line;
        String[]            fields;
        FitMessageNumber    number;
        
        messageNumbers=new ArrayList<FitMessageNumber>();
        try 
        {
            DebugLogger.info("Reading global message number from "+csvFile);
            InputStream is = getClass().getResourceAsStream(csvFile); 
            br = new BufferedReader(new InputStreamReader(is));            
            while ((line = br.readLine()) != null) 
            {

                // use comma as separator
                fields = line.split(cvsSplitBy);
                if (fields.length==3)
                {
                    number=new FitMessageNumber();
                    number.globalMessageNumberMin   =Integer.parseInt(fields[1]);
                    number.globalMessageNumberMax   =Integer.parseInt(fields[2]);
                    number.description              =fields[0];

                    messageNumbers.add(number);
                }
                else
                {
                    DebugLogger.error("Error in file "+csvFile);
                }

            }
        } 
        catch (FileNotFoundException e) 
        {
            DebugLogger.error("Error reading "+csvFile+": "+e.getMessage());
        } 
        catch (IOException e) 
        {
            DebugLogger.error("Error reading "+csvFile+": "+e.getMessage());
        } 
        finally 
        {
            if (br != null) 
            {
                try 
                {
                    br.close();
                } catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }        
        
    }
    
    /**
     * Read the field descriptions from file. Each field belonging to 
     * global message number has a 'Field definition number', defined 
     * in the 'Field Definition' in the 'Definition Message'
     */
    private void readFieldDescriptions()
    {
        String                      csvFile     = "/fit_messagefields.csv";
        String                      cvsSplitBy  = ",";
        BufferedReader              br          = null;
        String                      line;
        String[]                    stringFields;
        FitFieldDefinition   field;
        int                         messageNumber;
        int                         fieldNumber;
        String                      messageDescription;
        String                      fieldDescription;
        int                         lineNumber;
        
        fieldDefinitions=new ArrayList<FitFieldDefinition>();
        try 
        {
            DebugLogger.info("Reading field definitions from "+csvFile);
            lineNumber      =0;
            messageNumber   =65535;
            InputStream is = getClass().getResourceAsStream(csvFile); 
            br = new BufferedReader(new InputStreamReader(is));            
            // skip header lines
            br.readLine();
            br.readLine();
            lineNumber+=2;
            while ((line = br.readLine()) != null) 
            {

                // use comma as separator
                stringFields = line.split(cvsSplitBy);
                lineNumber++;
                if (stringFields.length>=0)
                {
                    messageDescription=stringFields[0];
                    if (!messageDescription.equals(""))
                    {
                        messageNumber=this.getGlobalMessageNumber(messageDescription);
                        if (messageNumber==65535)
                        {
                            DebugLogger.error("Message number not found for description "+messageDescription+" @ line: "+lineNumber+": "+line);
                        }
                    }
                    if (stringFields.length>=2)
                    {
                        if (!stringFields[1].equals(""))
                        {
                            fieldNumber                 =Integer.parseInt(stringFields[1]);
                            fieldDescription            =stringFields[2];
                            field                       =new FitFieldDefinition();
                            field.messageNumber         =messageNumber;
                            field.fieldNumber           =fieldNumber;
                            field.fieldDescription      =fieldDescription;
                            if (stringFields.length>=3)
                            {
                                field.fieldTypeDescription  =stringFields[3];
                            }
                            else
                            {
                                field.fieldTypeDescription  ="not defined";
                            }
                            fieldDefinitions.add(field);
                        }
                    }
                }
                else
                {
                    DebugLogger.error("Error in file "+csvFile+", line "+lineNumber+": "+line);
                }

            }
        } 
        catch (FileNotFoundException e) 
        {
            DebugLogger.error("Error reading "+csvFile+": "+e.getMessage());
        } 
        catch (IOException e) 
        {
            DebugLogger.error("Error reading "+csvFile+": "+e.getMessage());
        } 
        finally 
        {
            if (br != null) 
            {
                try 
                {
                    br.close();
                } catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }        
        
    }
    

    /**
     * Reads the base type definitions from fit_basetypes.csv
     */
    private void readBaseTypes()
    {
        String              csvFile      = "/fit_basetypes.csv";
        String              cvsSplitBy   = ",";
        BufferedReader      br           = null;
        String              line;
        String[]            fields;
        FitBaseType         baseType;
        
        baseTypes=new ArrayList<FitBaseType>();
        try 
        {
            DebugLogger.info("Reading base type descriptions from "+csvFile);
            InputStream is = getClass().getResourceAsStream(csvFile); 
            br = new BufferedReader(new InputStreamReader(is));            
            while ((line = br.readLine()) != null) 
            {

                // use comma as separator
                fields = line.split(cvsSplitBy);
                if (fields.length==2)
                {
                    baseType=new FitBaseType();
                    baseType.baseTypeNumber=Integer.parseInt(fields[1]);
                    baseType.baseTypeDescription=fields[0];
                    baseTypes.add(baseType);
                }
                else
                {
                    DebugLogger.error("Error in file "+csvFile);
                }

            }
        } 
        catch (FileNotFoundException e) 
        {
            DebugLogger.error("Error reading "+csvFile+": "+e.getMessage());
        } 
        catch (IOException e) 
        {
            DebugLogger.error("Error reading "+csvFile+": "+e.getMessage());
        } 
        finally 
        {
            if (br != null) 
            {
                try 
                {
                    br.close();
                } catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }        
        
    }
    
    
    
    /**
     * Returns the one and only singleton instance of this class
     * @return The instance of this class
     */
    public static FitGlobalProfile getInstance()
    {
        if (theInstance==null)
        {
            theInstance=new FitGlobalProfile();
        }
        
        return theInstance;
    }
    
    /**
     * Get the description of the message, based on the global message number
     * @param messageNumber The global message number as present in the definition message (0-65535)
     * @return The description, or "not found" if the number is illegal
     */
    public String getGlobalMessageDescription(int messageNumber)
    {
        String                          description;
        Iterator<FitMessageNumber>      iterator;
        boolean                         found;
        FitMessageNumber                number;
        
        iterator    =messageNumbers.iterator();
        found       =false;
        description="not found";
        while (iterator.hasNext() && !found)
        {
            number=iterator.next();
            if ((messageNumber>=number.globalMessageNumberMin) && (messageNumber<=number.globalMessageNumberMax))
            {
                description=number.description;
                found=true;
            }
        }
        
        return description;
    }

    /**
     * Returns the minimum message number based on the description
     * @param description The description to match
     * @return The message number or 65535 (not_defined) if not found
     */
    public int getGlobalMessageNumber(String description)
    {
        int                         messageNumber;
        Iterator<FitMessageNumber>     iterator;
        boolean                     found;
        FitMessageNumber               number;
        
        iterator        =messageNumbers.iterator();
        found           =false;
        messageNumber   =65535;
        while (iterator.hasNext() && !found)
        {
            number=iterator.next();
            if (description.equals(number.description))
            {
                messageNumber=number.globalMessageNumberMin;
                found=true;
            }
        }
        
        return messageNumber;
        
    }
    
    
    /**
     * This method finds the field description given the global message number
     * and field identification
     * @param globalMessageNumber The global message number (0-65535)
     * @param fieldNumber The field number (0-255)
     * @return The description or "not found" if not found
     */
    public String getMessageFieldDescription(int globalMessageNumber, int fieldNumber)
    {
        String                              description;
        Iterator<FitFieldDefinition>        iterator;
        boolean                             found;
        FitFieldDefinition                  field;
        
        iterator    =fieldDefinitions.iterator();
        found       =false;
        description="not found";
        while (iterator.hasNext() && !found)
        {
            field=iterator.next();
            if ((field.messageNumber==globalMessageNumber) && (field.fieldNumber==fieldNumber))
            {
                description=field.fieldDescription;
                found=true;
            }
        }
        
        return description;
    }

    /**
     * This method finds the field definition given the global message number
     * and field identification
     * @param globalMessageNumber The global message number (0-65535)
     * @param fieldNumber The field number (0-255)
     * @return The field or null if not found
     */
    public FitFieldDefinition getMessageField(int globalMessageNumber, int fieldNumber)
    {
        Iterator<FitFieldDefinition>    iterator;
        boolean                         found;
        FitFieldDefinition              field;
        
        iterator    =fieldDefinitions.iterator();
        found       =false;
        field       =null;
        while (iterator.hasNext() && !found)
        {
            field=iterator.next();
            if ((field.messageNumber==globalMessageNumber) && (field.fieldNumber==fieldNumber))
            {
                found=true;
            }
        }
        if (!found)
        {
            if (this.getGlobalMessageDescription(globalMessageNumber)!=null)
            {
                field=new FitFieldDefinition();
                field.messageNumber=globalMessageNumber;
                field.fieldNumber=-1;
                field.fieldDescription="not found";
            }
            else
            {
                field=this.fieldUnknown;
            }
        }

        return field;
    }

    /**
     * Get the description of the base type, based on the base type number as in the file.
     * @param baseTypeNumber The base type number as present in the definition message (0-255)
     * @return The description, or "not found" if the number is illegal
     */
    public String getBaseTypeDescription(int baseTypeNumber)
    {
        String                      description;
        Iterator<FitBaseType>       iterator;
        boolean                     found;
        FitBaseType                 baseType;
        
        iterator    =baseTypes.iterator();
        found       =false;
        description="not found";
        while (iterator.hasNext() && !found)
        {
            baseType=iterator.next();
            if (baseType.baseTypeNumber==baseTypeNumber)
            {
                description=baseType.baseTypeDescription;
                found=true;
            }
        }
        
        return description;
    }
    
    
    /**
     * Convert signed integer to latitude or longitude.
     * @param value The signed integer value
     * @return Lat or lon value
     */
    public static double sintToLatLon(int value)
    {
        double latlon;
        
        latlon=180.0/(double)value;
        
        return latlon;
    }
    
}
