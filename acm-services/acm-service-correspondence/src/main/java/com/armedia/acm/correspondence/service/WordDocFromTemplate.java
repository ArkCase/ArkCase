package com.armedia.acm.correspondence.service;

import com.armedia.acm.correspondence.model.ObjectType;
import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by marjan.stefanoski on 08.12.2014.
 */
public interface WordDocFromTemplate {
    EcmFile create(ObjectType objectType, String objectId);
}
