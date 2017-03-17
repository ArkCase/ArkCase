package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.service.CorrespondenceService;
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
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class CorrespondenceTemplateAPIController
{

    private CorrespondenceService correspondenceService;
    private String correspondenceFolderName;

    @RequestMapping(value = "/templates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceTemplateRequestResponse> getAllTemplates()
    {
        return correspondenceService.getAllTemplates().stream().map(t -> mapTemplateToResponse(Optional.of(t)))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/templates/active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceTemplateRequestResponse> getActiveVersionTemplates()
    {
        return correspondenceService.getActiveVersionTemplates().stream().map(t -> mapTemplateToResponse(Optional.of(t)))
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
        List<CorrespondenceTemplate> templates = correspondenceService.getTemplateVersionsById(templateId);
        String msg = "";
        for (CorrespondenceTemplate template : templates)
        {
            File templateFile = new File(getCorrespondenceFolderName(), template.getTemplateFilename());
            if (FileUtils.deleteQuietly(templateFile))
            {
                deleteResponse.add(mapTemplateToResponse(
                        correspondenceService.deleteTemplateByIdAndVersion(template.getTemplateId(), template.getTemplateVersion())));
            } else
            {
                msg += templateFile + ";";
            }
        }

        if (msg.isEmpty())
        {
            return deleteResponse;
        } else
        {
            throw new CorrespondenceTemplateNotFoundException(msg);
        }
    }

    @RequestMapping(value = "/template/{templateId}/{templateVersion:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CorrespondenceTemplateRequestResponse deleteTemplateByIdAndVersion(@PathVariable(value = "templateId") String templateId,
            @PathVariable(value = "templateVersion") String templateVersion) throws IOException
    {
        File templatesDir = new File(System.getProperty("user.home") + "/.arkcase/acm/correspondenceTemplates");
        File templateFile = new File(templatesDir,
                correspondenceService.getTemplateByIdAndVersion(templateId, templateVersion).get().getTemplateFilename());
        if (FileUtils.deleteQuietly(templateFile))
        {
            return mapTemplateToResponse(correspondenceService.deleteTemplateByIdAndVersion(templateId, templateVersion));
        } else
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
     * @param templateByFileName
     * @return
     */
    private CorrespondenceTemplateRequestResponse mapTemplateToResponse(Optional<CorrespondenceTemplate> templateHolder)
    {
        CorrespondenceTemplate template = templateHolder.orElseThrow(CorrespondenceTemplateNotFoundException::new);

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

        return response;
    }

    /**
     * @param request
     * @return
     */
    private CorrespondenceTemplate mapRequestToTemplate(CorrespondenceTemplateRequestResponse request, Authentication auth)
    {
        CorrespondenceTemplate template = new CorrespondenceTemplate();

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

}
