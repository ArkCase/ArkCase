package com.armedia.acm.calendar.service.integration.exchange.web.api;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 31, 2017
 *
 */
public class RecreateFoldersCallbackPayload
{

    private String message;

    private String data;

    public RecreateFoldersCallbackPayload()
    {
    }

    public RecreateFoldersCallbackPayload(String message, String data)
    {
        this.message = message;
        this.data = data;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return the data
     */
    public String getData()
    {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(String data)
    {
        this.data = data;
    }

}
