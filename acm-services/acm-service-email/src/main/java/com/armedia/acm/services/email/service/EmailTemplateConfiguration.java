package com.armedia.acm.services.email.service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public class EmailTemplateConfiguration
{

    private String emailPattern;

    private String objectType;

    private EmailSource source;

    private String templateName;

    private List<String> actions = new ArrayList<>();

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
     * @return the objectType
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
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
