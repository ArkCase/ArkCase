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

import com.armedia.acm.websockets.WebSocketConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

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
    private final Logger log = LogManager.getLogger(getClass());
    private WebSocketConfig socketConfig;

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
            configureBroker((StompBrokerRelayMessageHandler) bean);

            switch (socketConfig.getStompBrokerRelayProtocol())
            {
            case RELAY_PROTOCOL_SSL:
                setSSLOptions((StompBrokerRelayMessageHandler) bean);
                break;
            case RELAY_PROTOCOL_TCP:
                // by default Spring creates a regular TCP client
                break;
            default:
                throw new RuntimeException(
                        "Unknown relay protocol for Stomp broker relay handler: " + socketConfig.getStompBrokerRelayProtocol()
                                + ". Only 'tcp' and 'ssl' protocols are supported! Check your settings in the websocket.properties file.");
            }
        }

        return bean;
    }

    private void configureBroker(StompBrokerRelayMessageHandler bean)
    {
        bean.setRelayHost(socketConfig.getStompBrokerRelayHost());
        bean.setRelayPort(socketConfig.getStompBrokerRelayPort());
        bean.setSystemHeartbeatSendInterval(socketConfig.getStompBrokerRelayHeartbeatSendInterval());
        bean.setSystemHeartbeatReceiveInterval(socketConfig.getStompBrokerRelayHeartbeatReceiveInterval());
        bean.setClientLogin(socketConfig.getStompBrokerRelayClientLogin());
        bean.setClientPasscode(socketConfig.getStompBrokerRelayClientPasscode());
        bean.setSystemLogin(socketConfig.getStompBrokerRelaySystemLogin());
        bean.setSystemPasscode(socketConfig.getStompBrokerRelaySystemPasscode());
    }

    private void setSSLOptions(StompBrokerRelayMessageHandler handler)
    {
        try
        {
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(getTrustManagerFactory())
                    .keyManager(getKeyManagerFactory()).build();

            handler.setTcpClient(new ReactorNettyTcpClient<>(client -> client.host(socketConfig.getStompBrokerRelayHost())
                    .port(socketConfig.getStompBrokerRelayPort())
                    .secure(sslContextSpec -> {
                        sslContextSpec.sslContext(sslContext);
                    }), new StompReactorNettyCodec()));
        }
        catch (SSLException e)
        {
            log.error("Error initializing SSLContext", e.fillInStackTrace());
        }
    }

    private KeyManagerFactory getKeyManagerFactory()
    {
        try
        {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (InputStream in = new FileInputStream(socketConfig.getStompBrokerRelayKeystore()))
            {
                keystore.load(in, socketConfig.getStompBrokerRelayKeystorePassword().toCharArray());
            }
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, socketConfig.getStompBrokerRelayKeystorePassword().toCharArray());
            return keyManagerFactory;
        }
        catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | IOException e)
        {
            log.error("Error initializing KeyManager", e.fillInStackTrace());
            return null;
        }
    }

    private TrustManagerFactory getTrustManagerFactory()
    {
        TrustManagerFactory tmf;
        KeyStore trustedCertStore;

        try (InputStream trustStoreStream = new FileInputStream(socketConfig.getStompBrokerRelayTruststore()))
        {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustedCertStore = KeyStore.getInstance(socketConfig.getStompBrokerRelayTruststoreType());
            trustedCertStore.load(trustStoreStream, socketConfig.getStompBrokerRelayTruststorePass().toCharArray());
            tmf.init(trustedCertStore);
            return tmf;
        }
        catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException e)
        {
            log.error("Error initializing TrustManager", e.fillInStackTrace());
            return null;
        }
    }

    public WebSocketConfig getSocketConfig()
    {
        return socketConfig;
    }

    public void setSocketConfig(WebSocketConfig socketConfig)
    {
        this.socketConfig = socketConfig;
    }
}
