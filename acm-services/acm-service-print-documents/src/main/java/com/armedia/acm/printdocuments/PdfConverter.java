package com.armedia.acm.printdocuments;

import java.io.InputStream;

/**
 * Defines functionality for converting an input stream of given mime type to input stream of
 * <code>application/pdf</code> mime type.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
 *
 */
public interface PdfConverter
{

    /**
     * Converts input stream of given mime type to input stream of <code>application/pdf</code> mime type.
     *
     * @param sourceInputStream input stream of document with arbitrary mime type.
     * @param mimeType the mime type of the <code>sourceInputStream</code>.
     * @return the converted input stream.
     * @throws UnconvertableSourceException if the <code>sourceInputStream</code> can not be converted.
     */
    InputStream convertToPdf(InputStream sourceInputStream, String mimeType) throws UnconvertableSourceException;

    /**
     * Thrown in case the source input stream cannot be converted in input stream with binary data containing a PDF
     * document.
     *
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
     *
     */
    public static class UnconvertableSourceException extends Exception
    {

        private static final long serialVersionUID = 5639921122954148227L;

        public UnconvertableSourceException(String message)
        {
            super(message);
        }

        public UnconvertableSourceException(String message, Throwable e)
        {
            super(message, e);
        }

    }

}
