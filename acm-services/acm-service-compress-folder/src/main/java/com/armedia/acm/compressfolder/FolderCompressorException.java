/**
 *
 */
package com.armedia.acm.compressfolder;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 13, 2016
 *
 */
public class FolderCompressorException extends Exception
{

    private static final long serialVersionUID = -8701330465345902744L;

    /**
     * @param message
     */
    public FolderCompressorException(String message)
    {
        super(message);
    }

    /**
     * @param e
     */
    public FolderCompressorException(Exception e)
    {
        super(e);
    }

    /**
     * @param message
     */
    public FolderCompressorException(Long folderId)
    {
        super(String.format("No folder with id %d was found!", folderId));
    }

}
