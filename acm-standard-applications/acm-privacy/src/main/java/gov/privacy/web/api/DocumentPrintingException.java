package gov.privacy.web.api;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception thrown by <code>PrintDocumentsAPIController</code> if there was an error while processing the requested
 * Subject Access Requests.
 *
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error while printing documents.")
public class DocumentPrintingException extends RuntimeException
{

    private static final long serialVersionUID = -2628029472658230302L;

    public DocumentPrintingException()
    {
    }

    public DocumentPrintingException(Throwable t)
    {
        super(t);
    }

    /**
     * @param message
     */
    public DocumentPrintingException(String message)
    {
        super(message);
    }

}
