package com.armedia.acm.plugins.onlyoffice.service;

/*-
 * #%L
 * ACM Extra Plugin: OnlyOffice Integration
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

import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.onlyoffice.model.OnlyOfficeConfig;
import org.junit.Before;
import org.junit.Test;

public class JWTSigningServiceImplTest {

    private JWTSigningServiceImpl signingService;

    @Before
    public void setUp() {
        signingService = new JWTSigningServiceImpl();
        OnlyOfficeConfig onlyOfficeConfig = new OnlyOfficeConfig();
        onlyOfficeConfig.setJwtInboundKey("secret");
        onlyOfficeConfig.setJwtOutboundAlgorithm("HS256");
        onlyOfficeConfig.setJwtOutboundKey("secret");
        signingService.setConfig(onlyOfficeConfig);
    }

    @Test
    public void signJsonPayload() {

        String jsonString = "{\n" +
                "  \"sub\": \"1234567890\",\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"iat\": 1516239022\n" +
                "}";
        String signed = signingService.signJsonPayload(jsonString);


        System.out.println(signed);
    }

    @Test
    public void verifyJsonPayload() {

        String jsonString = "{\n" +
                "  \"sub\": \"1234567890\",\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"iat\": 1516239022\n" +
                "}";

        assertTrue(signingService.verifyToken(
                "eyJhbGciOiJIUzI1NiJ9.ewogICJzdWIiOiAiMTIzNDU2Nzg5MCIsCiAgIm5hbWUiOiAiSm9obiBEb2UiLAogICJpYXQiOiAxNTE2MjM5MDIyCn0.0Gh1Ilzj9aeD2gxmjTn2U-Yo-NxpW8hMet_CY6bDkKg"));
    }
}
