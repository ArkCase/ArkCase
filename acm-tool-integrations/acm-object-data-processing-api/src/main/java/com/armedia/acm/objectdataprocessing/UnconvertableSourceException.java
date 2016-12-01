package com.armedia.acm.objectdataprocessing;

/**
 * Thrown in case the source input stream cannot be converted in input stream with binary data containing a PDF
 * document.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 18, 2016
 *
 */
public class UnconvertableSourceException extends Exception
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