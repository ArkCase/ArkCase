
package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.model.TemplateUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by manoj.dhungana on 12/8/2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class ListAllTemplatesControllerAPI {
    private Logger log = LoggerFactory.getLogger(getClass());
    List<Object> templateUploadList = new ArrayList<>();

    @RequestMapping(value = "/template/list", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<Object> listTemplates(
            Authentication authentication) throws Exception {

        String userHome = System.getProperty("user.home");
        String pathName = userHome + "/.acm/correspondenceTemplates";
        File templateFolder = new File(pathName);
        File[] templates = templateFolder.listFiles();
        getUploadedTemplates().clear();
        if(templates != null){
            for (File template : templates) {
                //access creation and last modified date via file attributes
                TemplateUpload templateUpload = new TemplateUpload();
                Path path = Paths.get(pathName + "/" + template.getName());
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                FileTime creationTime = attributes.creationTime();
                FileTime modifiedTime = attributes.lastModifiedTime();

                //details
                templateUpload.setPath(template.getAbsolutePath());
                templateUpload.setModified(modifiedTime.toString());
                templateUpload.setName(template.getName());
                templateUpload.setCreator(authentication.getName());
                templateUpload.setCreated(creationTime.toString());
                getUploadedTemplates().add(templateUpload);
            }
        }
        return getUploadedTemplates();
    }


    public List<Object> getUploadedTemplates() {
        return templateUploadList;
    }

    public void setUploadedTemplates(List<Object> templateUploadList) {
        this.templateUploadList = templateUploadList;
    }
}
