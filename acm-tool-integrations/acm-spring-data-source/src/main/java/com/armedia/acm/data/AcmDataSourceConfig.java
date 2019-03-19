package com.armedia.acm.data;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

public class AcmDataSourceConfig
{
    @JsonProperty("schema.autoUpdate")
    @Value("${schema.autoUpdate}")
    private Boolean autoUpdate;

    @JsonProperty("database.platform")
    @Value("${database.platform}")
    private String databasePlatform;

    @JsonProperty("database.platform.showSql")
    @Value("${database.platform.showSql}")
    private Boolean showSql;

    @JsonProperty("database.encryption_properties")
    @Value("${database.encryption_properties}")
    private String encryptionProperties;

    @JsonProperty("database.encryption_function")
    @Value("${database.encryption_function}")
    private String encryptionFunction;

    @JsonProperty("database.encryption_enabled")
    @Value("${database.encryption_enabled}")
    private Boolean encryptionEnabled;

    @Value("${database.encryption_passphrase}")
    private String encryptionPassphrase;

    @JsonProperty("database.encryption_supported")
    @Value("${database.encryption_supported}")
    private Boolean encryptionSupported;

    @JsonProperty("eclipselink.logging.level")
    @Value("${eclipselink.logging.level}")
    private String eclipseLinkLoggingLevel;

    @JsonProperty("eclipselink.logging.logger")
    @Value("${eclipselink.logging.logger}")
    private String eclipseLinkLoggingLogger;

    @JsonProperty("jpa.model.packages")
    @Value("${jpa.model.packages}")
    private String jpaModelPackages;

    @JsonProperty("acm.driverClassName")
    @Value("${acm.driverClassName}")
    private String driverClassName;

    @JsonProperty("acm.url")
    @Value("${acm.url}")
    private String url;

    @JsonProperty("acm.schema")
    @Value("${acm.schema}")
    private String schema;

    @JsonProperty("acm.username")
    @Value("${acm.username}")
    private String username;

    @Value("${acm.password}")
    private String password;

    @JsonProperty("acm.initialSize")
    @Value("${acm.initialSize}")
    private Integer initialSize;

    @JsonProperty("acm.maxActive")
    @Value("${acm.maxActive}")
    private Integer maxActive;

    @JsonProperty("acm.minIdle")
    @Value("${acm.minIdle}")
    private Integer minIdle;

    @JsonProperty("acm.maxWait")
    @Value("${acm.maxWait}")
    private Integer maxWait;

    @JsonProperty("acm.testOnBorrow")
    @Value("${acm.testOnBorrow}")
    private Boolean testOnBorrow;

    @JsonProperty("acm.testOnReturn")
    @Value("${acm.testOnReturn}")
    private Boolean testOnReturn;

    @JsonProperty("acm.idleConnectionTestPeriodInSeconds")
    @Value("${acm.idleConnectionTestPeriodInSeconds}")
    private Integer idleConnectionTestPeriodInSeconds;

    @JsonProperty("acm.validationQuery")
    @Value("${acm.validationQuery}")
    private String validationQuery;

    @JsonProperty("acm.acquireRetryAttempts")
    @Value("${acm.acquireRetryAttempts}")
    private Integer acquireRetryAttempts;

    @JsonProperty("acm.acquireRetryDelay")
    @Value("${acm.acquireRetryDelay}")
    private Integer acquireRetryDelay;

    @JsonProperty("acm.abandonedTimeoutInSeconds")
    @Value("${acm.abandonedTimeoutInSeconds}")
    private Integer abandonedTimeoutInSeconds;

    @JsonProperty("acm.autoCommit")
    @Value("${acm.autoCommit}")
    private Boolean autoCommit;

    @JsonProperty("activiti.db.type")
    @Value("${activiti.db.type}")
    private String activitiDbType;

    @JsonProperty("acm.jdbcInterceptors")
    @Value("${acm.jdbcInterceptors}")
    private String jdbcInterceptors;

    public Boolean getAutoUpdate()
    {
        return autoUpdate;
    }

    public void setAutoUpdate(Boolean autoUpdate)
    {
        this.autoUpdate = autoUpdate;
    }

    public String getDatabasePlatform()
    {
        return databasePlatform;
    }

    public void setDatabasePlatform(String databasePlatform)
    {
        this.databasePlatform = databasePlatform;
    }

    public Boolean getShowSql()
    {
        return showSql;
    }

    public void setShowSql(Boolean showSql)
    {
        this.showSql = showSql;
    }

    public String getEncryptionProperties()
    {
        return encryptionProperties;
    }

