
package org.mule.module.cmis.oauth;

/*-
 * #%L
 * ACM Mule CMIS Connector
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

import javax.annotation.Generated;

/**
 * Wrapper around {@link org.mule.api.annotations.oauth.OAuth} annotated class that will infuse it with
 * access token management capabilities.
 * <p/>
 * It can receive a {@link org.mule.config.PoolingProfile} which is a configuration object used to
 * define the OAuth access tokens pooling parameters.
 * 
 * @param <C>
 *            Actual connector object that represents a connection
 * 
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public interface OAuthManager<C extends OAuthAdapter>
{

    /**
     * Create a new access token using the specified verifier
     * and insert it into the pool
     *
     * @param verifier
     *            OAuth verifier
     * @return A newly created connector
     * @throws Exception
     *             If the access token cannot be retrieved
     */
    C createAccessToken(String verifier) throws Exception;

    /**
     * Borrow an access token from the pool
     *
     * @param userId
     *            User identification used to borrow the access token
     * @return An existing authorized connector
     * @throws Exception
     *             If the access token cannot be retrieved
     */
    C acquireAccessToken(String userId) throws Exception;

    /**
     * Return an access token to the pool
     *
     * @param userId
     *            User identification used to borrow the access token
     * @param connector
     *            Authorized connector to be returned to the pool
     * @throws Exception
     *             If the access token cannot be returned
     */
    void releaseAccessToken(String userId, C connector) throws Exception;

    /**
     * Destroy an access token
     *
     * @param userId
     *            User identification used to borrow the access token
     * @param connector
     *            Authorized connector to the destroyed
     * @throws Exception
     *             If the access token could not be destroyed.
     */
    void destroyAccessToken(String userId, C connector) throws Exception;

    /**
     * Retrieve default unauthorized connector
     */
    C getDefaultUnauthorizedConnector();
}
