package com.armedia.acm.services.notification.model;

import java.util.Map;

import com.armedia.acm.services.notification.service.Executor;
import com.armedia.acm.services.notification.service.CustomTitleFormatter;
import com.armedia.acm.services.notification.service.UsersNotified;


public interface NotificationRule
{

    boolean isGlobalRule();

    QueryType getQueryType();

    Executor getExecutor();

    Map<String, Object> getJpaProperties();

    String getJpaQuery();

    CustomTitleFormatter getCustomTitleFormatter();

    UsersNotified getUsersNotified();

}
