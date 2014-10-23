package com.armedia.acm.services.search.model.solr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/21/14.
 */
public class SolrAdvancedSearchDocument
{
    private String id;
    private String object_type_s;
    private String title_parseable;
    private String name;
    private Date incident_date_tdt;
    private String priority_s;
    private String assignee_id_s;
    private String assignee_first_name_s;
    private String assignee_last_name_s;
    private String incident_type_s;
    private String status_s;
    private String person_title_s;
    private String person_type_s;
    private Long person_id_i;
    private String first_name_s;
    private String last_name_s;
    private List<String> phone_numbers = new ArrayList<>();
    private List<String> email_address_ss = new ArrayList<>();
    private List<String> organizations_ss = new ArrayList<>();
    private List<SolrLocation> _childDocuments_ = new ArrayList<>();

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getObject_type_s()
    {
        return object_type_s;
    }

    public void setObject_type_s(String object_type_s)
    {
        this.object_type_s = object_type_s;
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

    public String getPriority_s()
    {
        return priority_s;
    }

    public void setPriority_s(String priority_s)
    {
        this.priority_s = priority_s;
    }

    public String getAssignee_id_s()
    {
        return assignee_id_s;
    }

    public void setAssignee_id_s(String assignee_id_s)
    {
        this.assignee_id_s = assignee_id_s;
    }

    public String getAssignee_first_name_s()
    {
        return assignee_first_name_s;
    }

    public void setAssignee_first_name_s(String assignee_first_name_s)
    {
        this.assignee_first_name_s = assignee_first_name_s;
    }

    public String getAssignee_last_name_s()
    {
        return assignee_last_name_s;
    }

    public void setAssignee_last_name_s(String assignee_last_name_s)
    {
        this.assignee_last_name_s = assignee_last_name_s;
    }

    public String getIncident_type_s()
    {
        return incident_type_s;
    }

    public void setIncident_type_s(String incident_type_s)
    {
        this.incident_type_s = incident_type_s;
    }

    public String getStatus_s()
    {
        return status_s;
    }

    public void setStatus_s(String status_s)
    {
        this.status_s = status_s;
    }

    public String getPerson_title_s()
    {
        return person_title_s;
    }

    public void setPerson_title_s(String person_title_s)
    {
        this.person_title_s = person_title_s;
    }

    public String getPerson_type_s()
    {
        return person_type_s;
    }

    public void setPerson_type_s(String person_type_s)
    {
        this.person_type_s = person_type_s;
    }

    public String getFirst_name_s()
    {
        return first_name_s;
    }

    public void setFirst_name_s(String first_name_s)
    {
        this.first_name_s = first_name_s;
    }

    public String getLast_name_s()
    {
        return last_name_s;
    }

    public void setLast_name_s(String last_name_s)
    {
        this.last_name_s = last_name_s;
    }

    public List<String> getPhone_numbers()
    {
        return phone_numbers;
    }

    public void setPhone_numbers(List<String> phone_numbers)
    {
        this.phone_numbers = phone_numbers;
    }

    public List<String> getEmail_address_ss()
    {
        return email_address_ss;
    }

    public void setEmail_address_ss(List<String> email_address_ss)
    {
        this.email_address_ss = email_address_ss;
    }

    public List<String> getOrganizations_ss()
    {
        return organizations_ss;
    }

    public void setOrganizations_ss(List<String> organizations_ss)
    {
        this.organizations_ss = organizations_ss;
    }

    public List<SolrLocation> get_childDocuments_()
    {
        return _childDocuments_;
    }

    public void set_childDocuments_(List<SolrLocation> _childDocuments_)
    {
        this._childDocuments_ = _childDocuments_;
    }

    public Long getPerson_id_i()
    {
        return person_id_i;
    }

    public void setPerson_id_i(Long person_id_i)
    {
        this.person_id_i = person_id_i;
    }
}
