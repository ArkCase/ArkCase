package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.event.AcmEvent;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFolderDeclareRequestEvent extends AcmEvent
{
    private String parentObjectType;
    private Long parentObjectId;
    private String parentObjectName;
    private Long folderId;
    private AcmCmisObjectList source;
    private AcmContainer container;

    public EcmFolderDeclareRequestEvent(AcmCmisObjectList acmCmisObjectList, AcmContainer acmContainer, Authentication authentication)
    {
        super(acmCmisObjectList);

        setSource(acmCmisObjectList);

        setEventType("com.armedia.acm.ecm.folder.declare.requested");
        setObjectType("FOLDER");
        setObjectId(acmCmisObjectList.getFolderId());
        setEventDate(new Date());
        setUserId(authentication.getName());
        setParentObjectType(acmCmisObjectList.getContainerObjectType());
        setParentObjectId(acmCmisObjectList.getContainerObjectId());
        setFolderId(acmCmisObjectList.getFolderId());
        setContainer(acmContainer);
        setParentObjectName(acmContainer.getContainerObjectTitle());
        if ( authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
    }

    public String getParentObjectType()
    {
        return parentObjectType;
    }

    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }

    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    public AcmContainer getContainer() {
        return container;
    }

    public void setContainer(AcmContainer container) {
        this.container = container;
    }


    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getParentObjectName() {
        return parentObjectName;
    }

    public void setParentObjectName(String parentObjectName) {
        this.parentObjectName = parentObjectName;
    }


    @Override
    public AcmCmisObjectList getSource()
    {
        return source;
    }

    public void setSource(AcmCmisObjectList source)
    {
        this.source = source;
    }
}
