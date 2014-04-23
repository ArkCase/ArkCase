package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileTransaction
{
    private MuleClient muleClient;

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

    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
    }
}
