
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

import com.armedia.acm.plugins.admin.model.TemplateUpload;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class ListAllTemplatesControllerAPI
{
    List<Object> templateUploadList = new ArrayList<>();
    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/template/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public List<Object> listTemplates(Authentication authentication) throws Exception
    {

        String userHome = System.getProperty("user.home");
        String pathName = userHome + "/.arkcase/acm/correspondenceTemplates";
        File templateFolder = new File(pathName);
        File[] templates = templateFolder.listFiles();
        getUploadedTemplates().clear();
        if (templates != null)
        {
            for (File template : templates)
            {
                // access creation and last modified date via file attributes
                TemplateUpload templateUpload = new TemplateUpload();
                Path path = Paths.get(pathName + "/" + template.getName());
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                FileTime creationTime = attributes.creationTime();
                FileTime modifiedTime = attributes.lastModifiedTime();

                // details
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

    public List<Object> getUploadedTemplates()
    {
        return templateUploadList;
    }

    public void setUploadedTemplates(List<Object> templateUploadList)
    {
        this.templateUploadList = templateUploadList;
    }
}
