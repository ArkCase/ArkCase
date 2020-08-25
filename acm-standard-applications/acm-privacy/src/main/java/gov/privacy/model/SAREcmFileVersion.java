package gov.privacy.model;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
@Entity
@DiscriminatorValue("gov.privacy.model.SAREcmFileVersion")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class SAREcmFileVersion extends EcmFileVersion
{
    @Column(name = "sar_review_status")
    private String reviewStatus;

    @Column(name = "sar_redaction_status")
    private String redactionStatus;

    public String getReviewStatus()
    {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus)
    {
        this.reviewStatus = reviewStatus;
    }

    public String getRedactionStatus()
    {
        return redactionStatus;
    }

    public void setRedactionStatus(String redactionStatus)
    {
        this.redactionStatus = redactionStatus;
    }
}
