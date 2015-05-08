package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 06.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class ReplaceFileAPIController {

    private EcmFileService fileService;
    private EcmFileTransaction fileTransaction;
    private FileEventPublisher fileEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/replace/{fileToBeReplacedId}",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile replaceFile(
            @PathVariable("fileToBeReplacedId") Long fileToBeReplacedId,
            MultipartHttpServletRequest request,
            Authentication authentication,
            HttpSession session) throws  AcmUserActionFailedException {

        if( log.isInfoEnabled() )
            log.info("Replacing file, fileId: " + fileToBeReplacedId);
        String ipAddress = (String) session.getAttribute(EcmFileConstants.IP_ADDRESS_ATTRIBUTE);

        EcmFile fileToBeReplaced = getFileService().findById(fileToBeReplacedId);
        EcmFile replacedFile;
        if ( fileToBeReplaced == null ){
            if( log.isDebugEnabled() ) {
                log.debug("File, fileId: " + fileToBeReplacedId+" does not exists, and can not be replaced");
            }
            getFileEventPublisher().publishFileReplacedEvent(null,authentication,ipAddress,false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_REPLACE_FILE,EcmFileConstants.OBJECT_FILE_TYPE,fileToBeReplacedId,"File not found.",null);
        }
        InputStream replacementStream;
        try {
            replacementStream = getInputStreamFromAttachment(request,fileToBeReplacedId);
            if (replacementStream == null ){
                throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_REPLACE_FILE,EcmFileConstants.OBJECT_FILE_TYPE,fileToBeReplacedId,"No stream found!.",null);
            }
        } catch (IOException e) {
            if( log.isErrorEnabled() ){
               log.error("IO exception occurred while reading the inputStream of the attachment "+e.getMessage(),e);
            }
            getFileEventPublisher().publishFileReplacedEvent(fileToBeReplaced,authentication,ipAddress,false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_REPLACE_FILE,EcmFileConstants.OBJECT_FILE_TYPE,fileToBeReplacedId,e.getMessage(),e);
        }
        try {
            replacedFile = getFileTransaction().updateFileTransaction(authentication,fileToBeReplaced,replacementStream);
            getFileEventPublisher().publishFileReplacedEvent(replacedFile,authentication,ipAddress,true);
        } catch (MuleException e) {
            if(log.isErrorEnabled()){
                log.error("Exception occurred while trying to replace file  : "+fileToBeReplaced.getFileName()+"  "+e.getMessage(),e);
            }
            getFileEventPublisher().publishFileReplacedEvent(fileToBeReplaced,authentication,ipAddress,false);
            throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_REPLACE_FILE,EcmFileConstants.OBJECT_FILE_TYPE,fileToBeReplacedId,e.getMessage(),e);

        }
        return replacedFile;
    }

    private InputStream getInputStreamFromAttachment(MultipartHttpServletRequest request, Long fileToBeReplacedId) throws AcmUserActionFailedException, IOException {
        MultiValueMap<String, MultipartFile> attachments = request.getMultiFileMap();
        if (attachments != null) {
            for (Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet()) {
                final List<MultipartFile> attachmentsList = entry.getValue();
                if (attachmentsList != null && !attachmentsList.isEmpty()) {
                       return attachmentsList.get(AcmFolderConstants.ZERO).getInputStream();
                }
            }
        }
        if( log.isDebugEnabled() ) {
            log.debug("No File uploaded, nothing to be changed");
        }
        throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_REPLACE_FILE,EcmFileConstants.OBJECT_FILE_TYPE,fileToBeReplacedId,"No file attached found.",null);
    }

    public FileEventPublisher getFileEventPublisher() {
        return fileEventPublisher;
    }

    public void setFileEventPublisher(FileEventPublisher fileEventPublisher) {
        this.fileEventPublisher = fileEventPublisher;
    }

    public EcmFileTransaction getFileTransaction() {
        return fileTransaction;
    }

    public void setFileTransaction(EcmFileTransaction fileTransaction) {
        this.fileTransaction = fileTransaction;
    }

    public EcmFileService getFileService() {
        return fileService;
    }

    public void setFileService(EcmFileService fileService) {
        this.fileService = fileService;
    }
}
