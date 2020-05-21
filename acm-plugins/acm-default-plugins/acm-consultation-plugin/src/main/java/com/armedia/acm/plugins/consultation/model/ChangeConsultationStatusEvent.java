package com.armedia.acm.plugins.consultation.model;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.frevvo.model.UploadedFiles;

import java.util.Date;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ChangeConsultationStatusEvent extends AcmEvent
{
    private static final long serialVersionUID = 9214955996048509545L;

    private ChangeConsultationStatus request;
    private UploadedFiles uploadedFiles;
    private String consultationNumber;
    private Long consultationId;
    private String mode;

    public ChangeConsultationStatusEvent(String consultationNumber, Long consultationId, ChangeConsultationStatus source,
                                         UploadedFiles files, String mode, String userId, String ipAddress,
                                         boolean succeeded)
    {
        super(source);

        setMode(mode);
        setUserId(userId);
        setEventDate(new Date());

        String event = "edit".equals(mode) ? "updated" : "created";
        setEventType("com.armedia.acm.changeConsultationStatus." + event);

        setIpAddress(ipAddress);
        setObjectId(source.getId());
        setObjectType("CHANGE_CONSULTATION_STATUS");

        setSucceeded(succeeded);

        setRequest(source);
        setUploadedFiles(files);

        setConsultationNumber(consultationNumber);
        setConsultationId(consultationId);
    }

    public ChangeConsultationStatus getRequest()
    {
        return request;
    }

    public void setRequest(ChangeConsultationStatus request)
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

    public String getConsultationNumber() {
        return consultationNumber;
    }

    public void setConsultationNumber(String consultationNumber) {
        this.consultationNumber = consultationNumber;
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Long consultationId) {
        this.consultationId = consultationId;
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
