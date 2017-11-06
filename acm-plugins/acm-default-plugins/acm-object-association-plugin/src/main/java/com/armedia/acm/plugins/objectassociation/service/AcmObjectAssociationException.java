package com.armedia.acm.plugins.objectassociation.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Octobery 17, 2017
 *
 */
public class AcmObjectAssociationException extends Exception
{

    private static final long serialVersionUID = 2553139314868987427L;

    public AcmObjectAssociationException(String message)
    {
        super(message);
    }

    public AcmObjectAssociationException(Throwable t)
    {
        super(t);
    }

    public AcmObjectAssociationException(String message, Throwable t)
    {
        super(message, t);
    }

}
