package com.armedia.acm.form.closecomplaint.model;

/*-
 * #%L
 * ACM Forms: Close Complaint
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
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

import java.util.Date;

/**
 * Created by armdev on 11/5/14.
 */
public class CloseComplaintFormEvent extends AcmEvent
{
    private CloseComplaintRequest request;
    private UploadedFiles uploadedFiles;
    private String complaintNumber;
    private Long complaintId;
    private String mode;

    public CloseComplaintFormEvent(String complaintNumber, Long complaintId, CloseComplaintRequest source,
            UploadedFiles files, String mode, String user, String ipAddress,
            boolean succeeded)
    {
        super(source);
        setMode(mode);

        setUserId(user);
        setEventDate(new Date());

        String event = "edit".equals(mode) ? "updated" : "created";
        setEventType("com.armedia.acm.closeComplaintRequest." + event);

        setIpAddress(ipAddress);
        setObjectId(source.getId());
        setObjectType("CLOSE_COMPLAINT_REQUEST");

        setSucceeded(succeeded);

        setRequest(source);
        setUploadedFiles(files);

        setComplaintNumber(complaintNumber);
        setComplaintId(complaintId);
    }

    public CloseComplaintRequest getRequest()
    {
        return request;
    }

    public void setRequest(CloseComplaintRequest request)
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

    public String getComplaintNumber()
    {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber)
    {
        this.complaintNumber = complaintNumber;
    }

    public Long getComplaintId()
    {
        return complaintId;
    }

    public void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
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
