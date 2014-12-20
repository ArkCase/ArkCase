package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class TemplateDownloadControllerAPI {
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/template", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    // called for normal processing - file was found
    @ResponseBody
    public void downloadTemplate(
        @RequestParam("filePath") String filePath,
        HttpServletResponse response, boolean isInline) throws IOException,AcmObjectNotFoundException {

        Path path = Paths.get(filePath);
        String mimeType = Files.probeContentType(path);
        InputStream fileIs = null;

        try
        {
            File file = new File(filePath);
            if(file == null){
                fileNotFound(response);
            }
            String fileName = file.getName();
            fileIs = new FileInputStream(file);
            if(!isInline) {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            if ( fileIs != null )
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
}
