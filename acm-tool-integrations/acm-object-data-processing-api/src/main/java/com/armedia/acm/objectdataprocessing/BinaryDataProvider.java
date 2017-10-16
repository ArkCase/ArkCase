package com.armedia.acm.objectdataprocessing;

import java.io.InputStream;

/**
 * Abstraction containing binary data with arbitrary mime type, extracted from an instances of type<code>CF</code>.
 * Instance of <code>BinaryDataProvider</code> is produced by an implementation of
 * <code>ObjectDataExtractingProcessor</code>. It is up to the service implementation to decide what data associated
 * with the <code>CF</code> instance will be extracted and added to the content ready for printing or other purposes,
 * for example filtering files included in predefined folders, specific versions and so on.
 *
 * @see ObjectDataExtractingProcessor
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 16, 2016
 *
 */
public interface BinaryDataProvider<CF>
{

    /**
     * @return generated file name to be included in the <code>Content-Disposition</code> response header.
     */
    String getFileName();

    /**
     * @return input stream containing the binary data from the generated document.
     */
    InputStream getContent();

    /**
     * Return the number of bytes of the underlying binary data to be served.
     *
     * @return
     */
    long getContentLength();

    /**
     * Cleans up any resources associated with the content.
     */
    void releaseContent();

}