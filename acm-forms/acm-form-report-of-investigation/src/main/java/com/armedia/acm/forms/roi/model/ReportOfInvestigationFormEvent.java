/**
 * 
 */
package com.armedia.acm.forms.roi.model;

/*-
 * #%L
 * ACM Forms: Report of Investigation
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
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class ReportOfInvestigationFormEvent extends AcmEvent
{

    private static final long serialVersionUID = 1192631656494031812L;
    private ROIForm request;
    private UploadedFiles uploadedFiles;
    private String mode;
    private String forObjectType;
    private String forObjectNumber;

    public ReportOfInvestigationFormEvent(String forObjectType, String forObjectNumber, String parentObjectType, Long parentObjectId,
            ROIForm source,
            UploadedFiles files, String userId, String ipAddress, boolean succeeded)
    {
        super(source);

        setMode(mode);
        setUserId(userId);
        setEventDate(new Date());

        String event = "created";
        setEventType("com.armedia.acm.reportOfInvestigation." + event);

        setIpAddress(ipAddress);
        setObjectId(files.getPdfRendition().getId());
        setObjectType(EcmFileConstants.OBJECT_FILE_TYPE);

        setSucceeded(succeeded);

        setRequest(source);
        setUploadedFiles(files);

        setForObjectNumber(forObjectNumber);
        setForObjectType(forObjectType);

        setParentObjectType(parentObjectType);
        setParentObjectId(parentObjectId);

    }

    public ROIForm getRequest()
    {
        return request;
    }

    public void setRequest(ROIForm request)
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

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getForObjectType()
    {
        return forObjectType;
    }

    public void setForObjectType(String forObjectType)
    {
        this.forObjectType = forObjectType;
    }

    public String getForObjectNumber()
    {
        return forObjectNumber;
    }

    public void setForObjectNumber(String forObjectNumber)
    {
        this.forObjectNumber = forObjectNumber;
    }
}
