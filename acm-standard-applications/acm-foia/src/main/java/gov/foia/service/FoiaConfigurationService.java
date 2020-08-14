package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.plugins.casefile.service.SystemConfigurationService;

import gov.foia.model.FoiaConfig;
import gov.foia.model.FoiaConfiguration;

public class FoiaConfigurationService extends SystemConfigurationService
{
    private FoiaConfig foiaConfig;
    private ConfigurationPropertyService configurationPropertyService;

    public void writeConfiguration(FoiaConfiguration foiaConfiguration)
    {
        foiaConfig.setMaxDaysInBillingQueue(foiaConfiguration.getMaxDaysInBillingQueue());
        foiaConfig.setMaxDaysInHoldQueue(foiaConfiguration.getMaxDaysInHoldQueue());
        foiaConfig.setHoldedAndAppealedRequestsDueDateUpdateEnabled(foiaConfiguration.getHoldedAndAppealedRequestsDueDateUpdateEnabled());
        foiaConfig.setRequestExtensionWorkingDays(foiaConfiguration.getRequestExtensionWorkingDays());
        foiaConfig.setDashboardBannerEnabled(foiaConfiguration.getDashboardBannerEnabled());
        foiaConfig.setNotificationGroupsEnabled(foiaConfiguration.getNotificationGroupsEnabled());
        foiaConfig.setRequestExtensionWorkingDaysEnabled(foiaConfiguration.getRequestExtensionWorkingDaysEnabled());
        foiaConfig.setExpediteWorkingDaysEnabled(foiaConfiguration.getExpediteWorkingDaysEnabled());
        foiaConfig.setExpediteWorkingDays(foiaConfiguration.getExpediteWorkingDays());
        foiaConfig.setPurgeRequestWhenInHoldEnabled(foiaConfiguration.getPurgeRequestWhenInHoldEnabled());
        foiaConfig.setMoveToBillingQueueEnabled(foiaConfiguration.getMoveToBillingQueueEnabled());
        foiaConfig.setLimitedDeliveryToSpecificPageCountEnabled(foiaConfiguration.getLimitedDeliveryToSpecificPageCountEnabled());
        foiaConfig.setLimitedDeliveryToSpecificPageCount(foiaConfiguration.getLimitedDeliveryToSpecificPageCount());
        foiaConfig.setRequireTimesheetToCompleteStandardRequest(foiaConfiguration.getRequireTimesheetToCompleteStandardRequest());
        foiaConfig.setRequireTimesheetToCompleteDeniedRequest(foiaConfiguration.getRequireTimesheetToCompleteDeniedRequest());
        foiaConfig.setProvideReasonToHoldRequestEnabled(foiaConfiguration.getProvideReasonToHoldRequestEnabled());
        foiaConfig.setRedirectFunctionalityCalculationEnabled(foiaConfiguration.getRedirectFunctionalityCalculationEnabled());
        foiaConfig.setFeeWaivedRequestsEnabled(foiaConfiguration.getFeeWaivedRequestsEnabled());
        foiaConfig.setFeeWaivedAppealsEnabled(foiaConfiguration.getFeeWaivedAppealsEnabled());
        foiaConfig.setLitigationRequestsEnabled(foiaConfiguration.getLitigationRequestsEnabled());
        foiaConfig.setLitigationAppealsEnabled(foiaConfiguration.getLitigationAppealsEnabled());
        foiaConfig.setAutomaticCreationOfRequestWhenAppealIsRemandedEnabled(
                foiaConfiguration.getAutomaticCreationOfRequestWhenAppealIsRemandedEnabled());

        configurationPropertyService.updateProperties(foiaConfig);
    }

    @Override
    public FoiaConfiguration readConfiguration()
    {
        FoiaConfiguration foiaConfiguration = new FoiaConfiguration();
        foiaConfiguration.setMaxDaysInBillingQueue(foiaConfig.getMaxDaysInBillingQueue());
        foiaConfiguration.setMaxDaysInHoldQueue(foiaConfig.getMaxDaysInHoldQueue());
        foiaConfiguration.setHoldedAndAppealedRequestsDueDateUpdateEnabled(foiaConfig.getHoldedAndAppealedRequestsDueDateUpdateEnabled());
        foiaConfiguration.setRequestExtensionWorkingDays(foiaConfig.getRequestExtensionWorkingDays());
        foiaConfiguration.setDashboardBannerEnabled(foiaConfig.getDashboardBannerEnabled());
        foiaConfiguration.setNotificationGroupsEnabled(foiaConfig.getNotificationGroupsEnabled());
        foiaConfiguration.setRequestExtensionWorkingDaysEnabled(foiaConfig.getRequestExtensionWorkingDaysEnabled());
        foiaConfiguration.setExpediteWorkingDaysEnabled(foiaConfig.getExpediteWorkingDaysEnabled());
        foiaConfiguration.setExpediteWorkingDays(foiaConfig.getExpediteWorkingDays());
        foiaConfiguration.setPurgeRequestWhenInHoldEnabled(foiaConfig.getPurgeRequestWhenInHoldEnabled());
        foiaConfiguration.setMoveToBillingQueueEnabled(foiaConfig.getMoveToBillingQueueEnabled());
        foiaConfiguration.setLimitedDeliveryToSpecificPageCountEnabled(foiaConfig.getLimitedDeliveryToSpecificPageCountEnabled());
        foiaConfiguration.setLimitedDeliveryToSpecificPageCount(foiaConfig.getLimitedDeliveryToSpecificPageCount());
        foiaConfiguration.setRequireTimesheetToCompleteStandardRequest(foiaConfig.getRequireTimesheetToCompleteStandardRequest());
        foiaConfiguration.setRequireTimesheetToCompleteDeniedRequest(foiaConfig.getRequireTimesheetToCompleteDeniedRequest());
        foiaConfiguration.setProvideReasonToHoldRequestEnabled(foiaConfig.getProvideReasonToHoldRequestEnabled());
        foiaConfiguration.setRedirectFunctionalityCalculationEnabled(foiaConfig.getRedirectFunctionalityCalculationEnabled());
        foiaConfiguration.setFeeWaivedRequestsEnabled(foiaConfig.getFeeWaivedRequestsEnabled());
        foiaConfiguration.setFeeWaivedAppealsEnabled(foiaConfig.getFeeWaivedAppealsEnabled());
        foiaConfiguration.setLitigationRequestsEnabled(foiaConfig.getLitigationRequestsEnabled());
        foiaConfiguration.setLitigationAppealsEnabled(foiaConfig.getLitigationAppealsEnabled());
        foiaConfiguration.setAutomaticCreationOfRequestWhenAppealIsRemandedEnabled(
                foiaConfig.getAutomaticCreationOfRequestWhenAppealIsRemandedEnabled());
        foiaConfiguration.setDojYearlyReports(
                foiaConfig.getDojYearlyReports());

        return foiaConfiguration;
    }

    public FoiaConfig getFoiaConfig()
    {
        return foiaConfig;
    }

    public void setFoiaConfig(FoiaConfig foiaConfig)
    {
        this.foiaConfig = foiaConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
