package com.armedia.acm.plugins.phonehome.service;

/*-
 * #%L
 * ACM Plugins: Plugin Phone home
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

import com.armedia.acm.core.exceptions.AcmStateOfArkcaseGenerateReportException;
import com.armedia.acm.plugins.stateofarkcaseplugin.service.AcmStateOfArkcaseService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class PhoneHomeService
{

    private MultipartRequestGateway gateway;
    private AcmStateOfArkcaseService stateOfArkcaseService;

    public void uploadFile(File file)
    {
        Resource streamResource = new FileSystemResource(file);
        Map<String, Object> params = new HashMap<>();
        params.put("report", streamResource);
        gateway.postMultipartRequest(params);
    }

    public void generateAndUploadReportFile() throws AcmStateOfArkcaseGenerateReportException
    {
        uploadFile(stateOfArkcaseService.generateReportForDay(LocalDate.now().minus(1, ChronoUnit.DAYS)));
    }

    public void setStateOfArkcaseService(AcmStateOfArkcaseService stateOfArkcaseService)
    {
        this.stateOfArkcaseService = stateOfArkcaseService;
    }

    public void setGateway(MultipartRequestGateway gateway)
    {
        this.gateway = gateway;
    }
}
