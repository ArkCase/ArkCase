package com.armedia.acm.auth.okta.model.user;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.auth.okta.model.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OktaUser extends ErrorResponse
{
    private String id;
    private OktaUserStatus status;
    private Date created;
    private Date activated;
    private Date statusChanged;
    private Date lastLogin;
    private Date lastUpdated;
    private Date passwordChanged;
    private OktaUserStatus transitioningToStatus;
    private OktaUserProfile profile;
    private OktaUserCredentials credentials;

    @JsonProperty("_embedded")
    private Map embedded;

    @JsonProperty("_links")
    private Map links;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public OktaUserStatus getStatus()
    {
        return status;
    }

    public void setStatus(OktaUserStatus status)
    {
        this.status = status;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getActivated()
    {
        return activated;
    }

    public void setActivated(Date activated)
    {
        this.activated = activated;
    }

    public Date getStatusChanged()
    {
        return statusChanged;
    }

    public void setStatusChanged(Date statusChanged)
    {
        this.statusChanged = statusChanged;
    }

    public Date getLastLogin()
    {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin)
    {
        this.lastLogin = lastLogin;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public Date getPasswordChanged()
    {
        return passwordChanged;
    }

    public void setPasswordChanged(Date passwordChanged)
    {
        this.passwordChanged = passwordChanged;
    }

    public OktaUserStatus getTransitioningToStatus()
    {
        return transitioningToStatus;
    }

    public void setTransitioningToStatus(OktaUserStatus transitioningToStatus)
    {
        this.transitioningToStatus = transitioningToStatus;
    }

    public OktaUserProfile getProfile()
    {
        return profile;
    }

    public void setProfile(OktaUserProfile profile)
    {
        this.profile = profile;
    }

    public OktaUserCredentials getCredentials()
    {
        return credentials;
    }

    public void setCredentials(OktaUserCredentials credentials)
    {
        this.credentials = credentials;
    }

    public Map getEmbedded()
    {
        return embedded;
    }

    public void setEmbedded(Map embedded)
    {
        this.embedded = embedded;
    }

    public Map getLinks()
    {
        return links;
    }

    public void setLinks(Map links)
    {
        this.links = links;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("status", status)
                .append("created", created)
                .append("activated", activated)
                .append("statusChanged", statusChanged)
                .append("lastLogin", lastLogin)
                .append("lastUpdated", lastUpdated)
                .append("passwordChanged", passwordChanged)
                .append("transitioningToStatus", transitioningToStatus)
                .append("profile", profile)
                .append("credentials", credentials)
                .append("embedded", embedded)
                .append("links", links)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        OktaUser oktaUser = (OktaUser) o;

        return new EqualsBuilder()
                .append(id, oktaUser.id)
                .append(status, oktaUser.status)
                .append(created, oktaUser.created)
                .append(activated, oktaUser.activated)
                .append(statusChanged, oktaUser.statusChanged)
                .append(lastLogin, oktaUser.lastLogin)
                .append(lastUpdated, oktaUser.lastUpdated)
                .append(passwordChanged, oktaUser.passwordChanged)
                .append(transitioningToStatus, oktaUser.transitioningToStatus)
                .append(profile, oktaUser.profile)
                .append(credentials, oktaUser.credentials)
                .append(embedded, oktaUser.embedded)
                .append(links, oktaUser.links)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(status)
                .append(created)
                .append(activated)
                .append(statusChanged)
                .append(lastLogin)
                .append(lastUpdated)
                .append(passwordChanged)
                .append(transitioningToStatus)
                .append(profile)
                .append(credentials)
                .append(embedded)
                .append(links)
                .toHashCode();
    }
}
