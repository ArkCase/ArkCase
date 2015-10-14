package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by riste.tutureski on 9/14/2015.
 */

@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class UpdateFileTypeAPIController
{
    private EcmFileService ecmFileService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/file/{fileId}/type/{fileType}",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile updateFileType(
            @PathVariable("fileId") Long fileId,
            @PathVariable("fileType") String fileType,
            Authentication authentication,
            HttpSession session) throws AcmObjectNotFoundException {


        log.debug("Updating file type to '{}'", fileType);

        EcmFile file = updateFileType(fileId, fileType);

        if (file == null)
        {
            throw new AcmObjectNotFoundException("EcmFile", fileId, "Cannot update file type.");
        }

        return file;
    }

    @RequestMapping(value = "/file/bulk/type/{fileType}",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EcmFile> bulkUpdateFileType(@RequestBody List<Long> fileIds,
            @PathVariable("fileType") String fileType,
            Authentication authentication,
            HttpSession session) {

        log.debug("Updating file type to '{}' for multiple files.", fileType);

        List<EcmFile> files = new ArrayList<>();

        if (fileIds != null)
        {
            files = fileIds.stream().map(fileId -> updateFileType(fileId, fileType)).filter(file -> file != null).collect(Collectors.toList());
        }

        return files;
    }

    private EcmFile updateFileType(Long fileId, String fileType)
    {
        EcmFile file = null;

        try
        {
            file = getEcmFileService().updateFileType(fileId, fileType);
        }
        catch (AcmObjectNotFoundException e)
        {
            log.error("Error wile updating file type: {}", e.getMessage(), e);
        }

        return file;
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
