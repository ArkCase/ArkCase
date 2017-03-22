package com.armedia.acm.plugins.admin.model;


public class OpenLdapTemplateConfig implements TemplateConfig
{
    private String userPropertiesTemplate;
    private String userPropertiesTemplateName;
    private String userFileTemplate;
    private String userFileTemplateName;

    private String groupPropertiesTemplate;
    private String groupPropertiesTemplateName;
    private String groupFileTemplate;
    private String groupFileTemplateName;

    @Override
    public String getUserPropertiesTemplate()
    {
        return userPropertiesTemplate;
    }

    public void setUserPropertiesTemplate(String userPropertiesTemplate)
    {
        this.userPropertiesTemplate = userPropertiesTemplate;
    }

    @Override
    public String getUserPropertiesTemplateName()
    {
        return userPropertiesTemplateName;
    }

    public void setUserPropertiesTemplateName(String userPropertiesTemplateName)
    {
        this.userPropertiesTemplateName = userPropertiesTemplateName;
    }

    @Override
    public String getUserFileTemplate()
    {
        return userFileTemplate;
    }

    public void setUserFileTemplate(String userFileTemplate)
    {
        this.userFileTemplate = userFileTemplate;
    }

    @Override
    public String getUserFileTemplateName()
    {
        return userFileTemplateName;
    }

    public void setUserFileTemplateName(String userFileTemplateName)
    {
        this.userFileTemplateName = userFileTemplateName;
    }

    @Override
    public String getGroupPropertiesTemplate()
    {
        return groupPropertiesTemplate;
    }

    public void setGroupPropertiesTemplate(String groupPropertiesTemplate)
    {
        this.groupPropertiesTemplate = groupPropertiesTemplate;
    }

    @Override
    public String getGroupPropertiesTemplateName()
    {
        return groupPropertiesTemplateName;
    }

    public void setGroupPropertiesTemplateName(String groupPropertiesTemplateName)
    {
        this.groupPropertiesTemplateName = groupPropertiesTemplateName;
    }

    @Override
    public String getGroupFileTemplate()
    {
        return groupFileTemplate;
    }

    public void setGroupFileTemplate(String groupFileTemplate)
    {
        this.groupFileTemplate = groupFileTemplate;
    }

    @Override
    public String getGroupFileTemplateName()
    {
        return groupFileTemplateName;
    }

    public void setGroupFileTemplateName(String groupFileTemplateName)
    {
        this.groupFileTemplateName = groupFileTemplateName;
    }
}
