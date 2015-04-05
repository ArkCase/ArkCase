package com.armedia.acm.services.search.model.solr;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Property names must be identical to the desired SOLR field names.
 */
public class SolrAdvancedSearchDocument implements SolrBaseDocument, Serializable
{
    private static final long serialVersionUID = 1L;

    /////////////////////  fields for all documents ///////////////////////////
    private String id;
    private String object_id_s;
    private String object_type_s;
    private String object_sub_type_s;
    private String name;
    private Date create_date_tdt;
    private String creator_lcs;
    private Date modified_date_tdt;
    private String modifier_lcs;

    // access control fields
    private boolean public_doc_b;
    private boolean protected_object_b;

    private List<String> deny_acl_ss;
    private List<String> allow_acl_ss;

    /////////////////// for complaints, case files, other objects with a title or description ////////////
    private String title_parseable;
    private String description_parseable;

    /////////////////// for complaints, case files, tasks we introduce description and for personAssociation we introduce notes ////////////
    private String description_no_html_tags_parseable;
    private String notes_no_html_tags_parseable;

    /////////////////// for docs with an incident date ////////////
    private Date incident_date_tdt;

    /////////////////// for docs with a due date////////////////////
    private Date dueDate_tdt;

    /////////////////// for docs with a priority ////////////
    private String priority_lcs;

    /////////////////// for docs with an assignee ////////////
    private String assignee_id_lcs;
    private String assignee_first_name_lcs;
    private String assignee_last_name_lcs;
    private String assignee_full_name_lcs;
    private String incident_type_lcs;

    /////////////////// for docs with a status date ////////////
    private String status_lcs;

    /////////////////// for person records //////////////////////
    private String person_title_lcs;
    private String first_name_lcs;
    private String last_name_lcs;
    private String full_name_lcs;

    /////////////////// for acm users ///////////////
    private String email_lcs;

    /////////////////// for orgs, contact methods, and other objects with a type and a value /////////////
    private String type_lcs;
    private String value_parseable;

    ////////////////// for postal addresses /////////////////////
    private String location_street_address_lcs;
    private String location_city_lcs;
    private String location_state_lcs;
    private String location_postal_code_sdo;

    /////////////////////  for objects in a parent-child relationship, or association objects /////////////
    private String child_id_s;
    private String child_type_s;
    private String parent_id_s;
    private String parent_type_s;
    private String parent_name_t;
    private String parent_number_lcs;


    ////////////////// for objects that own organizations, e.g. persons /////////////////////
    private List<String> organization_id_ss;

    ////////////////// for objects that own postal addresses, e.g. persons /////////////////////
    private List<String> postal_address_id_ss;

    ////////////////// for objects that own contact methods, e.g. persons /////////////////////
    private List<String> contact_method_ss;
    
    ///////////////// for objects that have multiple children, supervisor, members ... e.g. group ///////////////////
    private String supervisor_id_s;
    private List<String> child_id_ss;
    private List<String> member_id_ss;
    private boolean adhocTask_b;
    private String owner_lcs;
    private String business_process_name_lcs;


    /////////////////////// for content files /////////////////////////////////////////
    private String content_type;
    private String ecmFileId;
    private List<String> tags_ss;

/////////////////////// for notification /////////////////////////////////////////
	private String state_lcs;
	private String data_lcs;
	private String action_lcs;
    private String notification_type_lcs;

    @JsonIgnore
    public String getEcmFileId() {
        return ecmFileId;
    }

