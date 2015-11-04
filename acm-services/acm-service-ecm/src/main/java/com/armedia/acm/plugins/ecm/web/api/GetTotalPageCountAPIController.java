package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * This API can be used for taking total count page for the files that are attached to the specific object.
 *
 * Created by riste.tutureski on 10/30/2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class GetTotalPageCountAPIController
{
    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;

    /**
     * Return total page count
     *
     * @param parentObjectType - the type of the object where the file is attached
     * @param parentObjectId - the id of the object where the file is attached
     * @param fileTypes - file types for which the count should be calculated. Format: "fileType1,fileType2,..."
     * @param mimeTypes - mime types for which the count should be calculated. Format: "mimeTpe1,mimeType2,..."
     * @param auth - authentication object
     * @return - integer representation of page total count (0 is default)
     */
    @RequestMapping(value = "/totalpagecount/{parentObjectType}/{parentObjectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public int getTotalPageCount(@PathVariable("parentObjectType") String parentObjectType,
                                 @PathVariable("parentObjectId") Long parentObjectId,
                                 @RequestParam(value = "fileTypes", required = false, defaultValue = "") String fileTypes,
                                 @RequestParam(value = "mimeTypes", required = false, defaultValue = "") String mimeTypes, Authentication auth)
    {
        List<String> fileTypesList = Arrays.asList(fileTypes.split("\\s*,\\s*"));
        List<String> mimeTypesList = Arrays.asList(mimeTypes.split("\\s*,\\s*"));

        LOG.debug("File types: {}", fileTypesList);
        LOG.debug("Mime types: {}", mimeTypesList);

        return getEcmFileService().getTotalPageCount(parentObjectType, parentObjectId, fileTypesList, mimeTypesList, auth);
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
