package com.armedia.acm.plugins.onlyoffice.service;

import com.armedia.acm.plugins.onlyoffice.model.config.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveDocumentsRepository
{
    /**
     *
     * Map that uses document key which is combination of fileId-fileVersion for Key
     * and for value is list of User which are currently editing/viewing/reviewing document.
     *
     */
    private Map<String, List<User>> documentsBeingEdited = new ConcurrentHashMap<>();

}