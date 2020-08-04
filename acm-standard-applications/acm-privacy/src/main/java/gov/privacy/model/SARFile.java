/**
 *
 */
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

import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import java.util.List;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
@Entity
@DiscriminatorValue("gov.privacy.model.SARFile")
public class SARFile extends EcmFile
{

    private static final long serialVersionUID = 3364794434531371617L;

    @ElementCollection
    @CollectionTable(name = "acm_exemption_code", joinColumns = @JoinColumn(name = "cm_file_id", referencedColumnName = "cm_file_id"))
    @Column(name = "cm_exemption_code")
    private List<String> exemptionCodes;

    @Column(name = "sar_public_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean publicFlag;

    /**
     * @return the exemptionCodes
     */
    public List<String> getExemptionCodes()
    {
        return exemptionCodes;
    }

    /**
     * @param exemptionCodes
     *            the exemptionCodes to set
     */
    public void setExemptionCodes(List<String> exemptionCodes)
    {
        this.exemptionCodes = exemptionCodes;
    }

    public Boolean getPublicFlag()
    {
        return publicFlag;
    }

    public void setPublicFlag(Boolean publicFlag)
    {
        this.publicFlag = publicFlag;
    }
}
