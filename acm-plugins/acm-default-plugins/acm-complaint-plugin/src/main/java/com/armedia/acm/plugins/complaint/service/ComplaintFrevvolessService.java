package com.armedia.acm.plugins.complaint.service;

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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;

import java.util.Calendar;
import java.util.Date;

public class ComplaintFrevvolessService
{
    private PersonDao personDao;
    private PersonAssociationDao personAssociationDao;
    private EcmFileService fileService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    public Complaint setMissingProperties(Complaint complaint)
    {
        complaint.setCreated(new Date());

        Calendar cal = Calendar.getInstance();
        cal.setTime(complaint.getCreated());
        cal.add(Calendar.DATE, 3);

        Date dueDate = cal.getTime();
        complaint.setDueDate(dueDate);

        return complaint;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    /**
     * @param personDao
     *            the personDao to set
     */
    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
