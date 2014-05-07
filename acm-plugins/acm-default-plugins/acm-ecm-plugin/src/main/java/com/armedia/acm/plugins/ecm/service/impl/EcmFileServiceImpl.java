package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import com.armedia.acm.plugins.ecm.model.FileUpload;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFileServiceImpl implements ApplicationEventPublisherAware, EcmFileService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileTransaction ecmFileTransaction;


    private ApplicationEventPublisher applicationEventPublisher;


    @Override
    @Transactional
    public ResponseEntity<? extends Object> upload(
            MultipartFile file,
            String acceptHeader,
            String contextPath,
            Authentication authentication,
            String targetCmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Single files");
            log.debug("Accept header: '" + acceptHeader + "'");
        }

        if ( log.isInfoEnabled() )
        {
            log.info("The user '" + authentication.getName() + "' uploaded file: '" + file.getOriginalFilename() + "'");
            log.info("File size: " + file.getSize() + "; content type: " + file.getContentType());
        }

        HttpHeaders responseHeaders = contentTypeFromAcceptHeader(acceptHeader);


        log.debug("context path: '" + contextPath + "'");

        EcmFileAddedEvent event = null;

        try
        {
            EcmFile uploaded = getEcmFileTransaction().addFileTransaction(
                    authentication,
                    file.getInputStream(),
                    file.getContentType(),
                    file.getOriginalFilename(),
                    targetCmisFolderId,
                    parentObjectType,
                    parentObjectId,
                    parentObjectName);

            event = new EcmFileAddedEvent(uploaded, authentication);

            FileUpload fileUpload = fileUploadFromEcmFile(file, contextPath, uploaded);

            Object retval = null;

            if ( responseHeaders.getContentType().equals(MediaType.TEXT_PLAIN) )
            {
                // sending a string with text/plain for IE.
                String json = constructJqueryFileUploadJson(fileUpload);
                retval = json;
            }
            else
            {
                // Jackson will convert this map into proper JSON for non-IE browsers.
                Map<String, List<FileUpload>> jsonMap = makeFileUploadMap(fileUpload);
                retval = jsonMap;
            }



            event.setSucceeded(true);
            applicationEventPublisher.publishEvent(event);

            return new ResponseEntity<>(retval, responseHeaders, HttpStatus.OK);
        } catch (IOException | MuleException e)
        {
            if ( event != null )
            {
                event.setSucceeded(false);
                applicationEventPublisher.publishEvent(event);
            }
            log.error("Could not upload file: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public String constructJqueryFileUploadJson(FileUpload fileUpload) throws IOException
    {
        Map<String, List<FileUpload>> retMap = makeFileUploadMap(fileUpload);

        ObjectMapper om = new ObjectMapper();

        return om.writeValueAsString(retMap);
    }

    public Map<String, List<FileUpload>> makeFileUploadMap(FileUpload fileUpload)
    {
        // construct JSON suitable for jQuery File Upload Plugin
        List<FileUpload> retval = Collections.singletonList(fileUpload);
        return Collections.singletonMap("files", retval);
    }

    public FileUpload fileUploadFromEcmFile(MultipartFile file, String contextPath, EcmFile uploaded)
    {
        FileUpload fileUpload = new FileUpload();
        String baseUrl = contextPath + "/file/" + uploaded.getFileId();
        fileUpload.setDeleteUrl(baseUrl);
        fileUpload.setName(file.getOriginalFilename());
        fileUpload.setSize(file.getSize());
        fileUpload.setUrl(baseUrl);
        return fileUpload;
    }

    public String determineResponseContentType(String acceptHeader)
    {
        // since IE is broken we have to conditionally set the response content type
        if ( acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE) || acceptHeader.contains("text/javascript"))
        {
            // good browser, it can send files via AJAX, so it can get the answer as JSON
            return MediaType.APPLICATION_JSON_VALUE;
        }
        else
        {
            // bad browser, must send the files via normal HTML file upload, so must get the answer as a string,
            // since if we send JSON response type, it will ask the user to download a JSON file.
            return MediaType.TEXT_PLAIN_VALUE;
        }
    }

    public HttpHeaders contentTypeFromAcceptHeader(String acceptType)
    {
        HttpHeaders responseHeaders = new HttpHeaders();

        String responseMimeType = determineResponseContentType(acceptType);

        responseHeaders.add("Content-Type", responseMimeType);

        return responseHeaders;
    }

    public EcmFileTransaction getEcmFileTransaction()
    {
        return ecmFileTransaction;
    }

    public void setEcmFileTransaction(EcmFileTransaction ecmFileTransaction)
    {
        this.ecmFileTransaction = ecmFileTransaction;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
