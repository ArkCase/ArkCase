package gov.privacy.transformer;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationToSolrTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import gov.privacy.model.SARPersonAssociation;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARPersonAssociationToSolrTransformer extends PersonAssociationToSolrTransformer
{
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return SARPersonAssociation.class.equals(acmObjectType);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PersonAssociation in)
    {
        SolrAdvancedSearchDocument solr = null;

        if (in instanceof SARPersonAssociation)
        {
            SARPersonAssociation sarPersonAssociation = (SARPersonAssociation) in;
            solr = super.toSolrAdvancedSearch(sarPersonAssociation);

            if (solr != null)
            {
                mapRequestProperties(sarPersonAssociation, solr.getAdditionalProperties());
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
        return SARPersonAssociation.class;
    }

    private void mapRequestProperties(SARPersonAssociation sarPersonAssociation, Map<String, Object> additionalProperties)
    {
        additionalProperties.put("requester_source_s", sarPersonAssociation.getRequesterSource());
    }
}
