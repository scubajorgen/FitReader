/*
    Copyright (c) 2023 Jorgen

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */

package net.studioblueplanet.fitreader;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class is the entry point for this library. It is the reader that 
 * reads the .FIT file.
 * @author Jorgen
 */
public class FitReader
{   
    private static final Logger     LOGGER = LogManager.getLogger(FitReader.class);
    private static FitReader        theInstance=null;
    private FitMessageRepository    repository;

    /**
     * Constructor. It is private, since the pattern used is Singleton.
     * Use getInstance() to get the one and only instance.
     */
    private FitReader()
    {
    }
    
    /**
     * Parses the record data, provided the record is a FIT "data message"
     * @param in The input reader
     * @param record The record to add the data to
     * @throws IOException In case of misread
     */
    private int parseDataMessage(CrcReader reader, InputStream in, FitMessage record) throws IOException
    {
        int     i;
        int     bytesRead;
        int     recordSize;
        int     j;
        int[]   bytes;
        
        bytesRead=0;
        
        i=0;
        recordSize=record.getRecordSize();
        bytes=new int[recordSize];
        while (i<recordSize)
        {
            bytes[i]=reader.read(in);
            bytesRead++;
            i++;
        }
        record.addDataRecord(bytes);
        return bytesRead;
    }
    

    /**
     * Parses the record data, provided the record is a FIT "data message"
     * @param in The input reader
     * @param record The record to add the data to
     * @throws IOException In case of misread
     */
    private int parseCompressedTimestampDataMessage(CrcReader reader, InputStream in, FitMessage record, int timeOffset) throws IOException
    {
        int bytesRead;
        
        // To do: store compressed timestamp offset with record
        
        bytesRead=parseDataMessage(reader, in, record);
        
        record.addTimeStampOffset(timeOffset);
        
        return bytesRead;
    }    
    
    /**
     * Parses the record data, provided the record is a FIT "definition message"
     * @param in The input reader
     * @param record The record to add the definition to
     * @throws IOException In case of misread
     */
    private int parseDefinitionMessage(CrcReader reader, InputStream in, FitMessage record, boolean hasDeveloperData) throws IOException
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
        reader.read(in);
        bytesRead++;
        
        // architecture
        architecture=reader.read(in);
        bytesRead++;
        if (architecture>0)
        {
            record.setEndianness(FitMessage.Endianness.BIGENDIAN);
        }
        else
        {
            record.setEndianness(FitMessage.Endianness.LITTLEENDIAN);
        }
        
        // Message Number
        globalMessageNumber=FitToolbox.readInt(reader, in, 2, record.isLittleEndian());
        
        bytesRead+=2;
        record.setGlobalMessageNumber(globalMessageNumber);
        
        // Number of data fields
        numberOfDataFields=reader.read(in);
        bytesRead++;
        
        // The data fields
        i=0;
        while (i<numberOfDataFields)
        {
            fieldDefinitionNumber   =reader.read(in);
            size                    =reader.read(in);
            baseType                =reader.read(in);
            bytesRead               +=3;
            record.addMessageField(globalMessageNumber, fieldDefinitionNumber, size, baseType);
            i++;
        }
        
