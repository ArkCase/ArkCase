package com.armedia.acm.services.search.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.search.model.SolrFilter;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by marjan.stefanoski on 06.01.2015.
 */
public class SolrFilterDao extends AcmAbstractDao<SolrFilter> {

    @Override
    protected Class<SolrFilter> getPersistenceClass() {
        return SolrFilter.class;
    }

    public List<SolrFilter> listAllFacetedFilters() throws AcmObjectNotFoundException {
        Query listFacetedFilters = getEm().createQuery("SELECT sf FROM SolrFilter sf " +
                "WHERE sf.filterType=:t1 OR sf.filterType=:t2 OR  sf.filterType=:t3");
        listFacetedFilters.setParameter("t1", "CASE_FILE");
        listFacetedFilters.setParameter("t2", "COMPLAINT");
        listFacetedFilters.setParameter("t3", "DOCUMENT");

        List<SolrFilter> result = listFacetedFilters.getResultList();
        if (!result.isEmpty()) {
            return result;
        } else {
            throw new AcmObjectNotFoundException("solr filters", null, "Object not found", null);
        }

    }

    @Transactional
    public int deleteAllFacetedFilters() {
        Query deleteAllFacetedFilters = getEm().createQuery(
                "DELETE FROM SolrFilter sf WHERE sf.filterType=:t1 OR sf.filterType=:t2 OR  sf.filterType=:t3");
        deleteAllFacetedFilters.setParameter("t1", "CASE_FILE");
        deleteAllFacetedFilters.setParameter("t2", "COMPLAINT");
        deleteAllFacetedFilters.setParameter("t3", "DOCUMENT");
        int i = deleteAllFacetedFilters.executeUpdate();
        return i;
    }
}