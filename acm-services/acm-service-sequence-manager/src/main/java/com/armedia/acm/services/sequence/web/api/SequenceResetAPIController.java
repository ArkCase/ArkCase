package com.armedia.acm.services.sequence.web.api;

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
import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;
import com.armedia.acm.services.sequence.service.AcmSequenceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */

@Controller
@RequestMapping({ "/api/v1/plugin/sequence", "/api/latest/plugin/sequence" })
public class SequenceResetAPIController
{

    private AcmSequenceService sequenceService;

    @RequestMapping(value = "/reset", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmSequenceReset> getSequenceReset(@RequestParam(value = "sequenceName", required = true) String sequenceName,
            @RequestParam(value = "sequencePartName", required = true) String sequencePartName, Authentication authentication,
            HttpSession httpSession) throws AcmSequenceException
    {
        return getSequenceService().getSequenceResetList(sequenceName, sequencePartName);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmSequenceReset addSequenceReset(@RequestBody AcmSequenceReset sequenceReset,
            Authentication authentication, HttpSession httpSession)
            throws AcmSequenceException
    {
        return getSequenceService().saveSequenceReset(sequenceReset);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmSequenceReset updateSequenceReset(@RequestBody AcmSequenceReset sequenceReset,
            Authentication authentication, HttpSession httpSession)
            throws AcmSequenceException
    {
        return getSequenceService().saveSequenceReset(sequenceReset);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteSequenceReset(@RequestBody AcmSequenceReset sequenceReset,
            Authentication authentication, HttpSession httpSession)
            throws AcmSequenceException
    {
        getSequenceService().deleteSequenceReset(sequenceReset);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * @return the sequenceService
     */
    public AcmSequenceService getSequenceService()
    {
        return sequenceService;
    }

    /**
     * @param sequenceService
     *            the sequenceService to set
     */
    public void setSequenceService(AcmSequenceService sequenceService)
    {
        this.sequenceService = sequenceService;
    }

}
