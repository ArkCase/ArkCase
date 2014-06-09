package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.web.api.AcmSpringMvcErrorManager;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;

import static org.easymock.EasyMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Created by armdev on 6/9/14.
 */
public class FileDownloadAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private FileDownloadAPIController unit;

    private EcmFileDao mockFileDao;
    private Authentication mockAuthentication;
    private AcmSpringMvcErrorManager mockErrorManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockFileDao = createMock(EcmFileDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        mockErrorManager = createMock(AcmSpringMvcErrorManager.class);

        unit = new FileDownloadAPIController();

        unit.setFileDao(mockFileDao);
        unit.setErrorManager(mockErrorManager);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void downloadFileById_fileNotFoundInDb() throws Exception
    {
        Long ecmFileId = 500L;
        String user = "user";

        expect(mockAuthentication.getName()).andReturn(user).atLeastOnce();
        expect(mockFileDao.find(EcmFile.class, ecmFileId)).andReturn(null);
        mockErrorManager.sendErrorResponse(
                eq(HttpStatus.BAD_REQUEST),
                eq("File not found."),
                anyObject(HttpServletResponse.class));

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/ecm/download/byId/{ecmFileId}", ecmFileId)
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andReturn();

        verifyAll();
    }
}
