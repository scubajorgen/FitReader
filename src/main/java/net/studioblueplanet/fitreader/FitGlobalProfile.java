/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.studioblueplanet.fitreader;

import net.studioblueplanet.logger.DebugLogger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


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
    
           
    
    
    private static final String                     GLOBALPROFILE="/profile.xlsx";
    /** The one and only instance */
    private static FitGlobalProfile                 theInstance=null;
    
    private ArrayList<FitFieldDefinition>           globalProfileFields;
    private Map<String,ProfileType>                 globalProfileTypes;
    
    private FitFieldDefinition                      fieldUnknown;
    
    
    /**
     * Constructor
     */
    private FitGlobalProfile()
    {
        readGlobalProfileExcelFile();
        fieldUnknown=new FitFieldDefinition();
        fieldUnknown.fieldNumber=-1;
        fieldUnknown.fieldName="unknown";
        fieldUnknown.messageNumber=-1;
        fieldUnknown.fieldType="unknown";
    }
    
    /**
     * Parse the type sheet from the Profile.xslx
     * @param sheet The type sheet
     */
    private void parseGlobalProfileTypeSheet(Sheet sheet)
    {
        int         maxRow;
        ProfileType type;
        long        value;
        
        type=null;
        globalProfileTypes=new HashMap<>();
        int i = 0;
        maxRow=sheet.getLastRowNum();
        for (i=1; i<maxRow; i++) 
        {
            Row  row=sheet.getRow(i);

            Cell typeNameCell       =row.getCell(0);
            Cell typeBaseTypeCell   =row.getCell(1);
            Cell valueNameCell      =row.getCell(2);
            Cell valueCell          =row.getCell(3);

            if (typeNameCell.getCellType()==CellType.STRING && typeNameCell.getStringCellValue().length()>0)
            {
                type=new ProfileType(typeNameCell.getStringCellValue(), 
                                     typeBaseTypeCell.getStringCellValue());
                globalProfileTypes.put(typeNameCell.getStringCellValue(), type);
            }
            else
            {
                if (type!=null)
                {

                    if (valueCell.getCellType()==CellType.STRING)
                    {
                        if (valueCell.getStringCellValue().toLowerCase().startsWith("0x"))
                        {
                            value=Long.decode(valueCell.getStringCellValue());
                        }
                        else
                        {
                            value=Long.parseLong(valueCell.getStringCellValue().trim());
                        }
                        type.addTypeValue(valueNameCell.getStringCellValue(), value);
                    }
                    else if (valueCell.getCellType()==CellType.NUMERIC)
                    {
                        value=(int)valueCell.getNumericCellValue();
                        type.addTypeValue(valueNameCell.getStringCellValue(), value);
                    }
                    else
                    {
                        DebugLogger.error("Unexpected value in "+GLOBALPROFILE);
                    }

                }
                else
                {
                    DebugLogger.error("Unexpected value in "+GLOBALPROFILE);
                }
            }
        }
//dumpGlobalTypes();        
    }

    /**
     * Parse the message sheet from the Profile.xslx
     * @param sheet The message sheet
     */
    private void parseGlobalProfileMessageSheet(Sheet sheet)
    {
        FitFieldDefinition          field;
        int                         messageNumber;
        int                         fieldNumber;
        String                      messageName;
        String                      name;
        String                      fieldDescription;
        int                         lineNumber;
        int                         maxRows;
        
        globalProfileFields=new ArrayList<FitFieldDefinition>();

        lineNumber      =0;
        messageName     ="";
        messageNumber   =65535;

        maxRows=sheet.getLastRowNum();
        for (lineNumber=2; lineNumber<maxRows; lineNumber++) 
        {
            Row row=sheet.getRow(lineNumber);
            name=row.getCell(0).getStringCellValue();
            if (name.trim().length()>0)
            {
                messageName=name;
                messageNumber=getGlobalMessageNumber(messageName);
                if (messageNumber==65535)
                {
                    DebugLogger.error("Message number not found for description "+messageName+" @ line: "+lineNumber);
                }
            }

            if (row.getCell(2).getStringCellValue().trim().length()>0)
            {
                if (row.getCell(1).getCellType()==CellType.NUMERIC)
                {
                    fieldNumber             =(int)row.getCell(1).getNumericCellValue();
                }
                else
                {
                    fieldNumber             =-1;
                }
                fieldDescription            =row.getCell(2).getStringCellValue();
                field                       =new FitFieldDefinition();
                field.messageName           =messageName;
                field.messageNumber         =messageNumber;
                field.fieldNumber           =fieldNumber;
                field.fieldName             =fieldDescription;
                field.fieldType             =row.getCell(3).getStringCellValue();
                
                if (row.getCell(6).getCellType()==CellType.NUMERIC)
                {
                    field.scale=row.getCell(6).getNumericCellValue();
                }
                else
                {
                    field.scale=1.0;
                }

                if (row.getCell(7).getCellType()==CellType.NUMERIC)
                {
                    field.offset=row.getCell(7).getNumericCellValue();
                }
                else
                {
                    field.offset=0.0;
                }                
                field.units=row.getCell(8).getStringCellValue();
                
                globalProfileFields.add(field);
            }
        }
        this.dumpGlobalMessages();
        System.out.println("");
    }
    
    /**
     * Read the Garmin Global Profile from the Profile.xslx excel file.
     * This file is delivered with the Garmin SDK.
     */
    private void readGlobalProfileExcelFile()
    {
        try
        {
            InputStream file = getClass().getResourceAsStream(GLOBALPROFILE);
            Workbook workbook = new XSSFWorkbook(file);
            
            this.parseGlobalProfileTypeSheet(workbook.getSheetAt(0));
            this.parseGlobalProfileMessageSheet(workbook.getSheetAt(1));
        }
        catch (FileNotFoundException e)
        {
            System.err.println("File "+GLOBALPROFILE+" not found: "+e.getMessage());
        }
        catch (IOException e)
        {
            System.err.println("Error reading file "+GLOBALPROFILE+": "+e.getMessage());
        }
        System.out.println("Global Profile read from "+GLOBALPROFILE);
        
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
     * Get the name of the message, based on the global message number
     * @param messageNumber The global message number as present in the definition message (0-65535)
     * @return The description, or "not found" if the number is illegal
     */
    public String getGlobalMessageName(int messageNumber)
    {
        String                          description;
        Iterator<ProfileTypeValue>      iterator;
        boolean                         found;
        ProfileTypeValue                value;
        
        ProfileType type=globalProfileTypes.get("mesg_num");
        iterator    =type.getValues().iterator();
        found       =false;
        description="not found";
        while (iterator.hasNext() && !found)
        {
            value=iterator.next();
            if (messageNumber==value.getValue())
            {
                description=value.getValueName();
                found=true;
            }
        }
        
        return description;
    }

    /**
     * Returns the minimum message number based on the description
     * @param messageName The description to match
     * @return The message number or 65535 (not_defined) if not found
     */
    public int getGlobalMessageNumber(String messageName)
    {
        int                         messageNumber;
        Iterator<ProfileTypeValue>  it;
        boolean                     found;
        ProfileTypeValue            value;
        
        ProfileType type=globalProfileTypes.get("mesg_num");
        it              =type.getValues().iterator();
        messageNumber   =65535;

        found=false;
        while (it.hasNext() && !found)
        {
            value=it.next();
            if (value.getValueName().equals(messageName))
            {
                messageNumber=(int)value.getValue();
                found=true;
            }
        }
        
        return messageNumber;
    }
    
    
    /**
     * This method finds the field name given the global message number
     * and field identification
     * @param globalMessageNumber The global message number (0-65535)
     * @param fieldNumber The field number (0-255)
     * @return The description or "not found" if not found
     */
    public String getMessageFieldName(int globalMessageNumber, int fieldNumber)
    {
        String                              description;
        Iterator<FitFieldDefinition>        iterator;
        boolean                             found;
        FitFieldDefinition                  field;
        
        iterator    =globalProfileFields.iterator();
        found       =false;
        description="not found";
        while (iterator.hasNext() && !found)
        {
            field=iterator.next();
            if ((field.messageNumber==globalMessageNumber) && (field.fieldNumber==fieldNumber))
            {
                description=field.fieldName;
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
        
        iterator    =globalProfileFields.iterator();
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
            if (this.getGlobalMessageName(globalMessageNumber)!=null)
            {
                field=new FitFieldDefinition();
                field.messageNumber=globalMessageNumber;
                field.fieldNumber=-1;
                field.fieldName="not found";
            }
            else
            {
                field=this.fieldUnknown;
            }
        }

        return field;
    }
    
    /**
     * Return the message field description based on message name and field name
     * @param messageName Name of the message
     * @param fieldName Name of the field
     * @return The field definition or null if not found
     */
    public FitFieldDefinition getMessageField(String messageName, String fieldName)
    {
        Iterator<FitFieldDefinition>    iterator;
        boolean                         found;
        FitFieldDefinition              field;
        FitFieldDefinition              theField;
        
        iterator    =globalProfileFields.iterator();
        found       =false;
        theField    =null;

        while (iterator.hasNext() && theField==null)
        {
            field=iterator.next();
            if (field.messageName.equals(messageName) && field.fieldName.equals(fieldName))
            {
                theField=field;
            }
        }
        return theField;
    }

    /**
     * Get the name of the base type, based on the base type number as in the file.
     * @param baseTypeNumber The base type number as present in the definition message (0-255)
     * @return The name, or "not found" if the number is illegal
     */
    public String getBaseTypeName(int baseTypeNumber)
    {
        String                          name;
        Iterator<ProfileTypeValue>      iterator;
        boolean                         found;
        ProfileTypeValue                value;
        
        ProfileType type=globalProfileTypes.get("fit_base_type");
        iterator    =type.getValues().iterator();
        found       =false;
        name="not found";
        while (iterator.hasNext() && !found)
        {
            value=iterator.next();
            if (baseTypeNumber==value.getValue())
            {
                name=value.getValueName();
                found=true;
            }
        }
        return name;        
    }
    
    
    /**
     * Convert signed integer to latitude or longitude.
     * @param value The signed integer value
     * @return Lat or lon value
     *//*
    public static double sintToLatLon(int value)
    {
        double latlon;
        
        latlon=180.0/(double)value;
        
        return latlon;
    }*/

    /**
     * Debugging: dump the global types
     */
    public void dumpGlobalTypes()
    {
        Iterator<ProfileTypeValue>  itValue;
        ProfileType                 type;
        ProfileTypeValue            value;
        
        for(String key : globalProfileTypes.keySet())
        {
            type=      globalProfileTypes.get(key);
            itValue=type.getValues().iterator();
            while (itValue.hasNext())
            {
                value=itValue.next();
                DebugLogger.info(type.getType()+" "+value.getValueName()+"("+value.getValue()+")");
            }
        }
    }
    
    /**
     * Debugging: dump the field definitions
     */
    public void dumpGlobalMessages()
    {
        Iterator<FitFieldDefinition>    itType;
        FitFieldDefinition              field;
        
        itType=globalProfileFields.iterator();
        while (itType.hasNext())
        {
            field=      itType.next();
            DebugLogger.info(field.toString());
        }
    }
}
