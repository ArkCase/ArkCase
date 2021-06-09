package com.armedia.acm.plugins.addressable.service;

/*-
 * #%L
 * ACM Default Plugin: Addressable
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

import com.armedia.acm.plugins.addressable.dao.ContactMethodDao;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/27/14.
 */
public class ContactMethodToSolrTransformer implements AcmObjectToSolrDocTransformer<ContactMethod>
{
    private ContactMethodDao contactMethodDao;
    private UserDao userDao;

    @Override
    public List<ContactMethod> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getContactMethodDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(ContactMethod cm)
    {
        SolrAdvancedSearchDocument cmDoc = new SolrAdvancedSearchDocument();
        cmDoc.setId(cm.getId() + "-CONTACT-METHOD");
        cmDoc.setObject_type_s("CONTACT-METHOD");
        cmDoc.setObject_id_s(cm.getId() + "");
        cmDoc.setType_lcs(cm.getType());
        cmDoc.setValue_parseable(cm.getValue());
        cmDoc.setCreate_date_tdt(cm.getCreated());
        cmDoc.setCreator_lcs(cm.getCreator());
        cmDoc.setModified_date_tdt(cm.getModified());
        cmDoc.setModifier_lcs(cm.getModifier());

        cmDoc.setName(cm.getValue());
        cmDoc.setTitle_parseable(cm.getValue());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(cm.getCreator());
        if (creator != null)
        {
            cmDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(cm.getModifier());
        if (modifier != null)
        {
            cmDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return cmDoc;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return ContactMethod.class.equals(acmObjectType);
    }

    public ContactMethodDao getContactMethodDao()
    {
        return contactMethodDao;
    }

    public void setContactMethodDao(ContactMethodDao contactMethodDao)
    {
        this.contactMethodDao = contactMethodDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return ContactMethod.class;
    }
}
