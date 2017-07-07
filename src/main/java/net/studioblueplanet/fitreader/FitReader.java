/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.studioblueplanet.fitreader;

import net.studioblueplanet.logger.DebugLogger;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class is the entry point for this library. It is the reader that 
 * reads the .FIT file.
 * @author Jorgen
 */
public class FitReader
{
    static FitReader theInstance=null;
    

    
    
    int[] crc_table =
    {
        0x0000, 0xCC01, 0xD801, 0x1400, 0xF001, 0x3C00, 0x2800, 0xE401,
        0xA001, 0x6C00, 0x7800, 0xB401, 0x5000, 0x9C01, 0x8801, 0x4400
    };
    
    /**
     * Constructor. It is private, since the pattern used is Singleton.
     * Use getInstance() to get the one and only instance.
     */
    private FitReader()
    {
        
    }
    
    /**
     * CRC calculation
     * @param crc Current crc value
     * @param theByte Byte to add?
     * @return crc value
     */
    private int getCrc(int crc, int theByte)
    {
        int tmp;
        // compute checksum of lower four bits of byte
        tmp = crc_table[crc & 0xF];
        crc = (crc >> 4) & 0x0FFF;
        crc = crc ^ tmp ^ crc_table[theByte & 0xF];
        // now compute checksum of upper four bits of byte
        tmp = crc_table[crc & 0xF];
        crc = (crc >> 4) & 0x0FFF;
        crc = crc ^ tmp ^ crc_table[(theByte >> 4) & 0xF];
        return crc;
    }    
    


    /**
     * Parses the record data, provided the record is a FIT "data message"
     * @param in The input reader
     * @param record The record to add the data to
     * @throws IOException In case of misread
     */
    private int parseDataMessage(InputStream in, FitRecord record) throws IOException
    {
        int     numberOfFields;
        int     i;
        int     bytesRead;
        int     recordSize;
        int     j;
        int[]   bytes;
        
        numberOfFields=record.getNumberOfFields();
        bytesRead=0;
        
        i=0;
        recordSize=record.getRecordLength();
        bytes=new int[recordSize];
        while (i<recordSize)
        {
            bytes[i]=in.read();
            bytesRead++;
            i++;
        }
        record.addRecordValues(bytes);
        return bytesRead;
    }
    

    /**
     * Parses the record data, provided the record is a FIT "data message"
     * @param in The input reader
     * @param record The record to add the data to
     * @throws IOException In case of misread
     */
    private int parseCompressedTimestampDataMessage(InputStream in, FitRecord record, int timeOffset) throws IOException
    {
        int bytesRead;
        
        // To do: store compressed timestamp offset with record
        
        bytesRead=parseDataMessage(in, record);
        
        return bytesRead;
    }    
    
    /**
     * Parses the record data, provided the record is a FIT "definition message"
     * @param in The input reader
     * @param record The record to add the definition to
     * @throws IOException In case of misread
     */
    private int parseDefinitionMessage(InputStream in, FitRecord record, boolean hasDeveloperData) throws IOException
    {
        int bytesRead;
        int architecture;
        int globalMessageNumber;
        int numberOfDataFields;
        int i;
        
        int fieldDefinitionNumber;
        int size;
        int baseType;
        
        int fieldNumber;
        int developerDataIndex;
        
        
        bytesRead=0;
        
        // Reserved
        in.read();
        bytesRead++;
        
        // architecture
        architecture=in.read();
        bytesRead++;
        if (architecture>0)
        {
            record.setEndianness(FitRecord.Endianness.BIGENDIAN);
        }
        else
        {
            record.setEndianness(FitRecord.Endianness.LITTLEENDIAN);
        }
        
        // Message Number
        globalMessageNumber=FitToolbox.readInt(in, 2, record.isLittleEndian());
        bytesRead+=2;
        record.setGlobalMessageNumber(globalMessageNumber);
        
        // Number of data fields
        numberOfDataFields=in.read();
        bytesRead++;
        record.setNumberOfFields(numberOfDataFields);
        
        
        // The data fields
        i=0;
        while (i<numberOfDataFields)
        {
            fieldDefinitionNumber   =in.read();
            size                    =in.read();
            baseType                =in.read();
            bytesRead               +=3;
            record.addMessageField(globalMessageNumber, fieldDefinitionNumber, size, baseType);
            i++;
        }
        
        // If the record contains developer data, read the developer field definition
        if (hasDeveloperData)
        {
            // Number of developer data fields
            numberOfDataFields=in.read();
            bytesRead++;
            record.setNumberOfFields(numberOfDataFields);

            // THe developer fields
            i=0;
            while (i<numberOfDataFields)
            {
                fieldDefinitionNumber   =in.read();
                size                    =in.read();
                baseType                =in.read();
                // TO DO: store development field defintion
                bytesRead               +=3;
                i++;
            }
        }
        
        return bytesRead;
    }
    
