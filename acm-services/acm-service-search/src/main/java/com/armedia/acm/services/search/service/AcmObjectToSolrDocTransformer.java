package com.armedia.acm.services.search.service;

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
import com.armedia.acm.services.search.model.solr.SolrDocument;

import org.json.JSONArray;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/22/14.
 */
public interface AcmObjectToSolrDocTransformer<T extends Object>
{
    /**
     * Support SOLR batch update mode... get all objects modified since the given date.
     * 
     * @param lastModified
     * @return
     */
    List<T> getObjectsModifiedSince(Date lastModified, int start, int pageSize);

    SolrAdvancedSearchDocument toSolrAdvancedSearch(T in);

    SolrDocument toSolrQuickSearch(T in);

    default JSONArray childrenUpdatesToSolr(T in)
    {
        return null;
    }

    default SolrContentDocument toContentFileIndex(T in)
    {
        return null;
    }

    boolean isAcmObjectTypeSupported(Class acmObjectType);

    Class<?> getAcmObjectTypeSupported();
}
