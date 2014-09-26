package com.armedia.acm.plugins.task.model;

import java.util.List;

public class Response {
    private Integer numFound;
    private List<AcmTaskSolr> docs;
    
    public Integer getNumFound() {
        return numFound;
    }
    public void setNumFound(Integer numFound) {
        this.numFound = numFound;
    }
    public List<AcmTaskSolr> getDocs() {
        return docs;
    }
    public void setDocs(List<AcmTaskSolr> docs) {
        this.docs = docs;
    }
    
    @Override
    public String toString() {
        return "Response [numFound=" + numFound + ", docs=" + docs + "]";
    }
    
}
