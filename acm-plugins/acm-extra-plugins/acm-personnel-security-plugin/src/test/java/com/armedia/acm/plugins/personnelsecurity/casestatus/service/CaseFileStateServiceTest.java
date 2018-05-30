package com.armedia.acm.plugins.personnelsecurity.casestatus.service;

/*-
 * #%L
 * ACM Personnel Security
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

import static org.easymock.EasyMock.expect;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.ChangeCaseFileStateService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by armdev on 12/10/14.
 */
public class CaseFileStateServiceTest extends EasyMockSupport
{
    private Authentication mockAuthentication;
    private AcmAuthenticationDetails mockAuthDetails;
    private ChangeCaseFileStateService mockChangeCaseFileStateService;

    private CaseFileStateService unit;

    @Before
    public void setUp() throws Exception
    {
        mockAuthentication = createMock(Authentication.class);
        mockAuthDetails = createMock(AcmAuthenticationDetails.class);
        mockChangeCaseFileStateService = createMock(ChangeCaseFileStateService.class);

        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        unit = new CaseFileStateService();
        unit.setChangeCaseFileStateService(mockChangeCaseFileStateService);
    }

    @Test
    public void changeCaseFileState() throws Exception
    {
        Long caseId = 500L;
        String newStatus = "NEW_STATUS";
        String remoteAddress = "192.168.1.1";
        String currentUser = "currentUser";

        expect(mockAuthentication.getDetails()).andReturn(mockAuthDetails).atLeastOnce();
        expect(mockAuthDetails.getRemoteAddress()).andReturn(remoteAddress);
        expect(mockAuthentication.getName()).andReturn(currentUser).atLeastOnce();

        expect(mockChangeCaseFileStateService.changeCaseState(mockAuthentication, caseId, newStatus, remoteAddress))
                .andReturn(new CaseFile());

        replayAll();

        unit.changeCaseFileState(caseId, newStatus);

        verifyAll();

    }
}