        // If the record contains developer data, read the developer field definition
        if (hasDeveloperData)
        {
            // Number of developer data fields
            numberOfDataFields=reader.read(in);
            bytesRead++;
 
            // The developer fields
            i=0;
            while (i<numberOfDataFields)
            {
                fieldDefinitionNumber   =reader.read(in);
                size                    =reader.read(in);
                developerDataIndex      =reader.read(in);
                record.addDeveloperField(globalMessageNumber, fieldDefinitionNumber, size, developerDataIndex, repository.getFitMessage("field_description"));
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
     * A 'Definition Message' defines the fields in the record. A 'Data Message'
     * contains field values.
     * A definition message generates a new Record instance, a data message
     * is transferred to a record values and added to the Record
     * @param reader CRC Reader to use for CRC verification
     * @param in Input stream
     * @param repository Repository to add the record to
     * @return The number of bytes read
     * @throws IOException In case of miss read
     */
    private int readRecord(CrcReader reader, InputStream in, FitMessageRepository repository) throws IOException
    {
        FitMessage                  record;
        int                         bytesRead;
        int                         recordHeader;
        FitMessage.HeaderType       headerType;
        int                         localMessageType;
        int                         timeOffset;
        boolean                     hasDeveloperData;
        boolean                     reservedBit;
        
        bytesRead               =0;
        hasDeveloperData        =false;
        reservedBit             =false;
            
        // Read the first byte: the record header
        recordHeader            =reader.read(in);
        bytesRead++;
        
        LOGGER.debug("********************* Record **************************");
        LOGGER.debug("Header {}", String.format("0x%02x", recordHeader));
        
        // Bit 7 defines whether the header is normal or compressed timestamp
        // This bit defines the encoding of the rest of the byte
        if ((recordHeader&0x80)!=0)
        {
            headerType      =FitMessage.HeaderType.COMPRESSED_TIMESTAMP;
            localMessageType=(recordHeader&0x60)>>5;
            timeOffset      =recordHeader&0x1F;

            LOGGER.debug("Compressed timestamp message!!");
            // Compressed timestamp Data Message

            // Find the record to add the data to
            record=repository.getFitMessage(localMessageType);
            // Check if the record has been found
            if (record!=null)
            {
                bytesRead+=this.parseCompressedTimestampDataMessage(reader, in, record, timeOffset);
            }
            else
            {
                LOGGER.error("Error: record to add the data to appears to have no definition message");
            }
        }
        else
        {
            // The header type - bit 7
            headerType      =FitMessage.HeaderType.NORMAL;
            
            // The local message type - bit 0-3 - (0-15)
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
                record=repository.getFitMessage(localMessageType);
                if (record!=null)
                {
                    LOGGER.debug("Record with local message number {} already exists. Creating new definition!", localMessageType);
                }
                
                // Create a new record
                record=new FitMessage(localMessageType, headerType, hasDeveloperData);
                // Parse the data (field definitions)
                bytesRead+=this.parseDefinitionMessage(reader, in, record, hasDeveloperData);
                // Add the record to the repository
                repository.addFitMessage(record);
                // Dump the record information
                record.dumpMessage();

            }
            else
            {
                // DATA MESSAGE
                if (hasDeveloperData)
                {
                    // Consistency check
                    LOGGER.info("Illegal bit 5 value in header of Data Message");
                }
                if (reservedBit)
                {
                    // Consistency check
                    LOGGER.info("Reserved bit in data message should be zero");
                }

                // Find the record to add the data to
                record=repository.getFitMessage(localMessageType);
                // Check if the record has been found
                if (record!=null)
                {
                    bytesRead+=this.parseDataMessage(reader, in, record);
                }
                else
                {
                    LOGGER.error("Error: record to add the data to appears to have no definition message");
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
     * This method reads a .FIT file from given input stream
     * @param in The input stream
     * @return A FitRepository containing all messages and data records read or
     *         null if a CRC error occurred.
     */
    public FitMessageRepository readInputStream(InputStream in, boolean ignoreCrc)
    {
        FitHeader               fitHeader;
        FitReader               fitReader;
        int                     bytesExpected;
        int                     bytesRead;
        int                     crc;
        CrcReader               reader;
     
        repository=new FitMessageRepository();
        reader=new CrcReader();

        try 
        {
        
            fitHeader       =FitHeader.readHeader(in, ignoreCrc);
            if (fitHeader!=null)
            {
                bytesExpected   =fitHeader.getDataSize();

                bytesRead       =0;

                while (bytesExpected-bytesRead>0)
                {
                    bytesRead+=readRecord(reader, in, repository);
                    LOGGER.debug("Bytes Remaining: {}", (bytesExpected-bytesRead));
                }

                if (bytesExpected-bytesRead!=0)
                {
                    LOGGER.error("Error reading records: unexpected end of file");
                }

                // Read the CRC
                crc=FitToolbox.readInt(reader, in, 2, true);

                // CRC check
                if (!reader.isValid())
                {
                    LOGGER.error("File has invalid CRC!");
                    if (!ignoreCrc)
                    {
                        repository=null;
                    }
                }
            }
            else
            {
                repository=null;
            }
            in.close();
        } 
        catch (IOException e)
        {
            LOGGER.error("Error reading input stream: {}", e.getMessage());
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
                    LOGGER.error("Error closing input stream: {}", e.getMessage());
                }
            }
        }
        return repository;
    }
            

    
    /**
     * Read and parse the .fit file. This method reads the file and returns
     * the FitRecordRepository. The repository contains all FitRecords read
     * and can be used for querying
     * @param fileName Name of the file to parse.
     * @param ignoreCrc Indicates whether to skip the CRC check
     * @return The FitRecordRepository
     */
    public FitMessageRepository readFile(String fileName, boolean ignoreCrc)
    {
        FitMessageRepository repo;
        FileInputStream in;
        
        repo=null;
        
        try
        {
            in=new FileInputStream(fileName);
            repo=this.readInputStream(in, ignoreCrc);
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error("File not found: {}", e.getMessage());
        }
        return repo;
    }
    
    /**
     * Read and parse the .fit file. This method reads the file and returns
     * the FitRecordRepository. The repository contains all FitRecords read
     * and can be used for querying
     * For backwards compatibility, doesn't execute CRC
     * @param fileName Name of the file to parse.
     * @return The FitRecordRepository
     */
    public FitMessageRepository readFile(String fileName)
    {
        return readFile(fileName, true);
    }
    
}
