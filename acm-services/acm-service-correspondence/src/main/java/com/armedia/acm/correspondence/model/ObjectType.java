package com.armedia.acm.correspondence.model;

/**
 * Created by marjan.stefanoski on 08.12.2014.
 */
public enum ObjectType {

    COMPLAINT("complaint"),
    CASE("case"),
    TASK("task"),
    NONE("none");

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
}
