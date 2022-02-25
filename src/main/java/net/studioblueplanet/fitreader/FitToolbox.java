/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.fitreader;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Jorgen
 */
public class FitToolbox
{
    /**
     * This method reads an integer value from the input stream
     * @param in Input stream
     * @param bytes Number of bytes
     * @param isLittleEndian True if the format in the stream is little endian, false for big endian
     * @return The integer value
     * @throws IOException When miss read
     */
    public static int readInt(CrcReader reader, InputStream in, int bytes, boolean isLittleEndian) throws IOException
    {
        int c;
        int b;
        int i;
        
        
        i=0;
        c=0;
        while (i<bytes)
        {
            b=reader.read(in);
            if (isLittleEndian)
            {
                b<<=(i*8);
            }
            else
            {
                b<<=((bytes-i-1)*8);
            }
            c |= b;
            i++;
        }
        
        return c;
    }
    
    /**
     * Reads a string from the input stream.
     * @param in Input stream
     * @param chars Number of characters to read
     * @return The string read
     * @throws IOException When an read error occurs
     */
    public static String readString(CrcReader reader, InputStream in, int chars) throws IOException
    {
        int     i;
        String  string;
        
        string="";
        i=0;
        while (i<chars)
        {
            string+=(char)reader.read(in);
            i++;
        }
        return string;
    }
        
}
