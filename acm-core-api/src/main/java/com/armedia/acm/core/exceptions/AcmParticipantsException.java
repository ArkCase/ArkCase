package com.armedia.acm.core.exceptions;

import java.util.List;
import java.util.stream.Collectors;

public class AcmParticipantsException extends Exception
{
    private List<String> listOfErrors;

    public AcmParticipantsException(List<String> listOfErrors, String message)
    {
        super(message);
        this.listOfErrors = listOfErrors;
    }

    public List<String> getListOfErrors()
    {
        return listOfErrors;
    }

    public void setListOfErrors(List<String> listOfErrors)
    {
        this.listOfErrors = listOfErrors;
    }

    @Override
    public String getMessage()
    {
        return getListOfErrors().stream().map(error -> "[" + error + "]").collect(Collectors.joining("\n", super.getMessage() + "\n", ""));
    }
}
