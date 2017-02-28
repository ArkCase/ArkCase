package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.service.CorrespondenceService;
import com.armedia.acm.plugins.admin.model.CorrespondenceTemplateRequestResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author sasko.tanaskoski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class CorrespondenceMergeFieldAPIController
{

    private CorrespondenceService correspondenceService;

    @RequestMapping(value = "/mergefields", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceMergeField> getMergeFields() throws IOException
    {
        return correspondenceService.getActiveVersionMergeFields();
    }

    /**
     * @param templateHolder
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

}
