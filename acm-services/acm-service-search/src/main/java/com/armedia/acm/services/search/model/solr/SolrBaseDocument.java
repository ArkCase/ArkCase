package com.armedia.acm.services.search.model.solr;

import java.util.List;

/**
 * Created by armdev on 10/23/14.
 */
public interface SolrBaseDocument
{
    String getId();
    void setId(String id);

    void setDeny_acl_ss(List<String> deny_acl_ss);

    void setAllow_acl_ss(List<String> allow_acl_ss);

    void setPublic_doc_b(boolean public_doc_b);

    void setProtected_object_b(boolean protected_object_b);
}
