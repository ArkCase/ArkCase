package com.armedia.mule.cmis.kerberos;

import org.apache.chemistry.opencmis.client.bindings.impl.ClientVersion;
import org.apache.chemistry.opencmis.client.bindings.impl.CmisBindingsHelper;
import org.apache.chemistry.opencmis.client.bindings.spi.AbstractAuthenticationProvider;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpInvoker;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by bojan.milenkoski on 29.9.2016
 */
public class KerberosHttpInvoker implements HttpInvoker
{
    private static final Logger LOG = LoggerFactory.getLogger(KerberosHttpInvoker.class);

    private static final String APP_CONFIGURATION_ENTRY_NAME = "MuleAlfrescoLogin";

    private HttpClient httpClient;

    public KerberosHttpInvoker()
    {
    }

    @Override
    public Response invokeGET(UrlBuilder url, BindingSession session)
    {
        return invoke(url, "GET", null, null, null, session, null, null);
    }

    @Override
    public Response invokeGET(UrlBuilder url, BindingSession session, BigInteger offset, BigInteger length)
    {
        return invoke(url, "GET", null, null, null, session, offset, length);
    }

    @Override
    public Response invokePOST(UrlBuilder url, String contentType, Output writer, BindingSession session)
    {
        return invoke(url, "POST", contentType, null, writer, session, null, null);
    }

    @Override
    public Response invokePUT(UrlBuilder url, String contentType, Map<String, String> headers, Output writer, BindingSession session)
    {
        return invoke(url, "PUT", contentType, headers, writer, session, null, null);
    }

    @Override
    public Response invokeDELETE(UrlBuilder url, BindingSession session)
    {
        return invoke(url, "DELETE", null, null, null, session, null, null);
    }

    private Response invoke(final UrlBuilder url, String method, String contentType, Map<String, String> headers, Output writer,
            BindingSession session, BigInteger offset, BigInteger length)
    {
        String user = "";
        String password = "";

        try
        {
            LOG.debug("Http method: {}. URL: {}", method, url);

            // authenticate
            AbstractAuthenticationProvider authProvider = (AbstractAuthenticationProvider) CmisBindingsHelper
                    .getAuthenticationProvider(session);
            if (authProvider != null)
            {

                Object userObject = authProvider.getSession().get(SessionParameter.USER);
                if (userObject instanceof String)
                {
                    user = (String) userObject;
                }

                Object passwordObject = authProvider.getSession().get(SessionParameter.PASSWORD);
                if (passwordObject instanceof String)
                {
                    password = (String) passwordObject;
                }
            }

            HttpResponse response = null;
            try
            {
                LOG.debug("User: {} | Password-length: {}", user, password.length());

                LoginContext loginContext = new LoginContext(APP_CONFIGURATION_ENTRY_NAME, new KerberosCallBackHandler(user, password));
                loginContext.login();

                PrivilegedAction<HttpResponse> sendAction = new PrivilegedAction<HttpResponse>()
                {
                    @Override
                    public HttpResponse run()
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

                            return call(url.toString(), method, contentType, headers, writer, session, offset, length, authProvider);
                        }
                        catch (IOException e)
                        {
                            LOG.error("IOException", e);
                            return null;
                        }
                    }
                };

                response = Subject.doAs(loginContext.getSubject(), sendAction);
            }
            catch (LoginException le)
            {
                LOG.error("Failed login!", le);
            }

            // get the response
            Map<String, List<String>> responseHeaders = Arrays.stream(response.getAllHeaders())
                    .collect(Collectors.toMap(header -> header.getName(), header -> Arrays.stream(header.getElements())
                            .map(headerElement -> headerElement.getValue()).collect(Collectors.toList())));

