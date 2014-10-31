package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.services.users.model.AcmUser;

import javax.annotation.Generated;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
@Entity
@Table(name="acm_user_org")
public class UserOrg implements Serializable{

    private static final long serialVersionUID = 4488531757561621833L;

    @Id
    @Column(name="cm_user_org_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userOrgId;

    @Column(name = "cm_company_name")
    private String companyName;

    @Column(name = "cm_first_address")
    private String firstAddress;

    @Column(name = "cm_second_address")
    private String secondAddress;

    @Column(name = "cm_main_office_phone")
    private String mainOfficePhone;

    @Column(name = "cm_fax")
    private String fax;

    @Column(name = "cm_city")
    private String city;

    @Column(name = "cm_state")
    private String state;

    @Column(name = "cm_zip")
    private String zip;

    @Column(name = "cm_website")
    private String website;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cm_organization")
    private Organization organization;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getUserOrgId() {
        return userOrgId;
    }

    public void setUserOrgId(Long userOrgId) {
        this.userOrgId = userOrgId;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFirstAddress() {
        return firstAddress;
    }

    public void setFirstAddress(String firstAddress) {
        this.firstAddress = firstAddress;
    }

    public String getSecondAddress() {
        return secondAddress;
    }

    public void setSecondAddress(String secondAddress) {
        this.secondAddress = secondAddress;
    }

    public String getMainOfficePhone() {
        return mainOfficePhone;
    }

    public void setMainOfficePhone(String mainOfficePhone) {
        this.mainOfficePhone = mainOfficePhone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

}
