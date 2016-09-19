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
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 15, 2016
 *
 */
public class MaxTroughputAwareFileOutputStream extends FileOutputStream
{

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    private long totalBytes;

    private final long maxBytes;

    /**
     * @param file
     * @throws FileNotFoundException
     */
    public MaxTroughputAwareFileOutputStream(File file, long size, SizeUnit sizeUnit) throws FileNotFoundException
    {
        super(file);
        maxBytes = calculateSizeInBytes(size, sizeUnit);
    }

    /**
     * @param size
     * @param sizeUnit
     * @return
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
