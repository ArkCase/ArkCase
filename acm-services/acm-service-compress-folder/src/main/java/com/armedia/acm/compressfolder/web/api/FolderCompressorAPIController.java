/**
 *
 */
package com.armedia.acm.compressfolder.web.api;

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.compressfolder.FolderCompressorException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * A REST endpoint for invoking the <code>FolderCompressor</code> service.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 15, 2016
 *
 */
@Controller
@RequestMapping(value = {"/api/v1/service/compressor", "/api/latest/service/compressor"})
public class FolderCompressorAPIController
{

    private FolderCompressor folderCompressor;

    @RequestMapping(value = "/{folderId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public FolderCompressorResponse compressFolder(@PathVariable(value = "folderId") long folderId) throws FolderCompressorException
    {
        String fileName = folderCompressor.compressFolder(folderId);

        return new FolderCompressorResponse(fileName);
    }

    public void setFolderCompressor(FolderCompressor folderCompressor)
    {
        this.folderCompressor = folderCompressor;
    }

}
