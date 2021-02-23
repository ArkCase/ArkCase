package com.armedia.acm.plugins.casefile.web.api;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.plugins.casefile.model.CaseFile;

import java.util.ArrayList;
import java.util.List;

public class CaseFileEnqueueResponse
{

    private final boolean success;
    private final ErrorReason reason;
    private final List<String> errors;
    private final String requestedQueue;

    // We have the caseId from the CaseFile instance.
    // private final Long caseId;
    private CaseFile caseFile;

    public CaseFileEnqueueResponse(ErrorReason reason, String requestedQueue, CaseFile caseFile)
    {
        success = ErrorReason.NO_ERROR.equals(reason);
        this.reason = reason;
        errors = new ArrayList<>();
        this.requestedQueue = requestedQueue;
        this.caseFile = caseFile;
    }

    public CaseFileEnqueueResponse(ErrorReason reason, List<String> errors, String requestedQueue, CaseFile caseFile)
    {
        success = ErrorReason.NO_ERROR.equals(reason);
        this.reason = reason;
        this.errors = errors;
        this.requestedQueue = requestedQueue;
        this.caseFile = caseFile;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public ErrorReason getReason()
    {
        return reason;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public String getRequestedQueue()
    {
        return requestedQueue;
    }

    // public Long getCaseId()
    // {
    // return caseId;
    // }

    public CaseFile getCaseFile()
    {
        return caseFile;
    }

    public void setCaseFile(CaseFile caseFile)
    {
        this.caseFile = caseFile;
    }

    public enum ErrorReason
    {
        NO_ERROR, LEAVE, NEXT_POSSIBLE, ENTER, ON_LEAVE, ON_ENTER
    }
}
