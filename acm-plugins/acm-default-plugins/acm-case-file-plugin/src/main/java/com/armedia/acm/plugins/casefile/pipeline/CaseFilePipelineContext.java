package com.armedia.acm.plugins.casefile.pipeline;

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
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.model.SaveCaseServiceCaller;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.springframework.security.core.Authentication;

/**
 * Store all the case file saving-related references in this context.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 11.08.2015.
 */
public class CaseFilePipelineContext extends AbstractPipelineContext
{
    /**
     * Flag showing whether new case file is created.
     */
    private boolean newCase;

    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    /**
     * The queue that case file is already in.
     */
    private String queueName;

    /**
     * The queue the case will be moved to; used in the queue pipeline.
     */
    private String enqueueName;

    /**
     * IP Address.
     */
    private String ipAddress;

    /*
     * Case File
     */
    private CaseFile caseFile;

    /*
     * Change Case Status
     */
    private ChangeCaseStatus changeCaseStatus;

    private SaveCaseServiceCaller caller;

    public boolean isNewCase()
    {
        return newCase;
    }

    public void setNewCase(boolean newCase)
    {
        this.newCase = newCase;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getEnqueueName()
    {
        return enqueueName;
    }

    public void setEnqueueName(String enqueueName)
    {
        this.enqueueName = enqueueName;
    }

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }

    public ChangeCaseStatus getChangeCaseStatus()
    {
        return changeCaseStatus;
    }

    public void setChangeCaseStatus(ChangeCaseStatus changeCaseStatus)
    {
        this.changeCaseStatus = changeCaseStatus;
    }

    public CaseFile getCaseFile()
    {
        return caseFile;
    }

    public void setCaseFile(CaseFile caseFile)
    {
        this.caseFile = caseFile;
    }

    public SaveCaseServiceCaller getCaller() {
        return caller;
    }

    public void setCaller(SaveCaseServiceCaller caller) {
        this.caller = caller;
    }
}
