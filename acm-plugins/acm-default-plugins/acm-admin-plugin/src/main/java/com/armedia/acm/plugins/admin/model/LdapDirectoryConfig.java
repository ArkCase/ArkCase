package com.armedia.acm.plugins.admin.model;


import java.util.Properties;

public class LdapDirectoryConfig
{
    private String ldapConfigurationLocation;
    private String ldapFile;
    private String ldapPropertiesFile;
    private String ldapConfigurationTemplatesLocation;
    private String ldapTemplateFile;
    private String ldapTemplatePropertiesFile;
    private String ldapPropertiesFileRegex;

    private Properties ldapUserPropertiesFile;
    private String ldapUserPropertiesFileRegex;
    private String ldapUserFileRegex;

    private Properties ldapGroupPropertiesFile;
    private String ldapGroupPropertiesFileRegex;
    private String ldapGroupFileRegex;

    public String getLdapConfigurationLocation()
    {
        return ldapConfigurationLocation;
    }

    public void setLdapConfigurationLocation(String ldapConfigurationLocation)
    {
        this.ldapConfigurationLocation = ldapConfigurationLocation;
    }

    public String getLdapFile()
    {
        return ldapFile;
    }

    public void setLdapFile(String ldapFile)
    {
        this.ldapFile = ldapFile;
    }

    public String getLdapPropertiesFile()
    {
        return ldapPropertiesFile;
    }

    public void setLdapPropertiesFile(String ldapPropertiesFile)
    {
        this.ldapPropertiesFile = ldapPropertiesFile;
    }

    public String getLdapConfigurationTemplatesLocation()
    {
        return ldapConfigurationTemplatesLocation;
    }

    public void setLdapConfigurationTemplatesLocation(String ldapConfigurationTemplatesLocation)
    {
        this.ldapConfigurationTemplatesLocation = ldapConfigurationTemplatesLocation;
    }

    public String getLdapTemplateFile()
    {
        return ldapTemplateFile;
    }

    public void setLdapTemplateFile(String ldapTemplateFile)
    {
        this.ldapTemplateFile = ldapTemplateFile;
    }

    public String getLdapTemplatePropertiesFile()
    {
        return ldapTemplatePropertiesFile;
    }

    public void setLdapTemplatePropertiesFile(String ldapTemplatePropertiesFile)
    {
        this.ldapTemplatePropertiesFile = ldapTemplatePropertiesFile;
    }

    public String getLdapPropertiesFileRegex()
    {
        return ldapPropertiesFileRegex;
    }

    public void setLdapPropertiesFileRegex(String ldapPropertiesFileRegex)
    {
        this.ldapPropertiesFileRegex = ldapPropertiesFileRegex;
    }

    public Properties getLdapUserPropertiesFile()
    {
        return ldapUserPropertiesFile;
    }

    public void setLdapUserPropertiesFile(Properties ldapUserPropertiesFile)
    {
        this.ldapUserPropertiesFile = ldapUserPropertiesFile;
    }

    public String getLdapUserPropertiesFileRegex()
    {
        return ldapUserPropertiesFileRegex;
    }

    public void setLdapUserPropertiesFileRegex(String ldapUserPropertiesFileRegex)
    {
        this.ldapUserPropertiesFileRegex = ldapUserPropertiesFileRegex;
    }

    public String getLdapUserFileRegex()
    {
        return ldapUserFileRegex;
    }

    public void setLdapUserFileRegex(String ldapUserFileRegex)
    {
        this.ldapUserFileRegex = ldapUserFileRegex;
    }

    public Properties getLdapGroupPropertiesFile()
    {
        return ldapGroupPropertiesFile;
    }

    public void setLdapGroupPropertiesFile(Properties ldapGroupPropertiesFile)
    {
        this.ldapGroupPropertiesFile = ldapGroupPropertiesFile;
    }

    public String getLdapGroupPropertiesFileRegex()
    {
        return ldapGroupPropertiesFileRegex;
    }

    public void setLdapGroupPropertiesFileRegex(String ldapGroupPropertiesFileRegex)
    {
        this.ldapGroupPropertiesFileRegex = ldapGroupPropertiesFileRegex;
    }

    public String getLdapGroupFileRegex()
    {
        return ldapGroupFileRegex;
    }

    public void setLdapGroupFileRegex(String ldapGroupFileRegex)
    {
        this.ldapGroupFileRegex = ldapGroupFileRegex;
    }
}
