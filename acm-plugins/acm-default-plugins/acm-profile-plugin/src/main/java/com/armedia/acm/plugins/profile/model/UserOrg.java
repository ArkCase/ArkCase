package com.armedia.acm.plugins.profile.model;

/*-
 * #%L
 * ACM Default Plugin: Profile
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

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.services.users.model.AcmUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import java.io.Serializable;

@Entity
@Table(name = "acm_user_org")
public class UserOrg implements Serializable
{
    private static final long serialVersionUID = 4488531757561621833L;

    @Id
    @TableGenerator(name = "acm_user_org_gen", table = "acm_user_org_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_user_org", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_user_org_gen")
    @Column(name = "cm_user_org_id")
    private Long userOrgId;

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

    @Column(name = "cm_location")
    private String location;

    @Column(name = "cm_im_account")
    private String imAccount;

    @Column(name = "cm_im_system")
    private String imSystem;

    @Column(name = "cm_office_phone")
    private String officePhoneNumber;

    @Column(name = "cm_mobile_phone")
    private String mobilePhoneNumber;

    @Column(name = "cm_object_type")
    private String objectType = UserOrgConstants.OBJECT_TYPE;

    /**
     * This field is only used when the profile is created. Usually it will be null. Use the container folder
     * to get the CMIS object ID of the complaint folder.
     */
    @Transient
    private String ecmFolderPath;

    /**
     * Container folder where the case file's attachments/content files are stored.
     */
    @OneToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container = new AcmContainer();

    @Column(name = "cm_ecm_file_id")
    private Long ecmFileId;

    @Column(name = "cm_ecm_signature_file_id")
    private Long ecmSignatureFileId;

    @Column(name = "cm_title")
    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cm_user")
    private AcmUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cm_organization")
    private Organization organization;

    @PrePersist
    public void beforeInsert()
    {
        setupChildPointers();
    }

    @PreUpdate
    public void beforeUpdate()
    {
        setupChildPointers();
    }

    private void setupChildPointers()
    {
        if (getContainer() != null)
        {
            getContainer().setContainerObjectId(getUserOrgId());
            getContainer().setContainerObjectType(UserOrgConstants.OBJECT_TYPE);
            getContainer().setContainerObjectTitle(getUser().getUserId());
        }
    }

    public String getEcmFolderPath()
    {
        return ecmFolderPath;
    }

    public void setEcmFolderPath(String ecmFolderPath)
    {
        this.ecmFolderPath = ecmFolderPath;
    }

    public Organization getOrganization()
    {
        return organization;
    }

    public void setOrganization(Organization organization)
    {
        this.organization = organization;
    }

    public Long getUserOrgId()
    {
        return userOrgId;
    }

    public void setUserOrgId(Long userOrgId)
    {
        this.userOrgId = userOrgId;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getImAccount()
    {
        return imAccount;
    }

    public void setImAccount(String imAccount)
    {
        this.imAccount = imAccount;
    }

    public String getImSystem()
    {
        return imSystem;
    }

    public void setImSystem(String imSystem)
    {
        this.imSystem = imSystem;
    }

    public String getOfficePhoneNumber()
    {
        return officePhoneNumber;
    }

    public void setOfficePhoneNumber(String officePhoneNumber)
    {
        this.officePhoneNumber = officePhoneNumber;
    }

    public String getMobilePhoneNumber()
    {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber)
    {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public AcmUser getUser()
    {
        return user;
    }

    public void setUser(AcmUser user)
    {
        this.user = user;
    }

    public String getFirstAddress()
    {
        return firstAddress;
    }

    public void setFirstAddress(String firstAddress)
    {
        this.firstAddress = firstAddress;
    }

    public String getSecondAddress()
    {
        return secondAddress;
    }

    public void setSecondAddress(String secondAddress)
    {
        this.secondAddress = secondAddress;
    }

    public String getMainOfficePhone()
    {
        return mainOfficePhone;
    }

    public void setMainOfficePhone(String mainOfficePhone)
    {
        this.mainOfficePhone = mainOfficePhone;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public Long getEcmFileId()
    {
        return ecmFileId;
    }

    public void setEcmFileId(Long ecmFileId)
    {
        this.ecmFileId = ecmFileId;
    }

    public Long getEcmSignatureFileId()
    {
        return ecmSignatureFileId;
    }

    public void setEcmSignatureFileId(Long ecmSignatureFileId)
    {
        this.ecmSignatureFileId = ecmSignatureFileId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public AcmContainer getContainer()
    {
        return container;
    }

    public void setContainer(AcmContainer container)
    {
        this.container = container;
    }

    public String getObjectType()
    {
        return objectType;
    }

}
