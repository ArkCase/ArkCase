package com.armedia.acm.plugins.onlyoffice.service;

public interface JWTSigningService {
    boolean verifyCallback(String callBackData, String token);

    String signJsonPayload(String jsonString);
}
