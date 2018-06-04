package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.plugins.onlyoffice.model.config.Config;

import org.springframework.security.core.Authentication;

public interface ConfigService
{
    Config getConfig(Long fileId, Authentication auth);

    String getDocumentServerUrlApi();

    String getArkcaseBaseUrl();
}
