package gov.foia.transformer;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.service.PersonToSolrTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import gov.foia.model.FOIAPerson;

/**
 * @author sasko.tanaskoski
 *
 */
public class FOIAPersonToSolrTransformer extends PersonToSolrTransformer
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return FOIAPerson.class.equals(acmObjectType);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Person in)
    {
        SolrAdvancedSearchDocument solr = null;

        if (in instanceof FOIAPerson)
        {
            FOIAPerson personIn = (FOIAPerson) in;
            solr = super.toSolrAdvancedSearch(personIn);

            if (solr != null)
            {
                // can't call mapRequestProperties, because SolrAdvancedSearchDocument
                // has a specific property 'object_sub_type_s', so also setting it via
                // mapRequestProperties may cause a duplicate key exception.
                solr.setObject_sub_type_s("FOIA_PERSON");
                solr.getAdditionalProperties().put("position_s", personIn.getPosition());
            }

            return solr;
        }
        else
        {
            log.error("Could not send to advanced search class name {}!.", in.getClass().getName());
        }
        throw new RuntimeException("Could not send to advanced search class name " + in.getClass().getName() + "!.");
    }

    @Override
    public SolrDocument toSolrQuickSearch(Person in)
    {
        SolrDocument solr = null;

        if (in instanceof FOIAPerson)
        {
            FOIAPerson personIn = (FOIAPerson) in;
            solr = super.toSolrQuickSearch(personIn);

            if (solr != null)
            {
                mapRequestProperties(personIn, solr.getAdditionalProperties());
            }

            return solr;
        }
        else
        {
            log.error("Could not send to quick search class name {}!.", in.getClass().getName());
        }
        throw new RuntimeException("Could not send to advanced search class name " + in.getClass().getName() + "!.");
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return FOIAPerson.class;
    }

    /**
     * @param requestIn
     * @param additionalProperties
     */
    protected void mapRequestProperties(FOIAPerson personIn, Map<String, Object> additionalProperties)
    {
        additionalProperties.put("object_sub_type_s", "FOIA_PERSON");
        additionalProperties.put("position_s", personIn.getPosition());

    }

}
