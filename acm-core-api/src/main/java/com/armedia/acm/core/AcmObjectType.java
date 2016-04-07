package com.armedia.acm.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 7/7/14.
 */
public class AcmObjectType implements Serializable
{
    private static final long serialVersionUID = 5721242123910985279L;

    private String name;
    private String description;
    private Map<String, String> url;
    private String urlEnd;
    private String iconName;
    private String urlContainerComplaint;
    private String urlContainerCase;
    private List<AcmObjectState> states;
    private List<AcmParticipantType> participantTypes;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Map<String, String> getUrl()
    {
        return url;
    }

    public void setUrl(Map<String, String> url)
    {
        this.url = url;
    }

    public String getUrlEnd()
    {
        return urlEnd;
    }

    public void setUrlEnd(String urlEnd)
    {
        this.urlEnd = urlEnd;
    }

    public String getIconName()
    {
        return iconName;
    }

    public void setIconName(String iconName)
    {
        this.iconName = iconName;
    }

    public String getUrlContainerComplaint()
    {
        return urlContainerComplaint;
    }

    public void setUrlContainerComplaint(String urlContainerComplaint)
    {
        this.urlContainerComplaint = urlContainerComplaint;
    }

    public String getUrlContainerCase()
    {
        return urlContainerCase;
    }

    public void setUrlContainerCase(String urlContainerCase)
    {
        this.urlContainerCase = urlContainerCase;
    }

    // @JsonIgnore
    public List<AcmObjectState> getStates()
    {
        return states;
    }

    public void setStates(List<AcmObjectState> states)
    {
        this.states = states;
    }

    // @JsonIgnore
    public List<AcmParticipantType> getParticipantTypes()
    {
        return participantTypes;
    }

    public void setParticipantTypes(List<AcmParticipantType> participantTypes)
    {
        this.participantTypes = participantTypes;
    }
}
