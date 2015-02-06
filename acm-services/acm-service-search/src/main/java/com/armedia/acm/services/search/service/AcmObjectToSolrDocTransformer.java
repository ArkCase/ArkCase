package com.armedia.acm.services.search.service;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/22/14.
 */
public interface AcmObjectToSolrDocTransformer<T extends Object>
{
    /**
     * Support SOLR batch update mode... get all objects modified since the given date.
     * @param lastModified
     * @return
     */
    List<T> getObjectsModifiedSince(Date lastModified, int start, int pageSize);

    SolrAdvancedSearchDocument toSolrAdvancedSearch(T in);

    SolrDocument toSolrQuickSearch(T in);

    SolrAdvancedSearchDocument toContentFileIndex(T in);

    boolean isAcmObjectTypeSupported(Class acmObjectType);
}
