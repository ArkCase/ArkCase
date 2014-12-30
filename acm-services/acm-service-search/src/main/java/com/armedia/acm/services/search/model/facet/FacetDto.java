package com.armedia.acm.services.search.model.facet;

import java.util.List;

/**
 * Created by marjan.stefanoski on 19.12.2014.
 */
public class FacetDto {

    private List<IFacetDto> facets;

    public List<IFacetDto> getFacets() {
        return facets;
    }

    public void setFacets(List<IFacetDto> facets) {
        this.facets = facets;
    }
}
