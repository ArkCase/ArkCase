package com.armedia.acm.correspondence.model;

/**
 * @author sasko.tanaskoski
 *
 */

/**
 * This POJO is used for storing mergeField parameters to json file
 */
public class CorrespondenceMergeFieldConfiguration
{

    private String fieldId;

    private String fieldValue;

    private String fieldDescription;

    private String fieldType;

    private String fieldVersion;

    /**
     * @return the fieldId
     */
    public String getFieldId()
    {
        return fieldId;
    }

    /**
     * @param fieldId
     *            the fieldId to set
     */
    public void setFieldId(String fieldId)
    {
        this.fieldId = fieldId;
    }

    /**
     * @return the fieldValue
     */
    public String getFieldValue()
    {
        return fieldValue;
    }

    /**
     * @param fieldValue
     *            the fieldValue to set
     */
    public void setFieldValue(String fieldValue)
    {
        this.fieldValue = fieldValue;
    }

    /**
     * @return the fieldDescription
     */
    public String getFieldDescription()
    {
        return fieldDescription;
    }

    /**
     * @param fieldDescription
     *            the fieldDescription to set
     */
    public void setFieldDescription(String fieldDescription)
    {
        this.fieldDescription = fieldDescription;
    }

    /**
     * @return the fieldType
     */
    public String getFieldType()
    {
        return fieldType;
    }

    /**
     * @param fieldType
     *            the fieldType to set
     */
    public void setFieldType(String fieldType)
    {
        this.fieldType = fieldType;
    }

    /**
     * @return the fieldVersion
     */
    public String getFieldVersion()
    {
        return fieldVersion;
    }

    /**
     * @param fieldVersion
     *            the fieldVersion to set
     */
    public void setFieldVersion(String fieldVersion)
    {
        this.fieldVersion = fieldVersion;
    }

}
