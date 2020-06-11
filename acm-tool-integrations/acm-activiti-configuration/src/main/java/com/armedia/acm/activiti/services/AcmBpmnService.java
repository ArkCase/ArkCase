package com.armedia.acm.activiti.services;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
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

import com.armedia.acm.activiti.exceptions.AcmBpmnException;
import com.armedia.acm.activiti.model.AcmProcessDefinition;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by nebojsha on 09.04.2015.
 */
public interface AcmBpmnService
{

    public List<AcmProcessDefinition> list(String orderBy, boolean isAsc);

    public List<AcmProcessDefinition> listPage(String orderBy, boolean isAsc);

    public InputStream getBpmnFileStream(AcmProcessDefinition wfFile);

    public void makeActive(AcmProcessDefinition processDefinition);

    public void makeInactive(AcmProcessDefinition processDefinition);

    public List<AcmProcessDefinition> getVersionHistory(AcmProcessDefinition processDefinition);

    public long count();

    public AcmProcessDefinition deploy(File processDefinitionFile, String fileDescription, boolean makeActive,
            boolean deleteFileAfterDeploy);

    public AcmProcessDefinition getActive(String processDefinitionKey);

    public AcmProcessDefinition getByKeyAndVersion(String processDefinitionKey, int version);

    public byte[] getDiagram(String deploymentId, String key, Integer version) throws AcmBpmnException;

    public List<AcmProcessDefinition> listAllDeactivatedVersions();

}
