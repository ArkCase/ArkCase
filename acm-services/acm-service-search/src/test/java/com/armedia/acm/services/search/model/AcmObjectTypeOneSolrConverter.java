package com.armedia.acm.services.search.model;

/*-
 * #%L
 * ACM Service: Search
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

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 10/23/14.
 */
public class AcmObjectTypeOneSolrConverter implements AcmObjectToSolrDocTransformer<AcmObjectTypeOne>
{
    private int handledObjectsCount = 0;

    @Override
    public List<AcmObjectTypeOne> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return null;
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmObjectTypeOne in)
    {
        ++handledObjectsCount;
        return new SolrAdvancedSearchDocument();
    }

    @Override
    public SolrContentDocument toContentFileIndex(AcmObjectTypeOne in)
    {
        ++handledObjectsCount;
        return new SolrContentDocument();
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmObjectTypeOne.class.equals(acmObjectType);
    }

    public int getHandledObjectsCount()
    {
        return handledObjectsCount;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmObjectTypeOne.class;
    }

    @Override
    public void mapAdditionalProperties(AcmObjectTypeOne in, Map<String, Object> additionalProperties)
    {

    }
}
