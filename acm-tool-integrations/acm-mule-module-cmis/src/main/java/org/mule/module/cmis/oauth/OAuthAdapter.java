
package org.mule.module.cmis.oauth;

import javax.annotation.Generated;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public interface OAuthAdapter {

    /**
     * Retrieve OAuth verifier
     *
     * @return A String representing the OAuth verifier
     */
    String getOauthVerifier();

    /**
     * Set OAuth verifier
     *
     * @param value OAuth verifier to set
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
     * Set the callback to be called when the access token and secret need to be saved for
     * later restoration
     *
     * @param saveCallback Callback to be called
     */
    void setOauthSaveAccessToken(SaveAccessTokenCallback saveCallback);

    /**
     * Set the callback to be called when the access token and secret need to be restored
     *
     * @param restoreCallback Callback to be called
     */
    void setOauthRestoreAccessToken(RestoreAccessTokenCallback restoreCallback);

    /**
     * Get the callback to be called when the access token and secret need to be saved for
     * later restoration
     */
    SaveAccessTokenCallback getOauthSaveAccessToken();

    /**
     * Get the callback to be called when the access token and secret need to be restored
     */
    RestoreAccessTokenCallback getOauthRestoreAccessToken();

    void hasBeenAuthorized() throws NotAuthorizedException;
}