    public void setEncryptionProperties(String encryptionProperties)
    {
        this.encryptionProperties = encryptionProperties;
    }

    public String getEncryptionFunction()
    {
        return encryptionFunction;
    }

    public void setEncryptionFunction(String encryptionFunction)
    {
        this.encryptionFunction = encryptionFunction;
    }

    public Boolean getEncryptionEnabled()
    {
        return encryptionEnabled;
    }

    public void setEncryptionEnabled(Boolean encryptionEnabled)
    {
        this.encryptionEnabled = encryptionEnabled;
    }

    @JsonIgnore
    public String getEncryptionPassphrase()
    {
        return encryptionPassphrase;
    }

    public void setEncryptionPassphrase(String encryptionPassphrase)
    {
        this.encryptionPassphrase = encryptionPassphrase;
    }

    public Boolean getEncryptionSupported()
    {
        return encryptionSupported;
    }

    public void setEncryptionSupported(Boolean encryptionSupported)
    {
        this.encryptionSupported = encryptionSupported;
    }

    public String getEclipseLinkLoggingLevel()
    {
        return eclipseLinkLoggingLevel;
    }

    public void setEclipseLinkLoggingLevel(String eclipseLinkLoggingLevel)
    {
        this.eclipseLinkLoggingLevel = eclipseLinkLoggingLevel;
    }

    public String getEclipseLinkLoggingLogger()
    {
        return eclipseLinkLoggingLogger;
    }

    public void setEclipseLinkLoggingLogger(String eclipseLinkLoggingLogger)
    {
        this.eclipseLinkLoggingLogger = eclipseLinkLoggingLogger;
    }

    public String getJpaModelPackages()
    {
        return jpaModelPackages;
    }

    public void setJpaModelPackages(String jpaModelPackages)
    {
        this.jpaModelPackages = jpaModelPackages;
    }

    public String getDriverClassName()
    {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName)
    {
        this.driverClassName = driverClassName;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getSchema()
    {
        return schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Integer getInitialSize()
    {
        return initialSize;
    }

    public void setInitialSize(Integer initialSize)
    {
        this.initialSize = initialSize;
    }

    public Integer getMaxActive()
    {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive)
    {
        this.maxActive = maxActive;
    }

    public Integer getMinIdle()
    {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle)
    {
        this.minIdle = minIdle;
    }

    public Integer getMaxWait()
    {
        return maxWait;
    }

    public void setMaxWait(Integer maxWait)
    {
        this.maxWait = maxWait;
    }

    public Boolean getTestOnBorrow()
    {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow)
    {
        this.testOnBorrow = testOnBorrow;
    }

    public Boolean getTestOnReturn()
    {
        return testOnReturn;
    }

    public void setTestOnReturn(Boolean testOnReturn)
    {
        this.testOnReturn = testOnReturn;
    }

    public Integer getIdleConnectionTestPeriodInSeconds()
    {
        return idleConnectionTestPeriodInSeconds;
    }

    public void setIdleConnectionTestPeriodInSeconds(Integer idleConnectionTestPeriodInSeconds)
    {
        this.idleConnectionTestPeriodInSeconds = idleConnectionTestPeriodInSeconds;
    }

    public String getValidationQuery()
    {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery)
    {
        this.validationQuery = validationQuery;
    }

    public Integer getAcquireRetryAttempts()
    {
        return acquireRetryAttempts;
    }

    public void setAcquireRetryAttempts(Integer acquireRetryAttempts)
    {
        this.acquireRetryAttempts = acquireRetryAttempts;
    }

    public Integer getAcquireRetryDelay()
    {
        return acquireRetryDelay;
    }

    public void setAcquireRetryDelay(Integer acquireRetryDelay)
    {
        this.acquireRetryDelay = acquireRetryDelay;
    }

    public Integer getAbandonedTimeoutInSeconds()
    {
        return abandonedTimeoutInSeconds;
    }

    public void setAbandonedTimeoutInSeconds(Integer abandonedTimeoutInSeconds)
    {
        this.abandonedTimeoutInSeconds = abandonedTimeoutInSeconds;
    }

    public Boolean getAutoCommit()
    {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit)
    {
        this.autoCommit = autoCommit;
    }

    public String getActivitiDbType()
    {
        return activitiDbType;
    }

    public void setActivitiDbType(String activitiDbType)
    {
        this.activitiDbType = activitiDbType;
    }

    public String getJdbcInterceptors()
    {
        return jdbcInterceptors;
    }

    public void setJdbcInterceptors(String jdbcInterceptors)
    {
        this.jdbcInterceptors = jdbcInterceptors;
    }
}
