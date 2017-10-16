package com.armedia.mule.cmis.basic.auth;

import org.apache.chemistry.opencmis.client.bindings.impl.ClientVersion;
import org.apache.chemistry.opencmis.client.bindings.impl.CmisBindingsHelper;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.DefaultHttpInvoker;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpInvoker;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.spi.AuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * This class is a copy of {@link DefaultHttpInvoker} class with added 'X-Alfresco-Remote-User' header field.
 * The only added line is:
 * <p>
 * {@code
 *    conn.addRequestProperty(HttpInvokerUtil.EXTERNAL_AUTH_KEY, HttpInvokerUtil.getExternalUserValue());
 * }
 * <p>
 * Created by bojan.milenkoski on 23.11.2016
 */
public class BasicAuthenticationHttpInvoker implements HttpInvoker
{

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    public BasicAuthenticationHttpInvoker()
    {
    }

    public Response invokeGET(UrlBuilder url, BindingSession session)
    {
        return invoke(url, "GET", null, null, null, session, null, null);
    }

    public Response invokeGET(UrlBuilder url, BindingSession session, BigInteger offset, BigInteger length)
    {
        return invoke(url, "GET", null, null, null, session, offset, length);
    }

    public Response invokePOST(UrlBuilder url, String contentType, Output writer, BindingSession session)
    {
        return invoke(url, "POST", contentType, null, writer, session, null, null);
    }

    public Response invokePUT(UrlBuilder url, String contentType, Map<String, String> headers, Output writer, BindingSession session)
    {
        return invoke(url, "PUT", contentType, headers, writer, session, null, null);
    }

    public Response invokeDELETE(UrlBuilder url, BindingSession session)
    {
        return invoke(url, "DELETE", null, null, null, session, null, null);
    }

    private Response invoke(UrlBuilder url, String method, String contentType, Map<String, String> headers, Output writer,
            BindingSession session, BigInteger offset, BigInteger length)
    {
        try
        {
            // log before connect
            if (LOG.isDebugEnabled())
            {
                LOG.debug(method + " " + url);
            }

            // connect
            HttpURLConnection conn = (HttpURLConnection) (new URL(url.toString())).openConnection();
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(writer != null);
            conn.setAllowUserInteraction(false);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", ClientVersion.OPENCMIS_CLIENT);

            // timeouts
            int connectTimeout = session.get(SessionParameter.CONNECT_TIMEOUT, -1);
            if (connectTimeout >= 0)
            {
                conn.setConnectTimeout(connectTimeout);
            }

            int readTimeout = session.get(SessionParameter.READ_TIMEOUT, -1);
            if (readTimeout >= 0)
            {
                conn.setReadTimeout(readTimeout);
            }

            // set content type
            if (contentType != null)
            {
                conn.setRequestProperty("Content-Type", contentType);
            }
            // set other headers
            if (headers != null)
            {
                for (Map.Entry<String, String> header : headers.entrySet())
                {
                    conn.addRequestProperty(header.getKey(), header.getValue());
                }
            }

            // authenticate
            AuthenticationProvider authProvider = CmisBindingsHelper.getAuthenticationProvider(session);
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
                                conn.setRequestProperty("User-Agent", header.getValue().get(0));
                            }
                            else
                            {
                                for (String value : header.getValue())
                                {
                                    if (value != null)
                                    {
                                        conn.addRequestProperty(key, value);
                                    }
                                }
                            }
                        }
                    }
                }
                
                conn.addRequestProperty(HttpInvokerUtil.EXTERNAL_AUTH_KEY, HttpInvokerUtil.getExternalUserIdValue());
                
                if (conn instanceof HttpsURLConnection)
                {
                    SSLSocketFactory sf = authProvider.getSSLSocketFactory();
                    if (sf != null)
                    {
                        ((HttpsURLConnection) conn).setSSLSocketFactory(sf);
                    }

                    HostnameVerifier hv = authProvider.getHostnameVerifier();
                    if (hv != null)
                    {
                        ((HttpsURLConnection) conn).setHostnameVerifier(hv);
                    }
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

                conn.setRequestProperty("Range", sb.toString());
            }

            // compression
            Object compression = session.get(SessionParameter.COMPRESSION);
            if ((compression != null) && Boolean.parseBoolean(compression.toString()))
            {
                conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            }

            // locale
            if (session.get(CmisBindingsHelper.ACCEPT_LANGUAGE) instanceof String)
            {
                conn.setRequestProperty("Accept-Language", session.get(CmisBindingsHelper.ACCEPT_LANGUAGE).toString());
            }

            // send data
            if (writer != null)
            {
                conn.setChunkedStreamingMode((64 * 1024) - 1);

                OutputStream connOut = null;

                Object clientCompression = session.get(SessionParameter.CLIENT_COMPRESSION);
                if ((clientCompression != null) && Boolean.parseBoolean(clientCompression.toString()))
                {
                    conn.setRequestProperty("Content-Encoding", "gzip");
                    connOut = new GZIPOutputStream(conn.getOutputStream(), 4096);
                }
                else
                {
                    connOut = conn.getOutputStream();
                }

                OutputStream out = new BufferedOutputStream(connOut, BUFFER_SIZE);
                writer.write(out);
                out.flush();

                if (connOut instanceof GZIPOutputStream)
                {
                    ((GZIPOutputStream) connOut).finish();
                }
            }

            // connect
            conn.connect();

            // get stream, if present
            int respCode = conn.getResponseCode();
            InputStream inputStream = null;
            if ((respCode == 200) || (respCode == 201) || (respCode == 203) || (respCode == 206))
            {
                inputStream = conn.getInputStream();
            }

            // log after connect
            if (LOG.isTraceEnabled())
            {
                LOG.trace(method + " " + url + " > Headers: " + conn.getHeaderFields());
            }

            // forward response HTTP headers
            if (authProvider != null)
            {
                authProvider.putResponseHeaders(url.toString(), respCode, conn.getHeaderFields());
            }

            // get the response
            return new Response(respCode, conn.getResponseMessage(), conn.getHeaderFields(), inputStream, conn.getErrorStream());
        }
        catch (Exception e)
        {
            throw new CmisConnectionException("Cannot access " + url + ": " + e.getMessage(), e);
        }
    }
}
