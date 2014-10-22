package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
@Entity
@Table(name="acm_user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 6302644116753261659L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name="cm_user_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long UserInfoId;

    @Column(name="cm_location")
    private String location;

    @Column(name="cm_im_account")
    private String imAccount;

    @Column(name="cm_im_system")
    private String imSystem;

    @Column(name="cm_office_phone")
    private String officePhoneNumber;

    @Column(name="cm_mobile_phone")
    private String mobilePhoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cm_user")
    private AcmUser user;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "cm_organization_detail")
    private OrganizationDetails organizationDetails;

    public Long getUserInfoId() {
        return UserInfoId;
    }

    public void setUserInfoId(Long userInfoId) {
        UserInfoId = userInfoId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImAccount() {
        return imAccount;
    }

    public void setImAccount(String imAccount) {
        this.imAccount = imAccount;
    }

    public String getImSystem() {
        return imSystem;
    }

    public void setImSystem(String imSystem) {
        this.imSystem = imSystem;
    }

    public String getOfficePhoneNumber() {
        return officePhoneNumber;
    }

    public void setOfficePhoneNumber(String officePhoneNumber) {
        this.officePhoneNumber = officePhoneNumber;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public AcmUser getUser() {
        return user;
    }

    public void setUser(AcmUser user) {
        this.user = user;
    }

    public OrganizationDetails getOrganizationDetails() {
        return organizationDetails;
    }

    public void setOrganizationDetails(OrganizationDetails organizationDetails) {
        this.organizationDetails = organizationDetails;
    }
}
