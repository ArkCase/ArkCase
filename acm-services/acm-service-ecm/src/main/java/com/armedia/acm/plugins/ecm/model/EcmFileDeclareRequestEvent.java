package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.event.AcmEvent;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFileDeclareRequestEvent extends AcmEvent
{
    private String parentObjectType;
    private Long parentObjectId;
    private String parentObjectName;
    private String ecmFileId;
    private EcmFile source;



    public EcmFileDeclareRequestEvent(EcmFile ecmFile, Authentication authentication)
    {
        super(ecmFile);

        setSource(ecmFile);

        setEventType("com.armedia.acm.ecm.file.declare.requested");
        setObjectType("FILE");
        setObjectId(ecmFile.getFileId());
        setEventDate(new Date());
        setUserId(ecmFile.getModifier());
        setEcmFileId(ecmFile.getVersionSeriesId());
        setParentObjectName(ecmFile.getContainer().getContainerObjectTitle());
        setParentObjectType(ecmFile.getContainer().getContainerObjectType());
        setParentObjectId(ecmFile.getContainer().getContainerObjectId());

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

    @Override
    public EcmFile getSource()
    {
        return source;
    }

    public void setSource(EcmFile source)
    {
        this.source = source;
    }
}
