package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;

import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileTransactionImpl implements EcmFileTransaction
{
    private MuleClient muleClient;

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public EcmFile addFileTransaction(
            Authentication authentication,
            String fileType,
            InputStream fileInputStream,
            String mimeType,
            String fileName,
            String cmisFolderId,
            String parentObjectType,
            Long parentObjectId,
            String parentObjectName)
            throws MuleException
    {
        // by default, files are documents
        String category = "DOCUMENT";
        EcmFile retval = addFileTransaction(authentication, fileType, category, fileInputStream, mimeType, fileName,
                cmisFolderId, parentObjectType, parentObjectId, parentObjectName);

        return retval;
    }

    @Override
    public EcmFile addFileTransaction(
            Authentication authentication,
            String fileType,
            String fileCategory,
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
        toAdd.setFileType(fileType);

        ObjectAssociation parent = new ObjectAssociation();
        parent.setParentId(parentObjectId);
        parent.setParentType(parentObjectType);
        parent.setParentName(parentObjectName);
        parent.setCategory(fileCategory);
        parent.setTargetSubtype(fileType);
        toAdd.addParentObject(parent);

        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("ecmFolderId", cmisFolderId);
        messageProps.put("inputStream", fileInputStream);
        messageProps.put("acmUser", authentication);
        messageProps.put("auditAdapter", getAuditPropertyEntityAdapter());
        MuleMessage received = getMuleClient().send("vm://addFile.in", toAdd, messageProps);
        EcmFile saved = received.getPayload(EcmFile.class);

        MuleException e = received.getInboundProperty("saveException");
        if ( e != null )
        {
            throw e;
        }

        return saved;
    }
    
    @Override
    public EcmFile updateFileTransaction(
            Authentication authentication,
            EcmFile ecmFile,
            InputStream fileInputStream)
            throws MuleException
    {
 
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("ecmFileId", ecmFile.getEcmFileId());
        messageProps.put("fileName", ecmFile.getFileName());
        messageProps.put("mimeType", ecmFile.getFileMimeType());
        messageProps.put("inputStream", fileInputStream);
        messageProps.put("acmUser", authentication);
        messageProps.put("auditAdapter", getAuditPropertyEntityAdapter());
        MuleMessage received = getMuleClient().send("vm://updateFile.in", ecmFile, messageProps);
        ObjectId objectId = received.getPayload(ObjectId.class);

        MuleException e = received.getInboundProperty("updateException");
        if ( e != null )
        {
            throw e;
        }
        
        if (null == objectId || !objectId.getId().replaceAll(";.*", "").equals(ecmFile.getEcmFileId()))
        {
        	throw new RuntimeException("Updating of the file " + ecmFile.getFileName() + " failed.");
        }

        return ecmFile;
    }

    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
