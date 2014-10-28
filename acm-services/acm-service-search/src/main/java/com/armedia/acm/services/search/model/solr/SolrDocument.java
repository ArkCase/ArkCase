package com.armedia.acm.services.search.model.solr;

import java.util.ArrayList;
import java.util.List;

public class SolrDocument implements SolrBaseDocument {
    private String id;
    private String status_s;
    private String author;
    private String author_s;
    private String modifier_s;
    private String last_modified;
    private String create_dt;
    private String title_t;
    private String name;
    private String object_id_s;
    private String owner_s;
    private String object_type_s;
    private String assignee_s;
    private Long priority_i;
    private List<SolrBaseDocument> _childDocuments_ = new ArrayList<>();
    
    private List<String> deny_acl_ss;
    private List<String> allow_acl_ss;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    public String getStatus_s() {
        return status_s;
    }
    public void setStatus_s(String status_s) {
        this.status_s = status_s;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAuthor_s() {
        return author_s;
    }
    public void setAuthor_s(String author_s) {
        this.author_s = author_s;
    }
    public String getModifier_s() {
        return modifier_s;
    }
    public void setModifier_s(String modifier_s) {
        this.modifier_s = modifier_s;
    }
    public String getLast_modified() {
        return last_modified;
    }
    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }
    public String getCreate_dt() {
        return create_dt;
    }
    public void setCreate_dt(String create_dt) {
        this.create_dt = create_dt;
    }
    public String getTitle_t() {
        return title_t;
    }
    public void setTitle_t(String title_t) {
        this.title_t = title_t;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getObject_id_s() {
        return object_id_s;
    }
    public void setObject_id_s(String object_id_s) {
        this.object_id_s = object_id_s;
    }
    public String getOwner_s() {
        return owner_s;
    }
    public void setOwner_s(String owner_s) {
        this.owner_s = owner_s;
    }
    public String getObject_type_s() {
        return object_type_s;
    }
    public void setObject_type_s(String object_type_s) {
        this.object_type_s = object_type_s;
    }
    public String getAssignee_s() {
        return assignee_s;
    }
    public void setAssignee_s(String assignee_s) {
        this.assignee_s = assignee_s;
    }
    public Long getPriority_i() {
        return priority_i;
    }
    public void setPriority_i(Long priority_i) {
        this.priority_i = priority_i;
    }
    public List<String> getDeny_acl_ss() {
        return deny_acl_ss;
    }
    public void setDeny_acl_ss(List<String> deny_acl_ss) {
        this.deny_acl_ss = deny_acl_ss;
    }
    public List<String> getAllow_acl_ss() {
        return allow_acl_ss;
    }
    public void setAllow_acl_ss(List<String> allow_acl_ss) {
        this.allow_acl_ss = allow_acl_ss;
    }

    public List<SolrBaseDocument> get_childDocuments_()
    {
        return _childDocuments_;
    }

    public void set_childDocuments_(List<SolrBaseDocument> _childDocuments_)
    {
        this._childDocuments_ = _childDocuments_;
    }

    @Override
    public String toString() {
        return "SolrDocument [id=" + id + ", status_s=" + status_s
                + ", author=" + author + ", author_s=" + author_s
                + ", modifier_s=" + modifier_s + ", last_modified="
                + last_modified + ", create_dt=" + create_dt + ", title_t="
                + title_t + ", name=" + name + ", object_id_s=" + object_id_s
                + ", owner_s=" + owner_s + ", object_type_s=" + object_type_s
                + ", assignee_s=" + assignee_s + ", priority_i=" + priority_i
                + ", deny_acl_ss=" + deny_acl_ss + ", allow_acl_ss="
                + allow_acl_ss + "]";
    }
    
    
}
