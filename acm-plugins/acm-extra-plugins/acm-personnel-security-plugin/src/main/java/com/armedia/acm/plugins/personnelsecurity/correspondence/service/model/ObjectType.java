package com.armedia.acm.plugins.personnelsecurity.correspondence.service.model;

/**
 * Created by marjan.stefanoski on 08.12.2014.
 */
public enum ObjectType {

    COMPLAINT("COMPLAINT"),
    CASE("CASE_FILE"),
    TASK("TASK"),
    NONE("NONE");

    private String objectType;

    ObjectType(String objectType){
        this.objectType = objectType;
    }

    public static ObjectType getObjectType( String oType ) {
        for ( ObjectType attribute : values() ) {
            if ( attribute.objectType.equals(oType) ) {
                return attribute;
            }
        }
        return ObjectType.NONE;
    }

    public String getObjectType() {
        return objectType;
    }
}
