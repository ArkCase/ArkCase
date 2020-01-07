package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import static gov.foia.model.UserRegistrationRequestRecord.FIND_BY_EMAIL_AND_PORTAL_ID;
import static gov.foia.model.UserRegistrationRequestRecord.FIND_BY_REGISTRATION_KEY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.io.Serializable;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 21, 2018
 *
 */
@Entity
@Table(name = "acm_foia_portal_registration")
@NamedQueries({
        @NamedQuery(name = FIND_BY_EMAIL_AND_PORTAL_ID, query = "SELECT r FROM UserRegistrationRequestRecord r WHERE r.emailAddress = :emailAddress AND r.portalId = :portalId"),
        @NamedQuery(name = FIND_BY_REGISTRATION_KEY, query = "SELECT r FROM UserRegistrationRequestRecord r WHERE r.registrationKey = :registrationKey") })
public class UserRegistrationRequestRecord implements Serializable
{

    private static final long serialVersionUID = -7099073684425170464L;

    public static final String FIND_BY_EMAIL_AND_PORTAL_ID = "UserRegistrationRequestRecord.findByEmailAndPortalId";

    public static final String FIND_BY_REGISTRATION_KEY = "UserRegistrationRequestRecord.findByRegistrationKey";

    @Id
    @TableGenerator(name = "foia_portal_registration_gen", table = "acm_foia_portal_registration_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_foia_portal_registration", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "foia_portal_registration_gen")
    @Column(name = "cm_registration_id")
    private Long id;

    @Column(name = "cm_email_address")
    private String emailAddress;

    @Column(name = "cm_registration_key")
    private String registrationKey;

    @Column(name = "cm_registration_time")
    private long registrationTime;

    @Column(name = "cm_portal_id")
    private String portalId;

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
     * @return the emailAddress
     */
    public String getEmailAddress()
    {
        return emailAddress;
    }

    /**
     * @param emailAddress
     *            the emailAddress to set
     */
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    /**
     * @return the registrationKey
     */
    public String getRegistrationKey()
    {
        return registrationKey;
    }

    /**
     * @param registrationKey
     *            the registrationKey to set
     */
    public void setRegistrationKey(String registrationKey)
    {
        this.registrationKey = registrationKey;
    }

    /**
     * @return the registrationTime
     */
    public long getRegistrationTime()
    {
        return registrationTime;
    }

    /**
     * @param registrationTime
     *            the registrationTime to set
     */
    public void setRegistrationTime(long registrationTime)
    {
        this.registrationTime = registrationTime;
    }

    /**
     * @return the portalId
     */
    public String getPortalId()
    {
        return portalId;
    }

    /**
     * @param portalId
     *            the portalId to set
     */
    public void setPortalId(String portalId)
    {
        this.portalId = portalId;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "UserRegistrationRequestRecord [id=" + id + ", emailAddress=" + emailAddress + ", registrationKey=" + registrationKey
                + ", registrationTime=" + registrationTime + ", portalId=" + portalId + "]";
    }

}
