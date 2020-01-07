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
import static gov.foia.model.PortalFOIAPerson.FIND_BY_EMAIL;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 23, 2018
 *
 */
@Entity
@DiscriminatorValue("gov.foia.model.PortalFOIAPerson")
@NamedQueries({
        @NamedQuery(name = FIND_BY_EMAIL, query = "SELECT p FROM PortalFOIAPerson p WHERE p.defaultEmail.value = :emailAddress") })
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class PortalFOIAPerson extends FOIAPerson
{

    private static final long serialVersionUID = 1676249990645454567L;

    public static final String FIND_BY_EMAIL = "PortalFOIAPerson.findByEmail";

    @ElementCollection
    @CollectionTable(name = "acm_foia_person_portal_roles", joinColumns = @JoinColumn(name = "cm_person_id", referencedColumnName = "cm_person_id"))
    @MapKeyColumn(name = "cm_portal_id")
    @Column(name = "cm_user_role")
    private Map<String, String> portalRoles = new HashMap<>();

    /**
     * @return the portalRoles
     */
    public Map<String, String> getPortalRoles()
    {
        return portalRoles;
    }

    /**
     * @param portalRoles
     *            the portalRoles to set
     */
    public void setPortalRoles(Map<String, String> portalRoles)
    {
        this.portalRoles = portalRoles;
    }

}
