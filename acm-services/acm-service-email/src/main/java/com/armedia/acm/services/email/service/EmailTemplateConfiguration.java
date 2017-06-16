package com.armedia.acm.services.email.service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public class EmailTemplateConfiguration
{

    /**
     * {@link #emailPattern} should be a valid java.util.regex.Pattern pattern
     * 
     * @see Pattern
     */
    private String emailPattern;

    private List<String> objectTypes;

    private EmailSource source;

    private String templateName;

    private List<String> actions;

    /**
     * @return the emailPattern
     */
    public String getEmailPattern()
    {
        return emailPattern;
    }

    /**
     * @param emailPattern
     *            the emailPattern to set
     */
    public void setEmailPattern(String emailPattern)
    {
        this.emailPattern = emailPattern;
    }

    /**
     * @return the objectTypes
     */
    public List<String> getObjectTypes()
    {
        return objectTypes;
    }

    /**
     * @param objectTypes
     *            the objectTypes to set
     */
    public void setObjectTypes(List<String> objectTypes)
    {
        this.objectTypes = objectTypes;
    }

    /**
     * @return the source
     */
    public EmailSource getSource()
    {
        return source;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(EmailSource source)
    {
        this.source = source;
    }

    /**
     * @return the templateName
     */
    public String getTemplateName()
    {
        return templateName;
    }

    /**
     * @param templateName
     *            the templateName to set
     */
    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    /**
     * @return the actions
     */
    public List<String> getActions()
    {
        return actions;
    }

    /**
     * @param actions
     *            the actions to set
     */
    public void setActions(List<String> actions)
    {
        this.actions = actions;
    }

}
