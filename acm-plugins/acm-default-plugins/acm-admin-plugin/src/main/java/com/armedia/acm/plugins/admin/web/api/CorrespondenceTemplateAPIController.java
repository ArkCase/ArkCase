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

import com.armedia.acm.correspondence.model.Template;
import com.armedia.acm.correspondence.service.CorrespondenceService;
import com.armedia.acm.plugins.admin.exception.CorrespondenceTemplateNotFoundException;
import com.armedia.acm.plugins.admin.model.CorrespondenceTemplateRequestResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 27, 2017
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class CorrespondenceTemplateAPIController
{

    private CorrespondenceService correspondenceService;
    private String correspondenceFolderName;
    private String emailTemplatesFolderName;

    @RequestMapping(value = "/templates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceTemplateRequestResponse> getAllTemplates()
    {
        return correspondenceService.getAllTemplates().stream().map(t -> mapTemplateToResponse(Optional.of(t)))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/templates/active/{templateType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceTemplateRequestResponse> getActiveVersionTemplates(@PathVariable(value = "templateType") String templateType)
    {
        return correspondenceService.getActiveVersionTemplatesByTemplateType(templateType).stream().map(template -> mapTemplateToResponse(Optional.of(template)))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/templates/activated/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceTemplateRequestResponse> getActivatedActiveVersionTemplatesByObjectType(
            @PathVariable(value = "objectType") String objectType)
    {
        return correspondenceService.getActivatedActiveVersionTemplatesByObjectType(objectType).stream()
                .map(t -> mapTemplateToResponse(Optional.of(t))).collect(Collectors.toList());
    }

    @RequestMapping(value = "/template/{templateId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CorrespondenceTemplateRequestResponse getTemplate(@PathVariable(value = "templateId") String templateId)
    {
        return mapTemplateToResponse(correspondenceService.getActiveTemplateById(templateId));
    }

    @RequestMapping(value = "/template/{templateId}/{templateFilename:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CorrespondenceTemplateRequestResponse getTemplateByIdAndFilename(@PathVariable(value = "templateId") String templateId,
            @PathVariable(value = "templateFilename") String templateFilename)
    {
        return mapTemplateToResponse(correspondenceService.getTemplateByIdAndFilename(templateId, templateFilename));
    }

    @RequestMapping(value = "/template/versions/{templateId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceTemplateRequestResponse> getTemplateVersionsById(@PathVariable(value = "templateId") String templateId)
    {
        return correspondenceService.getTemplateVersionsById(templateId).stream().map(t -> mapTemplateToResponse(Optional.of(t)))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/template/{templateId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceTemplateRequestResponse> deleteTemplate(@PathVariable(value = "templateId") String templateId)
            throws IOException
    {
        List<CorrespondenceTemplateRequestResponse> deleteResponse = new ArrayList<>();
        List<Template> templates = correspondenceService.getTemplateVersionsById(templateId);
        String msg = "";
        for (Template template : templates)
        {
            File templateFile = new File(template.getTemplateType().equals("emailTemplate") ? getEmailTemplatesFolderName() : getCorrespondenceFolderName(), template.getTemplateFilename());
            if (FileUtils.deleteQuietly(templateFile))
            {
                deleteResponse.add(mapTemplateToResponse(
                        correspondenceService.deleteTemplateByIdAndVersion(template.getTemplateId(), template.getTemplateVersion())));
            }
            else
            {
                msg += templateFile + ";";
            }
        }

        if (msg.isEmpty())
        {
            return deleteResponse;
        }
        else
        {
            throw new CorrespondenceTemplateNotFoundException(msg);
        }
    }

    @RequestMapping(value = "/template/{templateId}/{templateVersion:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CorrespondenceTemplateRequestResponse deleteTemplateByIdAndVersion(@PathVariable(value = "templateId") String templateId,
            @PathVariable(value = "templateVersion") String templateVersion) throws IOException, CorrespondenceTemplateNotFoundException
    {
        File templatesDir = new File(System.getProperty("user.home") + "/.arkcase/acm/correspondenceTemplates");
        Optional<Template> optionalChildDirectory = correspondenceService.getTemplateByIdAndVersion(templateId,
                templateVersion);
        String childDirectoryName = optionalChildDirectory.map(correspondenceTemplate -> correspondenceTemplate.getTemplateFilename())
                .orElseThrow(CorrespondenceTemplateNotFoundException::new);

        File templateFile = new File(templatesDir,
                childDirectoryName);
        if (FileUtils.deleteQuietly(templateFile))
        {
            return mapTemplateToResponse(correspondenceService.deleteTemplateByIdAndVersion(templateId, templateVersion));
        }
        else
        {
            throw new CorrespondenceTemplateNotFoundException();
        }
    }

    @RequestMapping(value = "/template", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CorrespondenceTemplateRequestResponse updateTemplate(@RequestBody CorrespondenceTemplateRequestResponse request,
            Authentication auth) throws IOException
    {
        return mapTemplateToResponse(correspondenceService.updateTemplate(mapRequestToTemplate(request, auth)));
    }

    @ExceptionHandler(CorrespondenceTemplateNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Error while retreiving correspondence template.")
    public void handleException()
    {
    }

    /**
     * @param templateHolder
     * @return
     */
    private CorrespondenceTemplateRequestResponse mapTemplateToResponse(Optional<Template> templateHolder)
    {
        Template template = templateHolder.orElseThrow(CorrespondenceTemplateNotFoundException::new);

        CorrespondenceTemplateRequestResponse response = new CorrespondenceTemplateRequestResponse();

        response.setTemplateId(template.getTemplateId());
        response.setTemplateVersion(template.getTemplateVersion());
        response.setTemplateVersionActive(template.isTemplateVersionActive());
        response.setLabel(template.getLabel());
        response.setDocumentType(template.getDocumentType());
        response.setTemplateFilename(template.getTemplateFilename());
        response.setObjectType(template.getObjectType());
        response.setDateFormatString(template.getDateFormatString());
        response.setNumberFormatString(template.getNumberFormatString());
        response.setActivated(template.isActivated());
        response.setModifier(template.getModifier());
        response.setModified(template.getModified());
        response.setTemplateModelProvider(template.getTemplateModelProvider());
        response.setTemplateType(template.getTemplateType());

        return response;
    }

    /**
     * @param request
     * @return
     */
    private Template mapRequestToTemplate(CorrespondenceTemplateRequestResponse request, Authentication auth)
    {
        Template template = new Template();

        template.setTemplateId(request.getTemplateId());
        template.setTemplateVersion(request.getTemplateVersion());
        template.setTemplateVersionActive(request.isTemplateVersionActive());
        template.setLabel(request.getLabel());
        template.setDocumentType(request.getDocumentType());
        template.setTemplateFilename(request.getTemplateFilename());
        template.setObjectType(request.getObjectType());
        template.setDateFormatString(request.getDateFormatString());
        template.setNumberFormatString(request.getNumberFormatString());
        template.setActivated(request.isActivated());
        template.setModifier(auth.getName());
        template.setModified(new Date());
        template.setTemplateModelProvider(request.getTemplateModelProvider());
        template.setTemplateType(request.getTemplateType());

        return template;
    }

    /**
     * @param correspondenceService
     *            the correspondenceService to set
     */
    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }

    public String getCorrespondenceFolderName()
    {
        return correspondenceFolderName;
    }

    public void setCorrespondenceFolderName(String correspondenceFolderName)
    {
        this.correspondenceFolderName = correspondenceFolderName;
    }

    public String getEmailTemplatesFolderName() 
    {
        return emailTemplatesFolderName;
    }

    public void setEmailTemplatesFolderName(String emailTemplatesFolderName) 
    {
        this.emailTemplatesFolderName = emailTemplatesFolderName;
    }
}
