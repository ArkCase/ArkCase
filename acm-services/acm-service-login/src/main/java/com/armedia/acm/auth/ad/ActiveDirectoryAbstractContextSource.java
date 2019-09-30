/*
 * #%L
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package com.armedia.acm.auth.ad;

import static com.armedia.acm.auth.ad.ActiveDirectoryUtils.badCredentials;
import static com.armedia.acm.auth.ad.ActiveDirectoryUtils.handleBindException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.JdkVersion;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.SimpleDirContextAuthenticationStrategy;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.SpringSecurityMessageSource;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.DirContext;

import java.util.Hashtable;
import java.util.Map;

public abstract class ActiveDirectoryAbstractContextSource implements BaseLdapPathContextSource, InitializingBean
{
    public static final String SUN_LDAP_POOLING_FLAG = "com.sun.jndi.ldap.connect.pool";
    private static final Class DEFAULT_CONTEXT_FACTORY = com.sun.jndi.ldap.LdapCtxFactory.class;
    private static final Class DEFAULT_DIR_OBJECT_FACTORY = DefaultDirObjectFactory.class;
    private static final boolean DONT_DISABLE_POOLING = false;
    private static final boolean EXPLICITLY_DISABLE_POOLING = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryAbstractContextSource.class);
    private static final String JDK_142 = "1.4.2";

    protected static MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    protected String userDn = "";
    protected String password = "";
    private Class dirObjectFactory = DEFAULT_DIR_OBJECT_FACTORY;
    private Class contextFactory = DEFAULT_CONTEXT_FACTORY;
    private DistinguishedName base = DistinguishedName.EMPTY_PATH;
    private String[] urls;
    private boolean pooled = false;
    private Hashtable baseEnv = new Hashtable();
    private Hashtable anonymousEnv;
    private AuthenticationSource authenticationSource;
    private boolean cacheEnvironmentProperties = true;
    private boolean anonymousReadOnly = false;
    private String referral = null;
    private DirContextAuthenticationStrategy authenticationStrategy = new SimpleDirContextAuthenticationStrategy();

    public DirContext getContext(String principal, String credentials)
    {
        // This method is typically called for authentication purposes, which means that we
        // should explicitly disable pooling in case passwords are changed (LDAP-183).
        return doGetContext(principal, credentials, EXPLICITLY_DISABLE_POOLING);
    }

    private DirContext doGetContext(String principal, String credentials, boolean explicitlyDisablePooling)
    {
        Hashtable env = getAuthenticatedEnv(principal, credentials);
        if (explicitlyDisablePooling)
        {
            env.remove(SUN_LDAP_POOLING_FLAG);
        }

        DirContext ctx = createContext(env);

        try
        {
            authenticationStrategy.processContextAfterCreation(ctx, principal, credentials);
            return ctx;
        }
        catch (NamingException e)
        {
            closeContext(ctx);
            if ((e instanceof AuthenticationException) || (e instanceof OperationNotSupportedException))
            {
                handleBindException(e);
                throw badCredentials(e);
            }
            else
            {
                throw LdapUtils.convertLdapException(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.ldap.core.ContextSource#getReadOnlyContext()
     */
    public DirContext getReadOnlyContext()
    {
        if (!anonymousReadOnly)
        {
            return doGetContext(
                    authenticationSource.getPrincipal(),
                    authenticationSource.getCredentials(),
                    DONT_DISABLE_POOLING);
        }
        else
        {
            return createContext(getAnonymousEnv());
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.ldap.core.ContextSource#getReadWriteContext()
     */
    public DirContext getReadWriteContext()
    {
        return doGetContext(
                authenticationSource.getPrincipal(),
                authenticationSource.getCredentials(),
                DONT_DISABLE_POOLING);
    }

    /**
     * Default implementation of setting the environment up to be authenticated.
     * This method should typically NOT be overridden; any customization to the
     * authentication mechanism should be managed by setting a different
     * {@link DirContextAuthenticationStrategy} on this instance.
     *
     * @param env
     *            the environment to modify.
     * @param principal
     *            the principal to authenticate with.
     * @param credentials
     *            the credentials to authenticate with.
     * @see DirContextAuthenticationStrategy
     * @see #setAuthenticationStrategy(DirContextAuthenticationStrategy)
     */
    protected void setupAuthenticatedEnvironment(Hashtable env, String principal, String credentials)
    {
        try
        {
            authenticationStrategy.setupEnvironment(env, principal, credentials);
        }
        catch (NamingException e)
        {
            if ((e instanceof AuthenticationException) || (e instanceof OperationNotSupportedException))
            {
                handleBindException(e);
                throw badCredentials(e);
            }
            else
            {
                throw LdapUtils.convertLdapException(e);
            }
        }
    }

    /**
     * Close the context and swallow any exceptions.
     *
     * @param ctx
     *            the DirContext to close.
     */
    private void closeContext(DirContext ctx)
    {
        if (ctx != null)
        {
            try
            {
                ctx.close();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * Assemble a valid url String from all registered urls to add as
     * <code>PROVIDER_URL</code> to the environment.
     *
     * @param ldapUrls
     *            all individual url Strings.
     * @return the full url String
     */
    protected String assembleProviderUrlString(String[] ldapUrls)
    {
        StringBuffer providerUrlBuffer = new StringBuffer(1024);
        for (int i = 0; i < ldapUrls.length; i++)
        {
            providerUrlBuffer.append(ldapUrls[i]);
            if (!DistinguishedName.EMPTY_PATH.equals(base))
            {
                if (!ldapUrls[i].endsWith("/"))
                {
                    providerUrlBuffer.append("/");
                }
            }
            providerUrlBuffer.append(base.toUrl());
            providerUrlBuffer.append(' ');
        }
        return providerUrlBuffer.toString().trim();
    }

    /**
     * Get the base suffix from which all operations should originate. If a base
     * suffix is set, you will not have to (and, indeed, must not) specify the
     * full distinguished names in any operations performed.
     *
     * @return the base suffix
     */
    protected DistinguishedName getBase()
    {
        return new DistinguishedName(base);
    }

    /**
     * Set the base suffix from which all operations should origin. If a base
     * suffix is set, you will not have to (and, indeed, must not) specify the
     * full distinguished names in any operations performed.
     *
     * @param base
     *            the base suffix.
     */
    public void setBase(String base)
    {
        this.base = new DistinguishedName(base);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.ldap.core.support.BaseLdapPathSource#getBaseLdapPath
     * ()
     */
    public DistinguishedName getBaseLdapPath()
    {
        return getBase().immutableDistinguishedName();
    }

    /*
     * (non-Javadoc)
     * @seeorg.springframework.ldap.core.support.BaseLdapPathSource#
     * getBaseLdapPathAsString()
     */
    public String getBaseLdapPathAsString()
    {
        return getBaseLdapPath().toString();
    }

    /**
     * Create a DirContext using the supplied environment.
     *
     * @param environment
     *            the LDAP environment to use when creating the
     *            <code>DirContext</code>.
     * @return a new DirContext implementation initialized with the supplied
     *         environment.
     */
    protected DirContext createContext(Hashtable environment)
    {
        DirContext ctx = null;

        try
        {
            ctx = getDirContextInstance(environment);

            if (LOGGER.isTraceEnabled())
            {
                Hashtable ctxEnv = ctx.getEnvironment();
                String ldapUrl = (String) ctxEnv.get(Context.PROVIDER_URL);
                LOGGER.trace("Got Ldap context on server '" + ldapUrl + "'");
            }

            return ctx;
        }
        catch (NamingException e)
        {
            closeContext(ctx);
            if (e instanceof AuthenticationException || e instanceof OperationNotSupportedException)
            {
                handleBindException(e);
                throw badCredentials(e);
            }
            else
            {
                throw LdapUtils.convertLdapException(e);
            }
        }
    }

    /**
     * Get the context factory.
     *
     * @return the context factory used when creating Contexts.
     */
    public Class getContextFactory()
    {
        return contextFactory;
    }

    /**
     * Set the context factory. Default is com.sun.jndi.ldap.LdapCtxFactory.
     *
     * @param contextFactory
     *            the context factory used when creating Contexts.
     */
    public void setContextFactory(Class contextFactory)
    {
        this.contextFactory = contextFactory;
    }

    /**
     * Get the DirObjectFactory to use.
     *
     * @return the DirObjectFactory to be used. <code>null</code> means that no
     *         DirObjectFactory will be used.
     */
    public Class getDirObjectFactory()
    {
        return dirObjectFactory;
    }

    /**
     * Set the DirObjectFactory to use. Default is
     * {@link DefaultDirObjectFactory}. The specified class needs to be an
     * implementation of javax.naming.spi.DirObjectFactory. <b>Note: </b>Setting
     * this value to null may have cause connection leaks when using
     * ContextMapper methods in LdapTemplate.
     *
     * @param dirObjectFactory
     *            the DirObjectFactory to be used. Null means that
     *            no DirObjectFactory will be used.
     */
    public void setDirObjectFactory(Class dirObjectFactory)
    {
        this.dirObjectFactory = dirObjectFactory;
    }

    /**
     * Checks that all necessary data is set and that there is no compatibility
     * issues, after which the instance is initialized. Note that you need to
     * call this method explicitly after setting all desired properties if using
     * the class outside of a Spring Context.
     */
    public void afterPropertiesSet() throws Exception
    {
        if (ArrayUtils.isEmpty(urls))
        {
            throw new IllegalArgumentException("At least one server url must be set");
        }

        if (!DistinguishedName.EMPTY_PATH.equals(base) && getJdkVersion().compareTo(JDK_142) < 0)
        {
            throw new IllegalArgumentException("Base path is not supported for JDK versions < 1.4.2");
        }

        if (authenticationSource == null)
        {
            LOGGER.debug("AuthenticationSource not set - using default implementation");
            if (StringUtils.isBlank(userDn))
            {
                LOGGER.debug("Property 'userDn' not set - anonymous context will be used for read-write operations");
            }
            else if (StringUtils.isBlank(password))
            {
                LOGGER.debug("Property 'password' not set - blank password will be used");
            }
            authenticationSource = new SimpleAuthenticationSource();
        }

        if (cacheEnvironmentProperties)
        {
            anonymousEnv = setupAnonymousEnv();
        }
    }

    private Hashtable setupAnonymousEnv()
    {
        if (pooled)
        {
            baseEnv.put(SUN_LDAP_POOLING_FLAG, "true");
            LOGGER.trace("Using LDAP pooling.");
        }
        else
        {
            baseEnv.remove(SUN_LDAP_POOLING_FLAG);
            LOGGER.trace("Not using LDAP pooling");
        }

        Hashtable env = new Hashtable(baseEnv);

        env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory.getName());
        env.put(Context.PROVIDER_URL, assembleProviderUrlString(urls));

        if (dirObjectFactory != null)
        {
            env.put(Context.OBJECT_FACTORIES, dirObjectFactory.getName());
        }

        if (!StringUtils.isBlank(referral))
        {
            env.put(Context.REFERRAL, referral);
        }

        if (!DistinguishedName.EMPTY_PATH.equals(base))
        {
            // Save the base path for use in the DefaultDirObjectFactory.
            env.put(DefaultDirObjectFactory.JNDI_ENV_BASE_PATH_KEY, base);
        }

        LOGGER.debug("Trying provider Urls: [{}]", assembleProviderUrlString(urls));

        return env;
    }

    /**
     * Set the password (credentials) to use for getting authenticated contexts.
     *
     * @param password
     *            the password.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Set the user distinguished name (principal) to use for getting
     * authenticated contexts.
     *
     * @param userDn
     *            the user distinguished name.
     */
    public void setUserDn(String userDn)
    {
        this.userDn = userDn;
    }

    /**
     * Get the urls of the LDAP servers.
     *
     * @return the urls of all servers.
     */
    public String[] getUrls()
    {
        return urls.clone();
    }

    /**
     * Set the urls of the LDAP servers. Use this method if several servers are
     * required.
     *
     * @param urls
     *            the urls of all servers.
     */
    public void setUrls(String[] urls)
    {
        this.urls = urls.clone();
    }

    /**
     * Set the url of the LDAP server. Utility method if only one server is
     * used.
     *
     * @param url
     *            the url of the LDAP server.
     */
    public void setUrl(String url)
    {
        this.urls = new String[] { url };
    }

    /**
     * Get whether the pooling flag should be set.
     *
     * @return whether Contexts should be pooled.
     */
    public boolean isPooled()
    {
        return pooled;
    }

    /**
     * Set whether the pooling flag should be set, enabling the built-in LDAP
     * connection pooling. Default is <code>false</code>. The built-in LDAP
     * connection pooling suffers from a number of deficiencies, e.g. no
     * connection validation. Also, enabling this flag when using TLS
     * connections will explicitly not work. Consider using the Spring LDAP
     * <code>PoolingContextSource</code> as an alternative instead of enabling
     * this flag.
     * <p>
     * Note that since LDAP pooling is system wide, full configuration of this
     * needs be done using system parameters as specified in the LDAP/JNDI
     * documentation. Also note, that pooling is done on user dn basis, i.e.
     * each individually authenticated connection will be pooled separately.
     * This means that LDAP pooling will be most efficient using anonymous
     * connections or connections authenticated using one single system user.
     *
     * @param pooled
     *            whether Contexts should be pooled.
     */
    public void setPooled(boolean pooled)
    {
        this.pooled = pooled;
    }

    /**
     * If any custom environment properties are needed, these can be set using
     * this method.
     *
     * @param baseEnvironmentProperties
     */
    public void setBaseEnvironmentProperties(Map baseEnvironmentProperties)
    {
        this.baseEnv = new Hashtable(baseEnvironmentProperties);
    }

    String getJdkVersion()
    {
        return JdkVersion.getJavaVersion();
    }

    protected Hashtable getAnonymousEnv()
    {
        if (cacheEnvironmentProperties)
        {
            return anonymousEnv;
        }
        else
        {
            return setupAnonymousEnv();
        }
    }

    protected Hashtable getAuthenticatedEnv(String principal, String credentials)
    {
        // The authenticated environment should always be rebuilt.
        Hashtable env = new Hashtable(getAnonymousEnv());
        setupAuthenticatedEnvironment(env, principal, credentials);
        return env;
    }

    /**
     * Get the authentication source.
     *
     * @return the {@link AuthenticationSource} that will provide user info.
     */
    public AuthenticationSource getAuthenticationSource()
    {
        return authenticationSource;
    }

    /**
     * Set the authentication source to use when retrieving user principal and
     * credentials.
     *
     * @param authenticationSource
     *            the {@link AuthenticationSource} that will
     *            provide user info.
     */
    public void setAuthenticationSource(AuthenticationSource authenticationSource)
    {
        this.authenticationSource = authenticationSource;
    }

    /**
     * Set whether environment properties should be cached between requsts for
     * anonymous environment. Default is <code>true</code>; setting this
     * property to <code>false</code> causes the environment Hashmap to be
     * rebuilt from the current property settings of this instance between each
     * request for an anonymous environment.
     *
     * @param cacheEnvironmentProperties
     *            <code>true</code> causes that the
     *            anonymous environment properties should be cached, <code>false</code>
     *            causes the Hashmap to be rebuilt for each request.
     */
    public void setCacheEnvironmentProperties(boolean cacheEnvironmentProperties)
    {
        this.cacheEnvironmentProperties = cacheEnvironmentProperties;
    }

    /**
     * Get whether an anonymous environment should be used for read-only
     * operations.
     *
     * @return <code>true</code> if an anonymous environment should be used for
     *         read-only operations, <code>false</code> otherwise.
     */
    public boolean isAnonymousReadOnly()
    {
        return anonymousReadOnly;
    }

    /**
     * Set whether an anonymous environment should be used for read-only
     * operations. Default is <code>false</code>.
     *
     * @param anonymousReadOnly
     *            <code>true</code> if an anonymous environment
     *            should be used for read-only operations, <code>false</code> otherwise.
     */
    public void setAnonymousReadOnly(boolean anonymousReadOnly)
    {
        this.anonymousReadOnly = anonymousReadOnly;
    }

    /**
     * Set the {@link DirContextAuthenticationStrategy} to use for preparing the
     * environment and processing the created <code>DirContext</code> instances.
     *
     * @param authenticationStrategy
     *            the
     *            {@link DirContextAuthenticationStrategy} to use; default is
     *            {@link SimpleDirContextAuthenticationStrategy}.
     */
    public void setAuthenticationStrategy(DirContextAuthenticationStrategy authenticationStrategy)
    {
        this.authenticationStrategy = authenticationStrategy;
    }

    /**
     * Set the method to handle referrals. Default is 'ignore'; setting this
     * flag to 'follow' will enable referrals to be automatically followed. Note
     * that this might require particular name server setup in order to work
     * (the referred URLs will need to be automatically found using standard DNS
     * resolution).
     * 
     * @param referral
     *            the value to set the system property
     *            <code>Context.REFERRAL</code> to, customizing the way that referrals are
     *            handled.
     */
    public void setReferral(String referral)
    {
        this.referral = referral;
    }

    /**
     * Implement in subclass to create a DirContext of the desired type (e.g.
     * InitialDirContext or InitialLdapContext).
     *
     * @param environment
     *            the environment to use when creating the instance.
     * @return a new DirContext instance.
     * @throws NamingException
     *             if one is encountered when creating the instance.
     */
    protected abstract DirContext getDirContextInstance(Hashtable environment) throws NamingException;

    class SimpleAuthenticationSource implements AuthenticationSource
    {

        public String getPrincipal()
        {
            return userDn;
        }

        public String getCredentials()
        {
            return password;
        }

    }
}
