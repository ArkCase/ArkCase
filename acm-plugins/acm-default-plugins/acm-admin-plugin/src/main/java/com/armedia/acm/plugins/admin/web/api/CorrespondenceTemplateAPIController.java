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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 27, 2017
 *
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class CorrespondenceTemplateAPIController
{

    private CorrespondenceService correspondenceService;

    @RequestMapping(value = "/templates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceTemplateRequestResponse> getAllTemplates()
    {
        return correspondenceService.getAllTemplates().stream().map(t -> mapTemplateToResponse(Optional.of(t)))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/template/{templateFileName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CorrespondenceTemplateRequestResponse getTemplate(@PathVariable(value = "templateFileName") String templateFileName)
    {
        return mapTemplateToResponse(correspondenceService.getTemplateByFileName(templateFileName));
    }

    @RequestMapping(value = "/template/{templateFileName:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CorrespondenceTemplateRequestResponse deleteTemplate(@PathVariable(value = "templateFileName") String templateFileName)
            throws IOException
    {
        File templatesDir = new File(System.getProperty("user.home") + "/.arkcase/acm/correspondenceTemplates");
        File template = new File(templatesDir, templateFileName);
        if (FileUtils.deleteQuietly(template))
        {
            return mapTemplateToResponse(correspondenceService.deleteTemplate(templateFileName));
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

        response.setDisplayName(template.getDisplayName());
        response.setDocumentType(template.getDocumentType());
        response.setTemplateFilename(template.getTemplateFilename());
        if (template.getQuery() != null)
        {
            response.setCorrespondenceQueryBeanId(correspondenceService.getQueryId(template.getQuery()));
            response.setQueryType(template.getQuery().getType().name());
        }
        response.setTemplateSubstitutionVariables(template.getTemplateSubstitutionVariables());
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

        template.setDisplayName(request.getDisplayName());
        template.setDocumentType(request.getDocumentType());
        template.setTemplateFilename(request.getTemplateFilename());
        template.setQuery(correspondenceService.getQueryByBeanId(request.getCorrespondenceQueryBeanId())
                .orElseThrow(CorrespondenceTemplateNotFoundException::new));
        template.setTemplateSubstitutionVariables(request.getTemplateSubstitutionVariables());
        template.setDateFormatString(request.getDateFormatString());
        template.setNumberFormatString(request.getNumberFormatString());
        template.setActivated(request.isActivated());
        template.setModifier(auth.getName());
        template.setModified(new Date());

        return template;
    }

    /**
     * @param correspondenceService the correspondenceService to set
     */
    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }

}
