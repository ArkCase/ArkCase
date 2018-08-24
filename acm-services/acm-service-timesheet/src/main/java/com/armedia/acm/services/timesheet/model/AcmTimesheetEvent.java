/**
 * 
 */
package com.armedia.acm.services.timesheet.model;

/*-
 * #%L
 * ACM Service: Timesheet
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

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class AcmTimesheetEvent extends AcmEvent
{

    private static final long serialVersionUID = 7323464693900974967L;

    private UploadedFiles uploadedFiles;
    private boolean startWorkflow;

    public AcmTimesheetEvent(AcmTimesheet source, String userId, String ipAddress, boolean succeeded, String type,
            UploadedFiles uploadedFiles, boolean startWorkflow)
    {
        super(source);

        setObjectId(source.getId());
        setObjectType(TimesheetConstants.OBJECT_TYPE);
        setUserId(userId);
        setIpAddress(ipAddress);
        setSucceeded(succeeded);
        setEventDate(new Date());
        setEventType(TimesheetConstants.EVENT_TYPE + "." + type);

        setUploadedFiles(uploadedFiles);
        setStartWorkflow(startWorkflow);
    }

    public UploadedFiles getUploadedFiles()
    {
        return uploadedFiles;
    }

    public void setUploadedFiles(UploadedFiles uploadedFiles)
    {
        this.uploadedFiles = uploadedFiles;
    }

    public boolean isStartWorkflow()
    {
        return startWorkflow;
    }

    public void setStartWorkflow(boolean startWorkflow)
    {
        this.startWorkflow = startWorkflow;
    }
}
