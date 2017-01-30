package com.armedia.acm.core.exceptions;


import java.util.List;

public class AccsessControlException extends Exception
{
    private List<String> listOfErrors;

    public AccsessControlException(String message, Throwable cause, List<String> listOfErrors)
    {
        super(message, cause);
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
        String message = "";
        if (getListOfErrors() != null)
        {
            message += "Encountered " + getListOfErrors() + " as a list of errors.\n";
        }
        if (super.getMessage() != null)
        {
            message += "Server encountered exception: " + super.getMessage() + "\n";
        }

        message += "Exception type was: '" + getClass().getName() + "'.";

        return message;
    }

}
