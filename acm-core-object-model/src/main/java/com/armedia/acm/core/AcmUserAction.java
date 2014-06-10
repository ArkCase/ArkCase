package com.armedia.acm.core;

import java.io.Serializable;

/**
 *
 */
public class AcmUserAction extends AcmAction implements Serializable
{
    private static final long serialVersionUID = -6032898147025493335L;
    private String url;
    private String iconName;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getIconName()
    {
        return iconName;
    }

    public void setIconName(String iconName)
    {
        this.iconName = iconName;
    }
}
