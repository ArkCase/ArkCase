package com.armedia.acm.plugins.profile.model;

import com.armedia.acm.services.users.model.AcmRole;

import javax.persistence.Column;
import java.util.List;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class ProfileDTO {

    private String userId;
    private String companyName;
    private String firstAddress;
    private String secondAddress;
    private String mainOfficePhone;
    private String fax;
    private String city;
    private String state;
    private String zip;
    private String website;
    private String location;
    private String imAccount;
    private String imSystem;
    private String officePhoneNumber;
    private String mobilePhoneNumber;
    private String fullName;
    private String email;
    private String pictureUrl;
    private Long ecmFileId;

    private List<String> groups;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Long getEcmFileId() {
        return ecmFileId;
    }

    public void setEcmFileId(Long ecmFileId) {
        this.ecmFileId = ecmFileId;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
}
