package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class TemplateDownloadControllerAPI
{
    private Logger log = LogManager.getLogger(getClass());
    private String correspondenceFolderName;

    @RequestMapping(value = "/template", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    // called for normal processing - file was found
    @ResponseBody
    public void downloadTemplate(@RequestParam("downloadFileName") String fileName, HttpServletResponse response, boolean isInline)
            throws IOException, AcmObjectNotFoundException
    {

        Resource templateFile = new FileSystemResource(getCorrespondenceFolderName() + File.separator + fileName);

        Path path = Paths.get(templateFile.getURI());
        String mimeType = Files.probeContentType(path);
        InputStream fileIs = null;

        try
        {
            File file = new File(templateFile.getURI());
            if (!file.exists())
            {
                fileNotFound(response);
            }
            String name = file.getName();
            fileIs = new FileInputStream(file);
            if (!isInline)
            {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
            }
            response.setContentType(mimeType);
            byte[] buffer = new byte[1024];
            int read;
            do
            {
                read = fileIs.read(buffer, 0, buffer.length);
                if (read > 0)
                {
                    response.getOutputStream().write(buffer, 0, read);
                }
            } while (read > 0);
            response.getOutputStream().flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fileIs != null)
            {
                try
                {
                    fileIs.close();
                }
                catch (IOException e)
                {
                    log.error("Could not close file stream: " + e.getMessage(), e);
                }
            }
        }
    }

    // called when the file was not found.
    private void fileNotFound(HttpServletResponse response) throws AcmObjectNotFoundException
    {
        throw new AcmObjectNotFoundException(null, null, "File not found", null);
    }

    public String getCorrespondenceFolderName()
    {
        return correspondenceFolderName;
    }

    public void setCorrespondenceFolderName(String correspondenceFolderName)
    {
        this.correspondenceFolderName = correspondenceFolderName;
    }
}
