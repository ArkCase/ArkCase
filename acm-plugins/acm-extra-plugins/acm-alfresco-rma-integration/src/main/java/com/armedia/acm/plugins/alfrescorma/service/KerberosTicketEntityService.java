package com.armedia.acm.plugins.alfrescorma.service;

import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Set;

/**
 * Implementation of {@link TicketEntityService} that uses Kerberos authentication for the Alfresco ticket service call.
 * <p>
 * Created by bojan.milenkoski on 10.11.2016
 */
public class KerberosTicketEntityService implements TicketEntityService
{
    private static final String APP_CONFIGURATION_ENTRY_NAME = "MuleAlfrescoLogin";

    private final String service = "wcservice/kerberosLogin.xml";

    private HttpComponentsClientHttpRequestFactory requestFactory;

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public ResponseEntity<String> getEntity(String baseUrl, String user, String password) throws RestClientException
    {
        String ticketUrl = baseUrl + "/" + service;

        try
        {
            LOG.debug("User: {} | Password-length: {}", user, password.length());

            LoginContext loginContext = new LoginContext(APP_CONFIGURATION_ENTRY_NAME, new KerberosCallBackHandler(user, password));
            loginContext.login();

            PrivilegedAction<ClientHttpResponse> sendAction = new PrivilegedAction<ClientHttpResponse>()
            {
                @Override
                public ClientHttpResponse run()
                {
                    try
                    {
                        Subject current = Subject.getSubject(AccessController.getContext());
                        LOG.debug("----------------------------------------");

                        Set<Principal> principals = current.getPrincipals();
                        for (Principal next : principals)
                        {
                            LOG.debug("DOAS Principal: {}", next.getName());
                        }
                        LOG.debug("----------------------------------------");

                        return call(ticketUrl);
                    }
                    catch (IOException e)
                    {
                        LOG.error("IOException", e);
                        throw new RestClientException("IOException on rest client call", e);
                    }
                }
            };

            ClientHttpResponse response = Subject.doAs(loginContext.getSubject(), sendAction);

            return new ResponseEntityResponseExtractor<String>(String.class).extractData(response);
        }
        catch (LoginException le)
        {
            LOG.error("Failed login!", le);
            throw new RestClientException("LoginException on rest client call", le);
        }
        catch (IOException e)
        {
            LOG.error("IOException", e);
            throw new RestClientException("IOException on rest client call", e);
        }
    }

    private synchronized HttpComponentsClientHttpRequestFactory getRequestFactory()
    {
        if (requestFactory == null)
        {
            Credentials use_jaas_creds = new Credentials()
            {
                @Override
                public String getPassword()
                {
                    return null;
                }

                @Override
                public Principal getUserPrincipal()
                {
                    return null;
                }
            };

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(null, -1, null), use_jaas_creds);
            Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider> create()
                    .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory(true)).build();

            HttpClient httpClient = HttpClients.custom().setDefaultAuthSchemeRegistry(authSchemeRegistry)
                    .setDefaultCredentialsProvider(credsProvider).build();

            requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        }
        return requestFactory;
    }

    class KerberosCallBackHandler implements CallbackHandler
    {

        private final String user;
        private final String password;

        public KerberosCallBackHandler(String user, String password)
        {
            this.user = user;
            this.password = password;
        }

        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
        {

            for (Callback callback : callbacks)
            {

                if (callback instanceof NameCallback)
                {
                    NameCallback nc = (NameCallback) callback;
                    nc.setName(user);
                }
                else if (callback instanceof PasswordCallback)
                {
                    PasswordCallback pc = (PasswordCallback) callback;
                    pc.setPassword(password.toCharArray());
                }
                else
                {
                    throw new UnsupportedCallbackException(callback, "Unknown Callback");
                }

            }
        }
    }

    private ClientHttpResponse call(String url) throws IOException
    {
        ClientHttpRequest request = null;
        try
        {
            request = getRequestFactory().createRequest(new URI(url), HttpMethod.GET);
        }
        catch (URISyntaxException e)
        {
            LOG.error("Invalid URI!", e);
            throw new IOException("Invalid URI!", e);
        }

        ClientHttpResponse response = request.execute();

        LOG.debug("----------------------------------------");

        LOG.debug("STATUS >> {}", response.getStatusCode());

        LOG.debug("----------------------------------------");

        return response;
    }

    /**
     * Response extractor for {@link HttpEntity}.
     */
    private class ResponseEntityResponseExtractor<T> implements ResponseExtractor<ResponseEntity<T>>
    {

        private final HttpMessageConverterExtractor<T> delegate;
        private final HttpMessageConverter<String> converter = new StringHttpMessageConverter();

        public ResponseEntityResponseExtractor(Type responseType)
        {
            if (responseType != null && !Void.class.equals(responseType))
            {
                this.delegate = new HttpMessageConverterExtractor<>(responseType, Collections.singletonList(converter));
            }
            else
            {
                this.delegate = null;
            }
        }

        @Override
        public ResponseEntity<T> extractData(ClientHttpResponse response) throws IOException
        {
            if (this.delegate != null)
            {
                T body = this.delegate.extractData(response);
                return new ResponseEntity<>(body, response.getHeaders(), response.getStatusCode());
            }
            else
            {
                return new ResponseEntity<>(response.getHeaders(), response.getStatusCode());
            }
        }
    }
}
