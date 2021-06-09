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

import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationToSolrTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

import gov.foia.model.FOIARequesterAssociation;

public class FOIARequesterAssociationToSolrTransformer extends PersonAssociationToSolrTransformer
{
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return FOIARequesterAssociation.class.equals(acmObjectType);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PersonAssociation in)
    {
        SolrAdvancedSearchDocument solr = null;

        if (in instanceof FOIARequesterAssociation)
        {
            FOIARequesterAssociation requesterAssociationIn = (FOIARequesterAssociation) in;
            solr = super.toSolrAdvancedSearch(requesterAssociationIn);

            if (solr != null)
            {
                mapRequestProperties(requesterAssociationIn, solr.getAdditionalProperties());
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
    public Class<?> getAcmObjectTypeSupported()
    {
        return FOIARequesterAssociation.class;
    }

    private void mapRequestProperties(FOIARequesterAssociation requesterAssociationIn, Map<String, Object> additionalProperties)
    {
        additionalProperties.put("requester_source_s", requesterAssociationIn.getRequesterSource());
    }
}
