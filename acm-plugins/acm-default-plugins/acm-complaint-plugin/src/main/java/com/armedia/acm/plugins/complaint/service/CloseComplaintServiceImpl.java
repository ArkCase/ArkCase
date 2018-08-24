package com.armedia.acm.plugins.complaint.service;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public class CloseComplaintServiceImpl implements CloseComplaintService
{

    private CloseComplaintRequestDao closeComplaintRequestDao;
    private PipelineManager<CloseComplaintRequest, CloseComplaintPipelineContext> pipelineManager;

    @Override
    @Transactional
    public void save(CloseComplaintRequest form, Authentication auth, String mode) throws Exception
    {
        CloseComplaintPipelineContext pipelineContext = new CloseComplaintPipelineContext();
        pipelineContext.setAuthentication(auth);
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        pipelineContext.setIpAddress(ipAddress);
        pipelineContext.addProperty("mode", mode);

        pipelineManager.executeOperation(form, pipelineContext, () -> {

            CloseComplaintRequest savedRequest = getCloseComplaintRequestDao().save(form);
            pipelineContext.setCloseComplaintRequest(savedRequest);
            return savedRequest;
        });
    }

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

    public PipelineManager<CloseComplaintRequest, CloseComplaintPipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<CloseComplaintRequest, CloseComplaintPipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }
}