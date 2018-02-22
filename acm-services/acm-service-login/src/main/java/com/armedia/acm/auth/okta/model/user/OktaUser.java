package com.armedia.acm.auth.okta.model.user;

import com.armedia.acm.auth.okta.model.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Map _embedded;
    private Map _links;


    public OktaUser()
    {
    }

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

    public Map get_embedded()
    {
        return _embedded;
    }

    public void set_embedded(Map _embedded)
    {
        this._embedded = _embedded;
    }

    public Map get_links()
    {
        return _links;
    }

    public void set_links(Map _links)
    {
        this._links = _links;
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
                .append("_embedded", _embedded)
                .append("_links", _links)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

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
                .append(_embedded, oktaUser._embedded)
                .append(_links, oktaUser._links)
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
                .append(_embedded)
                .append(_links)
                .toHashCode();
    }
}
