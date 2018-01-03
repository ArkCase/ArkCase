package com.armedia.acm.core.exceptions;

import java.util.List;
import java.util.stream.Collectors;

public class AcmParticipantsException extends Exception
{
    private List<String> errors;

    public AcmParticipantsException(List<String> errors, String message)
    {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public void setErrors(List<String> listOfErrors)
    {
        this.errors = listOfErrors;
    }

    @Override
    public String getMessage()
    {
        return getErrors().stream().map(error -> "[" + error + "]").collect(Collectors.joining("\n", super.getMessage() + "\n", ""));
    }
}
