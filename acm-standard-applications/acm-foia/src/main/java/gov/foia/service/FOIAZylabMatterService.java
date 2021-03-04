package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import javax.ws.rs.BadRequestException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.armedia.acm.tool.zylab.model.MatterDTO;
import com.armedia.acm.tool.zylab.model.ZylabIntegrationConfig;
import com.armedia.acm.tool.zylab.service.ZylabIntegrationService;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on February, 2021
 */
public class FOIAZylabMatterService
{

    private ZylabIntegrationService zylabIntegrationService;
    private ZylabIntegrationConfig zylabIntegrationConfig;
    private FOIARequestDao foiaRequestDao;

    private transient final Logger log = LogManager.getLogger(getClass());

    public FOIARequest createMatterFromRequest(Long requestId)
    {
        FOIARequest request = getFoiaRequestDao().find(requestId);
        if (request == null)
        {
            throw new BadRequestException("No request with id '" + requestId + "' found");
        }

        return createMatterFromRequest(request);
    }

    public FOIARequest createMatterFromRequest(FOIARequest request)
    {
        if (request.getExternalIdentifier() != null)
        {
            log.error("ZyLAB Matter for request [{}] already exists", request.getCaseNumber());
            throw new BadRequestException("ZyLAB Matter for request [{}] already exists");
        }

        String matterName = request.getCaseNumber();
        MatterDTO matter = getZylabIntegrationService().createMatter(matterName);

        request.setExternalIdentifier(String.valueOf(matter.getId()));
        getFoiaRequestDao().save(request);

        return request;
    }

    public ZylabIntegrationService getZylabIntegrationService()
    {
        return zylabIntegrationService;
    }

    public void setZylabIntegrationService(ZylabIntegrationService zylabIntegrationService)
    {
        this.zylabIntegrationService = zylabIntegrationService;
    }

    public ZylabIntegrationConfig getZylabIntegrationConfig()
    {
        return zylabIntegrationConfig;
    }

    public void setZylabIntegrationConfig(ZylabIntegrationConfig zylabIntegrationConfig)
    {
        this.zylabIntegrationConfig = zylabIntegrationConfig;
    }

    public FOIARequestDao getFoiaRequestDao()
    {
        return foiaRequestDao;
    }

    public void setFoiaRequestDao(FOIARequestDao foiaRequestDao)
    {
        this.foiaRequestDao = foiaRequestDao;
    }
}
