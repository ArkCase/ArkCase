package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.core.model.AcmEvent;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Created by armdev on 5/1/14.
 */
public class EcmFileAddedEvent extends AcmEvent
{
    private String ecmFileId;
    private EcmFile source;

    public EcmFileAddedEvent(EcmFile uploaded, Authentication authentication)
    {
        super(uploaded);

        setSource(uploaded);

        String fileType = uploaded.getFileType();
        String ft = "";

        if (StringUtils.isNotEmpty(fileType) && fileType.endsWith("_xml"))
        {
            ft = ".xml";
        }

        setEventType("com.armedia.acm.ecm.file.added" + ft);
        setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);
        setObjectId(uploaded.getFileId());
        setEventDate(new Date());
        setUserId(uploaded.getModifier());
        setEcmFileId(uploaded.getVersionSeriesId());
        setParentObjectType(uploaded.getContainer().getContainerObjectType());
        setParentObjectId(uploaded.getContainer().getContainerObjectId());

        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
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
