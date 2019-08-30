package com.armedia.acm.data.exceptions;

/*-
 * #%L
 * ACM Core API
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.List;

public class AcmAccessControlException extends Exception
{
    private List<String> listOfErrors;

    public AcmAccessControlException(List<String> listOfErrors, String message)
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
