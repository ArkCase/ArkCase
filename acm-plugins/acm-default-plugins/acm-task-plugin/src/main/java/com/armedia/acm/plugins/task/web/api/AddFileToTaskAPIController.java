package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;

import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping( { "/api/v1/plugin/task", "/api/latest/plugin/task"} )
public class AddFileToTaskAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private TaskDao taskDao;
    private MuleClient muleClient;
    private EcmFileService ecmFileService;
    private final String uploadFileType = "attachment_task";

    private List<ResponseEntity<? extends Object>> uploadedFiles = new ArrayList<>();
    private List<Object> uploadedFilesJSON = new ArrayList<>();

    @RequestMapping(value = "/file", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public ResponseEntity<? extends Object> uploadFile(
            @RequestParam("taskId") Long taskId,
            @RequestParam("files[]") MultipartFile file,
            @RequestHeader("Accept") String acceptType,
            HttpServletRequest request,
            Authentication authentication) throws AcmCreateObjectFailedException, AcmObjectNotFoundException, MuleException {
        if ( log.isInfoEnabled() )
        {
            log.info("Adding file to task id " + taskId);
        }

        String date = LocalDate.now().toString(DateTimeFormat.forPattern("yyyyMMdd"));
        String folderPath = "/Sites/acm/documentLibrary/Tasks/" + date + "_" + taskId;

        MuleMessage message = getMuleClient().send("vm://createFolder.in",folderPath,null);
        CmisObject cmisObject = message.getPayload(CmisObject.class);
        String cmisId = cmisObject.getId();
        try
        {
            AcmTask in = getTaskDao().findById(taskId);
            String folderId = cmisId;
            String objectType = "TASK";
            Long objectId = taskId;
            String objectName = date + "_" + taskId;

            String contextPath = request.getServletContext().getContextPath();

            //for multiple files
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
            MultiValueMap<String, MultipartFile> attachments = multipartHttpServletRequest.getMultiFileMap();

            if ( attachments != null )
            {
                for ( Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet() )
                {
                    final List<MultipartFile> attachmentsList = entry.getValue();

                    if (attachmentsList != null && !attachmentsList.isEmpty() )
                    {
                        for (final MultipartFile attachment : attachmentsList)
                        {
                            ResponseEntity<? extends Object> temp = getEcmFileService().upload(uploadFileType, attachment, acceptType, contextPath, authentication, folderId,
                                    objectType, objectId, objectName);
                            getUploadedFiles().add(temp);
                        }
                    }
                }
                for(ResponseEntity<? extends Object> uploadedFiles : getUploadedFiles()){
                    getUploadedFilesJSON().add(uploadedFiles.getBody());
                }
            }
            return new ResponseEntity<Object>(getUploadedFilesJSON(), HttpStatus.OK);
        }
        catch (PersistenceException e)
        {
            throw new AcmObjectNotFoundException("task", taskId, e.getMessage(), e);
        }

        catch (AcmTaskException e)
        {
            throw new AcmObjectNotFoundException("task", taskId, "No Such Task", null);
        }
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public MuleClient getMuleClient() {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient) {
        this.muleClient = muleClient;
    }

    public List<ResponseEntity<? extends Object>> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<ResponseEntity<? extends Object>> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public List<Object> getUploadedFilesJSON() {
        return uploadedFilesJSON;
    }

    public void setUploadedFilesJSON(List<Object> uploadedFilesJSON) {
        this.uploadedFilesJSON = uploadedFilesJSON;
    }
}
