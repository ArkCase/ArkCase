package com.armedia.acm.services.billing.model;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingInvoiceRequest
{

    private String parentObjectType;

    private Long parentObjectId;

    /**
     * @return the parentObjectType
     */
    public String getParentObjectType()
    {
        return parentObjectType;
    }

    /**
     * @param parentObjectType
     *            the parentObjectType to set
     */
    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }

    /**
     * @return the parentObjectId
     */
    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    /**
     * @param parentObjectId
     *            the parentObjectId to set
     */
    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

}
