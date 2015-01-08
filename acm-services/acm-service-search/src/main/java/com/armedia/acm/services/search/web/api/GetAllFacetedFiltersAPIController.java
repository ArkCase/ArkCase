package com.armedia.acm.services.search.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.search.dao.SolrFilterDao;
import com.armedia.acm.services.search.model.PropertyKeyByType;
import com.armedia.acm.services.search.model.SolrFilter;
import com.armedia.acm.services.search.model.facet.FacetedFilter;
import com.armedia.acm.services.search.model.facet.FacetedFiltersDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 06.01.2015.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class GetAllFacetedFiltersAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());
    private SolrFilterDao solrFilterDao;
    private AcmPlugin pluginSearch;

    @RequestMapping(value = "/getAvailableFilters", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FacetedFiltersDto getAllFacetedFilters(
            Authentication authentication,
            HttpSession session)  throws AcmObjectNotFoundException {

        String userId = (String) authentication.getName().toLowerCase();
        if (log.isInfoEnabled()) {
            log.info("Getting all available solr facet filters'");
        }

        List<SolrFilter> facetFilters = null;
        boolean isNewFilterConfiguration = false;
        try {
            isNewFilterConfiguration = Boolean.valueOf((String) getPluginSearch().getPluginProperties().get("acm.filters.reload"));
            if(isNewFilterConfiguration){
                getSolrFilterDao().deleteAllFacetedFilters();
                facetFilters = addSelectedFilters(userId,authentication);
            } else {
                facetFilters = getSolrFilterDao().listAllFacetedFilters();
            }
        } catch ( AcmObjectNotFoundException e ) {
            //Filters are not present into the DB table so property file will be  read and filters will be inserted into the DB
            facetFilters = addSelectedFilters(userId,authentication);
        }
        if (facetFilters!=null) {
            return prepareFacetedFilterDto(facetFilters);
        }
        else {
           throw  new AcmObjectNotFoundException("SOLR Filters",null,"No Faceted Filters Found",null);
        }
    }

    private FacetedFiltersDto prepareFacetedFilterDto(List<SolrFilter> solrFilters){
        FacetedFiltersDto facetedFiltersDto = new FacetedFiltersDto();
        List<FacetedFilter> caseFilters = new ArrayList<>();
        List<FacetedFilter> complaintsFilters = new ArrayList<>();
        List<FacetedFilter> documentFilters = new ArrayList<>();
        for(SolrFilter filter: solrFilters){
            switch (filter.getFilterType()){
                case "CASE_FILE":
                    caseFilters.add(prepareFacetedFilter(filter));
                    break;
                case "COMPLAINT":
                    complaintsFilters.add(prepareFacetedFilter(filter));
                    break;
                case "DOCUMENT":
                    documentFilters.add(prepareFacetedFilter(filter));
                    break;
                default:
                    break;
            }
        }

        facetedFiltersDto.setCaseFilters(caseFilters);
        facetedFiltersDto.setComplaintFilters(complaintsFilters);
        facetedFiltersDto.setDocumentFilters(documentFilters);

        return facetedFiltersDto;
    }

    private FacetedFilter prepareFacetedFilter(SolrFilter filter){
        FacetedFilter facetedFilter  = new FacetedFilter();
        facetedFilter.setKey(filter.getFilterName());
        facetedFilter.setTitle(filter.getFilterTitle());
        return facetedFilter;
    }

    private List<SolrFilter> addSelectedFilters(String userId, Authentication authentication) {
        List<SolrFilter> solrFilters = new ArrayList<>();
        if(!getPluginSearch().getPluginProperties().isEmpty()){
            List<PropertyKeyByType> allFilters = PropertyKeyByType.getAllPopertyKeyByTypeValues();
            SolrFilter solrFilter = null;
            for(PropertyKeyByType keyTypePair : allFilters){
                 String title = (String) getPluginSearch().getPluginProperties().get(keyTypePair.getPropertyKey());
                 if( title!=null ) {
                     solrFilter = new SolrFilter();
                     solrFilter.setFilterTitle(title);
                     solrFilter.setFilterName(keyTypePair.getPropertyKey());
                     solrFilter.setFilterType(keyTypePair.getType());
                     solrFilters.add(addFilterIntoDB(solrFilter));
                     //TODO raise event about inserted filter row
                 }
            }
        }
        return solrFilters;
    }

    private SolrFilter addFilterIntoDB(SolrFilter toSave){
       return getSolrFilterDao().save(toSave);
    }

    public AcmPlugin getPluginSearch() {
        return pluginSearch;
    }

    public void setPluginSearch(AcmPlugin pluginSearch) {
        this.pluginSearch = pluginSearch;
    }

    public SolrFilterDao getSolrFilterDao() {
        return solrFilterDao;
    }

    public void setSolrFilterDao(SolrFilterDao solrFilterDao) {
        this.solrFilterDao = solrFilterDao;
    }
}
