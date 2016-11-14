
package org.mule.module.cmis.connectivity;

import javax.annotation.Generated;


/**
 * A tuple of connection parameters
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CMISCloudConnectorConnectionKey {

    /**
     * 
     */
    private String username;
    /**
     * 
     */
    private String password;
    /**
     * 
     */
    private String baseUrl;
    /**
     * 
     */
    private String repositoryId;
    /**
     * 
     */
    private String endpoint;
    /**
     * 
     */
    private String connectionTimeout;
    /**
     * 
     */
    private String useAlfrescoExtension;
    /**
     * 
     */
    private String cxfPortProvider;

    public CMISCloudConnectorConnectionKey(String username, String password, String baseUrl, String repositoryId, String endpoint, String connectionTimeout, String useAlfrescoExtension, String cxfPortProvider) {
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
        this.repositoryId = repositoryId;
        this.endpoint = endpoint;
        this.connectionTimeout = connectionTimeout;
        this.useAlfrescoExtension = useAlfrescoExtension;
        this.cxfPortProvider = cxfPortProvider;
    }

    /**
     * Sets baseUrl
     * 
     * @param value Value to set
     */
    public void setBaseUrl(String value) {
        this.baseUrl = value;
    }

    /**
     * Retrieves baseUrl
     * 
     */
    public String getBaseUrl() {
        return this.baseUrl;
    }

    /**
     * Sets username
     * 
     * @param value Value to set
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Retrieves username
     * 
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets connectionTimeout
     * 
     * @param value Value to set
     */
    public void setConnectionTimeout(String value) {
        this.connectionTimeout = value;
    }

    /**
     * Retrieves connectionTimeout
     * 
     */
    public String getConnectionTimeout() {
        return this.connectionTimeout;
    }

    /**
     * Sets useAlfrescoExtension
     * 
     * @param value Value to set
     */
    public void setUseAlfrescoExtension(String value) {
        this.useAlfrescoExtension = value;
    }

    /**
     * Retrieves useAlfrescoExtension
     * 
     */
    public String getUseAlfrescoExtension() {
        return this.useAlfrescoExtension;
    }

    /**
     * Sets cxfPortProvider
     * 
     * @param value Value to set
     */
    public void setCxfPortProvider(String value) {
        this.cxfPortProvider = value;
    }

    /**
     * Retrieves cxfPortProvider
     * 
     */
    public String getCxfPortProvider() {
        return this.cxfPortProvider;
    }

    /**
     * Sets repositoryId
     * 
     * @param value Value to set
     */
    public void setRepositoryId(String value) {
        this.repositoryId = value;
    }

    /**
     * Retrieves repositoryId
     * 
     */
    public String getRepositoryId() {
        return this.repositoryId;
    }

    /**
     * Sets password
     * 
     * @param value Value to set
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Retrieves password
     * 
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets endpoint
     * 
     * @param value Value to set
     */
    public void setEndpoint(String value) {
        this.endpoint = value;
    }

    /**
     * Retrieves endpoint
     * 
     */
    public String getEndpoint() {
        return this.endpoint;
    }

    public int hashCode() {
        int hash = 1;
        hash = (hash* 31);
        if (this.username!= null) {
            hash += this.username.hashCode();
        }
        hash = (hash* 31);
        if (this.baseUrl!= null) {
            hash += this.baseUrl.hashCode();
        }
        return hash;
    }

    public boolean equals(Object obj) {
        return (((((obj instanceof CMISCloudConnectorConnectionKey)&&(this.username!= null))&&this.username.equals(((CMISCloudConnectorConnectionKey) obj).username))&&(this.baseUrl!= null))&&this.baseUrl.equals(((CMISCloudConnectorConnectionKey) obj).baseUrl));
    }

}
