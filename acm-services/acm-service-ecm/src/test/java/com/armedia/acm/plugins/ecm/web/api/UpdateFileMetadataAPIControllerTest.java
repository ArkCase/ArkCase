package com.armedia.acm.plugins.ecm.web.api;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

/**
 * Created by manoj.dhungana on 04/10/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-ecm-plugin-test.xml"
})
public class UpdateFileMetadataAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LogManager.getLogger(getClass());

    private MockMvc mockMvc;
    private UpdateFileMetadataAPIController unit;
    private EcmFileService mockEcmFileService;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver filePluginExceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        unit = new UpdateFileMetadataAPIController();

        mockEcmFileService = createMock(EcmFileService.class);
        unit.setEcmFileService(mockEcmFileService);
        mockAuthentication = createMock(Authentication.class);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(filePluginExceptionResolver).build();
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);
    }

    @Test
    public void updateFile() throws Exception
    {
        AcmContainer acmContainer = new AcmContainer();
        acmContainer.setContainerObjectType("CASE_FILE");
        acmContainer.setContainerObjectId(101L);

        EcmFile in = new EcmFile();
        in.setFileId(100L);
        in.setFileType("file_type");
        in.setStatus("new_status");
        in.setContainer(acmContainer);

        EcmFile out = new EcmFile();
        out.setFileId(in.getFileId());
        out.setStatus(in.getStatus());
        out.setContainer(acmContainer);

        Capture<EcmFile> saved = Capture.newInstance();

        expect(mockEcmFileService.findById(anyLong())).andReturn(in).anyTimes();
        expect(mockEcmFileService.updateFile(capture(saved))).andReturn(out);
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();
        expect(mockAuthentication.getDetails()).andReturn("details").anyTimes();
        expectLastCall();

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/service/ecm/file/metadata/{fileId}", "100")
                        .content(new ObjectMapper().writeValueAsString(in))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        String returned = result.getResponse().getContentAsString();
        LOG.info("results: " + returned);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        assertEquals(in.getFileId(), saved.getValue().getFileId());
        assertEquals(in.getStatus(), saved.getValue().getStatus());
    }

    @Test
    public void updateFile_exception() throws Exception
    {
        AcmContainer acmContainer = new AcmContainer();
        acmContainer.setContainerObjectType("CASE_FILE");
        acmContainer.setContainerObjectId(101L);

        EcmFile in = new EcmFile();
        in.setFileId(100L);
        in.setFileType("file_type");
        in.setStatus("new_status");
        in.setContainer(acmContainer);

        Capture<EcmFile> saved = Capture.newInstance();

        expect(mockEcmFileService.findById(anyLong())).andReturn(in).anyTimes();
        expect(mockEcmFileService.updateFile(capture(saved))).andReturn(null).anyTimes();
        expect(mockAuthentication.getName()).andReturn("user").anyTimes();

        replayAll();

        try
        {
            mockMvc.perform(
                    post("/api/latest/service/ecm/file/metadata/{fileId}", "101")
                            .content(new ObjectMapper().writeValueAsString(in))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(mockAuthentication))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.TEXT_PLAIN));
        }
        catch (Exception e)
        {
            // do nothing, exception expected
        }

        verifyAll();
    }
}
