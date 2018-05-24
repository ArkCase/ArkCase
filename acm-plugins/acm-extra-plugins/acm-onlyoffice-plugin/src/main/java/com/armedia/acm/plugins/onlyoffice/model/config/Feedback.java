package com.armedia.acm.plugins.onlyoffice.model.config;

public class Feedback
{
    /**
     * the absolute URL to the website address which will be opened when clicking the Feedback & Support menu button
     */
    private String url;
    /**
     * show or hide the Feedback & Support menu button
     */
    private boolean visible;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }
}
