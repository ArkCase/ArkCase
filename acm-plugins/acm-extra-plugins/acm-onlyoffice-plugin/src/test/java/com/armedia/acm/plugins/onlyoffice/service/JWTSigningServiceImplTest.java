package com.armedia.acm.plugins.onlyoffice.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JWTSigningServiceImplTest {
    private JWTSigningServiceImpl signingService;

    @Before
    public void setUp() {
        signingService = new JWTSigningServiceImpl();
        signingService.setJwtInboundKey("secret");
        signingService.setJwtOutboundAlgorithm("HS256");
        signingService.setJwtOutboundKey("secret");
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

        assertTrue(signingService.verifyCallback(jsonString, "eyJhbGciOiJIUzI1NiJ9.ewogICJzdWIiOiAiMTIzNDU2Nzg5MCIsCiAgIm5hbWUiOiAiSm9obiBEb2UiLAogICJpYXQiOiAxNTE2MjM5MDIyCn0.0Gh1Ilzj9aeD2gxmjTn2U-Yo-NxpW8hMet_CY6bDkKg"));
    }
}