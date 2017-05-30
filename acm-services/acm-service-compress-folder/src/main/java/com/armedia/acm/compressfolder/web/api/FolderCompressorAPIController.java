/**
 *
 */
package com.armedia.acm.compressfolder.web.api;

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.compressfolder.FolderCompressorException;
import org.apache.tika.io.FilenameUtils;
import org.mule.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{folderId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public FolderCompressorResponse compressFolder(@PathVariable(value = "folderId") long folderId) throws FolderCompressorException
    {
        String fileName = folderCompressor.compressFolder(folderId);

        return new FolderCompressorResponse(fileName);
    }

    @RequestMapping(value = "/download/{folderId}", method = RequestMethod.GET)
    @ResponseBody
    public void getCompressedFolder(@PathVariable(value = "folderId") long folderId
            , HttpServletResponse response)
            throws FolderCompressorException, IOException
    {
        log.info("Downloading compressed folder by ID '{}'", folderId);

        String filePath = folderCompressor.compressFolder(folderId);
        log.debug("Compressed file has path '{}'", filePath);
        String fileName = FilenameUtils.getName(filePath);
        log.debug("Compressed file File Name is '{}'", fileName);

        downloadCompressedFolder(filePath, fileName, response);
    }

    public void downloadCompressedFolder(String filePath, String fileName, HttpServletResponse response) throws IOException
    {
        if (filePath != null)
        {
            try (InputStream fileOutput = FileUtils.openInputStream(new File(filePath)))
            {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.setContentType("application/zip");
                byte[] buffer = new byte[1024];
                int read;
                do
                {
                    read = fileOutput.read(buffer, 0, buffer.length);
                    if (read > 0)
                    {
                        response.getOutputStream().write(buffer, 0, read);
                    }
                }
                while (read > 0);
                response.getOutputStream().flush();
            } catch (IOException e)
            {
                log.error("Could not close stream: {}", e.getMessage(), e);
            }
        }
    }

    public void setFolderCompressor(FolderCompressor folderCompressor)
    {
        this.folderCompressor = folderCompressor;
    }
}