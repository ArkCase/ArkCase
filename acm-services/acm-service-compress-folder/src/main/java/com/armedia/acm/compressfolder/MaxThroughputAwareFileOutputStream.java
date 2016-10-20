/**
 *
 */
package com.armedia.acm.compressfolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple wrapper to the <code>FileOutputStream</code> that tracks the size of the resulting file by counting the
 * bytes written to the stream. If the number of bytes surpasses the size limit throws an <code>IOExcpetion</code>.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 15, 2016
 *
 */
public class MaxThroughputAwareFileOutputStream extends FileOutputStream
{

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Total number of bytes written so far.
     */
    private long totalBytes;

    /**
     * Calculated maximum number of bytes that this stream is allowed to write. It can not be reset. Negative value
     * means there is no limit to the number of bytes to be written by this stream.
     */
    private final long maxBytes;

    /**
     * Construct a new instance of the stream with calculating the maximum number of bytes to be written by this stream.
     *
     * @param file the file to which the stream is going to write.
     * @param size the maximum size of the resulting output file expressed in <code>SizeUnit</code>s.
     * @param sizeUnit the size unit in which the maximum file size is represented.
     * @throws FileNotFoundException if the file does not exist.
     */
    public MaxThroughputAwareFileOutputStream(File file, long size, SizeUnit sizeUnit) throws FileNotFoundException
    {
        super(file);
        maxBytes = calculateSizeInBytes(size, sizeUnit);
    }

    /**
     * Calculates the the size in bytes.
     *
     * @param size size expressed in <code>SizeUnit</code>s.
     * @param sizeUnit the size unit in which the maximum file size is represented.
     * @return the calculated size in bytes.
     */
    private long calculateSizeInBytes(long size, SizeUnit sizeUnit)
    {
        if (size <= 0)
        {
            return -1;
        }
        switch (sizeUnit)
        {
        case UNLIMITED:
            return -1;
        case GIGA:
            return size * 1024 * 1024 * 1024;
        case MEGA:
            return size * 1024 * 1024;
        case KILO:
            return size * 1024;
        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        if (maxBytes > 0)
        {
            totalBytes += len;
            if (totalBytes > maxBytes)
            {
                String message = String.format("Resulting compressed file is bigger than %1$s", maxBytes);
                log.warn(message);
                throw new IOException(message);
            }
        }

        super.write(b, off, len);
    }

}
