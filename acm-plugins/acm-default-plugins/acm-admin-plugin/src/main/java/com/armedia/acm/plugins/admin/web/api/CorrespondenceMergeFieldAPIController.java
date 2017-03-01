package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceMergeFieldVersion;
import com.armedia.acm.correspondence.service.CorrespondenceService;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

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
        return correspondenceService.getMergeFields();
    }

    @RequestMapping(value = "/mergefields/versions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceMergeFieldVersion> getMergeFieldVersions() throws IOException
    {
        return correspondenceService.getMergeFieldVersions();
    }

    @RequestMapping(value = "/mergefields/versions/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceMergeFieldVersion> getMergeFieldVersionsByType(@PathVariable(value = "objectType") String objectType)
            throws IOException
    {
        return correspondenceService.getMergeFieldVersionsByType(objectType);
    }

    @RequestMapping(value = "/mergefields/active/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceMergeField> getActiveVersionMergeFieldsByType(@PathVariable(value = "objectType") String objectType)
            throws IOException
    {
        return correspondenceService.getActiveVersionMergeFieldsByType(objectType);
    }

    @RequestMapping(value = "/mergefields", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean saveMergeFieldsData(@RequestBody List<CorrespondenceMergeField> mergeFields, Authentication auth) throws IOException
    {
        return correspondenceService.saveMergeFieldsData(mergeFields, auth);
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
