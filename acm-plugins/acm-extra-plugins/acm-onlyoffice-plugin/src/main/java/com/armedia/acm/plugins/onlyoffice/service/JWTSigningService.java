package com.armedia.acm.plugins.onlyoffice.service;

public interface JWTSigningService
{
    boolean verifyToken(String token);

    String signJsonPayload(String jsonString);
}
