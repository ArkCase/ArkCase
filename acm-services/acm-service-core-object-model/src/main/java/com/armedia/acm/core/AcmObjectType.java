package com.armedia.acm.core;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

/**
 * Created by armdev on 7/7/14.
 */
public class AcmObjectType
{
    private String name;
    private String description;
    private String url;
    private String urlEnd;
    private String iconName;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlEnd() {
        return urlEnd;
    }

    public void setUrlEnd(String urlEnd) {
        this.urlEnd = urlEnd;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    @JsonIgnore
    public List<AcmObjectState> getStates()
    {
        return states;
    }

    public void setStates(List<AcmObjectState> states)
    {
        this.states = states;
    }

    @JsonIgnore
    public List<AcmParticipantType> getParticipantTypes()
    {
        return participantTypes;
    }

    public void setParticipantTypes(List<AcmParticipantType> participantTypes)
    {
        this.participantTypes = participantTypes;
    }
}
