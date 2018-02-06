package com.armedia.acm.services.email.model;

/**
 * @author sasko.tanaskoski
 *
 */
public class EmailTemplateValidationResponse
{

    private boolean validTemplate = true;

    private String objectType;

    private String action;

    private String emailPattern;

    /**
     * @return the validTemplate
     */
    public boolean isValidTemplate()
    {
        return validTemplate;
    }

    /**
     * @param validTemplate
     *            the validTemplate to set
     */
    public void setValidTemplate(boolean validTemplate)
    {
        this.validTemplate = validTemplate;
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
     * @return the action
     */
    public String getAction()
    {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(String action)
    {
        this.action = action;
    }

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

}
