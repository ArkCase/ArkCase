package com.armedia.acm.services.search.model.solr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/21/14.
 */
public class SolrAdvancedSearchDocument implements SolrBaseDocument
{
    private String id;
    private String object_id_s;
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
    private String first_name_s;
    private String last_name_s;
    private String type_s;
    private String value_s;
    private String location_street_address_s;
    private String location_city_s;
    private String location_state_s;
    private String location_postal_code_s;
    private List<SolrBaseDocument> _childDocuments_ = new ArrayList<>();
    private String child_id_s;
    private String child_type_s;
    private String parent_id_s;
    private String parent_type_s;
    private String description_parseable;

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

    public String getLocation_street_address_s()
    {
        return location_street_address_s;
    }

    public void setLocation_street_address_s(String location_street_address_s)
    {
        this.location_street_address_s = location_street_address_s;
    }

    public String getLocation_city_s()
    {
        return location_city_s;
    }

    public void setLocation_city_s(String location_city_s)
    {
        this.location_city_s = location_city_s;
    }

    public String getLocation_state_s()
    {
        return location_state_s;
    }

    public void setLocation_state_s(String location_state_s)
    {
        this.location_state_s = location_state_s;
    }

    public String getLocation_postal_code_s()
    {
        return location_postal_code_s;
    }

    public void setLocation_postal_code_s(String location_postal_code_s)
    {
        this.location_postal_code_s = location_postal_code_s;
    }

    public String getType_s()
    {
        return type_s;
    }

    public void setType_s(String type_s)
    {
        this.type_s = type_s;
    }

    public String getValue_s()
    {
        return value_s;
    }

    public void setValue_s(String value_s)
    {
        this.value_s = value_s;
    }

    public List<SolrBaseDocument> get_childDocuments_()
    {
        return _childDocuments_;
    }

    public void set_childDocuments_(List<SolrBaseDocument> _childDocuments_)
    {
        this._childDocuments_ = _childDocuments_;
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

    public void setDescription_parseable(String description_parseable)
    {
        this.description_parseable = description_parseable;
    }

    public String getDescription_parseable()
    {
        return description_parseable;
    }
}
