package com.armedia.acm.services.protecturl.exception;

/**
 * Created by nebojsha on 29.07.2016.
 */
public class AcmNotAccessibleYet extends RuntimeException
{
    public AcmNotAccessibleYet(String s)
    {
        super(s);
    }
}
