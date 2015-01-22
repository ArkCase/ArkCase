package com.armedia.acm.services.search.model.solr;

import java.util.List;

/**
 * Created by armdev on 10/27/14.
 */
public class SolrDocumentId implements SolrBaseDocument
{
    private String id;
    // access control fields
    private boolean public_doc_b;
    private boolean protected_object_b;

    private List<String> deny_acl_ss;
    private List<String> allow_acl_ss;



    public SolrDocumentId(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    public boolean isPublic_doc_b()
    {
        return public_doc_b;
    }

    @Override
    public void setPublic_doc_b(boolean public_doc_b)
    {
        this.public_doc_b = public_doc_b;
    }

    public boolean isProtected_object_b()
    {
        return protected_object_b;
    }

    @Override
    public void setProtected_object_b(boolean protected_object_b)
    {
        this.protected_object_b = protected_object_b;
    }

    public List<String> getDeny_acl_ss()
    {
        return deny_acl_ss;
    }

    @Override
    public void setDeny_acl_ss(List<String> deny_acl_ss)
    {
        this.deny_acl_ss = deny_acl_ss;
    }

    public List<String> getAllow_acl_ss()
    {
        return allow_acl_ss;
    }

    @Override
    public void setAllow_acl_ss(List<String> allow_acl_ss)
    {
        this.allow_acl_ss = allow_acl_ss;
    }
}
