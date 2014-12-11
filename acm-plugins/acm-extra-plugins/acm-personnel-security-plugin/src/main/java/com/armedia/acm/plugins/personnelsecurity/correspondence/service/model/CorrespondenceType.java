package com.armedia.acm.plugins.personnelsecurity.correspondence.service.model;

/**
 * Created by marjan.stefanoski on 08.12.2014.
 */
public enum CorrespondenceType {

    CLERANCE_DENIED("cDenied"),
    CLERANCE_GRANTED("cGranted"),
    GENERAL_RELEASE("gRelease"),
    MEDICAL_RELEASE("mRelease"),
    NONE("none");

    private String correspondenceType;

    CorrespondenceType(String correspondenceType) {
        this.correspondenceType  = correspondenceType;
    }

    public static CorrespondenceType getCorrespondenceType( String cType ) {
        for (CorrespondenceType attribute : values()) {
            if (attribute.correspondenceType.equals(cType)) {
                return attribute;
            }
        }
        return CorrespondenceType.NONE;
    }
}
