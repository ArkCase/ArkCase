package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.services.users.model.AcmRole;

import java.util.List;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class ProfileDTO {

    private List<AcmRole> groups;
    private OrganizationDetails companyDetails;
    private UserInfo userInfo;

    public List<AcmRole> getGroups() {
        return groups;
    }

    public void setGroups(List<AcmRole> groups) {
        this.groups = groups;
    }

    public OrganizationDetails getCompanyDetails() {
        return companyDetails;
    }

    public void setCompanyDetails(OrganizationDetails companyDetails) {
        this.companyDetails = companyDetails;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
