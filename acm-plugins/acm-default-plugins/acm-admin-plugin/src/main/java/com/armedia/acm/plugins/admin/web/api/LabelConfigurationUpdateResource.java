package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;

/**
 * Created by sergey.kolomiets on 4/24/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelConfigurationUpdateResource {
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/labelconfiguration/resource", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public void updateResource(
            @RequestParam("lang") String lang,
            @RequestParam("ns") String ns,
            @RequestBody String resource,
            HttpServletResponse response, boolean isInline) throws IOException,AcmObjectNotFoundException {


        String decodedResource = URLDecoder.decode(resource, "UTF-8");
        String userHome = System.getProperty("user.home");
        String labelsPath = userHome + "/.acm/labels/";
        String fileName = String.format("%s.%s.json", lang, ns);
        FileOutputStream fileOs = null;
        try {
            File rsFile = new File(labelsPath + fileName);
            rsFile.createNewFile();

            fileOs = new FileOutputStream(rsFile);

//            if (!rsFile.exists()) {
//                rsFile.createNewFile();
//            }

            byte[] buffer = decodedResource.getBytes();
            fileOs.write(buffer);
            response.getOutputStream().flush();
            fileOs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





