package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFileAddedEvent extends AcmEvent
{
    private String parentObjectType;
    private Long parentObjectId;
    private String parentObjectName;
    private String ecmFileId;



    public EcmFileAddedEvent(EcmFile uploaded, Authentication authentication)
    {
        super(uploaded);

        setEventType("com.armedia.acm.ecm.file.added");
        setObjectType("FILE");
        setObjectId(uploaded.getFileId());
        setEventDate(new Date());
        setUserId(uploaded.getModifier());
        setEcmFileId(uploaded.getEcmFileId());

        if ( uploaded.getParentObjects() != null && !uploaded.getParentObjects().isEmpty() )
        {
            ObjectAssociation parent = uploaded.getParentObjects().iterator().next();
            setParentObjectType(parent.getParentType());
            setParentObjectId(parent.getParentId());
            setParentObjectName(parent.getParentName());
        }

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

    public String getParentObjectName()
    {
        return parentObjectName;
    }

    public void setParentObjectName(String parentObjectName)
    {
        this.parentObjectName = parentObjectName;
    }

    public String getEcmFileId()
    {
        return ecmFileId;
    }

    public void setEcmFileId(String ecmFileId)
    {
        this.ecmFileId = ecmFileId;
    }
}
