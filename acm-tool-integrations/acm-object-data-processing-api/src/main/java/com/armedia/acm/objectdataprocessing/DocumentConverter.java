package com.armedia.acm.objectdataprocessing;

import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * Defines functionality for converting an input stream of input mime type to input stream of output mime type.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
 *
 */
public interface DocumentConverter
{

    /**
     * Returns the mime type of the binary data of the converted <code>InputStream</code>.
     *
     * @return the mime type of the converted <code>InputStream</code>.
     */
    default String getOutputMimeType()
    {
        return "";
    }

    /**
     * Returns a set of mime types of the binary data of the <code>InputStream</code> this converter is capable of
     * processing.
     *
     * @return set of mime types this converter is capable of processing.
     */
    default Set<String> getSupportedMimeTypes()
    {
        return Collections.emptySet();
    }

    /**
     * Converts input stream of input mime type to input stream of output mime type.
     *
     * @param sourceInputStream input stream of document with arbitrary mime type.
     * @param mimeType the mime type of the <code>sourceInputStream</code>.
     * @return the converted input stream.
     * @throws UnconvertableSourceException if the <code>sourceInputStream</code> can not be converted.
     */
    InputStream convertStream(InputStream sourceInputStream, String mimeType) throws UnconvertableSourceException;

}
