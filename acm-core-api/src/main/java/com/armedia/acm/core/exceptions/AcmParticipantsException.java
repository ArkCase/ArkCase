package com.armedia.acm.core.exceptions;

import java.util.List;

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
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(super.getMessage());
        getListOfErrors().stream().forEach(error -> errorMessage.append("\n[" + error + "]"));
        return errorMessage.toString();
    }

}
