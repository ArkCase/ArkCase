package com.armedia.acm.services.email.config.web.api;

/*-
 * #%L
 * ACM Service: Email
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 5, 2017
 *
 */
@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:/spring/spring-email-service-api-test.xml" })
public class AcmMailTemplateConfigurationServiceAPIControllerTest
{

    private static final String CONTROLLER_PATH = "/api/v1/service/email/configure/template";

    private MockMvc mockMvc;

    @Mock
    private AcmMailTemplateConfigurationService mailService;

    @InjectMocks
    private AcmMailTemplateConfigurationServiceAPIController controller;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.email.config.web.api.AcmMailTemplateConfigurationServiceAPIController#getEmailTemplate(String)}.
     *
     * @throws Exception
     */
    @Test
    public void getEmailTemplate() throws Exception
    {

        // given
        String contentOfTheTemplate = "Html - content";
        String response = "{\"content\":\"Html - content\"}";
        when(mailService.getTemplate("template_name")).thenReturn(contentOfTheTemplate);

        // when
        MvcResult result = mockMvc
                .perform(get(CONTROLLER_PATH + "/template_name").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();

        // then
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().equals(response));
    }
}
