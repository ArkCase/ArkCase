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

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceResetId implements Serializable
{

    private static final long serialVersionUID = -7936849454292080520L;

    private String sequenceName;

    private String sequencePartName;

    private LocalDateTime resetDate;

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
     * @return the sequencePartName
     */
    public String getSequencePartName()
    {
        return sequencePartName;
    }

    /**
     * @param sequencePartName
     *            the sequencePartName to set
     */
    public void setSequencePartName(String sequencePartName)
    {
        this.sequencePartName = sequencePartName;
    }

    /**
     * @return the resetDate
     */
    public LocalDateTime getResetDate()
    {
        return resetDate;
    }

    /**
     * @param resetDate
     *            the resetDate to set
     */
    public void setResetDate(LocalDateTime resetDate)
    {
        this.resetDate = resetDate;
    }

}
