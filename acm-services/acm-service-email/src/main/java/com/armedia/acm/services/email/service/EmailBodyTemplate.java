package com.armedia.acm.services.email.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
public class EmailBodyTemplate
{

    private String emailPattern;

    private String objectType;

    private EmailSource source;

    private String template;

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
     * @return the template
     */
    public String getTemplate()
    {
        return template;
    }

    /**
     * @param template
     *            the template to set
     */
    public void setTemplate(String template)
    {
        this.template = template;
    }

}
