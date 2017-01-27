package com.armedia.acm.correspondence.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 27, 2017
 *
 */
public class TemplateLabel
{
    private String template;

    private String label;

    /**
     *
     */
    public TemplateLabel()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param templateFilename
     * @param documentType
     */
    public TemplateLabel(String template, String label)
    {
        this.template = template;
        this.label = label;
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
}
