package com.armedia.acm.services.billing.model;

/*-
 * #%L
 * ACM Service: Billing
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingInvoiceRequest
{

    private String parentObjectType;

    private Long parentObjectId;

    private String objectSubtype;
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

    public String getObjectSubtype() {
        return objectSubtype;
    }

    public void setObjectSubtype(String objectSubtype) {
        this.objectSubtype = objectSubtype;
    }
}
