package com.armedia.acm.plugins.person.service;

/*-
 * #%L
 * ACM Default Plugin: Person
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

import com.armedia.acm.plugins.person.dao.PersonAliasDao;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by will.phillips on 8/4/2016.
 */
public class PersonAliasToSolrTransformer implements AcmObjectToSolrDocTransformer<PersonAlias>
{

    private PersonAliasDao personAliasDao;
    private UserDao userDao;

    @Override
    public List<PersonAlias> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPersonAliasDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PersonAlias org)
    {
        SolrAdvancedSearchDocument orgDoc = new SolrAdvancedSearchDocument();
        orgDoc.setId(org.getId() + "-PERSON-ALIAS");
        orgDoc.setObject_type_s("PERSON-ALIAS");
        orgDoc.setObject_id_s(org.getId() + "");

        if (org.getPerson() != null && org.getPerson().getId() != null)
        {
            orgDoc.setParent_type_s("PERSON");
            orgDoc.setParent_id_s(Long.toString(org.getPerson().getId()));
            orgDoc.setParent_ref_s(Long.toString(org.getPerson().getId()) + "-PERSON");
        }

        orgDoc.setCreate_date_tdt(org.getCreated());
        orgDoc.setCreator_lcs(org.getCreator());
        orgDoc.setModified_date_tdt(org.getModified());
        orgDoc.setModifier_lcs(org.getModifier());

        orgDoc.setType_lcs(org.getAliasType());
        orgDoc.setValue_parseable(org.getAliasValue());

        orgDoc.setName(org.getAliasValue());
        orgDoc.setTitle_parseable(org.getAliasValue());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(org.getCreator());
        if (creator != null)
        {
            orgDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(org.getModifier());
        if (modifier != null)
        {
            orgDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return orgDoc;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return PersonAlias.class.equals(acmObjectType);
    }

    public PersonAliasDao getPersonAliasDao()
    {
        return personAliasDao;
    }

    public void setPersonAliasDao(PersonAliasDao personAliasDao)
    {
        this.personAliasDao = personAliasDao;
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
        return PersonAlias.class;
    }
}
