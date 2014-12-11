package com.armedia.acm.plugins.personnelsecurity.casestatus.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.ChangeCaseFileStateService;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.easymock.EasyMock.expect;

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
