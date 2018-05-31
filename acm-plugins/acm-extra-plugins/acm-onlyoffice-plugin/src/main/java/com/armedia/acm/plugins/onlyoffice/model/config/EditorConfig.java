package com.armedia.acm.plugins.onlyoffice.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EditorConfig
{
    /**
     * Specifies absolute URL to the document storage service (which must be implemented by the software integrators who
     * use ONLYOFFICE Document Server on their own server).
     */
    private String callbackUrl;
    /**
     * Defines the absolute URL of the document where it will be created and available after creation. If not specified,
     * there will be no creation button.
     */
    private String createUrl;
    /**
     * Defines the editor interface language (if some other languages other than English are present). Is set using the
     * two letter (de, ru, it, etc.) or four letter (en-US, fr-FR, etc.) language codes. The default value is "en-US".
     */
    private String lang;
    /**
     * Defines the editor opening mode. Can be either view to open the document for viewing, or edit to open the
     * document in the editing mode allowing to apply changes to the document data. The default value is "edit".
     */
    private String mode;
    /**
     * Defines the presence or absence of the documents in the Open Recent...
     */
    private List<Recent> recent;

    private User user;

    /**
     * The customization section allows to customize the editor interface so that it looked like your other products (if
     * there are any) and change the presence or absence of the additional buttons, links, change logos and editor owner
     * details.
     * .
     */
    private EditorCustomization customization;
    /**
     * The embedded section is for the embedded document type only (see the config section to find out how to define the
     * embedded document type). It allows to change the settings which define the behavior of the buttons in the
     * embedded mode.
     */
    private EditorEmbedded embedded;
    /**
     * The plugins section allows to connect the special add-ons to your Document Server installation which will help
     * you add additional features to document editors.
     */
    private EditorPlugins plugins;

    public EditorConfig(String callbackUrl)
    {
        this.callbackUrl = callbackUrl;
    }

    public EditorCustomization getCustomization()
    {
        return customization;
    }

    public void setCustomization(EditorCustomization customization)
    {
        this.customization = customization;
    }

    public EditorEmbedded getEmbedded()
    {
        return embedded;
    }

    public void setEmbedded(EditorEmbedded embedded)
    {
        this.embedded = embedded;
    }

    public EditorPlugins getPlugins()
    {
        return plugins;
    }

    public void setPlugins(EditorPlugins plugins)
    {
        this.plugins = plugins;
    }

    public void setCreateUrl(String createUrl)
    {
        this.createUrl = createUrl;
    }

    public void setLang(String lang)
    {
        this.lang = lang;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public void setRecent(List<Recent> recent)
    {
        this.recent = recent;
    }

    public String getCallbackUrl()
    {
        return callbackUrl;
    }

    public String getCreateUrl()
    {
        return createUrl;
    }

    public String getLang()
    {
        return lang;
    }

    public String getMode()
    {
        return mode;
    }

    public List<Recent> getRecent()
    {
        return recent;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
