package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.StreamVideoService;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import org.apache.catalina.connector.ClientAbortException;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by riste.tutureski on 6/5/2017.
 */
@Controller
@RequestMapping({"/api/v1/plugin/ecm", "/api/latest/plugin/ecm"})
public class StreamVideoFileAPIController
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private StreamVideoService streamVideoService;
    private EcmFileService ecmFileService;
    private FolderAndFilesUtils folderAndFilesUtils;
    private CmisConfigUtils cmisConfigUtils;

    @RequestMapping(value = "/stream/video/{id}", method = RequestMethod.GET)
    @ResponseBody
    public void streamVideo(@PathVariable(value = "id") Long id,
                            @RequestParam(value = "version", required = false, defaultValue = "") String version,
                            Authentication authentication,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException, MuleException, AcmObjectNotFoundException, AcmUserActionFailedException
    {
        LOG.info("Streaming video file with ID '{}' for user '{}'", id, authentication.getName());

        EcmFile file = getEcmFileService().findById(id);
        String cmisFileId = getFolderAndFilesUtils().getVersionCmisId(file, version);

        try
        {
            getStreamVideoService().stream(cmisFileId, request, response, file, version);
        }
        catch (ClientAbortException e)
        {
            LOG.debug("The connection is terminated by the client");
            // Do nothing. The connection is closed by the client and writing to the output should be terminated
        }
    }

    public StreamVideoService getStreamVideoService()
    {
        return streamVideoService;
    }

    public void setStreamVideoService(StreamVideoService streamVideoService)
    {
        this.streamVideoService = streamVideoService;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
    }
}
