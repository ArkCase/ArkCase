package com.armedia.acm.calendar.service.integration.exchange.web.api;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 31, 2017
 *
 */

@JsonIdentityInfo(generator = JSOGGenerator.class)
public class RecreatedEntity
{

    private String objectType;

    private Long objectId;

    /**
     * @return the objectType
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    /**
     * @return the objectId
     */
    public Long getObjectId()
    {
        return objectId;
    }

    /**
     * @param objectId
     *            the objectId to set
     */
    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

}
