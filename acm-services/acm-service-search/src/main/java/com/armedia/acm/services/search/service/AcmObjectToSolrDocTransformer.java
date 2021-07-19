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

import org.json.JSONArray;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    default void mapRequiredProperties(SolrAdvancedSearchDocument doc, Long id, String creator, Date created, String modifier,
            Date modified, String objectType, String name)
    {
        doc.setId(id + "-" + objectType);
        doc.setObject_id_i(id);
        doc.setObject_id_s(Long.toString(id));
        doc.setObject_type_s(objectType);
        doc.setAuthor(creator);
        doc.setCreator_lcs(creator);
        doc.setCreate_date_tdt(created);
        doc.setModifier_lcs(modifier);
        doc.setModified_date_tdt(modified);
        doc.setName(name);
        doc.setName_lcs(name);
    }

    void mapAdditionalProperties(T in, Map<String, Object> additionalProperties);
}
