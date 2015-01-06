package com.armedia.acm.core;

/**
 * Marker interface to identify business objects.  Each POJO that represents a business object must implement this
 * interface.
 */
public interface AcmObject
{
    /**
     * Specify what type of object this is; determines the data access control, business processes, and
     * business rules to be applied to this object. There is not a one-to-one mapping from the class name to the
     * object type.  For example, a document's object type is the form type (a "Close Case Request" document's type is
     * "Close Case Request", not "Document").
     * <p/>
     * The value returned must match the "name" property for an AcmObjectType bean in the war project; otherwise
     * the system will not know which access controls, business rules, or business processes to apply.
     *
     * @return The object type; there should be an AcmObjectType bean whose 'name' property is set to this value.
     */
    String getObjectType();

    Long getId();
}
