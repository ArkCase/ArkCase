package com.armedia.acm.service.identity.dao;

import com.armedia.acm.service.identity.exceptions.AcmIdentityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

public class AcmArkcaseLocalIdentityDao implements AcmArkcaseIdentityDao
{
    public static final String PROPERTY_IDENTITY = "identity";
    public static final String PROPERTY_DIGEST = "digest";
    public static final String PROPERTY_DATE_CREATED = "date_created";
    public static final String ALGORITHM_MD5 = "MD5";
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private Path identityFilePath;

    @Override
    public String getIdentity() throws AcmIdentityException
    {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(identityFilePath))
        {
            properties.load(inputStream);
            // check if all properties exists
            if (!properties.containsKey(PROPERTY_IDENTITY) || !properties.containsKey(PROPERTY_DIGEST))
            {
                log.error("Some properties are missing. contains_identity=[{}], contains_digest=[{}]",
                        properties.containsKey(PROPERTY_IDENTITY),
                        properties.containsKey(PROPERTY_DIGEST));
                throw new AcmIdentityException("Missing some of the properties.");
            }
            String identity = properties.getProperty(PROPERTY_IDENTITY);
            byte[] digest = properties.getProperty(PROPERTY_DIGEST).getBytes();
            // check if identity is valid
            if (!Arrays.equals(digest, getMD5(identity)))
            {
                // identity has been changed
                throw new AcmIdentityException("Identity has been changed.");
            }
            return identity;
        }
        catch (IOException e)
        {
            log.error("Error generating digest for arkcase identity", e.getMessage());
            throw new AcmIdentityException("Error reading file " + identityFilePath, e);
        }
    }

    public String createIdentityIfNotExists() throws AcmIdentityException
    {
        String identity = UUID.randomUUID().toString();
        Properties properties = new Properties();

        if (!Files.exists(identityFilePath))
        {
            properties.setProperty(PROPERTY_IDENTITY, identity);
            properties.setProperty(PROPERTY_DATE_CREATED, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            properties.setProperty(PROPERTY_DIGEST, new String(getMD5(identity)));
            try (OutputStream out = Files.newOutputStream(identityFilePath))
            {
                properties.store(out, null);
            }
            catch (IOException e)
            {
                log.error("Error writing to file arkcase identity.", e.getMessage());
            }
        }
        else
        {
            return getIdentity();
        }

        return identity;
    }

    private byte[] getMD5(String identity)
    {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance(ALGORITHM_MD5);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("Error generating digest for arkcase identity", e.getMessage());
        }
        return md.digest(identity.getBytes());
    }

    public void setIdentityFilePath(String identityFilePath)
    {
        this.identityFilePath = Paths.get(identityFilePath);
    }
}
