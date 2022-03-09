/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.studioblueplanet.fitreader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    
           
    private final static Logger                     LOGGER = LogManager.getLogger(FitGlobalProfile.class);
    private static final String                     GLOBALPROFILE="/Profile.xlsx";
    /** The one and only instance */
    private static FitGlobalProfile                 theInstance=null;
    
    private ArrayList<FitFieldDefinition>           globalProfileFields;
    private Map<String,ProfileType>                 globalProfileTypes;
    
    private final FitFieldDefinition                fieldUnknown;
    
    
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
        String      typeName;
        String      valueName;
        
        type=null;
        int i = 0;
        maxRow=sheet.getLastRowNum();
        for (i=1; i<=maxRow; i++) 
        {
            Row  row=sheet.getRow(i);
            if (row!=null)
            {
                Cell typeNameCell       =row.getCell(0);
                Cell typeBaseTypeCell   =row.getCell(1);

                if (typeNameCell!=null && typeNameCell.getCellType()==CellType.STRING && typeNameCell.getStringCellValue().length()>0)
                {
                    typeName=typeNameCell.getStringCellValue();
                    // Check if the datatype already exists. If so, update it
                    if (globalProfileTypes.containsKey(typeName))
                    {
                        type=globalProfileTypes.get(typeName);
                    }
                    else
                    {
                        type=new ProfileType(typeName, 
                                             typeBaseTypeCell.getStringCellValue());
                        globalProfileTypes.put(typeNameCell.getStringCellValue(), type);
                    }
                }
                else
                {
                    Cell valueNameCell      =row.getCell(2);
                    Cell valueCell          =row.getCell(3);
                    if (type!=null && valueNameCell!=null && valueCell!=null)
                    {
                        valueName=valueNameCell.getStringCellValue();
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
                            // Remove existing value with the same name
                            if (type.valueExists(valueName))
                            {
                                type.removeValueByName(valueName);
                            }
                            type.addTypeValue(valueName, value);
                        }
                        else if (valueCell.getCellType()==CellType.NUMERIC)
                        {
                            value=(int)valueCell.getNumericCellValue();
                            // Remove existing value with the same name
                            if (type.valueExists(valueName))
                            {
                                type.removeValueByName(valueName);
                            }
                            type.addTypeValue(valueNameCell.getStringCellValue(), value);
                        }
                        else
                        {
                            LOGGER.error("Unexpected value in {}", GLOBALPROFILE);
                        }

                    }
                }
            }
        }
        LOGGER.info("Read {} global profile types", globalProfileTypes.size());
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
        
        lineNumber      =0;
        messageName     ="";
        messageNumber   =65535;

        maxRows=sheet.getLastRowNum();
        for (lineNumber=2; lineNumber<=maxRows; lineNumber++) 
        {
            Row row=sheet.getRow(lineNumber);
            
            if (row!=null)
            {
                Cell messageNameCell=row.getCell(0);
                if (messageNameCell!=null && messageNameCell.getCellType()==CellType.STRING && messageNameCell.getStringCellValue().length()>0)
                {
                    name= messageNameCell.getStringCellValue().trim();
                    if (name.length()>0)
                    {
                        messageName=name;
                        messageNumber=getGlobalMessageNumber(messageName);
                        if (messageNumber==65535)
                        {
                            LOGGER.error("Message number not found for description {} @ line: {}", messageName, lineNumber);
                        }
                    }
                }
                else
                {
                    Cell fieldNameCell  =row.getCell(2);
                    Cell fieldNumberCell=row.getCell(1);
                    if (fieldNumberCell!=null && fieldNameCell!=null && fieldNumberCell.getCellType()==CellType.NUMERIC)
                    {
                        name                        =fieldNameCell.getStringCellValue().trim();
                        fieldNumber                 =(int)row.getCell(1).getNumericCellValue();
                        fieldDescription            =row.getCell(2).getStringCellValue();
                        
                        // Check if the field already exists; if so, reuse
                        field=findFieldDefinition(messageNumber, fieldNumber);
                        if (field==null)
                        {       
                            field                   =new FitFieldDefinition();
                            globalProfileFields.add(field);
                        }
                        field.messageName           =messageName;
                        field.messageNumber         =messageNumber;
                        field.fieldNumber           =fieldNumber;
                        field.fieldName             =fieldDescription;
                        field.fieldType             =row.getCell(3).getStringCellValue();

                        if (row.getCell(6)!=null && row.getCell(6).getCellType()==CellType.NUMERIC)
                        {
                            field.scale=row.getCell(6).getNumericCellValue();
                        }
                        else
                        {
                            field.scale=1.0;
                        }

                        if (row.getCell(7)!=null && row.getCell(7).getCellType()==CellType.NUMERIC)
                        {
                            field.offset=row.getCell(7).getNumericCellValue();
                        }
                        else
                        {
                            field.offset=0.0;
                        }  
                        if (row.getCell(8)!=null)
                        {
                            field.units=row.getCell(8).getStringCellValue();
                        }
                        else
                        {
                            field.units="";
                        }
                    }
                }
            }
        }
        LOGGER.info("Read "+globalProfileFields.size()+" global profile field definitions");
    }
    
    /**
     * Finds the FIT field definition with given numbers in the array 
     * @param messageNumber Message ID
     * @param fieldNumber Field ID
     * @return 
     */
    private FitFieldDefinition findFieldDefinition(int messageNumber, int fieldNumber)
    {
        FitFieldDefinition returnField;
        
        returnField=null;
        for (FitFieldDefinition field : globalProfileFields)
        {
            if (field.messageNumber==messageNumber && field.fieldNumber==fieldNumber)
            {
                returnField=field;
            }
        }
        return returnField;
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
            
            if (file!=null)
            {
                globalProfileTypes  =new HashMap<>();
                globalProfileFields =new ArrayList<>();

                Workbook workbook   = new XSSFWorkbook(file);
                this.parseGlobalProfileTypeSheet(workbook.getSheetAt(0));
                this.parseGlobalProfileMessageSheet(workbook.getSheetAt(1));
                this.parseGlobalProfileTypeSheet(workbook.getSheetAt(2));
                this.parseGlobalProfileMessageSheet(workbook.getSheetAt(3));
                file.close();
            }
            else
            {
                LOGGER.error("Cannot find global profile file "+GLOBALPROFILE);
            }
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
            field=new FitFieldDefinition();
            field.messageNumber=globalMessageNumber;
            field.messageName=getGlobalMessageName(globalMessageNumber);
            field.fieldNumber=fieldNumber;
            field.fieldName="not found";
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
     * It is essentially equivalent to getTypeValueName("fit_base_type", baseTypeNumber).
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
        name="not found";
        if (type!=null)
        {
            name=type.getValueName(baseTypeNumber);
            if (name==null)
            {
                name="not found";
            }
        }
        return name;        
    }
   
    /**
     * Gets the value name for given data type, based on the value ID
     * @param typeName
     * @param valueId
     * @return The name or null if non-existent
     */
    public String getTypeValueName(String typeName, int valueId)
    {
        String name;
        
        name=null;
        ProfileType type=globalProfileTypes.get(typeName);
        if (type!=null)
        {
            name=type.getValueName(valueId);
        }
        return name;
    }
    
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
                LOGGER.info("{} {}({})", type.getType(), value.getValueName(), value.getValue());
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
            LOGGER.info(field.toString());
        }
    }
    
    /**
     * Debugging: get the number of profile types read
     * @return The number of global profile types
     */
    public int getNumberOfGlobalProfileTypes()
    {
        return this.globalProfileTypes.size();
    }

    /**
     * Debugging: get the number of profile messages read
     * @return The number of global profile messages
     */
    public int getNumberOfGlobalProfileFields()
    {
        return this.globalProfileFields.size();
    }
}
