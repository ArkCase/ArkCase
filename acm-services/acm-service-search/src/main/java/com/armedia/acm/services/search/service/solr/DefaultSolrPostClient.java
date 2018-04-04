package com.armedia.acm.services.search.service.solr;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class DefaultSolrPostClient implements SolrPostClient
{

    private String solrHost;
    private int solrPort;
    private String solrContextRoot;
    private String solrUpdateHandler;
    private String solrContentFileHandler;

    private RestTemplate restTemplate = new RestTemplate();
    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void sendToSolr(SolrCore core, SolrContentDocument solrContentDocument, InputStream stream, long contentLength)
            throws SolrPostException
    {
        Objects.requireNonNull(core, "Core must be specified");
        Objects.requireNonNull(core.getCore(), "The Solr core must have a value");
        Objects.requireNonNull(solrContentDocument, "Content document must be specified");
        Objects.requireNonNull(stream, "Content stream must be specified");

        final String url = String.format("https://%s:%s/%s/%s/%s?overwrite=true&%s",
                getSolrHost(),
                getSolrPort(),
                getSolrContextRoot(),
                core.getCore(),
                getSolrContentFileHandler(),
                solrContentDocument.getUrl());

        final String logUrl = trimForLogging(url);

        logger.debug("Sending content file [{}] to URL {}", solrContentDocument.getName(), logUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", solrContentDocument.getContent_type());
        // InputStreamFileResource inputStreamFileResource = new InputStreamFileResource(stream, contentLength);
        // HttpEntity<InputStreamFileResource> entity = new HttpEntity<>(inputStreamFileResource, headers);
        // HttpEntity<InputStreamResource> entity = new HttpEntity<>(new InputStreamResource(stream), headers);
        byte[] bytes;
        try
        {
            bytes = StreamUtils.copyToByteArray(stream);
            ByteArrayResource resource = new ByteArrayResource(bytes);
            HttpEntity<ByteArrayResource> entity = new HttpEntity<>(resource, headers);

            postToSolr(url, entity, logUrl);
        }
        catch (IOException e)
        {
            logger.error("Could not copy to byte array: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendToSolr(SolrCore core, String json) throws SolrPostException
    {
        Objects.requireNonNull(core, "Core must be specified");
        Objects.requireNonNull(json, "JSON must be specified");
        Objects.requireNonNull(core.getCore(), "The Solr core must have a value");

        String logJson = trimForLogging(json);
        logger.debug("Sending to Solr core {} with JSON {}", core.getCore(), logJson);

        final String url = String.format("https://%s:%s/%s/%s/%s?overwrite=true",
                getSolrHost(),
                getSolrPort(),
                getSolrContextRoot(),
                core.getCore(),
                getSolrUpdateHandler());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        postToSolr(url, entity, logJson);
    }

    private void postToSolr(String url, HttpEntity<? extends Object> entity, String logText) throws SolrPostException
    {
        try
        {

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError())
            {
                logger.error("Could not post to Solr: [{}], got response code {}", logText, response.getStatusCode().value());
                throw new SolrPostException("Could not post to Solr");
            }

            logger.debug("Posted to Solr: [{}], got response code {}", logText, response.getStatusCode().value());
        }
        catch (HttpClientErrorException e)
        {
            throw new SolrPostException(String.format("Could not post [%s] to Solr: %s", logText, e.getMessage()));
        }
    }

    private String trimForLogging(String longString)
    {
        String logString = longString != null && longString.length() > 50 ? longString.substring(0, 50) + "..." : longString;
        return logString;
    }

    private class InputStreamFileResource extends InputStreamResource
    {
        private final long contentLength;

        public InputStreamFileResource(InputStream inputStream, long contentLength)
        {
            super(inputStream);
            this.contentLength = contentLength;
        }

        @Override
        public long contentLength() throws IOException
        {
            return this.contentLength;
        }

    }

    public String getSolrHost()
    {
        return solrHost;
    }

    public void setSolrHost(String solrHost)
    {
        this.solrHost = solrHost;
    }

    public int getSolrPort()
    {
        return solrPort;
    }

    public void setSolrPort(int solrPort)
    {
        this.solrPort = solrPort;
    }

    public String getSolrContextRoot()
    {
        return solrContextRoot;
    }

    public void setSolrContextRoot(String solrContextRoot)
    {
        this.solrContextRoot = solrContextRoot;
    }

    public String getSolrUpdateHandler()
    {
        return solrUpdateHandler;
    }

    public void setSolrUpdateHandler(String solrUpdateHandler)
    {
        this.solrUpdateHandler = solrUpdateHandler;
    }

    public String getSolrContentFileHandler()
    {
        return solrContentFileHandler;
    }

    public void setSolrContentFileHandler(String solrContentFileHandler)
    {
        this.solrContentFileHandler = solrContentFileHandler;
    }

}
