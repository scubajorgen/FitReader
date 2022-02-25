/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.studioblueplanet.fitreader;

import java.io.InputStream;
import java.io.IOException;

/**
 * This class offers a read method that takes into account the CRC check
 * @author jorgen
 */
public class CrcReader 
{
    private final int[] crcTable =
    {
        0x0000, 0xCC01, 0xD801, 0x1400, 0xF001, 0x3C00, 0x2800, 0xE401,
        0xA001, 0x6C00, 0x7800, 0xB401, 0x5000, 0x9C01, 0x8801, 0x4400
    };

    private int crc;
    private int bytesRead;

    public CrcReader()
    {
        reset();
    }
    
    /**
     * CRC calculation
     * @param crc Current crc value
     * @param theByte Byte to add?
     * @return crc value
     */
    private int updateCrcForByte(int crc, int theByte)
    {
        int tmp;
        // compute checksum of lower four bits of byte
        tmp = crcTable[crc & 0xF];
        crc = (crc >> 4) & 0x0FFF;
        crc = crc ^ tmp ^ crcTable[theByte & 0xF];
        // now compute checksum of upper four bits of byte
        tmp = crcTable[crc & 0xF];
        crc = (crc >> 4) & 0x0FFF;
        crc = crc ^ tmp ^ crcTable[(theByte >> 4) & 0xF];
        return crc;
    }    
    /**
     * Reset the CRC calculation
     */
    public void reset()
    {
        crc         =0;
        bytesRead   =0;
    }
    
    
    /**
     * Read method. This method reads a byte from the input stream and 
     * updates the CRC and byte counter. This method must be called for all
     * bytes to be read, INCLUDING THE CRC bytes!
     * @param in Input stream
     * @return The byte
     * @throws IOException If reading fails
     */
    public int read(InputStream in) throws IOException
    {
        int read;
        read=in.read();
        crc=updateCrcForByte(crc, read);
        bytesRead++;
        return read;
    }

    /**
     * Indicates whether the CRC is correct after reading the bytes, including
     * the CRC
     * @return True if the CRC is valid, false if not.
     */
    public boolean isValid()
    {
        return (crc==0);
    }
    
    /**
     * Returns the cumulative number of bytes read after a reset.
     * @return The number of bytes read.
     */
    public int getNumberOfBytesRead()
    {
        return bytesRead;
    }
}
