package com.armedia.acm.websockets.configuration;

/*-
 * #%L
 * Tool Integrations: ArkCase Web Sockets
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.Reactor2StompCodec;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.messaging.tcp.reactor.Reactor2TcpClient;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import reactor.Environment;
import reactor.core.config.ConfigurationReader;
import reactor.core.config.PropertiesConfigurationReader;
import reactor.core.support.NamedDaemonThreadFactory;
import reactor.fn.Supplier;
import reactor.io.net.NetStreams;
import reactor.io.net.Spec;
import reactor.io.net.config.SslOptions;
import reactor.io.net.impl.netty.NettyClientSocketOptions;

/**
 * Since {@link DelegatingWebSocketMessageBrokerConfiguration} class does not expose the tcpClient property from
 * {@link StompBrokerRelayMessageHandler} class we cannot set SSL communication with ActiveMQ through configuration.
 * This class modifies the
 * bean created by setting the tcpClient property.
 * <p>
 * Created by Bojan Milenkoski on 25.7.2016
 */
public class StompBrokerRelayMessageHandlerBeanPostProcessor implements BeanPostProcessor
{
    private static final String RELAY_PROTOCOL_SSL = "ssl";
    private static final String RELAY_PROTOCOL_TCP = "tcp";
    private Logger log = LogManager.getLogger(getClass());
    private ApplicationContext applicationContext;
    private String relayProtocol;
    private String keyStore;
    private String keyStorePass;
    private String trustStore;
    private String trustStorePass;
    private String trustStoreType;
    private String host;
    private int port;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        // No changes before initialization
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        if (bean instanceof StompBrokerRelayMessageHandler)
        {
            switch (relayProtocol)
            {
            case RELAY_PROTOCOL_SSL:
                setSSLOptions((StompBrokerRelayMessageHandler) bean);
                break;
            case RELAY_PROTOCOL_TCP:
                // by default Spring creates a regular TCP client
                break;
            default:
                throw new RuntimeException("Unknown relay protocol for Stomp broker relay handler: " + relayProtocol
                        + ". Only 'tcp' and 'ssl' protocols are supported! Check your settings in the websocket.properties file.");
            }
        }

        return bean;
    }

    private void setSSLOptions(StompBrokerRelayMessageHandler handler)
    {
        Reactor2StompCodec codec = new Reactor2StompCodec();
        ConfigurationReader reader = new PropertiesConfigurationReader();


        SslOptions sslOptions = new SslOptions().sslProtocol("TLS").keystoreFile(keyStore).keystorePasswd(keyStorePass)
                .trustManagers(getTrustManager(trustStore, trustStorePass, trustStoreType)).trustManagerPasswd(trustStorePass);

        InetSocketAddress socketAddress = new InetSocketAddress(host, port);

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(),
                new NamedDaemonThreadFactory("reactor-tcp-io"));
        final NettyClientSocketOptions nettyClientSocketOptions = (new NettyClientSocketOptions()).eventLoopGroup(eventLoopGroup);

        NetStreams.TcpClientFactory<Message<byte[]>, Message<byte[]>> tcpClientSpecFactory = new NetStreams.TcpClientFactory<Message<byte[]>, Message<byte[]>>()
        {
            @Override
            public Spec.TcpClientSpec<Message<byte[]>, Message<byte[]>> apply(Spec.TcpClientSpec<Message<byte[]>, Message<byte[]>> spec)
            {
                try (Environment environment = new Environment(reader))
                {
                    return spec.env(environment).codec(codec).connect(socketAddress).ssl(sslOptions).options(nettyClientSocketOptions);
                }
                catch (IOException ex)
                {
                    log.error(ex.getMessage(), ex);
                    return null;
                }
            }
        };
        handler.setTcpClient(new Reactor2TcpClient<>(tcpClientSpecFactory));
    }

    private Supplier<TrustManager[]> getTrustManager(String trustStore, String trustStorePass, String trustStoreType)
    {
        return new Supplier<TrustManager[]>()
        {
            @Override
            public TrustManager[] get()
            {
                TrustManagerFactory tmf;
                KeyStore trustedCertStore;

                TrustManager[] trustManagers = null;

                try (InputStream trustStoreStream = new FileInputStream(new File(trustStore)))
                {
                    tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    trustedCertStore = KeyStore.getInstance(trustStoreType);
                    trustedCertStore.load(trustStoreStream, trustStorePass.toCharArray());
                    tmf.init(trustedCertStore);
                    trustManagers = tmf.getTrustManagers();
                }
                catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException e)
                {
                    log.error("Error initializing TrustManager [{}]", e.fillInStackTrace());
                }

                return trustManagers;
            }
        };
    }

    /**
     * @return the keyStore
     */
    public String getKeyStore()
    {
        return keyStore;
    }

    /**
     * @param keyStore
     *            the keyStore to set
     */
    public void setKeyStore(String keyStore)
    {
        this.keyStore = keyStore;
    }

    /**
     * @return the keyStorePass
     */
    public String getKeyStorePass()
    {
        return keyStorePass;
    }

    /**
     * @param keyStorePass
     *            the keyStorePass to set
     */
    public void setKeyStorePass(String keyStorePass)
    {
        this.keyStorePass = keyStorePass;
    }

    /**
     * @return the trustStore
     */
    public String getTrustStore()
    {
        return trustStore;
    }

    /**
     * @param trustStore
     *            the trustStore to set
     */
    public void setTrustStore(String trustStore)
    {
        this.trustStore = trustStore;
    }

    /**
     * @return the trustStorePass
     */
    public String getTrustStorePass()
    {
        return trustStorePass;
    }

    /**
     * @param trustStorePass
     *            the trustStorePass to set
     */
    public void setTrustStorePass(String trustStorePass)
    {
        this.trustStorePass = trustStorePass;
    }

    /**
     * @return the trustStoreType
     */
    public String getTrustStoreType()
    {
        return trustStoreType;
    }

    /**
     * @param trustStoreType
     *            the trustStoreType to set
     */
    public void setTrustStoreType(String trustStoreType)
    {
        this.trustStoreType = trustStoreType;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * @return the applicationContext
     */
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * @param applicationContext
     *            the applicationContext to set
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    /**
     * @return the relayProtocol
     */
    public String getRelayProtocol()
    {
        return relayProtocol;
    }

    /**
     * @param relayProtocol
     *            the relayProtocol to set
     */
    public void setRelayProtocol(String relayProtocol)
    {
        this.relayProtocol = relayProtocol;
    }
}
