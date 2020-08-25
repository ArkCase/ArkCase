package gov.privacy.pipeline.postsave;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.privacy.model.SubjectAccessRequest;
import gov.privacy.service.SARConfigurationService;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARConfigurationHandler implements PipelineHandler<SubjectAccessRequest, CaseFilePipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private SARConfigurationService SARConfigurationService;

    @Override
    public void execute(SubjectAccessRequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("SubjectAccessRequest configuration pre save handler called");

        entity.setSARConfiguration(SARConfigurationService.readConfiguration());

        log.debug("SubjectAccessRequest configuration pre save handler ended");
    }

    @Override
    public void rollback(SubjectAccessRequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do
    }

    public SARConfigurationService getSARConfigurationService()
    {
        return SARConfigurationService;
    }

    public void setSARConfigurationService(SARConfigurationService SARConfigurationService)
    {
        this.SARConfigurationService = SARConfigurationService;
    }
}
