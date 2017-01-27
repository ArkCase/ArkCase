package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.service.CorrespondenceService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 27, 2017
 *
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class CorrespondenceTemplateController
{

    private CorrespondenceService correspondenceService;

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
        return mapTemplateToResponse(correspondenceService.deleteTemplate(templateFileName));
    }

    @RequestMapping(value = "/template/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CorrespondenceTemplateRequestResponse updateTemplate(@RequestBody CorrespondenceTemplateRequestResponse request)
            throws IOException
    {
        return mapTemplateToResponse(correspondenceService.updateTemplate(mapRequestToTemplate(request)));
    }

    /**
     * @param templateByFileName
     * @return
     */
    private CorrespondenceTemplateRequestResponse mapTemplateToResponse(CorrespondenceTemplate template)
    {
        CorrespondenceTemplateRequestResponse response = new CorrespondenceTemplateRequestResponse();

        response.setDocumentType(template.getDocumentType());
        response.setTemplateFilename(template.getTemplateFilename());
        response.setCorrespondenceQueryBeanId(correspondenceService.getQuryId(template.getQuery()));
        response.setQueryTpe(template.getQuery().getType().name());
        response.setTemplateSubstitutionVariables(template.getTemplateSubstitutionVariables());
        response.setDateFormatString(template.getDateFormatString());
        response.setNumberFormatString(template.getNumberFormatString());

        return response;
    }

    /**
     * @param request
     * @return
     */
    private CorrespondenceTemplate mapRequestToTemplate(CorrespondenceTemplateRequestResponse request)
    {
        CorrespondenceTemplate template = new CorrespondenceTemplate();

        template.setDocumentType(request.getDocumentType());
        template.setTemplateFilename(request.getTemplateFilename());
        template.setQuery(correspondenceService.getQueryByBeanId(request.getCorrespondenceQueryBeanId()));
        template.setTemplateSubstitutionVariables(request.getTemplateSubstitutionVariables());
        template.setDateFormatString(request.getDateFormatString());
        template.setNumberFormatString(request.getNumberFormatString());

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
