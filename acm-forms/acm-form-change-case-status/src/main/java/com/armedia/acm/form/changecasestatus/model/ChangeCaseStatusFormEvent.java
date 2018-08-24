/**
 * 
 */
package com.armedia.acm.form.changecasestatus.model;

/*-
 * #%L
 * ACM Forms: Change Case Status
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

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class ChangeCaseStatusFormEvent extends AcmEvent
{

    private static final long serialVersionUID = 9214955996048509545L;

    private ChangeCaseStatus request;
    private UploadedFiles uploadedFiles;
    private String caseNumber;
    private Long caseId;
    private String mode;

    public ChangeCaseStatusFormEvent(String caseNumber, Long caseId, ChangeCaseStatus source,
            UploadedFiles files, String mode, String userId, String ipAddress,
            boolean succeeded)
    {
        super(source);

        setMode(mode);
        setUserId(userId);
        setEventDate(new Date());

        String event = "edit".equals(mode) ? "updated" : "created";
        setEventType("com.armedia.acm.changeCaseStatus." + event);

        setIpAddress(ipAddress);
        setObjectId(source.getId());
        setObjectType("CHANGE_CASE_STATUS");

        setSucceeded(succeeded);

        setRequest(source);
        setUploadedFiles(files);

        setCaseNumber(caseNumber);
        setCaseId(caseId);

    }

    public ChangeCaseStatus getRequest()
    {
        return request;
    }

    public void setRequest(ChangeCaseStatus request)
    {
        this.request = request;
    }

    public UploadedFiles getUploadedFiles()
    {
        return uploadedFiles;
    }

    public void setUploadedFiles(UploadedFiles uploadedFiles)
    {
        this.uploadedFiles = uploadedFiles;
    }

    public String getCaseNumber()
    {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber)
    {
        this.caseNumber = caseNumber;
    }

    public Long getCaseId()
    {
        return caseId;
    }

    public void setCaseId(Long caseId)
    {
        this.caseId = caseId;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

}
