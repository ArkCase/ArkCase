package com.armedia.acm.plugins.casefile.service;

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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SaveCaseServiceCaller;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 4/8/15.
 */
public interface SaveCaseService
{
    @Transactional
    CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress) throws PipelineProcessException;

    /**
     * save casefile data
     *
     * @param casefile
     *            casefile data
     * @param files
     *            casefile files
     * @param authentication
     *            authentication
     * @param ipAdress
     *            ipAddress
     * @return CaseFile saved casefile
     */
    @Transactional
    CaseFile saveCase(CaseFile casefile, Map<String, List<MultipartFile>> filesMap, Authentication authentication, String ipAddress)
            throws AcmUserActionFailedException,
            AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmObjectNotFoundException, PipelineProcessException,
            IOException;

    @Transactional
    default CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress, SaveCaseServiceCaller caller) throws PipelineProcessException
    {
        return null;
    }
}
