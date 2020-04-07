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

package com.armedia.acm.services.users.model.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

import javax.naming.directory.SearchControls;

public class ActiveDirectoryLdapSearchConfig implements InitializingBean
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterBasedLdapUserSearch.class);
    /**
     * The LDAP SearchControls object used for the search. Shared between searches so shouldn't be modified
     * once the bean has been configured.
     */
    private final SearchControls searchControls = new SearchControls();
    private BaseLdapPathContextSource contextSource;
    /** Context name to search in, relative to the base of the configured ContextSource. */
    private String searchBase = "";

    /**
     * The filter expression used in the user search. This is an LDAP search filter (as defined in 'RFC 2254')
     * with optional arguments. See the documentation for the <tt>search</tt> methods in {@link
     * javax.naming.directory.DirContext DirContext} for more information.
     *
     * <p>
     * In this case, the username is the only parameter.
     * </p>
     * Possible examples are:
     * <ul>
     * <li>(uid={0}) - this would search for a username match on the uid attribute.</li>
     * </ul>
     */
    private String searchFilter;

    /**
     * Sets the corresponding property on the {@link SearchControls} instance used in the search.
     *
     * @param deref
     *            the derefLinkFlag value as defined in SearchControls..
     */
    public void setDerefLinkFlag(boolean deref)
    {
        searchControls.setDerefLinkFlag(deref);
    }

    /**
     * If true then searches the entire subtree as identified by context, if false (the default) then only
     * searches the level identified by the context.
     *
     * @param searchSubtree
     *            true the underlying search controls should be set to SearchControls.SUBTREE_SCOPE
     *            rather than SearchControls.ONELEVEL_SCOPE.
     */
    public void setSearchSubtree(boolean searchSubtree)
    {
        searchControls.setSearchScope(searchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
    }

    /**
     * The time to wait before the search fails; the default is zero, meaning forever.
     *
     * @param searchTimeLimit
     *            the time limit for the search (in milliseconds).
     */
    public void setSearchTimeLimit(int searchTimeLimit)
    {
        searchControls.setTimeLimit(searchTimeLimit);
    }

    /**
     * Specifies the attributes that will be returned as part of the search.
     * <p>
     * null indicates that all attributes will be returned.
     * An empty array indicates no attributes are returned.
     *
     * @param attrs
     *            An array of attribute names identifying the attributes that
     *            will be returned. Can be null.
     */
    public void setReturningAttributes(String[] attrs)
    {
        searchControls.setReturningAttributes(attrs);
    }

    @Override
    public String toString()
    {

        return "[ searchFilter: '" + searchFilter + "', " +
                "searchBase: '" + searchBase + "'" +
                ", scope: " +
                (searchControls.getSearchScope() == SearchControls.SUBTREE_SCOPE ? "subtree" : "single-level, ") +
                ", searchTimeLimit: " + searchControls.getTimeLimit() +
                ", derefLinkFlag: " + searchControls.getDerefLinkFlag() + " ]";
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        setSearchSubtree(true);

        if (searchBase.length() == 0)
        {
            LOGGER.debug("SearchBase not set. Searches will be performed from the root: {}", contextSource.getBaseLdapPath());
        }
    }

    public BaseLdapPathContextSource getContextSource()
    {
        return contextSource;
    }

    public void setContextSource(BaseLdapPathContextSource contextSource)
    {
        this.contextSource = contextSource;
    }

    public SearchControls getSearchControls()
    {
        return searchControls;
    }

    public String getSearchBase()
    {
        return searchBase;
    }

    public void setSearchBase(String searchBase)
    {
        this.searchBase = searchBase;
    }

    public String getSearchFilter()
    {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter)
    {
        this.searchFilter = searchFilter;
    }
}