            return new Response(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), responseHeaders,
                    response.getEntity() == null ? null : response.getEntity().getContent(),
                    response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300 ? null
                            : new ByteArrayInputStream(response.getStatusLine().getReasonPhrase().getBytes()));
        }
        catch (Exception e)
        {
            throw new CmisConnectionException("Cannot access " + url + ": " + e.getMessage(), e);
        }
    }

    private HttpResponse call(String url, String method, String contentType, Map<String, String> headers, Output writer,
            BindingSession session, BigInteger offset, BigInteger length, AbstractAuthenticationProvider authProvider) throws IOException
    {
        HttpClient httpclient = getHttpClient(session);

        HttpUriRequest request = null;
        switch (method)
        {
        case "GET":
            request = new HttpGet(url);
            break;
        case "POST":
            request = new HttpPost(url);
            break;
        case "PUT":
            request = new HttpPut(url);
            break;
        case "DELETE":
            request = new HttpDelete(url);
            break;
        default:
            throw new RuntimeException("HTTP method: " + method + " not implemented!");
        }

        request.setHeader(HTTP.USER_AGENT, ClientVersion.OPENCMIS_CLIENT);

        if (authProvider != null)
        {
            Map<String, List<String>> httpHeaders = authProvider.getHTTPHeaders(url.toString());
            if (httpHeaders != null)
            {
                for (Map.Entry<String, List<String>> header : httpHeaders.entrySet())
                {
                    if (header.getKey() != null && header.getValue() != null && !header.getValue().isEmpty())
                    {
                        String key = header.getKey();
                        if (key.equalsIgnoreCase("user-agent"))
                        {
                            request.setHeader(HTTP.USER_AGENT, header.getValue().get(0));
                        }
                        else
                        {
                            for (String value : header.getValue())
                            {
                                if (value != null)
                                {
                                    request.addHeader(key, value);
                                }
                            }
                        }
                    }
                }
            }
        }

        // set content type
        if (contentType != null)
        {
            request.setHeader(HTTP.CONTENT_TYPE, contentType);
        }

        // set other headers
        if (headers != null)
        {
            for (Map.Entry<String, String> header : headers.entrySet())
            {
                request.setHeader(header.getKey(), header.getValue());
            }
        }

        // range
        if ((offset != null) || (length != null))
        {
            StringBuilder sb = new StringBuilder("bytes=");

            if ((offset == null) || (offset.signum() == -1))
            {
                offset = BigInteger.ZERO;
            }

            sb.append(offset.toString());
            sb.append("-");

            if ((length != null) && (length.signum() == 1))
            {
                sb.append(offset.add(length.subtract(BigInteger.ONE)).toString());
            }

            request.addHeader("Range", sb.toString());
        }

        // locale
        if (session.get(CmisBindingsHelper.ACCEPT_LANGUAGE) instanceof String)
        {
            request.addHeader("Accept-Language", session.get(CmisBindingsHelper.ACCEPT_LANGUAGE).toString());
        }

        // compression
        Object compression = session.get(SessionParameter.COMPRESSION);
        if ((compression != null) && Boolean.parseBoolean(compression.toString()))
        {
            request.addHeader("Accept-Encoding", "gzip,deflate");
        }

        // send data
        if (writer != null)
        {
            AbstractHttpEntity entity = new AbstractHttpEntity()
            {
                @Override
                public boolean isRepeatable()
                {
                    // must be repeatable because of Kerberos authentication flow
                    return true;
                }

                @Override
                public long getContentLength()
                {
                    return -1;
                }

                @Override
                public boolean isStreaming()
                {
                    return false;
                }

                @Override
                public InputStream getContent() throws IOException
                {
                    // Should be implemented as well but is irrelevant for this case
                    throw new UnsupportedOperationException();
                }

                @Override
                public void writeTo(final OutputStream outstream) throws IOException
                {
                    if (outstream == null)
                    {
                        throw new IllegalArgumentException("Output stream may not be null");
                    }
                    try
                    {
                        writer.write(outstream);
                    }
                    catch (Exception e)
                    {
                        throw new IOException(e);
                    }
                }
            };

            Object clientCompression = session.get(SessionParameter.CLIENT_COMPRESSION);

            if ((clientCompression != null) && Boolean.parseBoolean(clientCompression.toString()))
            {
                GzipCompressingEntity gzipCompressingEntity = new GzipCompressingEntity(entity);
                ((HttpPost) request).setEntity(gzipCompressingEntity);
            }
            else
            {
                ((HttpPost) request).setEntity(entity);
            }
        }

        // log after connect
        LOG.trace("Headers: {}", (Object[]) request.getAllHeaders());

        HttpResponse response = httpclient.execute(request);

        LOG.debug("----------------------------------------");

        LOG.debug("STATUS >> {}", response.getStatusLine());

        LOG.debug("----------------------------------------");

        return response;
    }

    private synchronized HttpClient getHttpClient(BindingSession session)
    {
        if (httpClient == null)
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

            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

            // timeouts
            int connectTimeout = session.get(SessionParameter.CONNECT_TIMEOUT, -1);
            if (connectTimeout >= 0)
            {
                requestConfigBuilder.setConnectTimeout(connectTimeout);
                requestConfigBuilder.setSocketTimeout(connectTimeout);
            }

            int readTimeout = session.get(SessionParameter.READ_TIMEOUT, -1);
            if (readTimeout >= 0)
            {
                requestConfigBuilder.setConnectionRequestTimeout(readTimeout);
            }

            // compression
            Object compression = session.get(SessionParameter.COMPRESSION);
            if ((compression != null) && Boolean.parseBoolean(compression.toString()))
            {
                requestConfigBuilder.setContentCompressionEnabled(true);
            }

            RequestConfig requestConfig = requestConfigBuilder.build();

            httpClient = HttpClients.custom().setDefaultAuthSchemeRegistry(authSchemeRegistry).setDefaultCredentialsProvider(credsProvider)
                    .setDefaultRequestConfig(requestConfig).build();
        }
        return httpClient;
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

}
