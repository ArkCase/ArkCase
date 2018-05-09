
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

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public interface OAuthAdapter
{

    /**
     * Retrieve OAuth verifier
     *
     * @return A String representing the OAuth verifier
     */
    String getOauthVerifier();

    /**
     * Set OAuth verifier
     *
     * @param value
     *            OAuth verifier to set
     */
    void setOauthVerifier(String value);

    /**
     * Retrieve access token
     */
    String getAccessToken();

    /**
     * Set access token
     *
     * @param value
     */
    void setAccessToken(String value);

    /**
     * Get the callback to be called when the access token and secret need to be saved for
     * later restoration
     */
    SaveAccessTokenCallback getOauthSaveAccessToken();

    /**
     * Set the callback to be called when the access token and secret need to be saved for
     * later restoration
     *
     * @param saveCallback
     *            Callback to be called
     */
    void setOauthSaveAccessToken(SaveAccessTokenCallback saveCallback);

    /**
     * Get the callback to be called when the access token and secret need to be restored
     */
    RestoreAccessTokenCallback getOauthRestoreAccessToken();

    /**
     * Set the callback to be called when the access token and secret need to be restored
     *
     * @param restoreCallback
     *            Callback to be called
     */
    void setOauthRestoreAccessToken(RestoreAccessTokenCallback restoreCallback);

    void hasBeenAuthorized() throws NotAuthorizedException;
}
