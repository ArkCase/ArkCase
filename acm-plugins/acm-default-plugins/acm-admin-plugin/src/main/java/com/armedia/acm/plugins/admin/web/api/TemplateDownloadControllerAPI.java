package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger log = LoggerFactory.getLogger(getClass());
    private String correspondenceFolderName;

    @RequestMapping(value = "/template", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    // called for normal processing - file was found
    @ResponseBody
    public void downloadTemplate(@RequestParam("fileName") String fileName, HttpServletResponse response, boolean isInline)
            throws IOException, AcmObjectNotFoundException
    {

        Resource templateFile = new FileSystemResource(getCorrespondenceFolderName() + File.separator + fileName);

        Path path = Paths.get(templateFile.getURI());
        String mimeType = Files.probeContentType(path);
        InputStream fileIs = null;

        try
        {
            File file = new File(templateFile.getURI());
            if (file == null)
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
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (fileIs != null)
            {
                try
                {
                    fileIs.close();
                } catch (IOException e)
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
