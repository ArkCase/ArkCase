package com.armedia.acm.correspondence.service;

import com.armedia.acm.correspondence.model.ObjectType;
import com.armedia.acm.plugins.ecm.model.EcmFile;

/**
 * Created by marjan.stefanoski on 08.12.2014.
 */
public class CreateMedicalRelease implements WordDocFromTemplate {

    @Override
    public EcmFile create(ObjectType objectType, String objectId) {
        switch (objectType){
            case CASE:
                break;
            case COMPLAINT:
                break;
            case TASK:
                break;
            default:
                break;
        }

        return null;
    }
}