    /**
     * Reads the next FIT record. A FIT file consists of a global header, 
     * records and 2 CRC bytes. This method assumes the global header has been
     * read and parses the file stream and processes the next record.
     * A record can be a 'definition message' or a 'data message', defined by
     * the first byte in the record.
     * A 'Defintion Message' defines the fields in the record. A 'Data Message'
     * contains field values.
     * A definition message generates a new Record instance, a data message
     * is transferred to a record values and added to the Record
     * @param in Input stream
     * @param repository Repository to add the record to
     * @return The number of bytes read
     * @throws IOException In case of miss read
     */
    private int readRecord(InputStream in, FitRecordRepository repository) throws IOException
    {
        FitRecord                   record;
        int                         bytesRead;
        int                         recordHeader;
        FitRecord.HeaderType        headerType;
        int                         localMessageType;
        int                         timeOffset;
        boolean                     hasDeveloperData;
        boolean                     reservedBit;
        
        bytesRead               =0;
        hasDeveloperData        =false;
        reservedBit             =false;
            
        // Read the first byte: the record header
        recordHeader            =in.read();
        bytesRead++;
        
        DebugLogger.debug("********************* Record **************************");
        DebugLogger.debug("Header "+String.format("0x%02x", recordHeader));
        
        // Bit 7 defines whether the header is normal or compressed timestamp
        // This bit defines the encoding of the rest of the byte
        if ((recordHeader&0x80)!=0)
        {
            headerType      =FitRecord.HeaderType.COMPRESSED_TIMESTAMP;
            localMessageType=(recordHeader&0x60)>>5;
            timeOffset      =recordHeader&0x1F;

            DebugLogger.debug("Compressed timestamp message!!");
            // Compressed timestamp Data Message

            // Find the record to add the data to
            record=repository.getFitRecord(localMessageType);
            // Check if the record has been found
            if (record!=null)
            {
                bytesRead+=this.parseCompressedTimestampDataMessage(in, record, timeOffset);
            }
            else
            {
                DebugLogger.error("Error: record to add the data to appears to have no definition message");
            }
        }
        else
        {
            // The header type - bit 7
            headerType      =FitRecord.HeaderType.NORMAL;
            
            // The local message type - bit 0-2 - (0-15)
            localMessageType=recordHeader&0x0F;
            timeOffset      =0;
            
            // Does the record have developer data? - bit 5
            if ((recordHeader&0x20)>0)
            {
                hasDeveloperData=true;
            }
            
            // Reserved bit value - bit 4
            if ((recordHeader&0x10)>0)
            {
                reservedBit=true;
            }

            // Bit 6 defines whether the record is a 'definition message' or a 
            // 'data message'
            if ((recordHeader&0x40)!=0)
            {
                // DEFINITION MESSGAGE
                record=repository.getFitRecord(localMessageType);
                if (record!=null)
                {
                    DebugLogger.debug("Record with local message number "+localMessageType+" already exists. Creating new definition!");
                }
                
                // Create a new record
                record=new FitRecord(localMessageType, headerType, hasDeveloperData);
                // Parse the data (field definitions)
                bytesRead+=this.parseDefinitionMessage(in, record, hasDeveloperData);
                // Add the record to the repository
                repository.addFitRecord(record);
                // Dump the record information
                record.dumpRecord();

            }
            else
            {
                // DATA MESSAGE
                if (hasDeveloperData)
                {
                    // Consistency check
                    DebugLogger.info("Illegal bit 5 value in header of Data Message");
                }
                if (reservedBit)
                {
                    // Consistency check
                    DebugLogger.info("Reserved bit in data message should be zero");
                }

                // Find the record to add the data to
                record=repository.getFitRecord(localMessageType);
                // Check if the record has been found
                if (record!=null)
                {
                    bytesRead+=this.parseDataMessage(in, record);
                }
                else
                {
                    DebugLogger.error("Error: record to add the data to appears to have no definition message");
                }
            }
            
        }
        
        
        return bytesRead;
    }
    
    
    /**
     * Returns the one and only singleton instance of this class
     * @return The instance
     */
    public static FitReader getInstance()
    {
        if (theInstance==null)
        {
            theInstance=new FitReader();
        }
        return theInstance;
    }
    
    /**
     * Read and parse the .fit file. This method reads the file and returns
     * the FitRecordRepository. The repository contains all FitRecrods read
     * and can be used for querying
     * @param fileName Name of the file to parse.
     * @return The FitRecordRepository
     */
    public FitRecordRepository readFile(String fileName)
    {
        FitHeader               fitHeader;
        FitReader               fitReader;
        FitRecordRepository     repository;
        int                     bytesExpected;
        int                     bytesRead;
        int                     crc;
        
        
        DebugLogger.info("Reading FIT file "+fileName);
        FileInputStream in = null;
        repository=new FitRecordRepository();

        try 
        {
            in = new FileInputStream(fileName);
        
            fitHeader=FitHeader.readHeader(in);
            bytesExpected=fitHeader.getDataSize();
            

            bytesRead=0;
            
            while (bytesExpected-bytesRead>0)
            {
                bytesRead+=readRecord(in, repository);
                DebugLogger.debug("Bytes Remaining: "+(bytesExpected-bytesRead));
            }
            
            if (bytesExpected-bytesRead!=0)
            {
                DebugLogger.error("Error reading records: unexpected end of file");
            }

            // Read the CRC
            crc=FitToolbox.readInt(in, 2, true);
            
            in.close();
        } 
        catch (IOException e)
        {
            DebugLogger.error(e.getMessage());
        }
        finally 
        {
            if (in != null) 
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    DebugLogger.error(e.getMessage());
                }
            }
        }
        return repository;
    }
    
    
}
