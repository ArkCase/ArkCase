/**
 *
 */
package com.armedia.acm.compressfolder.web.api;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 15, 2016
 *
 */
public class FolderCompressorResponse
{

    private String fileName;

    public FolderCompressorResponse()
    {
    }

    /**
     * @param fileName
     */
    public FolderCompressorResponse(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

}
