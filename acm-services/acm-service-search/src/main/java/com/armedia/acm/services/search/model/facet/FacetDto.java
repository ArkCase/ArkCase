package com.armedia.acm.services.search.model.facet;

import java.util.List;

/**
 * Created by marjan.stefanoski on 19.12.2014.
 */
public class FacetDto {

    private String q;
    private int start=0;
    private int n=100;

    private List<IFacetDto> facets;

    public List<IFacetDto> getFacets() {
        return facets;
    }

    public void setFacets(List<IFacetDto> facets) {
        this.facets = facets;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }
}
