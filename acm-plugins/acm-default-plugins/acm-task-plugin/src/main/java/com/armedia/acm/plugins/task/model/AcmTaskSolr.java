package com.armedia.acm.plugins.task.model;

import com.armedia.acm.core.AcmObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class AcmTaskSolr
{
    private String id;
    private String priority_s;
    private String title_t;
    private String object_type_s;
    private String parent_object_type_s;
    private String object_id_s;
    private String assignee_s;
    private String owner_s;					//creator
    private String name;
    private String status_s;
    private String _version_;
    private Date create_dt;
    private Date due_dt;
    private Long parent_object_id_i;
    private String deny_acl_ss;
    private String allow_acl_ss;
    private boolean adhocTask;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPriority_s() {
		return priority_s;
	}
	public void setPriority_s(String priority_s) {
		this.priority_s = priority_s;
	}
	public String getTitle_t() {
		return title_t;
	}
	public void setTitle_t(String title_t) {
		this.title_t = title_t;
	}
	public String getObject_type_s() {
		return object_type_s;
	}
	public void setObject_type_s(String object_type_s) {
		this.object_type_s = object_type_s;
	}
	public String getParent_object_type_s() {
		return parent_object_type_s;
	}
	public void setParent_object_type_s(String parent_object_type_s) {
		this.parent_object_type_s = parent_object_type_s;
	}
	public String getObject_id_s() {
		return object_id_s;
	}
	public void setObject_id_s(String object_id_s) {
		this.object_id_s = object_id_s;
	}
	public String getAssignee_s() {
		return assignee_s;
	}
	public void setAssignee_s(String assignee_s) {
		this.assignee_s = assignee_s;
	}
	public String getOwner_s() {
		return owner_s;
	}
	public void setOwner_s(String owner_s) {
		this.owner_s = owner_s;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus_s() {
		return status_s;
	}
	public void setStatus_s(String status_s) {
		this.status_s = status_s;
	}
	public String get_version_() {
		return _version_;
	}
	public void set_version_(String _version_) {
		this._version_ = _version_;
	}
	public Date getCreate_dt() {
		return create_dt;
	}
	public void setCreate_dt(Date create_dt) {
		this.create_dt = create_dt;
	}
    public Date getDue_dt() {
        return due_dt;
    }
    public void setDue_dt(Date due_dt) {
        this.due_dt = due_dt;
    }
	public Long getParent_object_id_i() {
		return parent_object_id_i;
	}
	public void setParent_object_id_i(Long parent_object_id_i) {
		this.parent_object_id_i = parent_object_id_i;
	}
	public String getDeny_acl_ss() {
		return deny_acl_ss;
	}
	public void setDeny_acl_ss(String deny_acl_ss) {
		this.deny_acl_ss = deny_acl_ss;
	}
	public String getAllow_acl_ss() {
		return allow_acl_ss;
	}
	public void setAllow_acl_ss(String allow_acl_ss) {
		this.allow_acl_ss = allow_acl_ss;
	}
    public Boolean isAdhocTask() {return adhocTask;}
    public void setAdhocTask(boolean adhocTask) {this.adhocTask = adhocTask;}

	@Override
	public String toString() {
		return "AcmTaskSolr [id=" + id + ", priority_s=" + priority_s
				+ ", title_t=" + title_t + ", object_type_s=" + object_type_s
				+ ", parent_object_type_s=" + parent_object_type_s
				+ ", object_id_s=" + object_id_s + ", assignee_s=" + assignee_s
				+ ", owner_s=" + owner_s + ", name=" + name + ", status_s="
				+ status_s + ", _version_=" + _version_ + ", create_dt="
				+ create_dt + ", due_dt=" + due_dt +", parent_object_id_i=" + parent_object_id_i
				+ ", deny_acl_ss=" + deny_acl_ss + ", allow_acl_ss="
				+ allow_acl_ss + ", adhocTask=" + adhocTask +"]";
	}

}
