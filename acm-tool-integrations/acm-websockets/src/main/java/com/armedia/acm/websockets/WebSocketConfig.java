package com.armedia.acm.websockets;

/*-
 * #%L
 * Tool Integrations: ArkCase Web Sockets
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import org.springframework.beans.factory.annotation.Value;

public class WebSocketConfig
{
    @Value("${acm.websockets.enabled}")
    private boolean websocketsEnabled;

    @Value("${acm.websockets.stomp_endpoint}")
    private String stompEndpoint;

    @Value("${acm.websockets.application_destination_prefix}")
    private String applicationDestinationPrefix;

    @Value("${acm.websockets.stomp_broker_relay.relay_protocol}")
    private String stompBrokerRelayProtocol;

    @Value("${acm.websockets.stomp_broker_relay.keystore}")
    private String stompBrokerRelayKeystore;

    @Value("${acm.websockets.stomp_broker_relay.keystore_pass}")
    private String stompBrokerRelayKeystorePassword;

    @Value("${acm.websockets.stomp_broker_relay.truststore}")
    private String stompBrokerRelayTruststore;

    @Value("${acm.websockets.stomp_broker_relay.truststore_pass}")
    private String stompBrokerRelayTruststorePass;

    @Value("${acm.websockets.stomp_broker_relay.truststore_type}")
    private String stompBrokerRelayTruststoreType;

    @Value("${acm.websockets.stomp_broker_relay.relay_host}")
    private String stompBrokerRelayHost;

    @Value("${acm.websockets.stomp_broker_relay.relay_port}")
    private Integer stompBrokerRelayPort;

    @Value("${acm.websockets.stomp_broker_relay.heartbeat_send_interval}")
    private Integer stompBrokerRelayHeartbeatSendInterval;

    @Value("${acm.websockets.stomp_broker_relay.heartbeat_receive_interval}")
    private Integer stompBrokerRelayHeartbeatReceiveInterval;

    @Value("${acm.websockets.stomp_broker_relay.client_login}")
    private String stompBrokerRelayClientLogin;

    @Value("${acm.websockets.stomp_broker_relay.client_passcode}")
    private String stompBrokerRelayClientPasscode;

    @Value("${acm.websockets.stomp_broker_relay.system_login}")
    private String stompBrokerRelaySystemLogin;

    @Value("${acm.websockets.stomp_broker_relay.system_passcode}")
    private String stompBrokerRelaySystemPasscode;

    @Value("${acm.websockets.socksjs.enabled}")
    private Boolean sockjsEnabled;

    public boolean isWebsocketsEnabled()
    {
        return websocketsEnabled;
    }

    public void setWebsocketsEnabled(boolean websocketsEnabled)
    {
        this.websocketsEnabled = websocketsEnabled;
    }

    public String getStompEndpoint()
    {
        return stompEndpoint;
    }

    public void setStompEndpoint(String stompEndpoint)
    {
        this.stompEndpoint = stompEndpoint;
    }

    public String getApplicationDestinationPrefix()
    {
        return applicationDestinationPrefix;
    }

    public void setApplicationDestinationPrefix(String applicationDestinationPrefix)
    {
        this.applicationDestinationPrefix = applicationDestinationPrefix;
    }

    public String getStompBrokerRelayProtocol()
    {
        return stompBrokerRelayProtocol;
    }

    public void setStompBrokerRelayProtocol(String stompBrokerRelayProtocol)
    {
        this.stompBrokerRelayProtocol = stompBrokerRelayProtocol;
    }

    public String getStompBrokerRelayKeystore()
    {
        return stompBrokerRelayKeystore;
    }

    public void setStompBrokerRelayKeystore(String stompBrokerRelayKeystore)
    {
        this.stompBrokerRelayKeystore = stompBrokerRelayKeystore;
    }

    public String getStompBrokerRelayKeystorePassword()
    {
        return stompBrokerRelayKeystorePassword;
    }

    public void setStompBrokerRelayKeystorePassword(String stompBrokerRelayKeystorePassword)
    {
        this.stompBrokerRelayKeystorePassword = stompBrokerRelayKeystorePassword;
    }

    public String getStompBrokerRelayTruststore()
    {
        return stompBrokerRelayTruststore;
    }

    public void setStompBrokerRelayTruststore(String stompBrokerRelayTruststore)
    {
        this.stompBrokerRelayTruststore = stompBrokerRelayTruststore;
    }

    public String getStompBrokerRelayTruststorePass()
    {
        return stompBrokerRelayTruststorePass;
    }

    public void setStompBrokerRelayTruststorePass(String stompBrokerRelayTruststorePass)
    {
        this.stompBrokerRelayTruststorePass = stompBrokerRelayTruststorePass;
    }

    public String getStompBrokerRelayTruststoreType()
    {
        return stompBrokerRelayTruststoreType;
    }

    public void setStompBrokerRelayTruststoreType(String stompBrokerRelayTruststoreType)
    {
        this.stompBrokerRelayTruststoreType = stompBrokerRelayTruststoreType;
    }

    public String getStompBrokerRelayHost()
    {
        return stompBrokerRelayHost;
    }

    public void setStompBrokerRelayHost(String stompBrokerRelayHost)
    {
        this.stompBrokerRelayHost = stompBrokerRelayHost;
    }

    public Integer getStompBrokerRelayPort()
    {
        return stompBrokerRelayPort;
    }

    public void setStompBrokerRelayPort(Integer stompBrokerRelayPort)
    {
        this.stompBrokerRelayPort = stompBrokerRelayPort;
    }

    public Integer getStompBrokerRelayHeartbeatSendInterval()
    {
        return stompBrokerRelayHeartbeatSendInterval;
    }

    public void setStompBrokerRelayHeartbeatSendInterval(Integer stompBrokerRelayHeartbeatSendInterval)
    {
        this.stompBrokerRelayHeartbeatSendInterval = stompBrokerRelayHeartbeatSendInterval;
    }

    public Integer getStompBrokerRelayHeartbeatReceiveInterval()
    {
        return stompBrokerRelayHeartbeatReceiveInterval;
    }

    public void setStompBrokerRelayHeartbeatReceiveInterval(Integer stompBrokerRelayHeartbeatReceiveInterval)
    {
        this.stompBrokerRelayHeartbeatReceiveInterval = stompBrokerRelayHeartbeatReceiveInterval;
    }

    public String getStompBrokerRelayClientLogin()
    {
        return stompBrokerRelayClientLogin;
    }

    public void setStompBrokerRelayClientLogin(String stompBrokerRelayClientLogin)
    {
        this.stompBrokerRelayClientLogin = stompBrokerRelayClientLogin;
    }

    public String getStompBrokerRelayClientPasscode()
    {
        return stompBrokerRelayClientPasscode;
    }

    public void setStompBrokerRelayClientPasscode(String stompBrokerRelayClientPasscode)
    {
        this.stompBrokerRelayClientPasscode = stompBrokerRelayClientPasscode;
    }

    public String getStompBrokerRelaySystemLogin()
    {
        return stompBrokerRelaySystemLogin;
    }

    public void setStompBrokerRelaySystemLogin(String stompBrokerRelaySystemLogin)
    {
        this.stompBrokerRelaySystemLogin = stompBrokerRelaySystemLogin;
    }

    public String getStompBrokerRelaySystemPasscode()
    {
        return stompBrokerRelaySystemPasscode;
    }

    public void setStompBrokerRelaySystemPasscode(String stompBrokerRelaySystemPasscode)
    {
        this.stompBrokerRelaySystemPasscode = stompBrokerRelaySystemPasscode;
    }

    public Boolean getSockjsEnabled()
    {
        return sockjsEnabled;
    }

    public void setSockjsEnabled(Boolean sockjsEnabled)
    {
        this.sockjsEnabled = sockjsEnabled;
    }
}
