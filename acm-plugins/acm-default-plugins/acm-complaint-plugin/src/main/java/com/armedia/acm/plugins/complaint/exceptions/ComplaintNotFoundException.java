package com.armedia.acm.plugins.complaint.exceptions;

public class ComplaintNotFoundException extends RuntimeException
{
    public ComplaintNotFoundException(String s)
    {
        super(s);
    }
}