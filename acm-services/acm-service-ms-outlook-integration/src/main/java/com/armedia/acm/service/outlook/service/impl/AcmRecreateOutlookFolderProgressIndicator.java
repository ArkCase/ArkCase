package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.data.AcmProgressIndicator;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 24, 2017
 *
 */
public class AcmRecreateOutlookFolderProgressIndicator extends AcmProgressIndicator
{
    private int progressFailed;

    /**
     * @return the progressFailed
     */
    public int getProgressFailed()
    {
        return progressFailed;
    }

    /**
     * @param progressFailed
     *            the progressFailed to set
     */
    public void setProgressFailed(int progressFailed)
    {
        this.progressFailed = progressFailed;
    }

}
