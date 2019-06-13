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
import static gov.foia.model.UserResetRequestRecord.FIND_BY_EMAIL;
import static gov.foia.model.UserResetRequestRecord.FIND_BY_RESET_KEY;

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
@Table(name = "acm_foia_portal_reset")
@NamedQueries({ @NamedQuery(name = FIND_BY_EMAIL, query = "SELECT r FROM UserResetRequestRecord r WHERE r.emailAddress = :emailAddress"),
        @NamedQuery(name = FIND_BY_RESET_KEY, query = "SELECT r FROM UserResetRequestRecord r WHERE r.resetKey = :resetKey") })
public class UserResetRequestRecord implements Serializable
{
    private static final long serialVersionUID = 4430974227371276139L;

    public static final String FIND_BY_EMAIL = "UserResetRequestRecord.findByEmail";

    public static final String FIND_BY_RESET_KEY = "UserResetRequestRecord.findByResetKey";

    @Id
    @TableGenerator(name = "foia_portal_reset_gen", table = "acm_foia_portal_reset_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_foia_portal_reset", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "foia_portal_reset_gen")
    @Column(name = "cm_reset_id")
    private Long id;

    @Column(name = "cm_email_address")
    private String emailAddress;

    @Column(name = "cm_reset_key")
    private String resetKey;

    @Column(name = "cm_request_time")
    private long requestTime;

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
     * @return the resetKey
     */
    public String getResetKey()
    {
        return resetKey;
    }

    /**
     * @param resetKey
     *            the resetKey to set
     */
    public void setResetKey(String registrationKey)
    {
        resetKey = registrationKey;
    }

    /**
     * @return the requestTime
     */
    public long getRequestTime()
    {
        return requestTime;
    }

    /**
     * @param requestTime
     *            the requestTime to set
     */
    public void setRequestTime(long registrationTime)
    {
        requestTime = registrationTime;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "UserResetRequestRecord [emailAddress=" + emailAddress + ", resetKey=" + resetKey + ", requestTime=" + requestTime + "]";
    }

}
