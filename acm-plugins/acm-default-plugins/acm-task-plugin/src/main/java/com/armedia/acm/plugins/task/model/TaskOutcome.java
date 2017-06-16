package com.armedia.acm.plugins.task.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by armdev on 11/10/14.
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class TaskOutcome implements Serializable
{
    private static final long serialVersionUID = 9212550688270421016L;

    private String name;
    private String description;
    private List<String> fieldsRequiredWhenOutcomeIsChosen = new ArrayList<>();

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

    public List<String> getFieldsRequiredWhenOutcomeIsChosen()
    {
        return fieldsRequiredWhenOutcomeIsChosen;
    }

    public void setFieldsRequiredWhenOutcomeIsChosen(List<String> fieldsRequiredWhenOutcomeIsChosen)
    {
        this.fieldsRequiredWhenOutcomeIsChosen = fieldsRequiredWhenOutcomeIsChosen;
    }
}
