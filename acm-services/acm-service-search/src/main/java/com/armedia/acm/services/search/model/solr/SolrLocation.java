package com.armedia.acm.services.search.model.solr;

/**
 * Created by armdev on 10/21/14.
 */
public class SolrLocation
{
    private String location_street_address_s;
    private String location_city_s;
    private String location_state_s;
    private String location_postal_code_s;
    private String id;
    private String object_type_s = "LOCATION";

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
}
