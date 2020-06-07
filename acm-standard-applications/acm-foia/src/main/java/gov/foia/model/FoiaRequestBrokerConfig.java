package gov.foia.model;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

public class FoiaRequestBrokerConfig
{
    @Value("${gov.foia.broker.url}")
    private String url;

    @Value("${gov.foia.broker.keystore}")
    private String keystore;

    @Value("${gov.foia.broker.keystore.password}")
    private String keystorePassword;

    @Value("${gov.foia.broker.truststore}")
    private String truststore;

    @Value("${gov.foia.broker.truststore.password}")
    private String truststorePassword;

    @Value("${gov.foia.broker.max_concurrent_workers}")
    private Integer maxConcurrentWorkers;

    @Value("${gov.foia.broker.max_concurrent_listeners}")
    private Integer maxConcurrentListeners;

    @Value("${gov.foia.broker.queues.external_requests}")
    private String queuesExternalRequests;

    @Value("${gov.foia.broker.queues.external_request_status_updates}")
    private String queuesExternalRequestStatusUpdates;

    @Value("${gov.foia.broker.queues.external_request_files}")
    private String queuesExternalRequestFiles;

    @Value("${gov.foia.broker.file_upload_url}")
    private String fileUploadUrl;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getKeystore()
    {
        return keystore;
    }

    public void setKeystore(String keystore)
    {
        this.keystore = keystore;
    }

    public String getKeystorePassword()
    {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword)
    {
        this.keystorePassword = keystorePassword;
    }

    public String getTruststore()
    {
        return truststore;
    }

    public void setTruststore(String truststore)
    {
        this.truststore = truststore;
    }

    public String getTruststorePassword()
    {
        return truststorePassword;
    }

    public void setTruststorePassword(String truststorePassword)
    {
        this.truststorePassword = truststorePassword;
    }

    public Integer getMaxConcurrentWorkers()
    {
        return maxConcurrentWorkers;
    }

    public void setMaxConcurrentWorkers(Integer maxConcurrentWorkers)
    {
        this.maxConcurrentWorkers = maxConcurrentWorkers;
    }

    public Integer getMaxConcurrentListeners()
    {
        return maxConcurrentListeners;
    }

    public void setMaxConcurrentListeners(Integer maxConcurrentListeners)
    {
        this.maxConcurrentListeners = maxConcurrentListeners;
    }

    public String getQueuesExternalRequests()
    {
        return queuesExternalRequests;
    }

    public void setQueuesExternalRequests(String queuesExternalRequests)
    {
        this.queuesExternalRequests = queuesExternalRequests;
    }

    public String getQueuesExternalRequestStatusUpdates()
    {
        return queuesExternalRequestStatusUpdates;
    }

    public void setQueuesExternalRequestStatusUpdates(String queuesExternalRequestStatusUpdates)
    {
        this.queuesExternalRequestStatusUpdates = queuesExternalRequestStatusUpdates;
    }

    public String getQueuesExternalRequestFiles()
    {
        return queuesExternalRequestFiles;
    }

    public void setQueuesExternalRequestFiles(String queuesExternalRequestFiles)
    {
        this.queuesExternalRequestFiles = queuesExternalRequestFiles;
    }

    public String getFileUploadUrl()
    {
        return fileUploadUrl.replace("\\", "/");
    }

    public void setFileUploadUrl(String fileUploadUrl)
    {
        this.fileUploadUrl = fileUploadUrl;
    }
}
