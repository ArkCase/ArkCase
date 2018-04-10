package com.armedia.acm.plugins.phonehome.service;

import java.util.Map;

public interface MultipartRequestGateway
{
    void postMultipartRequest(Map<String, Object> multipartRequest);
}