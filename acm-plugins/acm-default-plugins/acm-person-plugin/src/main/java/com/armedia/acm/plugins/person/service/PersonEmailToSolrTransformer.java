package com.armedia.acm.plugins.person.service;

/*-
 * #%L
 * ACM Default Plugin: Person
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.EMAIL_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TYPE_LCS;

import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ana.serafimoska
 */
public class PersonEmailToSolrTransformer implements AcmObjectToSolrDocTransformer<Person>
{
    private final Logger log = LogManager.getLogger(getClass());

    private PersonDao personDao;

    @Override
    public List<Person> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPersonDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Person in)
    {
        if (in.getDefaultEmail() != null)
        {
            SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
            log.debug("Creating Solr advanced search document for PERSON EMAIL.");

            solrDoc.setObject_type_s("EMAIL");
            solrDoc.setId(in.getId() + "-EMAIL");
            solrDoc.setObject_id_s(in.getId() + "");
            solrDoc.setName(in.getGivenName() + " " + in.getFamilyName());
            solrDoc.setName_lcs(in.getGivenName() + " " + in.getFamilyName());
            mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

            return solrDoc;
        }
        else
        {
            log.debug("Person has no default email. No EMAIL solr document will be added");
            return null;
        }
    }

    @Override
    public void mapAdditionalProperties(Person in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TYPE_LCS, in.getObjectType());
        additionalProperties.put(EMAIL_LCS, in.getDefaultEmail().getValue());
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Person.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return Person.class;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }
}
