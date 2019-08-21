package com.armedia.acm.service.identity.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author ivana.shekerova on 8/6/2019.
 */
@JsonSerialize(as = AcmInstanceIdentityConfig.class)
public class AcmInstanceIdentityConfig
{
    @JsonProperty("instance.indentity.identityId")
    @Value("${instance.indentity.identityId}")
    private String identityId;

    @JsonProperty("instance.indentity.dateCreated")
    @Value("${instance.indentity.dateCreated}")
    private String dateCreated;

    @JsonProperty("instance.indentity.digest")
    @Value("${instance.indentity.digest}")
    private String digest;

    public String getIdentityId()
    {
        return identityId;
    }

    public void setIdentityId(String identityId)
    {
        this.identityId = identityId;
    }

    public String getDateCreated()
    {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public String getDigest()
    {
        return digest;
    }

    public void setDigest(String digest)
    {
        this.digest = digest;
    }
}
