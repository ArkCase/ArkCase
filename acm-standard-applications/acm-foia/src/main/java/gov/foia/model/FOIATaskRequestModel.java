package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.task.model.AcmTask;

public class FOIATaskRequestModel
{
    private AcmTask task;
    private FOIARequest request;
    private Person taskContact;
    private String exemptionCodesAndDescription;
    private String redactions;
    private String exemptions;

    public FOIARequest getRequest()
    {
        return request;
    }

    public void setRequest(FOIARequest request)
    {
        this.request = request;
    }

    public AcmTask getTask()
    {
        return task;
    }

    public void setTask(AcmTask task)
    {
        this.task = task;
    }

    public Person getTaskContact() {
        return taskContact;
    }

    public void setTaskContact(Person taskContact) {
        this.taskContact = taskContact;
    }

    public String getExemptionCodesAndDescription()
    {
        return exemptionCodesAndDescription;
    }

    public void setExemptionCodesAndDescription(String exemptionCodesAndDescription)
    {
        this.exemptionCodesAndDescription = exemptionCodesAndDescription;
    }

    public String getRedactions()
    {
        return redactions;
    }

    public void setRedactions(String redactions)
    {
        this.redactions = redactions;
    }

    public String getExemptions()
    {
        return exemptions;
    }

    public void setExemptions(String exemptions)
    {
        this.exemptions = exemptions;
    }
}
