package com.armedia.acm.portalgateway.model;

/*-
 * #%L
 * ACM Service: Portal Gateway Service
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

import static com.armedia.acm.portalgateway.model.PortalInfo.FIND_PORTAL_INFO_BY_ID;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.io.Serializable;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 28, 2018
 *
 */
@Entity
@Table(name = "acm_portal_info")
@NamedQueries({ @NamedQuery(name = FIND_PORTAL_INFO_BY_ID, query = "SELECT pi FROM PortalInfo pi WHERE pi.portalId = :portalId") })
public class PortalInfo implements Serializable
{

    private static final long serialVersionUID = 1139980144194262982L;

    public static final String FIND_PORTAL_INFO_BY_ID = "PortalInfo.findById";

    @Id
    @TableGenerator(name = "portal_info_gen", table = "acm_portal_info_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_portal_info", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "portal_info_gen")
    @Column(name = "cm_portal_info_id")
    private Long id;

    /**
     * Generated unique ID that a portal uses to identify itself to ArkCase. A portal is associated with a given user,
     * so the user portal ID pair must match the configured values.
     */
    @Column(name = "cm_portal_id", unique = true, nullable = false, updatable = false)
    private String portalId;

    /**
     * Short summary about the portal.
     */
    @Column(name = "cm_portal_description")
    private String portalDescription;

    /**
     * The URL where the portal can be reached. It is not used by the system, only by the administrators to check the
     * portal.
     */
    @Column(name = "cm_portal_url")
    private String portalUrl;

    /**
     * User that a given portal logs in with on ArkCase.
     */
    @ManyToOne
    @JoinColumn(name = "cm_user_id")
    private AcmUser user;

    @OneToOne
    @JoinColumn(name = "cm_group_name")
    private AcmGroup group;

    @Column(name = "cm_portal_authentication_flag")
    private Boolean portalAuthenticationFlag;

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

    /**
     * @return the portalDescription
     */
    public String getPortalDescription()
    {
        return portalDescription;
    }

    /**
     * @param portalDescription
     *            the portalDescription to set
     */
    public void setPortalDescription(String portalDescription)
    {
        this.portalDescription = portalDescription;
    }

    /**
     * @return the portalUrl
     */
    public String getPortalUrl()
    {
        return portalUrl;
    }

    /**
     * @param portalUrl
     *            the portalUrl to set
     */
    public void setPortalUrl(String portalUrl)
    {
        this.portalUrl = portalUrl;
    }

    /**
     * @return the user
     */
    public AcmUser getUser()
    {
        return user;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(AcmUser user)
    {
        this.user = user;
    }

    /**
     * @return the group
     */
    public AcmGroup getGroup()
    {
        return group;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup(AcmGroup group)
    {
        this.group = group;
    }

    public Boolean getPortalAuthenticationFlag()
    {
        return portalAuthenticationFlag;
    }

    public void setPortalAuthenticationFlag(Boolean portalAuthenticationFlag)
    {
        this.portalAuthenticationFlag = portalAuthenticationFlag;
    }
}
