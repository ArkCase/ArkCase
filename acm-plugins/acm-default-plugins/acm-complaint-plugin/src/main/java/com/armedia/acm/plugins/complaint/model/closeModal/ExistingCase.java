package com.armedia.acm.plugins.complaint.model.closeModal;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import java.util.Date;

public class ExistingCase
{
    private String caseNumber;
    private String caseTitle;
    private Date caseCreationDate;
    private String casePriority;

    public String getCaseNumber()
    {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber)
    {
        this.caseNumber = caseNumber;
    }

    public String getCaseTitle()
    {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle)
    {
        this.caseTitle = caseTitle;
    }

    public Date getCaseCreationDate()
    {
        return caseCreationDate;
    }

    public void setCaseCreationDate(Date caseCreationDate)
    {
        this.caseCreationDate = caseCreationDate;
    }

    public String getCasePriority()
    {
        return casePriority;
    }

    public void setCasePriority(String casePriority)
    {
        this.casePriority = casePriority;
    }
}
