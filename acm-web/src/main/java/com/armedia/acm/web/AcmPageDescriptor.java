package com.armedia.acm.web;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: jwu
 * Date: 5/26/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */

public class AcmPageDescriptor implements Serializable
{
    private static final long serialVersionUID = -1;

    private String title;
    private String descShort;
    private String descLong;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescShort() {
        return descShort;
    }

    public void setDescShort(String descShort) {
        this.descShort = descShort;
    }

    public String getDescLong() {
        return descLong;
    }

    public void setDescLong(String descLong) {
        this.descLong = descLong;
    }
}