    public void setEcmFileId(String ecmFileId) {
        this.ecmFileId = ecmFileId;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
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

    public String getObject_id_s()
    {
        return object_id_s;
    }

    public void setObject_id_s(String object_id_s)
    {
        this.object_id_s = object_id_s;
    }

    public String getObject_type_s()
    {
        return object_type_s;
    }

    public void setObject_type_s(String object_type_s)
    {
        this.object_type_s = object_type_s;
    }

    public String getObject_sub_type_s() 
    {
		return object_sub_type_s;
	}

	public void setObject_sub_type_s(String object_sub_type_s) 
	{
		this.object_sub_type_s = object_sub_type_s;
	}

	public String getTitle_parseable()
    {
        return title_parseable;
    }

    public void setTitle_parseable(String title_parseable)
    {
        this.title_parseable = title_parseable;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getIncident_date_tdt()
    {
        return incident_date_tdt;
    }

    public void setIncident_date_tdt(Date incident_date_tdt)
    {
        this.incident_date_tdt = incident_date_tdt;
    }

    public String getPriority_lcs()
    {
        return priority_lcs;
    }

    public void setPriority_lcs(String priority_lcs)
    {
        this.priority_lcs = priority_lcs;
    }

    public String getAssignee_id_lcs()
    {
        return assignee_id_lcs;
    }

    public void setAssignee_id_lcs(String assignee_id_lcs)
    {
        this.assignee_id_lcs = assignee_id_lcs;
    }

    public String getAssignee_first_name_lcs()
    {
        return assignee_first_name_lcs;
    }

    public void setAssignee_first_name_lcs(String assignee_first_name_lcs)
    {
        this.assignee_first_name_lcs = assignee_first_name_lcs;
    }

    public String getAssignee_last_name_lcs()
    {
        return assignee_last_name_lcs;
    }

    public void setAssignee_last_name_lcs(String assignee_last_name_lcs)
    {
        this.assignee_last_name_lcs = assignee_last_name_lcs;
    }

    public String getIncident_type_lcs()
    {
        return incident_type_lcs;
    }

    public void setIncident_type_lcs(String incident_type_lcs)
    {
        this.incident_type_lcs = incident_type_lcs;
    }

    public String getStatus_lcs()
    {
        return status_lcs;
    }

    public void setStatus_lcs(String status_lcs)
    {
        this.status_lcs = status_lcs;
    }

    public String getPerson_title_lcs()
    {
        return person_title_lcs;
    }

    public void setPerson_title_lcs(String person_title_lcs)
    {
        this.person_title_lcs = person_title_lcs;
    }

    public String getFirst_name_lcs()
    {
        return first_name_lcs;
    }

    public void setFirst_name_lcs(String first_name_lcs)
    {
        this.first_name_lcs = first_name_lcs;
    }

    public String getLast_name_lcs()
    {
        return last_name_lcs;
    }

    public void setLast_name_lcs(String last_name_lcs)
    {
        this.last_name_lcs = last_name_lcs;
    }

    public String getLocation_street_address_lcs()
    {
        return location_street_address_lcs;
    }

    public void setLocation_street_address_lcs(String location_street_address_lcs)
    {
        this.location_street_address_lcs = location_street_address_lcs;
    }

    public String getLocation_city_lcs()
    {
        return location_city_lcs;
    }

    public void setLocation_city_lcs(String location_city_lcs)
    {
        this.location_city_lcs = location_city_lcs;
    }

    public String getLocation_state_lcs()
    {
        return location_state_lcs;
    }

    public void setLocation_state_lcs(String location_state_lcs)
    {
        this.location_state_lcs = location_state_lcs;
    }

    public String getLocation_postal_code_sdo()
    {
        return location_postal_code_sdo;
    }

    public void setLocation_postal_code_sdo(String location_postal_code_sdo)
    {
        this.location_postal_code_sdo = location_postal_code_sdo;
    }

    public String getType_lcs()
    {
        return type_lcs;
    }

    public void setType_lcs(String type_lcs)
    {
        this.type_lcs = type_lcs;
    }

    public String getValue_parseable()
    {
        return value_parseable;
    }

    public void setValue_parseable(String value_parseable)
    {
        this.value_parseable = value_parseable;
    }

    public void setChild_id_s(String child_id_s)
    {
        this.child_id_s = child_id_s;
    }

    public String getChild_id_s()
    {
        return child_id_s;
    }

    public void setChild_type_s(String child_type_s)
    {
        this.child_type_s = child_type_s;
    }

    public String getChild_type_s()
    {
        return child_type_s;
    }


    public void setParent_id_s(String parent_id_s)
    {
        this.parent_id_s = parent_id_s;
    }

    public String getParent_id_s()
    {
        return parent_id_s;
    }

    public void setParent_type_s(String parent_type_s)
    {
        this.parent_type_s = parent_type_s;
    }

    public String getParent_type_s()
    {
        return parent_type_s;
    }

    public String getParent_name_t() {
        return parent_name_t;
    }

    public void setParent_name_t(String parent_name_t) {
        this.parent_name_t = parent_name_t;
    }

    public String getParent_number_lcs() {
        return parent_number_lcs;
    }

    public void setParent_number_lcs(String parent_number_lcs) {
        this.parent_number_lcs = parent_number_lcs;
    }

    public String getDescription_no_html_tags_parseable() {
        return description_no_html_tags_parseable;
    }

    public void setDescription_no_html_tags_parseable(String description_no_html_tags_parseable) {
        this.description_no_html_tags_parseable = description_no_html_tags_parseable;
    }

    public String getNotes_no_html_tags_parseable() {
        return notes_no_html_tags_parseable;
    }

    public void setNotes_no_html_tags_parseable(String notes_no_html_tags_parseable) {
        this.notes_no_html_tags_parseable = notes_no_html_tags_parseable;
    }

    public List<String> getOrganization_id_ss()
    {
        return organization_id_ss;
    }

    public void setOrganization_id_ss(List<String> organization_id_ss)
    {
        this.organization_id_ss = organization_id_ss;
    }

    public List<String> getPostal_address_id_ss()
    {
        return postal_address_id_ss;
    }

    public void setPostal_address_id_ss(List<String> postal_address_id_ss)
    {
        this.postal_address_id_ss = postal_address_id_ss;
    }

    public List<String> getContact_method_ss()
    {
        return contact_method_ss;
    }

    public void setContact_method_ss(List<String> contact_method_ss)
    {
        this.contact_method_ss = contact_method_ss;
    }

    public Date getCreate_date_tdt()
    {
        return create_date_tdt;
    }

    public void setCreate_date_tdt(Date create_date_tdt)
    {
        this.create_date_tdt = create_date_tdt;
    }

    public String getCreator_lcs()
    {
        return creator_lcs;
    }

    public void setCreator_lcs(String creator_lcs)
    {
        this.creator_lcs = creator_lcs;
    }

    public Date getModified_date_tdt()
    {
        return modified_date_tdt;
    }

    public void setModified_date_tdt(Date modified_date_tdt)
    {
        this.modified_date_tdt = modified_date_tdt;
    }

    public String getModifier_lcs()
    {
        return modifier_lcs;
    }

    public void setModifier_lcs(String modifier_lcs)
    {
        this.modifier_lcs = modifier_lcs;
    }

    public String getEmail_lcs() {
        return email_lcs;
    }

    public void setEmail_lcs(String email_lcs) {
        this.email_lcs = email_lcs;
    }

    public Date getDueDate_tdt() {
        return dueDate_tdt;
    }

    public String getDescription_parseable() {
        return description_parseable;
    }

    public void setDescription_parseable(String description_parseable) {
        this.description_parseable = description_parseable;
    }

    public void setDueDate_tdt(Date dueDate_tdt) {
        this.dueDate_tdt = dueDate_tdt;
    }

    public String getAssignee_full_name_lcs() {
        return assignee_full_name_lcs;
    }
    
    public void setAssignee_full_name_lcs(String assignee_full_name_lcs) {
        this.assignee_full_name_lcs = assignee_full_name_lcs;
    }
    
    public String getFull_name_lcs() {
        return full_name_lcs;
    }
    
    public void setFull_name_lcs(String full_name_lcs) {
        this.full_name_lcs = full_name_lcs;
    }

    public List<String> getChild_id_ss() {
		return child_id_ss;
	}

	public void setChild_id_ss(List<String> child_id_ss) {
		this.child_id_ss = child_id_ss;
	}

	public String getSupervisor_id_s() {
		return supervisor_id_s;
	}

	public void setSupervisor_id_s(String supervisor_id_s) {
		this.supervisor_id_s = supervisor_id_s;
	}

	public List<String> getMember_id_ss() {
		return member_id_ss;
	}

	public void setMember_id_ss(List<String> member_id_ss) {
		this.member_id_ss = member_id_ss;
	}

    public void setAdhocTask_b(boolean adhocTask_b)
    {
        this.adhocTask_b = adhocTask_b;
    }

    public boolean isAdhocTask_b()
    {
        return adhocTask_b;
    }

    public void setOwner_lcs(String owner_lcs)
    {
        this.owner_lcs = owner_lcs;
    }

    public String getOwner_lcs()
    {
        return owner_lcs;
    }

    public void setBusiness_process_name_lcs(String business_process_name_lcs)
    {
        this.business_process_name_lcs = business_process_name_lcs;
    }

    public String getBusiness_process_name_lcs()
    {
        return business_process_name_lcs;
    }

    public boolean isPublic_doc_b()
    {
        return public_doc_b;
    }

	public String getState_lcs() {
		return state_lcs;
	}

	public void setState_lcs(String state_lcs) {
		this.state_lcs = state_lcs;
	}

	public String getData_lcs() {
		return data_lcs;
	}

	public void setData_lcs(String data_lcs) {
		this.data_lcs = data_lcs;
	}

	public String getAction_lcs() {
		return action_lcs;
	}

	public void setAction_lcs(String action_lcs) {
		this.action_lcs = action_lcs;
	}

    public String getNotification_type_lcs()
    {
        return notification_type_lcs;
    }

    public void setNotification_type_lcs(String notification_type_lcs)
    {
        this.notification_type_lcs = notification_type_lcs;
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

    public List<String> getTags_ss() {
        return tags_ss;
    }

    public void setTags_ss(List<String> tags_ss) {
        this.tags_ss = tags_ss;
    }


    @Override
    public String toString() {
        return "SolrAdvancedSearchDocument{" +
                "id='" + id + '\'' +
                ", object_id_s='" + object_id_s + '\'' +
                ", object_type_s='" + object_type_s + '\'' +
                ", object_sub_type_s='" + object_sub_type_s + '\'' +
                ", name='" + name + '\'' +
                ", create_date_tdt=" + create_date_tdt +
                ", creator_lcs='" + creator_lcs + '\'' +
                ", modified_date_tdt=" + modified_date_tdt +
                ", modifier_lcs='" + modifier_lcs + '\'' +
                ", public_doc_b=" + public_doc_b +
                ", protected_object_b=" + protected_object_b +
                ", deny_acl_ss=" + deny_acl_ss +
                ", allow_acl_ss=" + allow_acl_ss +
                ", title_parseable='" + title_parseable + '\'' +
                ", description_parseable='" + description_parseable + '\'' +
                ", description_no_html_tags_parseable='" + description_no_html_tags_parseable + '\'' +
                ", notes_no_html_tags_parseable='" + notes_no_html_tags_parseable + '\'' +
                ", incident_date_tdt=" + incident_date_tdt +
                ", dueDate_tdt=" + dueDate_tdt +
                ", priority_lcs='" + priority_lcs + '\'' +
                ", assignee_id_lcs='" + assignee_id_lcs + '\'' +
                ", assignee_first_name_lcs='" + assignee_first_name_lcs + '\'' +
                ", assignee_last_name_lcs='" + assignee_last_name_lcs + '\'' +
                ", assignee_full_name_lcs='" + assignee_full_name_lcs + '\'' +
                ", incident_type_lcs='" + incident_type_lcs + '\'' +
                ", status_lcs='" + status_lcs + '\'' +
                ", person_title_lcs='" + person_title_lcs + '\'' +
                ", first_name_lcs='" + first_name_lcs + '\'' +
                ", last_name_lcs='" + last_name_lcs + '\'' +
                ", full_name_lcs='" + full_name_lcs + '\'' +
                ", email_lcs='" + email_lcs + '\'' +
                ", type_lcs='" + type_lcs + '\'' +
                ", value_parseable='" + value_parseable + '\'' +
                ", location_street_address_lcs='" + location_street_address_lcs + '\'' +
                ", location_city_lcs='" + location_city_lcs + '\'' +
                ", location_state_lcs='" + location_state_lcs + '\'' +
                ", location_postal_code_sdo='" + location_postal_code_sdo + '\'' +
                ", child_id_s='" + child_id_s + '\'' +
                ", child_type_s='" + child_type_s + '\'' +
                ", parent_id_s='" + parent_id_s + '\'' +
                ", parent_type_s='" + parent_type_s + '\'' +
                ", parent_name_t='" + parent_name_t + '\'' +
                ", parent_number_lcs='" + parent_number_lcs + '\'' +
                ", organization_id_ss=" + organization_id_ss +
                ", postal_address_id_ss=" + postal_address_id_ss +
                ", contact_method_ss=" + contact_method_ss +
                ", supervisor_id_s='" + supervisor_id_s + '\'' +
                ", child_id_ss=" + child_id_ss +
                ", member_id_ss=" + member_id_ss +
                ", adhocTask_b=" + adhocTask_b +
                ", owner_lcs='" + owner_lcs + '\'' +
                ", business_process_name_lcs='" + business_process_name_lcs + '\'' +
                ", content_type='" + content_type + '\'' +
                ", ecmFileId='" + ecmFileId + '\'' +
                ", tags_ss=" + tags_ss +
                ", state_lcs='" + state_lcs + '\'' +
                ", data_lcs='" + data_lcs + '\'' +
                ", action_lcs='" + action_lcs + '\'' +
                ", notification_type_lcs='" + notification_type_lcs + '\'' +
                '}';
    }
}