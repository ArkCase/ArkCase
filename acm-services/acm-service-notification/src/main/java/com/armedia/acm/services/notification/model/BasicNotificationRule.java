/**
 *
 */
package com.armedia.acm.services.notification.model;

import com.armedia.acm.services.notification.service.CustomTitleFormatter;
import com.armedia.acm.services.notification.service.Executor;
import com.armedia.acm.services.notification.service.UsersNotified;

import java.util.Map;

/**
 * @author riste.tutureski
 */
public class BasicNotificationRule implements NotificationRule
{

    private boolean globalRule;
    private QueryType queryType;
    private Executor executor;
    private Map<String, Object> jpaProperties;
    private String jpaQuery;
    private CustomTitleFormatter customTitleFormatter;
    private UsersNotified usersNotified;

    @Override
    public boolean isGlobalRule()
    {
        return globalRule;
    }

    public void setGlobalRule(boolean globalRule)
    {
        this.globalRule = globalRule;
    }

    @Override
    public QueryType getQueryType()
    {
        return queryType;
    }

    public void setQueryType(QueryType queryType)
    {
        this.queryType = queryType;
    }

    @Override
    public Executor getExecutor()
    {
        return executor;
    }

    public void setExecutor(Executor executor)
    {
        this.executor = executor;
    }

    @Override
    public Map<String, Object> getJpaProperties()
    {
        return jpaProperties;
    }

    public void setJpaProperties(Map<String, Object> jpaProperties)
    {
        this.jpaProperties = jpaProperties;
    }

    @Override
    public String getJpaQuery()
    {
        return jpaQuery;
    }

    public void setJpaQuery(String jpaQuery)
    {
        this.jpaQuery = jpaQuery;
    }

    @Override
    public CustomTitleFormatter getCustomTitleFormatter()
    {
        return customTitleFormatter;
    }

    public void setCustomTitleFormatter(CustomTitleFormatter customTitleFormatter)
    {
        this.customTitleFormatter = customTitleFormatter;
    }

    @Override
    public UsersNotified getUsersNotified()
    {
        return usersNotified;
    }

    public void setUsersNotified(UsersNotified usersNotified)
    {
        this.usersNotified = usersNotified;
    }
}
