package com.armedia.acm.service.identity.dao;

/*-
 * #%L
 * ACM Service: Arkcase Identity
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

import com.armedia.acm.service.identity.exceptions.AcmIdentityException;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
    public static final String HASH_ALGORITHM = "SHA-256";
    private transient final Logger log = LogManager.getLogger(getClass());
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
            byte[] digest = Base64.decodeBase64(properties.getProperty(PROPERTY_DIGEST));
            /*
             * FIXME using hash is not secure at all, anyone with some knowledge can change identity and change the
             * hash.
             * If needs to be more secure, than we should handle it differently like using encryption or signing the
             * file.
             */
            // check if identity is valid
            if (!Arrays.equals(digest, getIdentityDigest(identity)))
            {
                // identity has been changed
                throw new AcmIdentityException("Identity has been changed.");
            }
            return identity;
        }
        catch (IOException e)
        {
            log.error("Error generating digest for arkcase identity. [{}]", e.getMessage());
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
            properties.setProperty(PROPERTY_DIGEST, Base64.encodeBase64String(getIdentityDigest(identity)));
            try (OutputStream out = Files.newOutputStream(identityFilePath))
            {
                properties.store(out, null);
            }
            catch (IOException e)
            {
                log.error("Error writing to file arkcase identity. [{}]", e.getMessage());
            }
        }
        else
        {
            return getIdentity();
        }

        return identity;
    }

    private byte[] getIdentityDigest(String identity)
    {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance(HASH_ALGORITHM);
            return md.digest(identity.getBytes());
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("Error generating digest for arkcase identity. [{}]", e.getMessage());
        }

        return new byte[0];
    }

    public void setIdentityFilePath(String identityFilePath)
    {
        this.identityFilePath = Paths.get(identityFilePath);
    }
}
