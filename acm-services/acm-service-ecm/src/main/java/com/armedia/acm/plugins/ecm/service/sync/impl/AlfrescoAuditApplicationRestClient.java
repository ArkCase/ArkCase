package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.mule.cmis.basic.auth.HttpInvokerUtil;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.kerberos.client.KerberosRestTemplate;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;


public class AlfrescoAuditApplicationRestClient
{
    private static final String KERBEROS_USERNAME_PREFIX = "KERBEROS/";
    private static final String APP_CONFIGURATION_ENTRY_NAME = "MuleAlfrescoLogin";

    private String protocol;
    private String host;
    private String port;
    private String contextRoot;
    private String username;
    private String password;

    private RestTemplate restTemplate;
    private String basicAuthenticationHeaderValue;

    private enum AlfrescoAuthenticationType
    {
        BASIC,
        KERBEROS
    }

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    public String baseUrl()
    {
        String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + getContextRoot();
        return url;
    }

    public JSONObject service(String applicationName, long startingAuditId) throws Exception
    {
        String url = baseUrl() + "/service/api/audit/query/" + applicationName;
        url = url + "?verbose=true&forward=true&limit=50&fromId=" + startingAuditId;

        HttpEntity<String> getEntity = buildRestEntity();

        ResponseEntity<String> response = getRestTemplate().exchange(url, HttpMethod.GET, getEntity, String.class);

        LOG.debug("query audit response: {}", response.getBody());

        if (HttpStatus.OK.equals(response.getStatusCode()))
        {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse;
        }
        else
        {
            throw new Exception("Could not complete record: " + response.getStatusCode());
        }
    }

    protected HttpEntity<String> buildRestEntity()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // external authentication header is added always, even if Alfresco is not setup to use it
        headers.set(HttpInvokerUtil.EXTERNAL_AUTH_KEY, HttpInvokerUtil.getExternalUserIdValue());

        // add basic authentication header for the call
        if (findAlfrescoAuthenticationType().equals(AlfrescoAuthenticationType.BASIC))
        {
            headers.set(HttpHeaders.AUTHORIZATION, getBasicAuthenticationHeaderValue());
        }

        return new HttpEntity<>(headers);
    }

    private AlfrescoAuthenticationType findAlfrescoAuthenticationType()
    {
        if (getUsername().startsWith(KERBEROS_USERNAME_PREFIX))
        {
            return AlfrescoAuthenticationType.KERBEROS;
        }

        return AlfrescoAuthenticationType.BASIC;
    }

    // not synchronizing this method because the value would always be set the same
    private String getBasicAuthenticationHeaderValue()
    {
        if (basicAuthenticationHeaderValue == null)
        {
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
            basicAuthenticationHeaderValue = "Basic " + new String(encodedAuth);
        }

        return basicAuthenticationHeaderValue;
    }


    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getContextRoot()
    {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot)
    {
        this.contextRoot = contextRoot;
    }

    synchronized protected RestTemplate getRestTemplate()
    {
        if (restTemplate == null)
        {
            createAndSetRestTemplate();
        }

        return restTemplate;
    }

    private void createAndSetRestTemplate()
    {

        switch (findAlfrescoAuthenticationType())
        {
            case KERBEROS:
                AppConfigurationEntry appConfigurationEntry = Configuration.getConfiguration()
                        .getAppConfigurationEntry(APP_CONFIGURATION_ENTRY_NAME)[0];
                restTemplate = new KerberosRestTemplate((String) appConfigurationEntry.getOptions().get("keytab"),
                        (String) appConfigurationEntry.getOptions().get("principal"));
                break;
            case BASIC:
                // basic authentication header will be added in the headers before each call
                restTemplate = new RestTemplate();
                break;
            default:
                // should not happen
                throw new RuntimeException("Alfresco authentication type unknown!");
        }
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

}
