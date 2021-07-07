/**
 *
 */
package com.armedia.acm.compressfolder.web.api;

/*-
 * #%L
 * ACM Service: Folder Compressing Service
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.compressfolder.model.CompressNode;
import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.io.FilenameUtils;
import org.owasp.encoder.Encode;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * A REST endpoint for invoking the <code>FolderCompressor</code> service.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 15, 2016
 *
 */
@Controller
@RequestMapping(value = { "/api/v1/service/compressor", "/api/latest/service/compressor" })
public class FolderCompressorAPIController
{
    private FolderCompressor folderCompressor;
    private ArkPermissionEvaluator arkPermissionEvaluator;
    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/{folderId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public FolderCompressorResponse compressFolder(@PathVariable(value = "folderId") long folderId) throws AcmFolderException
    {
        String fileName = folderCompressor.compressFolder(folderId);

        return new FolderCompressorResponse(fileName);
    }

    @RequestMapping(value = "/download/{folderId}", method = RequestMethod.GET)
    @ResponseBody
    public void getCompressedFolder(@PathVariable(value = "folderId") long folderId, HttpServletResponse response)
            throws IOException, AcmFolderException
    {
        log.info("Downloading compressed folder by ID '{}'", folderId);

        String filePath = folderCompressor.compressFolder(folderId);
        log.debug("Compressed file has path '{}'", filePath);
        String fileName = FilenameUtils.getName(filePath);
        log.debug("Compressed file File Name is '{}'", fileName);

        downloadCompressedFolder(filePath, fileName, response);
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getCompressedSelectedFolderAndFiles(@RequestBody CompressNode compressNode, Authentication auth)
            throws AcmFolderException
    {
        folderCompressor.compressFolder(compressNode, auth);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/download/files", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> downloadCompressedSelectedFiles(
            @RequestParam(value = "fileIds") List<Long> fileIds, Authentication auth) throws Exception
    {

        for (Long fileId : fileIds)
        {
            if (!getArkPermissionEvaluator().hasPermission(auth, fileId, "FILE", "write|group-write|read|group-read"))
            {
                throw new AcmAccessControlException(Arrays.asList(""),
                        "The user {" + auth.getName() + "} is not allowed to read a file with id=" + fileId);
            }
        }

        folderCompressor.compressFiles(fileIds, auth);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @RequestMapping(value = "/download/files/zip", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> downloadCompressedZipFile(
            @RequestParam(value = "zipFilePath") String zipFilePath) throws Exception
    {

        String fileName = zipFilePath.substring(zipFilePath.lastIndexOf(File.separator) + 1);
        File zipFile = new File(zipFilePath);
        if (zipFile.exists())
        {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(zipFile.length())
                    .body(new InputStreamResource(new FileInputStream(zipFile)));
        }
        else
        {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void downloadCompressedFolder(String filePath, String fileName, HttpServletResponse response) throws IOException
    {
        if (filePath != null)
        {
            try (InputStream fileOutput = FileUtils.openInputStream(new File(filePath)))
            {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + Encode.forJava(fileName) + "\"");
                response.setContentType(Encode.forJava("application/zip"));
                byte[] buffer = new byte[1024];
                int read;
                do
                {
                    read = fileOutput.read(buffer, 0, buffer.length);
                    if (read > 0)
                    {
                        response.getOutputStream().write(buffer, 0, read);
                    }
                } while (read > 0);
                response.getOutputStream().flush();
            }
            catch (IOException e)
            {
                log.error("Could not close stream: {}", e.getMessage(), e);
            }
        }
    }

    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }

    public void setFolderCompressor(FolderCompressor folderCompressor)
    {
        this.folderCompressor = folderCompressor;
    }
}
