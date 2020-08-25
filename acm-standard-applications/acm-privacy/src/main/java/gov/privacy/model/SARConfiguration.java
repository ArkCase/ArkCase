package gov.privacy.model;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.plugins.businessprocess.model.SystemConfiguration;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARConfiguration extends SystemConfiguration
{
    private Integer maxDaysInHoldQueue;
    private Boolean dashboardBannerEnabled;
    private Boolean purgeRequestWhenInHoldEnabled;
    private Boolean limitedDeliveryToSpecificPageCountEnabled;
    private Integer limitedDeliveryToSpecificPageCount;
    private Boolean provideReasonToHoldRequestEnabled;

    public Integer getMaxDaysInHoldQueue()
    {
        return maxDaysInHoldQueue;
    }

    public void setMaxDaysInHoldQueue(Integer maxDaysInHoldQueue)
    {
        this.maxDaysInHoldQueue = maxDaysInHoldQueue;
    }

    public Boolean getDashboardBannerEnabled()
    {
        return dashboardBannerEnabled;
    }

    public void setDashboardBannerEnabled(Boolean dashboardBannerEnabled)
    {
        this.dashboardBannerEnabled = dashboardBannerEnabled;
    }

    public Boolean getPurgeRequestWhenInHoldEnabled()
    {
        return purgeRequestWhenInHoldEnabled;
    }

    public void setPurgeRequestWhenInHoldEnabled(Boolean purgeRequestWhenInHoldEnabled)
    {
        this.purgeRequestWhenInHoldEnabled = purgeRequestWhenInHoldEnabled;
    }

    /**
     * @return the provideReasonToHoldRequestEnabled
     */
    public Boolean getProvideReasonToHoldRequestEnabled()
    {
        return provideReasonToHoldRequestEnabled;
    }

    /**
     * @param provideReasonToHoldRequestEnabled
     *            the provideReasonToHoldRequestEnabled to set
     */
    public void setProvideReasonToHoldRequestEnabled(Boolean provideReasonToHoldRequestEnabled)
    {
        this.provideReasonToHoldRequestEnabled = provideReasonToHoldRequestEnabled;
    }

    public Boolean getLimitedDeliveryToSpecificPageCountEnabled()
    {
        return limitedDeliveryToSpecificPageCountEnabled;
    }

    public void setLimitedDeliveryToSpecificPageCountEnabled(Boolean limitedDeliveryToSpecificPageCountEnabled)
    {
        this.limitedDeliveryToSpecificPageCountEnabled = limitedDeliveryToSpecificPageCountEnabled;
    }

    public Integer getLimitedDeliveryToSpecificPageCount()
    {
        return limitedDeliveryToSpecificPageCount;
    }

    public void setLimitedDeliveryToSpecificPageCount(Integer limitedDeliveryToSpecificPageCount)
    {
        this.limitedDeliveryToSpecificPageCount = limitedDeliveryToSpecificPageCount;
    }
}
