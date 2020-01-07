package com.armedia.acm.services.mediaengine.pipeline;

/*-
 * #%L
 * ACM Service: MediaEngine
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

import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEnginePipelineContext extends AbstractPipelineContext
{
    private EcmFileVersion ecmFileVersion;
    private MediaEngineType type;
    private String processId;
    private String serviceName;

    public EcmFileVersion getEcmFileVersion()
    {
        return ecmFileVersion;
    }

    public void setEcmFileVersion(EcmFileVersion ecmFileVersion)
    {
        this.ecmFileVersion = ecmFileVersion;
    }

    public MediaEngineType getType()
    {
        return type;
    }

    public void setType(MediaEngineType type)
    {
        this.type = type;
    }

    public String getProcessId()
    {
        return processId;
    }

    public void setProcessId(String processId)
    {
        this.processId = processId;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }
}
