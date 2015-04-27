package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Created by sergey.kolomiets on 4/24/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelConfigurationRetrieveLanguages {
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/labelconfiguration/languages", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public void retrieveResource(
            HttpServletResponse response, boolean isInline) throws IOException,AcmObjectNotFoundException {


        String userHome = System.getProperty("user.home");
        String labelsPath = userHome + "/.acm/labels/";
        String fileName = "languages.json";
        InputStream fileIs = null;
        try {
            File lngFile = new File(labelsPath + fileName);
            fileIs = new FileInputStream(lngFile);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





