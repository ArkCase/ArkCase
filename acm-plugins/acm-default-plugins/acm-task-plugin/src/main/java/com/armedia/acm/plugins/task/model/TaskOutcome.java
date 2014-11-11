package com.armedia.acm.plugins.task.model;

import java.util.List;

/**
 * Created by armdev on 11/10/14.
 */
public class TaskOutcome
{
    private String name;
    private String description;
    private List<String> fieldsRequiredWhenOutcomeIsChosen;

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
