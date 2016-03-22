package com.armedia.acm.services.search.model.solr;

import com.armedia.acm.services.search.model.SearchConstants;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SolrDocument extends SolrAbstractDocument implements Serializable
{
    private static final long serialVersionUID = -5017291241815218652L;

    private String id;
    private String status_s;
    private String author;
    private String author_s;
    private String modifier_s;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date last_modified_tdt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date create_tdt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date due_tdt;
    private String title_t;
    private String name;
    private String object_id_s;
    private String owner_s;
    private String object_type_s;
    private String assignee_s;
    private Long priority_i;
    private String priority_s;
    private String parent_object_type_s;
    private String parent_object_id_s;
    private boolean adhocTask_b;
    private String target_object_number_s;
    private boolean public_doc_b;
    private boolean protected_object_b;

    /////////////////// for complaints, case files, other objects with a title or description ////////////
    private String title_parseable;
    private String description_parseable;

    /////////////////// for complaints, case files, tasks we introduce description and for personAssociation we introduce notes ////////////
    private String description_no_html_tags_parseable;
    private String notes_no_html_tags_parseable;

    private List<String> deny_acl_ss;
    private List<String> allow_acl_ss;
    private Long parent_object_id_i;

    //////////////////disposition id and type //////////////////////////////////////
    private String disposition_id_s;
    private String disposition_type_s;

    ////////////////// for Timesheet and other objects that have start/end date //////////////////////////////////////
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date startDate_s;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SearchConstants.SOLR_DATE_FORMAT, timezone = SearchConstants.TIME_ZONE_UTC)
    private Date endDate_s;
    private Long folder_id_i;
    private String folder_name_s;
    private String type_s;
    private String version_s;
    private String name_lcs;
    private Long parent_folder_id_i;
    private String category_s;
    private String cmis_version_series_id_s;
    private String mime_type_s;

    private boolean hidden_b;

    ///////////////for ASN /////////////////
    private String notification_type_s;

    private String parent_ref_s;

    private String data_s;


    ////for assigned tags/////////////////////
    private String tag_token_lcs;

    ////for tasks associated with business processes/////////////////////
    private String business_process_id_s;
    private String business_process_name_lcs;

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

    public String getStatus_s()
    {
        return status_s;
    }

    public void setStatus_s(String status_s)
    {
        this.status_s = status_s;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getAuthor_s()
    {
        return author_s;
    }

    public void setAuthor_s(String author_s)
    {
        this.author_s = author_s;
    }

    public String getModifier_s()
    {
        return modifier_s;
    }

    public void setModifier_s(String modifier_s)
    {
        this.modifier_s = modifier_s;
    }

    public String getTitle_t()
    {
        return title_t;
    }

    public void setTitle_t(String title_t)
    {
        this.title_t = title_t;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getObject_id_s()
    {
        return object_id_s;
    }

    public void setObject_id_s(String object_id_s)
    {
        this.object_id_s = object_id_s;
    }

    public String getOwner_s()
    {
        return owner_s;
    }

    public void setOwner_s(String owner_s)
    {
        this.owner_s = owner_s;
    }

    public String getObject_type_s()
    {
        return object_type_s;
    }

    public void setObject_type_s(String object_type_s)
    {
        this.object_type_s = object_type_s;
    }

    public String getAssignee_s()
    {
        return assignee_s;
    }

    public void setAssignee_s(String assignee_s)
    {
        this.assignee_s = assignee_s;
    }

    public Long getPriority_i()
    {
        return priority_i;
    }

    public void setPriority_i(Long priority_i)
    {
        this.priority_i = priority_i;
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

    public boolean isAdhocTask_b()
    {
        return adhocTask_b;
    }

    public void setAdhocTask_b(boolean adhocTask_b)
    {
        this.adhocTask_b = adhocTask_b;
    }

    public String getParent_object_type_s()
    {
        return parent_object_type_s;
    }

    public void setParent_object_type_s(String parent_object_type_s)
    {
        this.parent_object_type_s = parent_object_type_s;
    }

    public String getPriority_s()
    {
        return priority_s;
    }

    public void setPriority_s(String priority_s)
    {
        this.priority_s = priority_s;
    }

    public String getDescription_parseable()
    {
        return description_parseable;
    }

    public void setDescription_parseable(String description_parseable)
    {
        this.description_parseable = description_parseable;
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

    public Date getLast_modified_tdt()
    {
        return last_modified_tdt;
    }

    public void setLast_modified_tdt(Date last_modified_tdt)
    {
        this.last_modified_tdt = last_modified_tdt;
    }

    public Date getCreate_tdt()
    {
        return create_tdt;
    }

    public void setCreate_tdt(Date create_tdt)
    {
        this.create_tdt = create_tdt;
    }

    public Date getDue_tdt()
    {
        return due_tdt;
    }

    public void setDue_tdt(Date due_tdt)
    {
        this.due_tdt = due_tdt;
    }

    public String getTitle_parseable()
    {
        return title_parseable;
    }

    public void setTitle_parseable(String title_parseable)
    {
        this.title_parseable = title_parseable;
    }

    public String getDescription_no_html_tags_parseable()
    {
        return description_no_html_tags_parseable;
    }

    public void setDescription_no_html_tags_parseable(String description_no_html_tags_parseable)
    {
        this.description_no_html_tags_parseable = description_no_html_tags_parseable;
    }

    public String getNotes_no_html_tags_parseable()
    {
        return notes_no_html_tags_parseable;
    }

    public void setNotes_no_html_tags_parseable(String notes_no_html_tags_parseable)
    {
        this.notes_no_html_tags_parseable = notes_no_html_tags_parseable;
    }

    public void setParent_object_id_i(Long parent_object_id_i)
    {
        this.parent_object_id_i = parent_object_id_i;
    }

    public Long getParent_object_id_i()
    {
        return parent_object_id_i;
    }

    public String getDisposition_id_s()
    {
        return disposition_id_s;
    }

    public void setDisposition_id_s(String disposition_id_s)
    {
        this.disposition_id_s = disposition_id_s;
    }

    public String getDisposition_type_s()
    {
        return disposition_type_s;
    }

    public void setDisposition_type_s(String disposition_type_s)
    {
        this.disposition_type_s = disposition_type_s;
    }

    public String getParent_object_id_s()
    {
        return parent_object_id_s;
    }

    public void setParent_object_id_s(String parent_object_id_s)
    {
        this.parent_object_id_s = parent_object_id_s;
    }

    public String getTarget_object_number_s()
    {
        return target_object_number_s;
    }

    public void setTarget_object_number_s(String target_object_number_s)
    {
        this.target_object_number_s = target_object_number_s;
    }

    public Date getStartDate_s()
    {
        return startDate_s;
    }

    public void setStartDate_s(Date startDate_s)
    {
        this.startDate_s = startDate_s;
    }

    public Date getEndDate_s()
    {
        return endDate_s;
    }

    public void setEndDate_s(Date endDate_s)
    {
        this.endDate_s = endDate_s;
    }

    public void setFolder_id_i(Long folder_id_i)
    {
        this.folder_id_i = folder_id_i;
    }

    public Long getFolder_id_i()
    {
        return folder_id_i;
    }

    public void setFolder_name_s(String folder_name_s)
    {
        this.folder_name_s = folder_name_s;
    }

    public String getFolder_name_s()
    {
        return folder_name_s;
    }

    public void setType_s(String type_s)
    {
        this.type_s = type_s;
    }

    public String getType_s()
    {
        return type_s;
    }

    public void setVersion_s(String version_s)
    {
        this.version_s = version_s;
    }

    public String getVersion_s()
    {
        return version_s;
    }

    public void setName_lcs(String name_lcs)
    {
        this.name_lcs = name_lcs;
    }

    public String getName_lcs()
    {
        return name_lcs;
    }

    public void setParent_folder_id_i(Long parent_folder_id_i)
    {
        this.parent_folder_id_i = parent_folder_id_i;
    }

    public Long getParent_folder_id_i()
    {
        return parent_folder_id_i;
    }

    public void setCategory_s(String category_s)
    {
        this.category_s = category_s;
    }

    public String getCategory_s()
    {
        return category_s;
    }

    public void setCmis_version_series_id_s(String cmis_version_series_id_s)
    {
        this.cmis_version_series_id_s = cmis_version_series_id_s;
    }

    public String getCmis_version_series_id_s()
    {
        return cmis_version_series_id_s;
    }

    public String getMime_type_s()
    {
        return mime_type_s;
    }

    public void setMime_type_s(String mime_type_s)
    {
        this.mime_type_s = mime_type_s;
    }

    public boolean isHidden_b()
    {
        return hidden_b;
    }

    public void setHidden_b(boolean hidden_b)
    {
        this.hidden_b = hidden_b;
    }

    public String getNotification_type_s()
    {
        return notification_type_s;
    }

    public void setNotification_type_s(String notification_type_s)
    {
        this.notification_type_s = notification_type_s;
    }

    public void setParent_ref_s(String parent_ref_s)
    {
        this.parent_ref_s = parent_ref_s;
    }

    public String getParent_ref_s()
    {
        return parent_ref_s;
    }

    public String getData_s()
    {
        return data_s;
    }

    public void setData_s(String data_s)
    {
        this.data_s = data_s;
    }

    public String getTag_token_lcs()
    {
        return tag_token_lcs;
    }

    public void setTag_token_lcs(String tag_token_lcs)
    {
        this.tag_token_lcs = tag_token_lcs;
    }

    public String getBusiness_process_id_s()
    {
        return business_process_id_s;
    }

    public void setBusiness_process_id_s(String business_process_id_s)
    {
        this.business_process_id_s = business_process_id_s;
    }

    public String getBusiness_process_name_lcs()
    {
        return business_process_name_lcs;
    }

    public void setBusiness_process_name_lcs(String business_process_name_lcs)
    {
        this.business_process_name_lcs = business_process_name_lcs;
    }

    @Override
    public String toString()
    {
        return "SolrDocument{" +
                "id='" + id + '\'' +
                ", status_s='" + status_s + '\'' +
                ", author='" + author + '\'' +
                ", author_s='" + author_s + '\'' +
                ", modifier_s='" + modifier_s + '\'' +
                ", last_modified_tdt=" + last_modified_tdt +
                ", create_tdt=" + create_tdt +
                ", due_tdt=" + due_tdt +
                ", title_t='" + title_t + '\'' +
                ", name='" + name + '\'' +
                ", object_id_s='" + object_id_s + '\'' +
                ", owner_s='" + owner_s + '\'' +
                ", object_type_s='" + object_type_s + '\'' +
                ", assignee_s='" + assignee_s + '\'' +
                ", priority_i=" + priority_i +
                ", priority_s='" + priority_s + '\'' +
                ", parent_object_type_s='" + parent_object_type_s + '\'' +
                ", parent_object_id_s='" + parent_object_id_s + '\'' +
                ", adhocTask_b=" + adhocTask_b +
                ", target_object_number_s='" + target_object_number_s + '\'' +
                ", public_doc_b=" + public_doc_b +
                ", protected_object_b=" + protected_object_b +
                ", title_parseable='" + title_parseable + '\'' +
                ", description_parseable='" + description_parseable + '\'' +
                ", description_no_html_tags_parseable='" + description_no_html_tags_parseable + '\'' +
                ", notes_no_html_tags_parseable='" + notes_no_html_tags_parseable + '\'' +
                ", deny_acl_ss=" + deny_acl_ss +
                ", allow_acl_ss=" + allow_acl_ss +
                ", parent_object_id_i=" + parent_object_id_i +
                ", disposition_id_s='" + disposition_id_s + '\'' +
                ", disposition_type_s='" + disposition_type_s + '\'' +
                ", startDate_s=" + startDate_s +
                ", endDate_s=" + endDate_s +
                ", folder_id_i=" + folder_id_i +
                ", folder_name_s='" + folder_name_s + '\'' +
                ", type_s='" + type_s + '\'' +
                ", version_s='" + version_s + '\'' +
                ", name_lcs='" + name_lcs + '\'' +
                ", parent_folder_id_i=" + parent_folder_id_i +
                ", category_s='" + category_s + '\'' +
                ", cmis_version_series_id_s='" + cmis_version_series_id_s + '\'' +
                ", mime_type_s='" + mime_type_s + '\'' +
                ", hidden_b=" + hidden_b +
                ", notification_type_s='" + notification_type_s + '\'' +
                ", parent_ref_s='" + parent_ref_s + '\'' +
                ", data_s='" + data_s + '\'' +
                ", tag_token_lcs='" + tag_token_lcs + '\'' +
                ", business_process_name_lcs='" + business_process_name_lcs + '\'' +
                ", business_process_id_s='" + business_process_id_s + '\'' +
                '}';
    }
}
