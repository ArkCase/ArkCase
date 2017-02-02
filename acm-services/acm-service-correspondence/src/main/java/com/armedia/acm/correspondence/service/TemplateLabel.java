package com.armedia.acm.correspondence.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 27, 2017
 *
 */
public class TemplateLabel
{
    private String template;

    private String label;

    private boolean activated;

    public TemplateLabel()
    {
    }

    /**
     * @param templateFilename
     * @param documentType
     */
    public TemplateLabel(String template, String label, boolean activated)
    {
        this.template = template;
        this.label = label;
        this.activated = activated;
    }

    /**
     * @return the template
     */
    public String getTemplate()
    {
        return template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(String template)
    {
        this.template = template;
    }

    /**
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * @return the activated
     */
    public boolean isActivated()
    {
        return activated;
    }

    /**
     * @param activated the activated to set
     */
    public void setActivated(boolean activated)
    {
        this.activated = activated;
    }
}
