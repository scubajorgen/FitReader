/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import java.io.IOException;
import java.io.InputStream;
import net.studioblueplanet.logger.DebugLogger;

/**
 * Represents the FIT file header. Contains the fields in the header
 * @author Jorgen
 */
public class FitHeader
{
    private int         headerSize;
    private int         protocolVersion;
    private int         profileVersion;
    private int         dataSize;
    private String      dataType;
    private int         crc;   

    /**
     * Private constructor. Use readHeader() to create an instance.
     */
    private FitHeader()
    {
        
    }
    
    /**
     * Returns the header size. Specification: Indicates the length of this file header including 
     * header size. Minimum size is 12. This may be increased in future to add 
     * additional optional information.
     * @return Header size
     */
    public int getHeaderSize()
    {
        return headerSize;
    }

    /**
     * Returns the protocol version as provided in the FIT SDK
     * @return The version
     */
    public int getProtocolVersion()
    {
        return protocolVersion;
    }

    /**
     * Returns the profile version as provided in the FIT SDK
     * @return The version
     */
    public int getProfileVersion()
    {
        return profileVersion;
    }

    /**
     * Returns the data size. Specification: Length of the Data Records section 
     * in bytes Does not include Header or CRC
     * @return The size
     */
    public int getDataSize()
    {
        return dataSize;
    }

    /**
     * Returns the data type. Specification: ASCII values for “.FIT”. A FIT 
     * binary file opened with a text editor will contain a readable “.FIT” 
     * in the first line.
     * @return 
     */
    public String getDataType()
    {
        return dataType;
    }

    /**
     * Returns the value of the CRC (see section 3.3.2 ) of Bytes 0 through 11, 
     * or may be set to 0x0000. This field is optional.
     * @return The crc 
     */
    public int getCrc()
    {
        return crc;
    }
    
    /**
     * This method reads the global header of the FIT file.
     * @param in Input stream to read from
     * @return The object containing header information
     * @throws IOException When miss read
     */
    public static FitHeader readHeader(InputStream in) throws IOException
    {
        FitHeader header;
        int crc;
        
        header=new FitHeader();
       
        header.headerSize           =FitToolbox.readInt(in, 1, true);
        header.protocolVersion      =FitToolbox.readInt(in, 1, true);
        header.profileVersion       =FitToolbox.readInt(in, 2, true);
        header.dataSize             =FitToolbox.readInt(in, 4, true);
        header.dataType             =FitToolbox.readString(in, 4);
        header.crc                  =FitToolbox.readInt(in, 2, true);
        
        
        
        DebugLogger.info("Header size      : "+header.headerSize);
        DebugLogger.info("Protocol Version : "+header.protocolVersion);
        DebugLogger.info("Profile Version  : "+header.profileVersion);
        DebugLogger.info("Data size        : "+header.dataSize);
        DebugLogger.info("Data type        : "+header.dataType);
        DebugLogger.info("CRC              : "+header.crc);
        
        return header; 
    }    
}
