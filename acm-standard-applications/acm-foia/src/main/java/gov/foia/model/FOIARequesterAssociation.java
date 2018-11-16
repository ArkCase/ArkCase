/**
 *
 */
package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 22, 2016
 */
@Entity
@DiscriminatorValue("gov.foia.model.FOIARequesterAssociation")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class FOIARequesterAssociation extends PersonAssociation
{

    private static final long serialVersionUID = -6645831984920786486L;

    @Column(name = "fo_requester_source")
    private String requesterSource;

    /**
     * @return the requesterSource
     */
    public String getRequesterSource()
    {
        return requesterSource;
    }

    /**
     * @param requesterSource
     *            the requesterSource to set
     */
    public void setRequesterSource(String requesterSource)
    {
        this.requesterSource = requesterSource;
    }

}
