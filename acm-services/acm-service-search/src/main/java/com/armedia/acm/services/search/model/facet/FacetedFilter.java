package com.armedia.acm.services.search.model.facet;

/**
 * Created by marjan.stefanoski on 07.01.2015.
 */
public class FacetedFilter {
    private String key;
    private String title;

    public FacetedFilter(){
        super();
    }

    public FacetedFilter(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
