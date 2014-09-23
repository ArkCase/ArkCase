package com.armedia.acm.plugins.task.model;

public class SolrResponse {
    private ResponseHeader responseHeader;
    private Response response;
    
    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }
    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }
    public Response getResponse() {
        return response;
    }
    public void setResponse(Response response) {
        this.response = response;
    }
    @Override
    public String toString() {
        return "SolrResponse [responseHeader=" + responseHeader + ", response="
                + response + "]";
    }
   
}
