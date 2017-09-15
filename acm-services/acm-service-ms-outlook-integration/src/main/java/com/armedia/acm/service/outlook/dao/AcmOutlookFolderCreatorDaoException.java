package com.armedia.acm.service.outlook.dao;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 9, 2017
 *
 */
public class AcmOutlookFolderCreatorDaoException extends Exception
{

    private static final long serialVersionUID = 8791310512650286015L;

    public AcmOutlookFolderCreatorDaoException(String message)
    {
        super(message);
    }

    public AcmOutlookFolderCreatorDaoException(Throwable t)
    {
        super(t);
    }

    public AcmOutlookFolderCreatorDaoException(String message, Throwable t)
    {
        super(message, t);
    }

}
