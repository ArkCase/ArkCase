package com.armedia.acm.plugins.search.model.solr;

import java.util.List;

public class Response {
    private Integer numFound;
    private List<SolrDocument> docs;
    
    public Integer getNumFound() {
        return numFound;
    }
    public void setNumFound(Integer numFound) {
        this.numFound = numFound;
    }
    public List<SolrDocument> getDocs() {
        return docs;
    }
    public void setDocs(List<SolrDocument> docs) {
        this.docs = docs;
    }
    
    @Override
    public String toString() {
        return "Response [numFound=" + numFound + ", docs=" + docs + "]";
    }
    
}
