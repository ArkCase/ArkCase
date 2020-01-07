package com.armedia.acm.services.signature.web.api;

/*-
 * #%L
 * ACM Service: Electronic Signature
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.signature.dao.SignatureDao;
import com.armedia.acm.services.signature.model.Signature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-electronic-signature-test.xml" })
public class FindSignaturesByTypeByIdAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private Authentication mockAuthentication;

    private FindSignaturesByTypeByIdAPIController unit;

    private SignatureDao mockSignatureDao;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockSignatureDao = createMock(SignatureDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new FindSignaturesByTypeByIdAPIController();

        unit.setSignatureDao(mockSignatureDao);

        mockMvc = MockMvcBuilders.standaloneSetup(unit)
                .setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void findSignatures() throws Exception
    {
        Long objectId = 500L;
        String objectType = "TASK";
        String ipAddress = "ipAddress";
        String userName = "userName";

        Signature foundSignature = new Signature();
        foundSignature.setObjectId(objectId);
        foundSignature.setObjectType(objectType);

        List<Signature> signatureList = new ArrayList<>();
        signatureList.add(foundSignature);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockSignatureDao.findByObjectIdObjectType(objectId, objectType)).andReturn(signatureList);
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn(userName).atLeastOnce();

        replayAll();

        // To see details on the HTTP calls, change .andReturn() to .andDo(print())
        // ResultActions resultAction = mockMvc
        // .perform(
        // get("/api/v1/plugin/signature/find/{objectType}/{objectId}", objectType, objectId)
        // .session(mockHttpSession)
        // .principal(mockAuthentication)).andDo(print());

        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/plugin/signature/find/{objectType}/{objectId}", objectType, objectId)
                                .session(mockHttpSession)
                                .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType()
                .startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        List<Signature> returnedSignatureList = objectMapper.readValue(
                returned,
                objectMapper.getTypeFactory().constructParametricType(List.class, Signature.class));

        assertNotNull(returnedSignatureList);
        assertEquals(returnedSignatureList.size(), 1);
        assertEquals(returnedSignatureList.get(0).getObjectId(), objectId);
        assertEquals(returnedSignatureList.get(0).getObjectType(), objectType);
    }

    @Test
    public void findSignatures_exception() throws Exception
    {
        Long objectId = 500L;
        String objectType = "TASK";
        String ipAddress = "ipAddress";
        String userName = "userName";

        Signature foundSignature = new Signature();
        foundSignature.setObjectId(objectId);
        foundSignature.setObjectType(objectType);

        List<Signature> signatureList = new ArrayList<>();
        signatureList.add(foundSignature);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockSignatureDao.findByObjectIdObjectType(objectId, objectType)).andThrow(new RuntimeException("testException"));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn(userName).atLeastOnce();

        replayAll();

        // To see details on the HTTP calls, change .andReturn() to .andDo(print())
        // ResultActions resultAction = mockMvc
        // .perform(
        // get("/api/v1/plugin/signature/find/{objectType}/{objectId}", objectType, objectId)
        // .session(mockHttpSession)
        // .principal(mockAuthentication)).andDo(print());

        MvcResult result = mockMvc
                .perform(
                        get("/api/v1/plugin/signature/find/{objectType}/{objectId}", objectType, objectId)
                                .session(mockHttpSession)
                                .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

    }
}
