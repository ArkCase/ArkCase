package com.armedia.acm.service.outlook.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

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
@JsonInclude(Include.NON_NULL)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmOutlookFolderCreator
{

    @Id
    @TableGenerator(name = "outlook_folder_creator_gen", table = "acm_outlook_folder_creator_id", pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num", pkColumnValue = "acm_outlook_folder_creator", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "outlook_folder_creator_gen")
    @Column(name = "cm_outlook_folder_creator_id")
    private Long id;

    @Column(name = "cm_system_email_address", unique = true)
    private String systemEmailAddress;

    @Column(name = "cm_system_password")
    private String systemPassword;

    @OneToMany(mappedBy = "folderCreator")
    private Set<AcmOutlookObjectReference> outlookObjectReferences;

    public AcmOutlookFolderCreator()
    {
    }

    public AcmOutlookFolderCreator(String systemEmailAddress, String systemPassword)
    {
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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((outlookObjectReferences == null) ? 0 : outlookObjectReferences.hashCode());
        result = prime * result + ((systemEmailAddress == null) ? 0 : systemEmailAddress.hashCode());
        result = prime * result + ((systemPassword == null) ? 0 : systemPassword.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        AcmOutlookFolderCreator other = (AcmOutlookFolderCreator) obj;
        if (id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        } else if (!id.equals(other.id))
        {
            return false;
        }
        if (outlookObjectReferences == null)
        {
            if (other.outlookObjectReferences != null)
            {
                return false;
            }
        } else if (!outlookObjectReferences.equals(other.outlookObjectReferences))
        {
            return false;
        }
        if (systemEmailAddress == null)
        {
            if (other.systemEmailAddress != null)
            {
                return false;
            }
        } else if (!systemEmailAddress.equals(other.systemEmailAddress))
        {
            return false;
        }
        if (systemPassword == null)
        {
            if (other.systemPassword != null)
            {
                return false;
            }
        } else if (!systemPassword.equals(other.systemPassword))
        {
            return false;
        }
        return true;
    }

}
