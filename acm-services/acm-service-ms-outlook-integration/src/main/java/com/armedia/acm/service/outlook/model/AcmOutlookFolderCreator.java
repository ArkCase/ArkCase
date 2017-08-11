package com.armedia.acm.service.outlook.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.util.Set;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 8, 2017
 *
 */
@Entity
@Table(name = "acm_outlook_folder_creator")
public class AcmOutlookFolderCreator
{

    @Id
    @TableGenerator(name = "outlook_folder_creator_gen", table = "acm_outlook_folder_creator_id", pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num", pkColumnValue = "acm_outlook_folder_creator", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "outlook_folder_creator_gen")
    @Column(name = "cm_outlook_folder_creator_id")
    private Long id;

    @Column(name = "cm_creator_hash", unique = true)
    private String creatorHash;

    @Column(name = "cm_system_email_address", unique = true)
    private String systemEmailAddress;

    @Column(name = "cm_system_password")
    private String systemPassword;

    @OneToMany(mappedBy = "folderCreator")
    private Set<AcmOutlookObjectReference> outlookObjectReferences;

    public AcmOutlookFolderCreator()
    {
    }

    public AcmOutlookFolderCreator(String creatorHash, String systemEmailAddress, String systemPassword)
    {
        this.creatorHash = creatorHash;
        this.systemEmailAddress = systemEmailAddress;
        this.systemPassword = systemPassword;
    }

    /**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the creatorHash
     */
    public String getCreatorHash()
    {
        return creatorHash;
    }

    /**
     * @param creatorHash
     *            the creatorHash to set
     */
    public void setCreatorHash(String creatorHash)
    {
        this.creatorHash = creatorHash;
    }

    /**
     * @return the systemEmailAddress
     */
    public String getSystemEmailAddress()
    {
        return systemEmailAddress;
    }

    /**
     * @param systemEmailAddress
     *            the systemEmailAddress to set
     */
    public void setSystemEmailAddress(String systemEmailAddress)
    {
        this.systemEmailAddress = systemEmailAddress;
    }

    /**
     * @return the systemPassword
     */
    public String getSystemPassword()
    {
        return systemPassword;
    }

    /**
     * @param systemPassword
     *            the systemPassword to set
     */
    public void setSystemPassword(String systemPassword)
    {
        this.systemPassword = systemPassword;
    }

    /**
     * @return the outlookObjectReferences
     */
    public Set<AcmOutlookObjectReference> getOutlookObjectReferences()
    {
        return outlookObjectReferences;
    }

    /**
     * @param outlookObjectReferences
     *            the outlookObjectReferences to set
     */
    public void setOutlookObjectReferences(Set<AcmOutlookObjectReference> outlookObjectReferences)
    {
        this.outlookObjectReferences = outlookObjectReferences;
    }

}
