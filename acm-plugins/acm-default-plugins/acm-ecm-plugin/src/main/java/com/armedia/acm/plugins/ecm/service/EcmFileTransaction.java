package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.FileUpload;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import org.codehaus.jackson.map.ObjectMapper;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileTransaction
{
    private MuleClient muleClient;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    public EcmFile addFileTransaction(
            Authentication authentication,
            InputStream fileInputStream,
            String mimeType,
            String fileName,
            String cmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName)
            throws MuleException
    {
        EcmFile toAdd = new EcmFile();
        toAdd.setFileMimeType(mimeType);
        toAdd.setFileName(fileName);

        ObjectAssociation parent = new ObjectAssociation();
        parent.setParentId(parentObjectId);
        parent.setParentType(parentObjectType);
        parent.setParentName(parentObjectName);
        toAdd.addParentObject(parent);

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("ecmFolderId", cmisFolderId);
        messageProps.put("inputStream", fileInputStream);
        messageProps.put("acmUser", authentication);
        MuleMessage received = getMuleClient().send("vm://addFile.in", toAdd, messageProps);
        EcmFile saved = received.getPayload(EcmFile.class);

        MuleException e = received.getInboundProperty("saveException");
        if ( e != null )
        {
            throw e;
        }

        return saved;
    }

    public HttpHeaders contentTypeFromAcceptHeader(String acceptType)
    {
        HttpHeaders responseHeaders = new HttpHeaders();

        // since IE is broken we have to conditionally set the response content type
        if ( acceptType.contains(MediaType.APPLICATION_JSON_VALUE) || acceptType.contains("text/javascript"))
        {
            log.debug("sending javascript via response entity");
            // good browsers
            responseHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        }
        else
        {
            // ie
            log.debug("sending text plain via response entity");
            responseHeaders.add("Content-Type", MediaType.TEXT_PLAIN_VALUE);
        }
        return responseHeaders;
    }

    public String constructJqueryFileUploadJson(FileUpload fileUpload) throws IOException
    {
        // construct JSON suitable for jQuery File Upload Plugin
        List<FileUpload> retval = Collections.singletonList(fileUpload);
        Map<String, List<FileUpload>> retMap = Collections.singletonMap("files", retval);

        ObjectMapper om = new ObjectMapper();

        return om.writeValueAsString(retMap);
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

    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
    }
}
