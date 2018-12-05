package com.armedia.acm.services.sequence.model;

/*-
 * #%L
 * ACM Service: Sequence Manager
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

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceConfiguration
{

    private String sequenceName;
    private Boolean sequenceEnabled;
    private String sequenceDescription;
    private List<AcmSequencePart> sequenceParts;

    /**
     * @return the sequenceName
     */
    public String getSequenceName()
    {
        return sequenceName;
    }

    /**
     * @param sequenceName
     *            the sequenceName to set
     */
    public void setSequenceName(String sequenceName)
    {
        this.sequenceName = sequenceName;
    }

    /**
     * @return the sequenceEnabled
     */
    public Boolean getSequenceEnabled()
    {
        return sequenceEnabled;
    }

    /**
     * @param sequenceEnabled
     *            the sequenceEnabled to set
     */
    public void setSequenceEnabled(Boolean sequenceEnabled)
    {
        this.sequenceEnabled = sequenceEnabled;
    }

    /**
     * @return the sequenceDescription
     */
    public String getSequenceDescription()
    {
        return sequenceDescription;
    }

    /**
     * @param sequenceDescription
     *            the sequenceDescription to set
     */
    public void setSequenceDescription(String sequenceDescription)
    {
        this.sequenceDescription = sequenceDescription;
    }

    /**
     * @return the sequenceParts
     */
    public List<AcmSequencePart> getSequenceParts()
    {
        return sequenceParts;
    }

    /**
     * @param sequenceParts
     *            the sequenceParts to set
     */
    public void setSequenceParts(List<AcmSequencePart> sequenceParts)
    {
        this.sequenceParts = sequenceParts;
    }

}
