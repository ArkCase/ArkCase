package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.plugins.ecm.model.DeleteFileResult;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping({"/api/v1/plugin/ecm/temp", "/api/latest/plugin/ecm/temp"})
public class UploadTempFilesAPIController
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private EcmFileService fileFolderService;

    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public List<EcmFile> uploadTempFiles(HttpServletRequest request, Authentication authentication, HttpSession session)
    {
        log.debug("Uploading files to tmp directory by user {}", authentication.getName());

        // Obtains the files from the HTTP POST request multipart body
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultiValueMap<String, MultipartFile> files = multipartHttpServletRequest.getMultiFileMap();

        // Writes the uploaded files to a temporary directory
        return getFileFolderService().saveFilesToTempDirectory(files);
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public DeleteFileResult deleteTempFile(@RequestParam(value = "fileName") String fileName, HttpServletRequest request, Authentication authentication, HttpSession session)
    {
        log.debug("Deleting temp file {} by {}", fileName, authentication.getName());
        DeleteFileResult deleteFileResult = new DeleteFileResult();

        // Removes the temporary attachment file from the file system
        boolean fileDeleted = getFileFolderService().deleteTempFile(fileName);
        if (!fileDeleted)
        {
            deleteFileResult.setDeletedFileName(fileName);
            deleteFileResult.setSuccess(false);
            log.warn("The temp file {} was not deleted. The server file system will require manual cleanup.", fileName);
        } else
        {
            deleteFileResult.setDeletedFileName(fileName);
            deleteFileResult.setSuccess(true);
        }

        return deleteFileResult;
    }

    public EcmFileService getFileFolderService()
    {
        return fileFolderService;
    }

    public void setFileFolderService(EcmFileService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }
}
