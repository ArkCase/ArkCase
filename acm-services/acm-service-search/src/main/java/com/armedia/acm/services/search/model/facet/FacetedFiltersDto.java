package com.armedia.acm.services.search.model.facet;

import java.util.List;

/**
 * Created by marjan.stefanoski on 06.01.2015.
 */
public class FacetedFiltersDto {

    private List<FacetedFilter> caseFilters;
    private List<FacetedFilter> complaintFilters;
    private List<FacetedFilter> documentFilters;

    public List<FacetedFilter> getCaseFilters() {
        return caseFilters;
    }

    public void setCaseFilters(List<FacetedFilter> caseFilters) {
        this.caseFilters = caseFilters;
    }

    public List<FacetedFilter> getComplaintFilters() {
        return complaintFilters;
    }

    public void setComplaintFilters(List<FacetedFilter> complaintFilters) {
        this.complaintFilters = complaintFilters;
    }

    public List<FacetedFilter> getDocumentFilters() {
        return documentFilters;
    }

    public void setDocumentFilters(List<FacetedFilter> documentFilters) {
        this.documentFilters = documentFilters;
    }
}
