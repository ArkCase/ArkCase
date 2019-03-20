package com.armedia.acm.services.sequence.web.api;

import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;
import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.service.AcmSequenceConfigurationService;
import com.armedia.acm.services.sequence.service.AcmSequenceService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.FlushModeType;
import javax.servlet.http.HttpSession;
import java.util.List;

/*-
 * #%L
 * ACM Service: Sequence Manager
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
/**
 * @author sasko.tanaskoski
 *
 */

@Controller
@RequestMapping({ "/api/v1/plugin/sequence", "/api/latest/plugin/sequence" })
public class SequenceConfigurationAPIController
{

    private AcmSequenceConfigurationService sequenceConfigurationService;
    private AcmSequenceService sequenceService;

    @RequestMapping(value = "/configuration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmSequenceConfiguration> getSequenceConfiguration(
            Authentication authentication, HttpSession httpSession)
            throws AcmSequenceException
    {
        return getSequenceConfigurationService().getSequenceConfiguration();
    }

    @RequestMapping(value = "/configuration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmSequenceConfiguration> addSequenceConfiguration(@RequestBody List<AcmSequenceConfiguration> sequenceConfigurationList,
            Authentication authentication, HttpSession httpSession)
            throws AcmSequenceException
    {
        return getSequenceConfigurationService().saveSequenceConfiguration(sequenceConfigurationList);
    }

    @RequestMapping(value = "/configuration", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmSequenceConfiguration> updateSequenceConfiguration(@RequestBody List<AcmSequenceConfiguration> sequenceConfigurationList,
            Authentication authentication, HttpSession httpSession)
            throws AcmSequenceException
    {
        return getSequenceConfigurationService().saveSequenceConfiguration(sequenceConfigurationList);
    }

    @RequestMapping(value = "/configuration/updateSequenceNumber", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmSequenceEntity updateSequenceNumber(@RequestBody AcmSequenceEntity acmSequenceEntity) throws AcmSequenceException
    {
        return getSequenceService().updateSequenceEntity(acmSequenceEntity);
    }

    @RequestMapping(value = "configuration/getSequence", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmSequenceEntity getSequenceEntity(@RequestParam(value = "sequenceName", required = true) String sequenceName,
                                               @RequestParam(value = "sequencePartName", required = true) String sequencePartName) throws AcmSequenceException
    {
        return getSequenceService().getSequenceEntity(sequenceName, sequencePartName, FlushModeType.AUTO);
    }

    /**
     * @return the sequenceConfigurationService
     */
    public AcmSequenceConfigurationService getSequenceConfigurationService()
    {
        return sequenceConfigurationService;
    }

    /**
     * @param sequenceConfigurationService
     *            the sequenceConfigurationService to set
     */
    public void setSequenceConfigurationService(AcmSequenceConfigurationService sequenceConfigurationService)
    {
        this.sequenceConfigurationService = sequenceConfigurationService;
    }

    public AcmSequenceService getSequenceService() {
        return sequenceService;
    }

    public void setSequenceService(AcmSequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }
}
