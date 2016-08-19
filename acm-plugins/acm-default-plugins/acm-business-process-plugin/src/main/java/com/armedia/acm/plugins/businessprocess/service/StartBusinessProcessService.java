package com.armedia.acm.plugins.businessprocess.service;

import java.util.Map;

public interface StartBusinessProcessService
{
    void startBusinessProcess(String processName, Map<String, Object> processVariables);
}
